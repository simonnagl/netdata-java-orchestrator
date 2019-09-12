// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.configuration;

import java.util.ArrayList;
import java.util.List;

import org.firehol.netdata.module.jmx.JmxModule;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration scheme to configure {@link JmxModule}
 * 
 * @since 1.0.0
 * @author Simon Nagl
 */
@Getter
@Setter
public class JmxModuleConfiguration {

	/**
	 * If true auto detect and monitor running local virtual machines.
	 */
	private boolean autoDetectLocalVirtualMachines = true;

	/**
	 * A list of JMX servers to monitor.
	 */
	private List<JmxServerConfiguration> jmxServers = new ArrayList<>();

	/**
	 * A list of chart configurations.
	 * 
	 * <p>
	 * Every monitored JMX Servers tries to monitor each chart in this list. If
	 * a JMX Server does not have the required M(X)Beans we won't try adding it
	 * over and over again.
	 * </p>
	 */
	private List<JmxChartConfiguration> commonCharts = new ArrayList<>();
}
