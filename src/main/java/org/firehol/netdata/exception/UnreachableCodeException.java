// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.exception;

public class UnreachableCodeException extends RuntimeException {
	private static final long serialVersionUID = 9126523210158499016L;

	public UnreachableCodeException() {
		super();
	}

	public UnreachableCodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
