package org.firehol.netdata.testutils;

import lombok.Getter;

@Getter
public class ExitException extends SecurityException {
	private static final long serialVersionUID = 1000706666595954099L;
	
	private final int status;
	
	public ExitException(int status) {
		super();
		this.status = status;
	}
}
