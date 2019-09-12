// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata;

import org.firehol.netdata.exception.AssertionException;
import org.firehol.netdata.exception.IllegalCommandLineArumentException;

public final class CommandLineArgs {

	private final String[] args;

	public CommandLineArgs(final String[] args) {
		this.args = args;
	}

	public int getUpdateEveryInSeconds() throws IllegalCommandLineArumentException {
		try {
			assertJustOne();
		} catch (Exception e) {
			throw new IllegalCommandLineArumentException("Wrong number of command lines found.", e);
		}

		try {
			return Integer.valueOf(args[0]);
		} catch (Exception e) {
			throw new IllegalCommandLineArumentException("First command line argument is no integer.", e);
		}

	}

	private void assertJustOne() throws AssertionException {
		if (args.length < 1) {
			throw new AssertionException("Expected just one command line argument. " + args.length + " are present.");
		}
	}

}
