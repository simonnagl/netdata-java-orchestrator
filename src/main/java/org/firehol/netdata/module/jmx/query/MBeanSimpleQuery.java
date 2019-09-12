// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.query;

import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import lombok.Getter;

/**
 * A MBeanQuery is responsible to query a attribute of a MBean and place it's
 * value into {@link Dimension}
 */
@Getter
public class MBeanSimpleQuery extends MBeanQuery {

	private final MBeanValueStore valueStore;

	MBeanSimpleQuery(final MBeanServerConnection mBeanServer, final ObjectName name, final String attribute,
			final MBeanValueStore valueStore) {
		super(mBeanServer, name, attribute);
		this.valueStore = valueStore;
	}

	public List<Dimension> getDimensions() {
		return valueStore.getAllDimension();
	}

	/**
	 * Add a dimension which value should be updated by this query.
	 * <p>
	 * Attribute must match {@link #getAttribute()} or be more precise.
	 *
	 * @param dimension
	 *            to add to the list of dimensions
	 * @param attribute
	 *            of the MBean which should be queried
	 */
	@Override
	public void addDimension(Dimension dimension, final String attribute) {
		if (!this.getAttribute().equals(attribute)) {
			throw new IllegalArgumentException(
					String.format("attribute '%s' must match this.attribute '%s'", attribute, this.getAttribute()));
		}

		this.valueStore.addDimension(dimension);
	}

	@Override
	public void query() throws JmxMBeanServerQueryException {
		Object result = MBeanServerUtils.getAttribute(getMBeanServer(), this.getName(), this.getAttribute());
		valueStore.updateValue(result);
	}
}
