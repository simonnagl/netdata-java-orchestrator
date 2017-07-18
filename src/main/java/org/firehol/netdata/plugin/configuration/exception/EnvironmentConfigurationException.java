package org.firehol.netdata.plugin.configuration.exception;

import javax.naming.ConfigurationException;

public class EnvironmentConfigurationException extends ConfigurationException {
	private static final long serialVersionUID = 3081984800137015485L;

	public EnvironmentConfigurationException(String message) {
		super(message);
	}
}
