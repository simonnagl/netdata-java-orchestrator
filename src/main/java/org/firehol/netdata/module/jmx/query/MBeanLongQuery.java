package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;

import javax.management.ObjectName;

class MBeanLongQuery extends MBeanQuery {

    MBeanLongQuery(final ObjectName name, final String attribute) {
        super(name, attribute);
    }

    @Override
    protected long toLong(final Object queryResult) {
        return (long) queryResult;
    }
}
