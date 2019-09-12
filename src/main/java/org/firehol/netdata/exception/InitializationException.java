// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.exception;

/**
 * Wraps an Exception during initialization. Marks the throwing object as
 * invalid.
 */
public class InitializationException extends Exception {
	private static final long serialVersionUID = 6446433868518776741L;

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
