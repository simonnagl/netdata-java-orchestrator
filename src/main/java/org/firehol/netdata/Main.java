// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.firehol.netdata.exception.UnreachableCodeException;
import org.firehol.netdata.module.Module;
import org.firehol.netdata.module.jmx.JmxModule;
import org.firehol.netdata.orchestrator.Orchestrator;
import org.firehol.netdata.orchestrator.Printer;
import org.firehol.netdata.orchestrator.configuration.ConfigurationService;
import org.firehol.netdata.utils.LoggingUtils;

public final class Main {
	private static final Logger log = Logger.getLogger("org.firehol.netdata.orchestrator");

	private static List<Module> modules = Collections.emptyList();

	private Main() {
	}

	public static void main(final String[] args) {
		int updateEverySecond = getUpdateEveryInSecondsFomCommandLineFailFast(args);
		configureModules();
		new Orchestrator(updateEverySecond, modules).start();
	}

	static int getUpdateEveryInSecondsFomCommandLineFailFast(final String[] args) {
		try {
			return new CommandLineArgs(args).getUpdateEveryInSeconds();
		} catch (Exception failureReason) {
			exit(LoggingUtils.buildMessage("Invalid command line options supplied.", failureReason));
			throw new UnreachableCodeException();
		}
	}

	private static void configureModules() {
		ConfigurationService configService = ConfigurationService.getInstance();
		modules = new LinkedList<>();
		modules.add(new JmxModule(configService));
	}

	public static void exit(String info) {
		log.severe(info);
		Printer.disable();
		System.exit(1);
	}
}
