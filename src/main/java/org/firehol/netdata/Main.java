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

package org.firehol.netdata;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.exception.UnreachableCodeException;
import org.firehol.netdata.plugin.Printer;
import org.firehol.netdata.plugin.jmx.JmxPlugin;
import org.firehol.netdata.utils.AlignToTimeIntervalService;
import org.firehol.netdata.utils.LoggingUtils;
import org.firehol.netdata.utils.UnitConversion;

/**
 * Netdata Java Plugin Daemon.
 *
 * Holds the main Function of the daemon.
 *
 * @author Simon Nagl
 * @since 1.0.0
 */
public final class Main {
	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	/**
	 * Don't let anyone instantiate this class.
	 */
	private Main() {
	}
	
	/**
	 * Exit the process.
	 * 
	 * <ol>
	 * <li>Log {@code info}</li>
	 * <li>Tell the caller not to start the plugin again</li>
	 * <li>Kill the process</li>
	 * </ol>
	 * 
	 * @param info
	 *            message to log.
	 */
	public static void exit(String info) {
		log.severe(info);
		Printer.disable();
		System.exit(1);
	}

	/**
	 * Parse one parameter (from command line).
	 * 
	 * Exit the program if it could not be parsed.
	 * 
	 * @param commandLineParameter
	 * @return
	 */
	protected static long getUpdateEveryFailFast(String commandLineParameter) {
		try {
			return Long.valueOf(commandLineParameter);
		} catch (NumberFormatException e) {
			exit("Could not parse command line parameter '" + commandLineParameter + "'");
			throw new UnreachableCodeException();
		}
	}

	/**
	 * Initialize plugins and start the main loop.
	 * 
	 * @param args
	 *            First arg must be updateEvery.
	 */
	public static void main(final String[] args) {
		log.fine("Read command line options.");
		long updateEverySec = getUpdateEveryFailFast(args[0]);

		// Create plugins
		JmxPlugin jmxPlugin = new JmxPlugin();

		// Initialize plugins
		Collection<Chart> chartsToInitialize = new LinkedList<>();
		try {
			chartsToInitialize.addAll(jmxPlugin.initialize());
		} catch (InitializationException e) {
			log.severe(LoggingUtils.buildMessage("Could not initialize jmxPlugin", e));
		}

		if (chartsToInitialize.size() < 1) {
			exit("No Charts to initialize. Disabling Java Plugin Daemon.");
		}

		// Initialize charts
		for (Chart chart : chartsToInitialize) {
			Printer.initializeChart(chart);
		}

		// Start the main loop
		long updateEveryNSec = updateEverySec * UnitConversion.NANO_PER_PLAIN;
		AlignToTimeIntervalService timeService = new AlignToTimeIntervalService(updateEveryNSec);
		while (true) {
			timeService.alignToNextInterval();
			
			// Collect values and print them.
			jmxPlugin.collectValues().stream().forEach(Printer::collect);
		}
	}
}
