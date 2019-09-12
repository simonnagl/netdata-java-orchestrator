// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.exception;

public class JmxMBeanServerQueryException extends JmxModuleException {
	private static final long serialVersionUID = 5480135001434254831L;

	public JmxMBeanServerQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxMBeanServerQueryException(String message) {
		super(message);
	}
}
