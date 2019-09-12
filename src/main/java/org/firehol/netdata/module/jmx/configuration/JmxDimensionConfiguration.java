// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.configuration;

import org.firehol.netdata.module.jmx.JmxModule;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration scheme of a dimension of a chart created by the
 * {@link JmxModule}.
 */
@Getter
@Setter
public class JmxDimensionConfiguration {

	/**
	 * Jmx Object Name.
	 */
	private String from;

	/**
	 * JmxBean property.
	 *
	 * To access a property of a CompositeData use
	 * <code>property.compositeDataKey</code>
	 */
	private String value;

	/**
	 * Multiply the collected value before displaying it.
	 */
	private int multiplier = 1;
	/**
	 * Divide the collected value before displaying it.
	 */
	private int divisor = 1;

	/**
	 * Name displayed to user.
	 */
	private String name;

	/**
	 * If true the value get's collected but not displayed.
	 */
	private boolean hidden = false;
}
