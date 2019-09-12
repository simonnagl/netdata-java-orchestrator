// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

public final class StringUtils {

	private StringUtils() {
	}

	/**
	 * Checks if a String is only whitespace, empty ("") or null.
	 */
	public static boolean isBlank(String string) {
		int length;
		if (string == null || (length = string.length()) == 0) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
