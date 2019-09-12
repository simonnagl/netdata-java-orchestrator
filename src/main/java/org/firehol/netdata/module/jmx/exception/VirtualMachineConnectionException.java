// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.exception;

public class VirtualMachineConnectionException extends JmxModuleException {
	private static final long serialVersionUID = -4214747861980964583L;

	public VirtualMachineConnectionException(String message) {
		super(message);
	}

	public VirtualMachineConnectionException(String message, Throwable reason) {
		super(message, reason);
	}
}
