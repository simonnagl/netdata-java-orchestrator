package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.junit.Test;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MBeanCompositeDataQueryTest {

	private MBeanServerConnection mBeanServer = mock(MBeanServerConnection.class);

	private final MBeanQuery query = new MBeanCompositeDataQuery(mBeanServer, ObjectName.WILDCARD, "Attribute");

	@Test
	public void testConstructorAttributeWithoutKey() {
		assertEquals("Attribute", query.getAttribute());
	}

	@Test
	public void testQueryInteger() throws AttributeNotFoundException, MBeanException, ReflectionException,
			InstanceNotFoundException, IOException, JmxMBeanServerQueryException {
		final CompositeData compositeData = mock(CompositeData.class);
		when(compositeData.get("key")).thenReturn(1234);

		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(compositeData);

		final Dimension dim = new Dimension();
		query.addDimension(dim, "Attribute.key");

		query.query();

		assertEquals((Long) 1234L, dim.getCurrentValue());
	}

	@Test
	public void testQueryDouble() throws JmxMBeanServerQueryException, AttributeNotFoundException, MBeanException,
			ReflectionException, InstanceNotFoundException, IOException {
		final CompositeData compositeData = mock(CompositeData.class);
		when(compositeData.get("key")).thenReturn(12.34);

		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(compositeData);

		final Dimension dim = new Dimension();
		query.addDimension(dim, "Attribute.key");

		query.query();

		assertEquals((Long) 1234L, dim.getCurrentValue());
	}

	@Test
	public void testQueryTwoKey() throws JmxMBeanServerQueryException, AttributeNotFoundException, MBeanException,
			ReflectionException, InstanceNotFoundException, IOException {
		final CompositeData compositeData = mock(CompositeData.class);
		when(compositeData.get("first")).thenReturn(1234L);
		when(compositeData.get("second")).thenReturn(4321L);

		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(compositeData);

		final Dimension dimension1 = new Dimension();
		query.addDimension(dimension1, "Attribute.first");

		final Dimension dimension2 = new Dimension();
		query.addDimension(dimension2, "Attribute.second");

		query.query();

		assertEquals((Long) 1234L, dimension1.getCurrentValue());
		assertEquals((Long) 4321L, dimension2.getCurrentValue());
	}

	@Test
	public void testQueryTwoDimensionSameKey() throws JmxMBeanServerQueryException, AttributeNotFoundException,
			MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		final CompositeData compositeData = mock(CompositeData.class);
		when(compositeData.get("first")).thenReturn(1234L);

		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(compositeData);

		final Dimension dimension1 = new Dimension();
		query.addDimension(dimension1, "Attribute.first");

		final Dimension dimension2 = new Dimension();
		query.addDimension(dimension2, "Attribute.first");

		query.query();

		assertEquals((Long) 1234L, dimension1.getCurrentValue());
		assertEquals((Long) 1234L, dimension2.getCurrentValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDimensionAttributeNoMatch() throws JmxMBeanServerQueryException {
		final Dimension dim = new Dimension();

		query.addDimension(dim, "NoMatch.key");
	}
}
