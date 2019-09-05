package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.query.MBeanQuery;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MBeanServerUtils.class)
public class MBeanDefaultQueryTest {

    @Test
    public void testConstructor() throws MalformedObjectNameException {
        final ObjectName name = new ObjectName("*:type=MBean");
        final MBeanDefaultQuery query = new MBeanDefaultQuery(name, "MBeanAttributeName", Long.class);
        
        Assert.assertEquals(name, query.getName());
        Assert.assertEquals("MBeanAttributeName", query.getAttribute());
        Assert.assertEquals(Long.class, query.getType());
        Assert.assertTrue(query.getDimensions().isEmpty());
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
        final MBeanDefaultQuery query = new MBeanDefaultQuery(name, "MBeanAttributeName", Long.class);
        final Dimension dim1 = new Dimension();
        dim1.setName("Dimension 1");
        query.getDimensions().add(dim1);
        final Dimension dim2 = new Dimension();
        dim2.setName("Dimension 2");
        query.getDimensions().add(dim2);

        final MBeanServerConnection mBeanServer = mock(MBeanServerConnection.class);

        PowerMockito.mockStatic(MBeanServerUtils.class);
        when(MBeanServerUtils.getAttribute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(queryResult);

        // test
        query.query(mBeanServer);

        // assert
        for(Dimension dimension : query.getDimensions()) {
            Assert.assertEquals(dimension.getName(), (Long) 1234L, dimension.getCurrentValue());
        }
    }
}