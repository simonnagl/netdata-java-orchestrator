// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

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
		assertEquals(
				"[java.lang.Exception] Something went wrong. Detail: This is the reason. Detail: Here are the details.",
				message);
	}

	@Test
	public void testBuildMessageStringThrowable() {
		// Test
		String message = LoggingUtils.buildMessage("Could not do it.", exception);

		// Verify
		assertEquals(
				"Could not do it. Reason: [java.lang.Exception] Something went wrong. Detail: This is the reason. Detail: Here are the details.",
				message);
	}

	@Test
	public void testBuildMessageStrings() {
		// Test
		String message = LoggingUtils.buildMessage("This ", "should ", "be ", "one ", "message.");

		// Verify
		assertEquals("This should be one message.", message);
	}

	@Test
	public void testBuildMessageStringsNoArg() {
		// Test
		String message = LoggingUtils.buildMessage();

		// Verify
		assertEquals("", message);
	}

	@Test
	public void testBuildMessageStringsOneArg() {
		// Test
		String message = LoggingUtils.buildMessage("One Argument.");

		// Verify
		assertEquals("One Argument.", message);
	}

	@Test
	public void getMessageSupplierThrowable() {
		// Test
		Supplier<String> messageSupplier = LoggingUtils.getMessageSupplier(exception);

		// Verify
		assertEquals(
				"[java.lang.Exception] Something went wrong. Detail: This is the reason. Detail: Here are the details.",
				messageSupplier.get());
	}

	@Test
	public void getMessageSupplierStringThrowable() {
		// Test
		Supplier<String> messageSupplier = LoggingUtils.getMessageSupplier("Could not do it.", exception);

		// Verify
		assertEquals(
				"Could not do it. Reason: [java.lang.Exception] Something went wrong. Detail: This is the reason. Detail: Here are the details.",
				messageSupplier.get());

	}

	public void getMessageSuplierStrings() {
		// Test
		Supplier<String> messageSupplier = LoggingUtils.getMessageSupplier("This ", "should ", "be ", "one ",
				"message.");

		// Verify
		assertEquals("This should be one message.", messageSupplier.get());

	}

}
