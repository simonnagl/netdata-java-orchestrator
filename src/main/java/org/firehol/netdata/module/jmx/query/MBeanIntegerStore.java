package org.firehol.netdata.module.jmx.query;

public class MBeanIntegerStore extends MBeanValueStore {
    @Override
    long toLong(final Object value) {
        return ((Integer) value).longValue();
    }
}
