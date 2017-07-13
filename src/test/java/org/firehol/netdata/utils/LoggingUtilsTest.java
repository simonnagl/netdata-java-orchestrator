package org.firehol.netdata.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LoggingUtilsTest {

	private Exception exception;

	@Before
	public void init() {
		Exception fine = new Exception("Here are the details.");
		Exception detail = new Exception("This is the reason.", fine);
		exception = new Exception("Something went wrong.", detail);
	}

	@Test
	public void testBuildMessageThrowable() {
		// Test
		String message = LoggingUtils.buildMessage(exception);

		// Verify
		assertEquals("Something went wrong. Detail: This is the reason. Detail: Here are the details.", message);
	}

	@Test
	public void testBuildMessageStringThrowable() {
		// Test
		String message = LoggingUtils.buildMessage("Could not do it.", exception);

		// Verify
		assertEquals(
				"Could not do it. Reason: Something went wrong. Detail: This is the reason. Detail: Here are the details.",
				message);
	}

}
