// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.management.*;

import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MBeanServerUtilsTest {

	@Mock
	private MBeanServerConnection mBeanServer;

	@Test
	public void testGetAttribute() throws MalformedObjectNameException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException, JmxMBeanServerQueryException {
		// Static Objects
		ObjectName name = new ObjectName("org.firehol.netdata.module.jmx", "key", "value");
		String attribute = "attribute";

		// Mock
		when(mBeanServer.getAttribute(name, attribute)).thenReturn(1234L);

		// Test
		Object value = MBeanServerUtils.getAttribute(mBeanServer, name, attribute);

		// Verify
		assertEquals(1234L, value);
	}

	@Test(expected = JmxMBeanServerQueryException.class)
	public void testGetAttributeFailure() throws MalformedObjectNameException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException, JmxMBeanServerQueryException {
		// Static Objects
		ObjectName name = new ObjectName("org.firehol.netdata.module.jmx", "key", "value");
		String attribute = "attribute";

		// Mock
		when(mBeanServer.getAttribute(name, attribute)).thenThrow(new AttributeNotFoundException());

		// Test
		MBeanServerUtils.getAttribute(mBeanServer, name, attribute);
	}
}
