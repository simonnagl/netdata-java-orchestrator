package org.firehol.netdata.exception;


/**
 * Wraps an Exception during initialization. Marks the object as invalid.
 * 
 * @author Simon Nagl
 */
public class InitializationException extends Exception {
	private static final long serialVersionUID = 6446433868518776741L;

	public InitializationException(Throwable cause) {
		super(cause);
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
