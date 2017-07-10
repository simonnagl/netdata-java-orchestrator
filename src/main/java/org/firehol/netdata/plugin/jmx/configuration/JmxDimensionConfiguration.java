package org.firehol.netdata.plugin.jmx.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmxDimensionConfiguration {

	/**
	 * Jmx Object Name.
	 */
	private String from;

	/**
	 * jmxBean property
	 */
	private String value;

	/**
	 * Name displayed to user.
	 */
	private String name;

	private boolean hidden = false;
}
