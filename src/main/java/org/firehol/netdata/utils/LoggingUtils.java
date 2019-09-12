// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import java.util.function.Supplier;

public final class LoggingUtils {

	private LoggingUtils() {
	}

	private static void appendMessage(Throwable reason, StringBuilder sb) {

		sb.append('[');
		sb.append(reason.getClass().getName());
		sb.append("] ");
		sb.append(reason.getMessage());

		Throwable detail = reason.getCause();
		while (detail != null) {
			sb.append(" Detail: ");
			sb.append(detail.getMessage());
			detail = detail.getCause();
		}
	}

	public static String buildMessage(Throwable reason) {
		StringBuilder sb = new StringBuilder();
		appendMessage(reason, sb);
		return sb.toString();
	}

	public static String buildMessage(String message, Throwable reason) {
		StringBuilder sb = new StringBuilder(message);

		sb.append(" Reason: ");
		appendMessage(reason, sb);
		return sb.toString();
	}

	public static String buildMessage(String... messages) {
		if (messages.length == 0) {
			return "";
		}

		if (messages.length == 1) {
			return messages[0];
		}

		// Find String length.
		int totalLength = 0;
		for (String message : messages) {
			totalLength = +message.length();
		}

		StringBuilder sb = new StringBuilder(totalLength);

		// Build the message
		for (String message : messages) {
			sb.append(message);
		}

		return sb.toString();
	}

	public static Supplier<String> getMessageSupplier(Throwable reason) {
		return () -> buildMessage(reason);
	}

	public static Supplier<String> getMessageSupplier(String message, Throwable reason) {
		return () -> buildMessage(message, reason);
	}

	public static Supplier<String> getMessageSupplier(String... messages) {
		return () -> buildMessage(messages);
	}
}
