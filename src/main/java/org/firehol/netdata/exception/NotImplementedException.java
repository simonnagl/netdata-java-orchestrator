// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.exception;

public class NotImplementedException extends UnsupportedOperationException {
	private static final long serialVersionUID = 129273964181145272L;

	public NotImplementedException() {
	}

	public NotImplementedException(String message) {
		super(message);
	}
}
