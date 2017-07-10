package org.firehol.netdata.plugin.jmx;

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

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.exception.NotImplementedException;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxDimensionConfiguration;

import lombok.Getter;
import lombok.Setter;

public class MBeanServerCollector {

	@Getter
	@Setter
	private class MBeanQueryInfo {
		private ObjectName name;
		private String attribute;
		private List<Dimension> dimensions = new LinkedList<>();
	}

	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private MBeanServerConnection mBeanServer;

	private List<MBeanQueryInfo> allMBeanQueryInfo = new LinkedList<>();

	private List<Chart> allChart = new LinkedList<>();

	/**
	 * Name represented to the user.
	 */
	String name;

	public MBeanServerCollector(String name, MBeanServerConnection mBeanServer) {
		this.name = name;
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerConnection getmBeanServer() {
		return mBeanServer;
	}

	public Collection<Chart> initialize(Collection<JmxChartConfiguration> allChartConfig)
			throws InitializationException {

		// Step 1
		// Check commonChart configuration
		for (JmxChartConfiguration chartConfig : allChartConfig) {
			try {
				Chart chart = new Chart();
				chart.setType("Jmx");
				chart.setFamily(name);
				chart.setId(chartConfig.getId());
				chart.setTitle(chartConfig.getTitle());
				chart.setUnits(chartConfig.getUnits());
				chart.setPriority(8000);
				chart.setContext(name);
				chart.setUpdateEvery(1);
				chart.setChartType(chartConfig.getChartType());

				// Check if the mBeanServer has the desired sources.
				for (JmxDimensionConfiguration dimensionConfig : chartConfig.getDimensions()) {
					ObjectName name = ObjectName.getInstance(dimensionConfig.getFrom());
					Object info = mBeanServer.getAttribute(name, dimensionConfig.getValue());

					log.info("mBean value " + info);

					Dimension dim = new Dimension(dimensionConfig.getName(), dimensionConfig.getName(),
							chartConfig.getDimType(), 1, 1, false, 0);
					chart.getAllDimension().add(dim);

					MBeanQueryInfo queryInfo = new MBeanQueryInfo();
					queryInfo.setName(name);
					queryInfo.setAttribute(dimensionConfig.getValue());
					queryInfo.getDimensions().add(dim);
					allMBeanQueryInfo.add(queryInfo);
				}

				allChart.add(chart);

			} catch (MalformedObjectNameException e) {
				log.warning("dimensionConfig contains invalid object name");
				continue;
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return allChart;
	}

	public Collection<Chart> collectValues() {

		// Step 1
		// Query all attributes and fill charts.
		for (MBeanQueryInfo queryInfo : allMBeanQueryInfo) {

			try {
				Object value = mBeanServer.getAttribute(queryInfo.name, queryInfo.attribute);

				long longValue = 0L;

				if (value instanceof Integer) {
					longValue = ((Integer) value).longValue();
				} else {
					longValue = (long) value;
				}

				for (Dimension dim : queryInfo.dimensions) {
					dim.setCurrentValue(longValue);
				}
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

		// Return Updated Charts.
		return allChart;
	}

}
