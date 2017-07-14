package org.firehol.netdata.plugin.jmx.exception;

/**
 * Errors while initializing a connection to a JxmMBean Server.
 * 
 * @author Simon Nagl
 */
public class JmxMBeanServerConnectionException extends Exception {
	private static final long serialVersionUID = -6153969842214336278L;

	public JmxMBeanServerConnectionException() {
		super();
	}

	public JmxMBeanServerConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxMBeanServerConnectionException(String message) {
		super(message);
	}

	public JmxMBeanServerConnectionException(Throwable cause) {
		super(cause);
	}
}
