package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.*;
import java.util.stream.Collectors;

class MBeanCompositeDataQuery extends MBeanQuery {

    private final Map<String, Collection<Dimension>> allDimensionByKey = new TreeMap<>();

    MBeanCompositeDataQuery(final ObjectName name, final String attribute) {
        super(name, attribute);
    }

    @Override
    public void addDimension(Dimension dimension, final String attribute) {
        final String[] splitString = attribute.split("\\.");
        if (splitString.length != 2) {
            throw new IllegalArgumentException(String.format("Expected attribute to be in format '<attribute>.<key>', but was '%s'", attribute));
        }
        if(!this.getAttribute().equals(splitString[0])) {
            throw new IllegalArgumentException(String.format("Expected attribute to start with '%s', but was '%s'", this.getAttribute(), attribute));
        }

        allDimensionByKey.computeIfAbsent(splitString[1], k -> new ArrayList<>()).add(dimension);
    }

    @Override
    public List<Dimension> getDimensions() {
        return allDimensionByKey.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public void query(MBeanServerConnection mBeanServer) throws JmxMBeanServerQueryException {
        final CompositeData compositeData = (CompositeData) MBeanServerUtils.getAttribute(mBeanServer, this.getName(), this.getAttribute());

        allDimensionByKey.forEach((key, allDimension) -> {
            final long result = (long) compositeData.get(key);

            allDimension.forEach(dimension -> dimension.setCurrentValue(result));
        });
    }
}
