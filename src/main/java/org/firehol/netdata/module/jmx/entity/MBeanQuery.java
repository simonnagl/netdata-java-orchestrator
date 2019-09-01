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

package org.firehol.netdata.module.jmx.entity;

import java.util.LinkedList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.firehol.netdata.model.Dimension;

import lombok.Getter;
import lombok.Setter;
import org.firehol.netdata.module.jmx.MBeanServerCollector;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

/**
 * Technical Object which contains information which attributes of an M(X)Bean
 * we collect and where to store the collected values.
 */
@Getter
@Setter
public class MBeanQuery {

	private ObjectName name;

	private String attribute;

	/**
	 * The Class of the object returned by the query.
	 */
	private Class<?> type;

	private List<Dimension> dimensions = new LinkedList<>();

	public void query(MBeanServerConnection mBeanServer) throws JmxMBeanServerQueryException {
		Long result = toLong(MBeanServerUtils.getAttribute(mBeanServer, this.name, this.attribute));

		dimensions.forEach(dimension -> dimension.setCurrentValue(result));
	}

	private long toLong(Object any) {
		if (any instanceof Integer) {
			return ((Integer) any).longValue();
		} else if (any instanceof Double) {
			double doubleValue = (double) any;
			return (long) (doubleValue * MBeanServerCollector.LONG_RESOLUTION);
		} else {
			return (long) any;
		}
	}
}