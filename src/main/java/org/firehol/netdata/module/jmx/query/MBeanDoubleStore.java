package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;

public class MBeanDoubleStore extends MBeanValueStore {
    private static final int LONG_RESOLUTION = 100;

    @Override
    public void addDimension(final Dimension dimension) {
        dimension.setDivisor(dimension.getDivisor() * LONG_RESOLUTION);
        super.addDimension(dimension);
    }

    @Override
    long toLong(final Object value) {
        return (long) ((double) value * LONG_RESOLUTION);
    }
}
