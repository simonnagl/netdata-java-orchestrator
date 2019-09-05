package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MBeanServerUtils.class)
public class MBeanIntegerQueryTest {

    @Test
    public void testConstructor() throws MalformedObjectNameException {
        final ObjectName name = new ObjectName("*:type=MBean");
        final MBeanIntegerQuery query = new MBeanIntegerQuery(name, "MBeanAttributeName");
        
        Assert.assertEquals(name, query.getName());
        Assert.assertEquals("MBeanAttributeName", query.getAttribute());
        Assert.assertTrue(query.getDimensions().isEmpty());
    }
}