package org.firehol.netdata.plugin.configuration.exception;

import javax.naming.ConfigurationException;

public class ParseException extends ConfigurationException {
	private static final long serialVersionUID = 1383816306854639084L;

	public ParseException() {
		super();
	}

	public ParseException(String explanation) {
		super(explanation);
	}
	
	public ParseException(Throwable cause) {
		this.setRootCause(cause);
	}
	
	public ParseException(String explanation, Throwable cause) {
		super(explanation);
		this.setRootCause(cause);
	}

}
