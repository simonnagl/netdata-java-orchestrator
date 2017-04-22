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

import org.firehol.netdata.plugin.config.BaseConfig;
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

	public static void main(final String[] args) {
		log.fine("Get options from environment");
		Long updateEverySec = Long.valueOf(System.getenv("NETDATA_UPDATE_EVERY"));
		Path configDir = Paths.get(System.getenv("NETDATA_CONFIG_DIR"));

		PluginHolder pluginHolder = PluginHolder.getInstance();

		// Here is the place all available plugins get added.
		// If they are disabled by configuration or cannot collect data
		// PluginHolder will remove them later.
		pluginHolder.add(new JmxPlugin());

		BaseConfig globalConfig = pluginHolder.readConfiguration(configDir);

		pluginHolder.initializeCharts();

		if (pluginHolder.getAllPluginSize() < 1) {
			log.severe("No Java Plugins avaiable. Disabling Java Plugin Daemon.");
			Printer.disable();
			System.exit(1);
		}

		// Check if configuration tells us to update less often the caller tells
		// us to.
		long configuredUpdateEverySec = Long
				.valueOf(globalConfig.get("global", "update every", updateEverySec.toString()));
		if (configuredUpdateEverySec < updateEverySec) {
			log.warning("Invalid option in sectino global detected. 'update every' is less than " + updateEverySec
					+ "which is the minimal requested value.");
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
