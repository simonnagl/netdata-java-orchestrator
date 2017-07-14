package org.firehol.netdata.plugin.jmx.exception;

/**
 * Errors while querying a MBean
 * 
 * @author Simon Nagl
 */
public class JmxMBeanServerQueryException extends Exception {
	private static final long serialVersionUID = 5480135001434254831L;

	public JmxMBeanServerQueryException() {
		super();
	}

	public JmxMBeanServerQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxMBeanServerQueryException(String message) {
		super(message);
	}

	public JmxMBeanServerQueryException(Throwable cause) {
		super(cause);
	}
}
