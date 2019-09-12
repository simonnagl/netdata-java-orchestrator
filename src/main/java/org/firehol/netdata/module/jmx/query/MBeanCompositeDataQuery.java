package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class MBeanCompositeDataQuery extends MBeanQuery {

	private final Map<String, MBeanValueStore> allDimensionByKey = new TreeMap<>();

	MBeanCompositeDataQuery(final MBeanServerConnection mBeanServer, final ObjectName name, final String attribute) {
		super(mBeanServer, name, attribute);
	}

	@Override
	public void addDimension(Dimension dimension, final String attribute) throws JmxMBeanServerQueryException {
		final String[] splitString = attribute.split("\\.");
		if (splitString.length != 2) {
			throw new IllegalArgumentException(
					String.format("Expected attribute to be in format '<attribute>.<key>', but was '%s'", attribute));
		}
		if (!this.getAttribute().equals(splitString[0])) {
			throw new IllegalArgumentException(String.format("Expected attribute to start with '%s', but was '%s'",
					this.getAttribute(), attribute));
		}

		final CompositeData queryResult = queryServer();
		final Object result = queryResult.get(splitString[1]);

		allDimensionByKey.computeIfAbsent(splitString[1], k -> MBeanValueStore.newInstance(result))
				.addDimension(dimension);
	}

	@Override
	public List<Dimension> getDimensions() {
		return allDimensionByKey.values().stream().flatMap(MBeanValueStore::streamAllDimension).collect(
				Collectors.toList());
	}

	@Override
	public void query() throws JmxMBeanServerQueryException {
		final CompositeData compositeData = queryServer();

		allDimensionByKey.forEach((key, allDimension) -> {
			final Object result = compositeData.get(key);
			allDimension.updateValue(result);
		});
	}

	private CompositeData queryServer() throws JmxMBeanServerQueryException {
		return (CompositeData) MBeanServerUtils.getAttribute(getMBeanServer(), this.getName(), this.getAttribute());
	}
}
