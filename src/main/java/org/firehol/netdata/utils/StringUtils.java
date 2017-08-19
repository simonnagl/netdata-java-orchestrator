/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.firehol.netdata.utils;

/**
 * Operations on {@link java.lang.String}.
 *
 * @since 1.0.0
 * @author Simon Nagl
 * @see java.lang.String
 */
public class StringUtils {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private StringUtils() {
	}

	/**
	 * Checks if a String is only whitespace, empty ("") or null.
	 *
	 * @param string
	 *            the String to check
	 * @return {@code true} if the String is blank
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
