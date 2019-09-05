package org.firehol.netdata.module.jmx.query;

import lombok.Getter;
import org.firehol.netdata.model.Dimension;

import javax.management.ObjectName;

@Getter
class MBeanIntegerQuery extends MBeanQuery {
    MBeanIntegerQuery(final ObjectName name, final String attribute) {
        super(name, attribute);

    }

    @Override
    protected long toLong(final Object queryResult) {
        return ((Integer) queryResult).longValue();
    }
}
