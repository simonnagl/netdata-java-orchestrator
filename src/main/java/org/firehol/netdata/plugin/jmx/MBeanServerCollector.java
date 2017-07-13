package org.firehol.netdata.plugin.jmx;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
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
import org.firehol.netdata.exception.NotImplementedException;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxDimensionConfiguration;

import lombok.Getter;
import lombok.Setter;

public class MBeanServerCollector implements Closeable {

	private final int LONG_RESOLUTION = 100;

	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private MBeanServerConnection mBeanServer;

	private JMXConnector jmxConnector;

	private List<MBeanQueryInfo> allMBeanQueryInfo = new LinkedList<>();

	private List<Chart> allChart = new LinkedList<>();

	/**
	 * Name represented to the user.
	 */
	private String name;

	@Getter
	@Setter
	private class MBeanQueryInfo {
		private ObjectName name;
		private String attribute;
		private List<Dimension> dimensions = new LinkedList<>();
	}

	/**
	 * Creates an MBeanServerCollector.
	 * 
	 * Only use this when you do not want to close the underlying JMXConnetor
	 * when closing the generated MBeanServerCollector.
	 * 
	 * @param name
	 *            presented at the submenu.
	 * @param mBeanServer
	 */
	public MBeanServerCollector(String name, MBeanServerConnection mBeanServer) {
		this.name = name;
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerCollector(String name, MBeanServerConnection mBeanServer, JMXConnector jmxConnector) {
		this(name, mBeanServer);
		this.jmxConnector = jmxConnector;
	}

	public MBeanServerConnection getmBeanServer() {
		return mBeanServer;
	}

	public Collection<Chart> initialize(Collection<JmxChartConfiguration> allChartConfig)
			throws InitializationException {

		// Step 1
		// Check commonChart configuration
		for (JmxChartConfiguration chartConfig : allChartConfig) {
			Chart chart = initializeChart(chartConfig);

			// Check if the mBeanServer has the desired sources.
			for (JmxDimensionConfiguration dimensionConfig : chartConfig.getDimensions()) {
				ObjectName name = null;
				try {
					name = ObjectName.getInstance(dimensionConfig.getFrom());
				} catch (MalformedObjectNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (name == null) {
					// TODO: Proper error message
					continue;
				}

				Object value = getAttribute(name, dimensionConfig.getValue());

				Dimension dimension = new Dimension();
				dimension.setId(dimensionConfig.getName());
				dimension.setName(dimensionConfig.getName());
				dimension.setAlgorithm(chartConfig.getDimType());
				// dimension.setMultiplier();
				// dimension.setDivisor();
				if (value instanceof Double) {
					dimension.setDivisor(this.LONG_RESOLUTION);
				}

				// Add to chart
				chart.getAllDimension().add(dimension);

				// Add to queryInfo
				MBeanQueryInfo queryInfo = new MBeanQueryInfo();
				queryInfo.setName(name);
				queryInfo.setAttribute(dimensionConfig.getValue());
				queryInfo.getDimensions().add(dimension);
				allMBeanQueryInfo.add(queryInfo);
			}

			allChart.add(chart);
		}

		return allChart;
	}

	private Chart initializeChart(JmxChartConfiguration config) {
		Chart chart = new Chart();

		chart.setType("Jmx");
		chart.setFamily(name);
		chart.setId(config.getId());
		chart.setTitle(config.getTitle());
		chart.setUnits(config.getUnits());
		chart.setPriority(8000);
		chart.setContext(name);
		chart.setUpdateEvery(1);
		chart.setChartType(config.getChartType());

		return chart;
	}

	private Object getAttribute(ObjectName name, String attribute) {
		try {
			return mBeanServer.getAttribute(name, attribute);
		} catch (AttributeNotFoundException e) {
			// The attribute specified is not accessible in the MBean.
			// This should never happen here.

			throw new NotImplementedException("Error handling of AttributeNotFoundException");
		} catch (InstanceNotFoundException e) {
			// The MBean specified was unregistered in the MBean server.

			throw new NotImplementedException("Error handling of InstanceNotFoundException");
		} catch (MBeanException e) {
			// Wraps an exception thrown by the MBean's getter.

			throw new NotImplementedException("Error handling of MBeanException");
		} catch (ReflectionException e) {
			// Wraps a java.lang.Exception thrown when trying to invoke the
			// setter.
			// This should never happen. We only Query getter.

			throw new NotImplementedException("Error handling of ReflectionException");
		} catch (IOException e) {
			// A communication problem occurred when talking to the MBean
			// server.

			throw new NotImplementedException("Error handling of IOException");
		}
	}

	private long toLong(Object any) {
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
		// Step 1
		// Query all attributes and fill charts.
		for (MBeanQueryInfo queryInfo : allMBeanQueryInfo) {

			long value = toLong(getAttribute(queryInfo.name, queryInfo.attribute));
			for (Dimension dim : queryInfo.dimensions) {
				dim.setCurrentValue(value);
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
}
