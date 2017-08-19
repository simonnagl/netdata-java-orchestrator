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

/**
 * Represents an exception while querying a MBean.
 * 
 * @since 1.0.0
 * @author Simon Nagl
 */
public class JmxMBeanServerQueryException extends JmxPluginException {
	private static final long serialVersionUID = 5480135001434254831L;

	public JmxMBeanServerQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmxMBeanServerQueryException(String message) {
		super(message);
	}
}
