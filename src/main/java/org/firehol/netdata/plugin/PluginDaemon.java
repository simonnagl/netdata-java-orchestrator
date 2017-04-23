/*
 * Copyright (C) 2017 Simon Nagl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.firehol.netdata.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.firehol.netdata.plugin.configuration.PluginDaemonConfiguration;
import org.firehol.netdata.plugin.jmx.JmxPlugin;
import org.firehol.netdata.utils.AlignToTimeIntervalService;
import org.firehol.netdata.utils.UnitConversion;

/**
 * Netdata Java Plugin Daemon.
 *
 * Holds the main Function of the daemon.
 *
 */
public class PluginDaemon {
	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private static void exit(String info) {
		log.severe(info);
		Printer.disable();
		System.exit(1);
	}

	public static void main(final String[] args) {
		log.fine("Get options from environment");

		log.finer("Parse environment variable NETDATA_UPDATE_EVERY");
		String updateEverySecString = System.getenv("NETDATA_UPDATE_EVERY");
		Long updateEverySec;
		try {
			updateEverySec = Long.valueOf(updateEverySecString);
		} catch (NumberFormatException e) {
			exit("Could not parse NETDATA_UPDATE_EVERY = " + updateEverySecString);
			throw new RuntimeException(); // To prevent compile errors.
		}

		log.finer("Parse environment variable NETDATA_CONFIG_DIR");
		String configDirString = System.getenv("NETDATA_CONFIG_DIR");
		if (configDirString == null) {
			exit("No NETDATA_CONFIG_DIR provided");
		}
		Path configDir = Paths.get(configDirString);

		PluginHolder pluginHolder = PluginHolder.getInstance();

		// Here is the place all available plugins get added.
		// If they are disabled by configuration or cannot collect data
		// PluginHolder will remove them later.
		pluginHolder.add(new JmxPlugin());

		PluginDaemonConfiguration globalConfig = pluginHolder.readConfiguration(configDir);

		pluginHolder.initializeCharts();

		if (pluginHolder.getAllPluginSize() < 1) {
			exit("No Java Plugins avaiable. Disabling Java Plugin Daemon.");
		}

		// Check if configuration tells us to update less often the caller tells
		// us to.
		long configuredUpdateEverySec = globalConfig.getUpdateEvery();
		if (configuredUpdateEverySec < updateEverySec) {
			log.warning("Invalid option detected. 'update every' is less than " + updateEverySec
					+ " which is the minimal requested value.");
			globalConfig.setUpdateEvery(updateEverySec);
		} else {
			updateEverySec = configuredUpdateEverySec;
		}

		// Start the main loop
		long updateEveryNSec = updateEverySec * UnitConversion.NANO_PER_PLAIN;
		AlignToTimeIntervalService timeService = new AlignToTimeIntervalService(updateEveryNSec);
		while (true) {
			timeService.alignToNextInterval();

			pluginHolder.collectValues();
		}
	}
}
