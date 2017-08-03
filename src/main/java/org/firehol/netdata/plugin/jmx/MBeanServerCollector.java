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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.Collector;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxDimensionConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxServerConfiguration;
import org.firehol.netdata.plugin.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.utils.LoggingUtils;

import lombok.Getter;

public class MBeanServerCollector implements Collector, Closeable {

	private final int LONG_RESOLUTION = 100;

	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private JmxServerConfiguration serverConfiguration;

	@Getter
	private final MBeanServerConnection mBeanServer;

	private JMXConnector jmxConnector;

	private List<MBeanQueryInfo> allMBeanQueryInfo = new LinkedList<>();

	private List<Chart> allChart = new LinkedList<>();

	/**
	 * Creates an MBeanServerCollector.
	 * 
	 * Only use this when you do not want to close the underlying JMXConnetor when
	 * closing the generated MBeanServerCollector.
	 * 
	 * @param name
	 *            presented at the submenu.
	 * @param mBeanServer
	 */
	public MBeanServerCollector(JmxServerConfiguration configuration, MBeanServerConnection mBeanServer) {
		this.serverConfiguration = configuration;
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerCollector(JmxServerConfiguration configuration, MBeanServerConnection mBeanServer,
			JMXConnector jmxConnector) {
		this(configuration, mBeanServer);
		this.jmxConnector = jmxConnector;
	}

	public Collection<Chart> initialize() throws InitializationException {

		// Step 1
		// Check commonChart configuration
		for (JmxChartConfiguration chartConfig : serverConfiguration.getCharts()) {
			Chart chart = initializeChart(chartConfig);

			// Check if the mBeanServer has the desired sources.
			for (JmxDimensionConfiguration dimensionConfig : chartConfig.getDimensions()) {

				// Add to queryInfo
				MBeanQueryInfo queryInfo;
				try {
					queryInfo = initializeMBeanQueryInfo(dimensionConfig);
				} catch (JmxMBeanServerQueryException e) {
					log.warning(LoggingUtils.buildMessage("Could not query one dimension. Skipping...", e));
					continue;
				}
				allMBeanQueryInfo.add(queryInfo);

				Dimension dimension = initializeDimension(chartConfig, dimensionConfig, queryInfo.getType());
				queryInfo.getDimensions().add(dimension);
				chart.getAllDimension().add(dimension);
			}

			allChart.add(chart);
		}

		return allChart;
	}

	protected Chart initializeChart(JmxChartConfiguration config) {
		Chart chart = new Chart();

		chart.setType("Jmx");
		chart.setFamily(serverConfiguration.getName());
		chart.setId(config.getId());
		chart.setTitle(config.getTitle());
		chart.setUnits(config.getUnits());
		chart.setContext(serverConfiguration.getName());
		chart.setChartType(config.getChartType());
		if (config.getPriority() != null) {
			chart.setPriority(config.getPriority());
		}

		return chart;
	}

	protected Dimension initializeDimension(JmxChartConfiguration chartConfig,
			JmxDimensionConfiguration dimensionConfig, Class<?> valueType) {
		Dimension dimension = new Dimension();
		dimension.setId(dimensionConfig.getName());
		dimension.setName(dimensionConfig.getName());
		dimension.setAlgorithm(chartConfig.getDimType());
		dimension.setMultiplier(dimensionConfig.getMultiplier());
		dimension.setDivisor(dimensionConfig.getDivisor());

		if (Double.class.isAssignableFrom(valueType)) {
			dimension.setDivisor(dimension.getDivisor() * this.LONG_RESOLUTION);
		}

		return dimension;
	}

	protected MBeanQueryInfo initializeMBeanQueryInfo(JmxDimensionConfiguration dimensionConfig)
			throws JmxMBeanServerQueryException {

		// Query once to get dataType.
		ObjectName name = null;
		try {
			name = ObjectName.getInstance(dimensionConfig.getFrom());
		} catch (MalformedObjectNameException e) {
			throw new JmxMBeanServerQueryException("'" + dimensionConfig.getFrom() + "' is no valid JMX ObjectName", e);
		} catch (NullPointerException e) {
			throw new JmxMBeanServerQueryException("'' is no valid JMX OBjectName", e);
		}
		Object value = getAttribute(name, dimensionConfig.getValue());

		// Add to queryInfo
		MBeanQueryInfo queryInfo = new MBeanQueryInfo();
		queryInfo.setName(name);
		queryInfo.setAttribute(dimensionConfig.getValue());
		queryInfo.setType(value.getClass());

		return queryInfo;
	}

	protected Object getAttribute(ObjectName name, String attribute) throws JmxMBeanServerQueryException {
		try {
			return mBeanServer.getAttribute(name, attribute);
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			throw new JmxMBeanServerQueryException(
					"Could not query attribute '" + attribute + "' of MBean '" + name + "'", e);
		}

	}

	protected long toLong(Object any) {
		if (any instanceof Integer) {
			return ((Integer) any).longValue();
		} else if (any instanceof Double) {
			double doubleValue = (double) any;
			return (long) (doubleValue * this.LONG_RESOLUTION);
		} else {
			return (long) any;
		}

	}

	public Collection<Chart> collectValues() {
		// Query all attributes and fill charts.
		Iterator<MBeanQueryInfo> queryInfoIterator = allMBeanQueryInfo.iterator();

		while (queryInfoIterator.hasNext()) {
			MBeanQueryInfo queryInfo = queryInfoIterator.next();

			try {
				long value = toLong(getAttribute(queryInfo.getName(), queryInfo.getAttribute()));
				for (Dimension dim : queryInfo.getDimensions()) {
					dim.setCurrentValue(value);
				}
			} catch (JmxMBeanServerQueryException e) {
				// Stop collecting this value.
				log.warning(LoggingUtils.buildMessage(
						"Stop collection value '" + queryInfo.getAttribute() + "' of '" + queryInfo.getName() + "'.",
						e));
				queryInfoIterator.remove();
			}
		}

		// Return Updated Charts.
		return allChart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (this.jmxConnector != null) {
			this.jmxConnector.close();
		}
	}

	@Override
	public void cleanup() {
		try {
			close();
		} catch (IOException e) {
			log.warning(LoggingUtils.buildMessage("Could not cleanup MBeanServerCollector.", e));
		}
	}
}
