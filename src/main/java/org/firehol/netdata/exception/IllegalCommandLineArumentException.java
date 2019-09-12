// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.exception;

public class IllegalCommandLineArumentException extends Exception {
	private static final long serialVersionUID = 3530966792041074621L;

	public IllegalCommandLineArumentException(String message, Throwable reason) {
		super(message, reason);
	}
}
