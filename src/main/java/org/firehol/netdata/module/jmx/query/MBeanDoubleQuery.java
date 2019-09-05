package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;

import javax.management.ObjectName;

class MBeanDoubleQuery extends MBeanQuery {
    private static final int LONG_RESOLUTION = 100;

    MBeanDoubleQuery(final ObjectName name, final String attribute) {
        super(name, attribute);
    }

    public void addDimension(Dimension dimension) {
        dimension.setDivisor(dimension.getDivisor() * LONG_RESOLUTION);
        super.addDimension(dimension);
    }

    @Override
    protected long toLong(final Object queryResult) {
        return (long) ((double) queryResult * LONG_RESOLUTION);

    }
}
