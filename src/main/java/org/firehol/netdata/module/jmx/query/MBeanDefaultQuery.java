package org.firehol.netdata.module.jmx.query;

import lombok.Getter;
import org.firehol.netdata.model.Dimension;

import javax.management.ObjectName;

@Getter
public class MBeanDefaultQuery extends MBeanQuery {
    private static final int LONG_RESOLUTION = 100;

    /**
     * The Class of the object returned by the query.
     */
    private final Class<?> type;

    MBeanDefaultQuery(final ObjectName name, final String attribute, final Class<?> attributeType) {
        super(name, attribute);
        this.type = attributeType;

    }

    public void addDimension(Dimension dimension) {
        if (Double.class.isAssignableFrom(type)) {
            dimension.setDivisor(dimension.getDivisor() * LONG_RESOLUTION);
        }

        this.getDimensions().add(dimension);
    }

    @Override
    protected long toLong(Object any) {
        if (any instanceof Integer) {
            return ((Integer) any).longValue();
        } else if (any instanceof Double) {
            double doubleValue = (double) any;
            return (long) (doubleValue * LONG_RESOLUTION);
        } else {
            return (long) any;
        }
    }
}
