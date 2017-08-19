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

package org.firehol.netdata.plugin.jmx.exception;

public class JmxPluginException extends Exception {
	private static final long serialVersionUID = -9084555240752421197L;

	public JmxPluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxPluginException(String message) {
		super(message);
	}
}
