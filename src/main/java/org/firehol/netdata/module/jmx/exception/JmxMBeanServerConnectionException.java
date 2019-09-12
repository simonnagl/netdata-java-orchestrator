// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.exception;

public class JmxMBeanServerConnectionException extends JmxModuleException {
	private static final long serialVersionUID = -6153969842214336278L;

	public JmxMBeanServerConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
