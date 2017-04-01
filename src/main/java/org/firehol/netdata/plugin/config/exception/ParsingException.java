package org.firehol.netdata.plugin.config.exception;

import javax.naming.ConfigurationException;

public class ParsingException extends ConfigurationException {
	private static final long serialVersionUID = 1383816306854639084L;

	public ParsingException() {
		super();
	}

	public ParsingException(String explanation) {
		super(explanation);
	}
}
