package org.firehol.netdata.plugin.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.AbstractPlugin;

public class JmxPlugin extends AbstractPlugin {
	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private final List<MBeanServerCollector> allMBeanCollector = new ArrayList<>();

	@Override
	public String setName() {
		return "jmx";
	}

	@Override
	public Collection<Chart> initialize() {

		// Step 1
		// Connect to MBeanServer

		// Read configuration

		getConfig().get("global", "server", "");
		MBeanServerCollector collector = new MBeanServerCollector(ManagementFactory.getPlatformMBeanServer());
		allMBeanCollector.add(collector);

		// Step 2
		// Initialize MBeanServer

		List<Chart> allChart = new LinkedList<>();
		Iterator<MBeanServerCollector> mBeanCollectorIterator = allMBeanCollector.iterator();

		while (mBeanCollectorIterator.hasNext()) {
			MBeanServerCollector mBeanCollector = mBeanCollectorIterator.next();
			try {
				allChart.addAll(mBeanCollector.initialize());
			} catch (InitializationException e) {
				log.warning("Could not initialize JMX plugin " + mBeanCollector.getmBeanServer().toString());
				mBeanCollectorIterator.remove();
			}
		}

		return allChart;
	}

	@Override
	public Collection<Chart> collectValues() {
		return allMBeanCollector.stream().map(MBeanServerCollector::collectValues).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

}
