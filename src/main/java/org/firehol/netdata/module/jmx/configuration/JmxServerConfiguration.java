// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.configuration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration scheme to configure JMX agents to monitor.
 */
@Getter
@Setter
public class JmxServerConfiguration {
	/**
	 * JMX Service URL used to connect to the JVM.
	 * 
	 * <blockquote> {@code service:jmx:rmi://[host[:port]][urlPath]}
	 * </blockquote>
	 * 
	 * @see <a href=
	 *      "https://docs.oracle.com/cd/E19159-01/819-7758/gcnqf/index.html">Oracle
	 *      Developer's Guide for JMX Clients</a>
	 * 
	 */
	private String serviceUrl;

	/**
	 * Name displayed at the dashboard.
	 */
	private String name;

	@JsonIgnore
	// This property is not part of the configuration scheme.
	// This is a technical property used by the module.
	private List<JmxChartConfiguration> charts;
}
