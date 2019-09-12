package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MBeanServerUtils.class)
public class MBeanQueryTest {

	private final MBeanServerConnection mBeanServer = mock(MBeanServerConnection.class);

	@Test
	public void testNewInsctanceInteger() throws AttributeNotFoundException, MBeanException, ReflectionException,
			InstanceNotFoundException, IOException, JmxMBeanServerQueryException {
		final String testAttribute = "Attribute";
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, testAttribute)).thenReturn(1234);

		final MBeanQuery mBeanQuery = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, testAttribute);

		assertInstanceOf(MBeanSimpleQuery.class, mBeanQuery);

		verify(mBeanServer).getAttribute(ObjectName.WILDCARD, testAttribute);
		assertEquals(testAttribute, mBeanQuery.getAttribute());
		assertEquals(ObjectName.WILDCARD, mBeanQuery.getName());
		assertEquals(mBeanServer, mBeanQuery.getMBeanServer());
	}

	@Test
	public void testNewInstanceLong() throws JmxMBeanServerQueryException, AttributeNotFoundException, MBeanException,
			ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(1234L);

		final MBeanQuery mBeanQuery = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "Attribute");

		assertInstanceOf(MBeanSimpleQuery.class, mBeanQuery);
	}

	@Test
	public void testNewInstanceDouble() throws AttributeNotFoundException, MBeanException, ReflectionException,
			InstanceNotFoundException, IOException, JmxMBeanServerQueryException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(12.34);

		final MBeanQuery mBeanQuery = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "Attribute");

		assertInstanceOf(MBeanSimpleQuery.class, mBeanQuery);
	}

	@Test
	public void testNewInstanceCompositeData() throws JmxMBeanServerQueryException, AttributeNotFoundException,
			MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "Attribute")).thenReturn(buildCompositeData());

		final MBeanQuery mBeanQuery = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "Attribute.compkey");

		assertInstanceOf(MBeanCompositeDataQuery.class, mBeanQuery);
	}

	private void assertInstanceOf(final Class<?> expectedClass, final MBeanQuery mBeanQuery) {
		if (!expectedClass.isInstance(mBeanQuery)) {
			fail(String.format("%s should be instance of %s but is instance of %s", mBeanQuery.toString(),
					expectedClass.toString(), mBeanQuery.getClass().toString()));
		}
	}

	private CompositeData buildCompositeData() {
		return new CompositeData() {
			@Override
			public CompositeType getCompositeType() {
				return null;
			}

			@Override
			public Object get(final String key) {
				return null;
			}

			@Override
			public Object[] getAll(final String[] keys) {
				return new Object[0];
			}

			@Override
			public boolean containsKey(final String key) {
				return false;
			}

			@Override
			public boolean containsValue(final Object value) {
				return false;
			}

			@Override
			public Collection<?> values() {
				return null;
			}
		};
	}

	@Test
	public void testQueryLong() throws JmxMBeanServerQueryException, MalformedObjectNameException,
			AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		testQuery(1234L);
	}

	@Test
	public void testQueryDouble() throws JmxMBeanServerQueryException, MalformedObjectNameException,
			AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		testQuery(12.34);

	}

	@Test
	public void testQueryInteger() throws JmxMBeanServerQueryException, MalformedObjectNameException,
			AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		testQuery(1234);
	}

	public void testQuery(Object queryResult) throws JmxMBeanServerQueryException, MalformedObjectNameException,
			AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		final ObjectName name = new ObjectName("*:type=MBean");
		when(mBeanServer.getAttribute(name, "MBeanAttributeName")).thenReturn(queryResult);

		final MBeanQuery query = MBeanQuery.newInstance(mBeanServer, name, "MBeanAttributeName");
		final Dimension dim1 = new Dimension();
		dim1.setName("Dimension 1");
		query.getDimensions().add(dim1);
		final Dimension dim2 = new Dimension();
		dim2.setName("Dimension 2");
		query.getDimensions().add(dim2);

		PowerMockito.mockStatic(MBeanServerUtils.class);
		when(MBeanServerUtils.getAttribute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(queryResult);

		// test
		query.query();

		// assert
		for (Dimension dimension : query.getDimensions()) {
			Assert.assertEquals(dimension.getName(), (Long) 1234L, dimension.getCurrentValue());
		}
	}

	@Test
	public void testAddDimension() throws JmxMBeanServerQueryException, AttributeNotFoundException, MBeanException,
			ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "attribute")).thenReturn(1234L);
		final MBeanQuery query = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "attribute");
		final Dimension dimension = new Dimension();

		query.addDimension(dimension, "attribute");

		assertEquals(dimension, query.getDimensions().get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDimensionAttributeNotMatch() throws JmxMBeanServerQueryException, AttributeNotFoundException,
			MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "attribute")).thenReturn(1234L);
		final MBeanQuery query = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "attribute");
		final Dimension dimension = new Dimension();

		query.addDimension(dimension, "no match");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDimensionAttributeNull() throws JmxMBeanServerQueryException, AttributeNotFoundException,
			MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "attribute")).thenReturn(1234L);
		final MBeanQuery query = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "attribute");
		final Dimension dimension = new Dimension();

		query.addDimension(dimension, null);
	}

	@Test()
	public void testAddDimensionCompositeData() throws JmxMBeanServerQueryException, AttributeNotFoundException,
			MBeanException, ReflectionException, InstanceNotFoundException, IOException {
		when(mBeanServer.getAttribute(ObjectName.WILDCARD, "attribute")).thenReturn(buildCompositeData());
		final MBeanQuery query = MBeanQuery.newInstance(mBeanServer, ObjectName.WILDCARD, "attribute");
		final Dimension dimension = new Dimension();

		query.addDimension(dimension, "attribute.key");

		assertEquals(dimension, query.getDimensions().get(0));
	}

}
