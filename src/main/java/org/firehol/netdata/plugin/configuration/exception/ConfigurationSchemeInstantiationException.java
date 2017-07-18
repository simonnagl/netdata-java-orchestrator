package org.firehol.netdata.plugin.configuration.exception;

import javax.naming.ConfigurationException;

public class ConfigurationSchemeInstantiationException extends ConfigurationException {
	private static final long serialVersionUID = -5538037492659066003L;

	public ConfigurationSchemeInstantiationException() {
	}

	public ConfigurationSchemeInstantiationException(String explanation) {
		super(explanation);
	}
}
