/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
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

public final class Main {
	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private Main() {
	}

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
	 *            updateEvery part from the command line
	 * @return parsed update interval in seconds
	 */
	protected static long getUpdateEveryFailFast(String commandLineParameter) {
		try {
			return Long.valueOf(commandLineParameter);
		} catch (NumberFormatException e) {
			exit("Could not parse command line parameter '" + commandLineParameter + "'");
			throw new UnreachableCodeException();
		}
	}

	public static void main(final String[] args) {
		log.fine("Read command line options.");

		if (args.length < 1) {
			exit("Can't find a command line parameter. Expected one which configures the update interval in seconds.");
		}
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
