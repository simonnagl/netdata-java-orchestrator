package org.firehol.netdata.module.jmx.query;

public class MBeanLongStore extends MBeanValueStore {

    @Override
    long toLong(final Object value) {
        return (long) value;
    }

}
