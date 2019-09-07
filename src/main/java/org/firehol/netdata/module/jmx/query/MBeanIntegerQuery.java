package org.firehol.netdata.module.jmx.query;

import javax.management.ObjectName;

class MBeanIntegerQuery extends MBeanSimpleQuery {
    MBeanIntegerQuery(final ObjectName name, final String attribute) {
        super(name, attribute);
    }

    @Override
    protected long toLong(final Object queryResult) {
        return ((Integer) queryResult).longValue();
    }
}
