package org.firehol.netdata.utils;

public abstract class LoggingUtils {

	private static void appendMessage(Throwable reason, StringBuilder sb) {
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
}
