package org.firehol.netdata.module.jmx;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.MBeanQuery;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MBeanServerUtils.class)
public class MBeanQueryTest {

    @Test
    public void testConstructor() throws MalformedObjectNameException {
        final ObjectName name = new ObjectName("*:type=MBean");
        final Dimension dimension = new Dimension();
        final MBeanQuery query = new MBeanQuery(name, "MBeanAttributeName", Long.class, dimension);
        
        assertEquals(name, query.getName());
        assertEquals("MBeanAttributeName", query.getAttribute());
        assertEquals(Long.class, query.getType());
        assertEquals(dimension, query.getDimensions().get(0));
    }

    @Test
    public void testQueryLong() throws JmxMBeanServerQueryException, MalformedObjectNameException {
        testQuery(1234L);
        testQuery(12.34);
        testQuery(1234);
    }

    public void testQuery(Object queryResult) throws JmxMBeanServerQueryException, MalformedObjectNameException {
        // prepare
        final ObjectName name = new ObjectName("*:type=MBean");
        final Dimension dim = new Dimension();
        final MBeanQuery query = new MBeanQuery(name, "MBeanAttributeName", Long.class, dim);
        final Dimension dim1 = new Dimension();
        dim1.setName("Dimension 1");
        query.getDimensions().add(dim1);
        final Dimension dim2 = new Dimension();
        dim2.setName("Dimension 2");
        query.getDimensions().add(dim2);

        final MBeanServerConnection mBeanServer = mock(MBeanServerConnection.class);

        PowerMockito.mockStatic(MBeanServerUtils.class);
        when(MBeanServerUtils.getAttribute(any(), any(), any())).thenReturn(queryResult);

        // test
        query.query(mBeanServer);

        // assert
        for(Dimension dimension : query.getDimensions()) {
            assertEquals(dimension.getName(), (Long) 1234L, dimension.getCurrentValue());
        }
    }
}