package org.firehol.netdata.module.jmx.query;

import javax.management.ObjectName;

class MBeanLongQuery extends MBeanSimpleQuery {
    MBeanLongQuery(final ObjectName name, final String attribute) {
        super(name, attribute);
    }

    @Override
    protected long toLong(final Object queryResult) {
        return (long) queryResult;
    }
}
