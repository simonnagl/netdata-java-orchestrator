// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.exception;

public class JmxModuleException extends Exception {
	private static final long serialVersionUID = -9084555240752421197L;

	public JmxModuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxModuleException(String message) {
		super(message);
	}
}
