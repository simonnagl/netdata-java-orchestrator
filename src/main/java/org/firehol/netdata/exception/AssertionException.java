// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.exception;

public class AssertionException extends Exception {
	private static final long serialVersionUID = -8681237240624248839L;

	public AssertionException(String message) {
		super(message);
	}
}
