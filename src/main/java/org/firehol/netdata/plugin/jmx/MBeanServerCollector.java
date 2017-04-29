package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.entity.DimensionAlgorithm;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.Collector;

public class MBeanServerCollector implements Collector {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private class MBeanCollector implements Collector {

		private ObjectName mBeanName;
		private List<Chart> allChart = new LinkedList<>();

		public MBeanCollector(ObjectName mBeanName) {
			this.mBeanName = mBeanName;
		}

		@Override
		public Collection<Chart> initialize() throws InitializationException {

			Chart chart = ObjectNameToChartConverter.getInstance().convert(mBeanName);

			MBeanAttributeInfo[] mBeanInfo;
			try {
				mBeanInfo = mBeanServer.getMBeanInfo(mBeanName).getAttributes();
			} catch (InstanceNotFoundException | IntrospectionException | ReflectionException | IOException e) {
				throw new InitializationException(e);
			}

			for (MBeanAttributeInfo info : mBeanInfo) {
				switch (info.getType()) {
				case "long":
					try {
						long value = (long) mBeanServer.getAttribute(mBeanName, info.getName());

						Dimension dim = new Dimension(info.getName(), info.getName(), DimensionAlgorithm.ABSOLUTE, 1, 1,
								false, value);
						chart.getAllDimension().add(dim);

					} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
							| ReflectionException | IOException e) {
						System.err.println("Wrap");
						e.printStackTrace();
					} catch (RuntimeMBeanException e) {
						log.warning("" + info.getName() + ": " + e.getMessage());
					}
					break;
				case "int":
					try {
						int value = (int) mBeanServer.getAttribute(mBeanName, info.getName());

						Dimension dim = new Dimension(info.getName(), info.getName(), DimensionAlgorithm.ABSOLUTE, 1, 1,
								false, value);
						chart.getAllDimension().add(dim);

					} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
							| ReflectionException | IOException e) {
						System.err.println("Wrap");
						e.printStackTrace();
					} catch (RuntimeMBeanException e) {
						log.warning("" + info.getName() + ": " + e.getMessage());
					}
					break;
				default:
					log.info(info.getType());
					break;
				}
			}

			allChart.add(chart);

			return allChart;
		}

		@Override
		public Collection<Chart> collectValues() {

			for (Chart chart : allChart) {
				for (Dimension dim : chart.getAllDimension()) {
					try {
						long value = Long.parseLong(mBeanServer.getAttribute(mBeanName, dim.getName()).toString());
						dim.setCurrentValue(value);
					} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
							| ReflectionException | IOException e) {
						System.err.println("Wrap");
						e.printStackTrace();
					} catch (RuntimeMBeanException e) {
						log.warning("" + dim.getName() + ": " + e.getMessage());
					}
				}
			}

			return allChart;
		}

	}

	private MBeanServerConnection mBeanServer;

	private List<MBeanCollector> allMBeanCollector = new LinkedList<>();

	public MBeanServerCollector(MBeanServerConnection mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerConnection getmBeanServer() {
		return mBeanServer;
	}

	@Override
	public Collection<Chart> initialize() throws InitializationException {
		List<Chart> allChart = new LinkedList<>();

		// Step 1
		// Get all MBeans
		Set<ObjectName> allMBeanName;
		try {
			allMBeanName = mBeanServer.queryNames(null, null);
		} catch (IOException e) {
			throw new InitializationException("Could not initialize mBeanServer", e);
		}

		Iterator<ObjectName> allMBeanNameIterator = allMBeanName.iterator();
		while (allMBeanNameIterator.hasNext()) {
			ObjectName mBeanName = allMBeanNameIterator.next();

			MBeanCollector collector = new MBeanCollector(mBeanName);

			try {
				allChart.addAll(collector.initialize());
				allMBeanCollector.add(collector);
			} catch (InitializationException e) {
				allMBeanNameIterator.remove();
				continue;
			}

		}

		return allChart;
	}

	@Override
	public Collection<Chart> collectValues() {
		return allMBeanCollector.stream().map(MBeanCollector::collectValues).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

}
