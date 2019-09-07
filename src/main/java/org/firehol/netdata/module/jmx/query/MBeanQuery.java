package org.firehol.netdata.module.jmx.query;

import lombok.Getter;
import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

/**
 * MBeanQuery is able to query one attribute of a MBeanServer and update the currentValue of added Dimensions.
 *
 * <p>
 * Supported attributes are
 *
 * <ul>
 *     <li>Simple attributes which return long, int or double</li>
 *     <li>Composite attributes which may return more than one value at once. For this cases you have to add the attribute in format {@code <attribute_to_query>.<key_of_the_result_to_store>}</li>
 * </ul>
 * </p>
 */
@Getter
public abstract class MBeanQuery {
    private final ObjectName name;

    private final String attribute;

    MBeanQuery(final ObjectName name, final String attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    static MBeanQuery newInstance(final ObjectName name, final String attribute, final Class<?> attributeType) {
        if (CompositeData.class.isAssignableFrom(attributeType)) {
            return new MBeanCompositeDataQuery(name, attribute);
        }

        if (Double.class.isAssignableFrom(attributeType)) {
            return new MBeanDoubleQuery(name, attribute);
        }

        if (Integer.class.isAssignableFrom(attributeType)) {
            return new MBeanIntegerQuery(name, attribute);
        }

        return new MBeanLongQuery(name, attribute);
    }

    public static MBeanQuery newInstance(final MBeanServerConnection mBeanServer, final ObjectName mBeanName, final String attribute) throws JmxMBeanServerQueryException {
        final String mBeanAttribute = attribute.split("\\.", 2)[0];

        final Object testQueryResult = MBeanServerUtils.getAttribute(mBeanServer, mBeanName, mBeanAttribute);

        return MBeanQuery.newInstance(mBeanName, mBeanAttribute, testQueryResult.getClass());
    }

    public abstract void addDimension(Dimension dimension, String attribute);

    public abstract void query(MBeanServerConnection mBeanServer) throws JmxMBeanServerQueryException;

    public abstract java.util.List<Dimension> getDimensions();
}
