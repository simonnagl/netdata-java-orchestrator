package org.firehol.netdata.exception;

/**
 * Thrown to indicate that a block of code has not been implemented yet.
 * 
 * @author Simon Nagl
 */
public class NotImplementedException extends UnsupportedOperationException {
	private static final long serialVersionUID = 129273964181145272L;

	/**
	 * Constructs a NotImplementedException.
	 */
	public NotImplementedException() {
	}

	/**
	 * Constructs a NotImplementedException.
	 * 
	 * 
	 * @param message
	 *            Description what is missing here
	 */
	public NotImplementedException(String message) {
		super(message);
	}
}
