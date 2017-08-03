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

package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.Collector;
import org.firehol.netdata.plugin.configuration.ConfigurationService;
import org.firehol.netdata.plugin.configuration.exception.ConfigurationSchemeInstantiationException;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxPluginConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxServerConfiguration;
import org.firehol.netdata.plugin.jmx.exception.JmxMBeanServerConnectionException;
import org.firehol.netdata.utils.LoggingUtils;
import org.firehol.netdata.utils.ResourceUtils;

public class JmxPlugin implements Collector {

	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private final ConfigurationService configurationService = ConfigurationService.getInstance();

	private final List<MBeanServerCollector> allMBeanCollector = new ArrayList<>();

	@Override
	public Collection<Chart> initialize() throws InitializationException {

		// Read configuration
		JmxPluginConfiguration configuration;
		try {
			configuration = configurationService.readPluginConfiguration("jmx", JmxPluginConfiguration.class);
		} catch (ConfigurationSchemeInstantiationException e) {
			throw new InitializationException("Could not read jmx plugin configuration", e);
		}

		// Propagate Common Charts to Server configurations.
		for (JmxServerConfiguration serverConfiguartion : configuration.getJmxServers()) {
			Map<String, JmxChartConfiguration> chartConfigById = chartConfigurationsById(
					serverConfiguartion.getCharts());

			for (JmxChartConfiguration chartConfig : configuration.getCommonCharts()) {
				chartConfigById.putIfAbsent(chartConfig.getId(), chartConfig);
			}

			List<JmxChartConfiguration> chartConfigs = chartConfigById.values().stream().collect(Collectors.toList());
			serverConfiguartion.setCharts(chartConfigs);
		}

		// Connect to MBeanServers of configuration.
		for (JmxServerConfiguration serverConfiguartion : configuration.getJmxServers()) {
			MBeanServerCollector collector;
			try {
				collector = buildMBeanServerCollector(serverConfiguartion);
			} catch (JmxMBeanServerConnectionException e) {
				log.warning(LoggingUtils.buildMessage(e));
				continue;
			}

			allMBeanCollector.add(collector);
		}

		// Connect to the local MBeanServer
		JmxServerConfiguration localConfiguration = new JmxServerConfiguration();
		localConfiguration.setCharts(configuration.getCommonCharts());
		localConfiguration.setName("NetdataJavaDaemon");

		MBeanServerCollector collector = new MBeanServerCollector(localConfiguration,
				ManagementFactory.getPlatformMBeanServer());
		allMBeanCollector.add(collector);

		// Initialize charts

		List<Chart> allChart = new LinkedList<>();
		Iterator<MBeanServerCollector> mBeanCollectorIterator = allMBeanCollector.iterator();

		while (mBeanCollectorIterator.hasNext()) {
			MBeanServerCollector mBeanCollector = mBeanCollectorIterator.next();
			try {
				allChart.addAll(mBeanCollector.initialize());
			} catch (InitializationException e) {
				log.warning("Could not initialize JMX plugin " + mBeanCollector.getMBeanServer().toString());
				ResourceUtils.close(mBeanCollector);
				mBeanCollectorIterator.remove();
			}
		}

		return allChart;
	}

	private Map<String, JmxChartConfiguration> chartConfigurationsById(List<JmxChartConfiguration> charts) {
		return charts.stream().collect(Collectors.toMap(JmxChartConfiguration::getId, Function.identity()));
	}

	protected MBeanServerCollector buildMBeanServerCollector(JmxServerConfiguration config)
			throws JmxMBeanServerConnectionException {
		JMXConnector connection = null;
		try {
			JMXServiceURL url = new JMXServiceURL(null, null, config.getPort());
			connection = JMXConnectorFactory.connect(url);
			MBeanServerConnection server = connection.getMBeanServerConnection();
			MBeanServerCollector collector = new MBeanServerCollector(config, server, connection);
			return collector;
		} catch (IOException e) {
			if (connection != null) {
				ResourceUtils.close(connection);
			}
			throw new JmxMBeanServerConnectionException(
					"Faild to connect to JMX Server at port " + config.getPort() + ".", e);
		}
	}

	public void cleanup() {
		try {
			CompletableFuture
					.allOf(allMBeanCollector.stream().map(ResourceUtils::close).toArray(CompletableFuture[]::new))
					.get();
		} catch (InterruptedException | ExecutionException e) {
			log.fine("Could not close connection to at least one JMX Server");
		}

	}

	@Override
	public Collection<Chart> collectValues() {
		return allMBeanCollector.stream().map(MBeanServerCollector::collectValues).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

}
