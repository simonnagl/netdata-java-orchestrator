package org.firehol.netdata.module.jmx.query;

import lombok.Getter;
import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.List;

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

    private final MBeanServerConnection mBeanServer;

    MBeanQuery(final MBeanServerConnection mBeanServer, final ObjectName name, final String attribute) {
        this.mBeanServer = mBeanServer;
        this.name = name;
        this.attribute = attribute;
    }

    public static MBeanQuery newInstance(final MBeanServerConnection mBeanServer, final ObjectName mBeanName, final String attribute) throws JmxMBeanServerQueryException {
        final String mBeanAttribute = attribute.split("\\.", 2)[0];
        final Object queryResult = MBeanServerUtils.getAttribute(mBeanServer, mBeanName, mBeanAttribute);

        if (CompositeData.class.isAssignableFrom(queryResult.getClass())) {
            return new MBeanCompositeDataQuery(mBeanServer, mBeanName, mBeanAttribute);
        }

        return new MBeanSimpleQuery(mBeanServer, mBeanName, mBeanAttribute, MBeanValueStore.newInstance(queryResult));
    }

    public abstract void addDimension(Dimension dimension, String attribute) throws JmxMBeanServerQueryException;

    public abstract void query() throws JmxMBeanServerQueryException;

    public abstract List<Dimension> getDimensions();
}
