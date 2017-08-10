/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.firehol.netdata.plugin.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.ChartType;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.entity.DimensionAlgorithm;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxDimensionConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxServerConfiguration;
import org.firehol.netdata.plugin.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.testutils.ReflectionUtils;
import org.firehol.netdata.testutils.TestObjectBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MBeanServerCollectorTest {

	@InjectMocks
	private MBeanServerCollector mBeanServerCollector;

	@Mock
	private JMXConnector jmxConnector;

	@Mock
	private MBeanServerConnection mBeanServer;

	@Test
	public void testInitializeChart() throws NoSuchFieldException, IllegalAccessException, SecurityException {
		// Static Objects
		JmxChartConfiguration config = TestObjectBuilder.buildJmxChartConfiguration();
		JmxServerConfiguration serverConfig = new JmxServerConfiguration();
		String serverName = "TestServer";
		serverConfig.setName(serverName);
		ReflectionUtils.setPrivateFiled(mBeanServerCollector, "serverConfiguration", serverConfig);

		// Test
		Chart chart = mBeanServerCollector.initializeChart(config);

		// Verify
		assertEquals("Jmx", chart.getType());
		assertEquals("id", chart.getId());
		assertNull(chart.getName());
		assertEquals("title", chart.getTitle());
		assertEquals("units", config.getUnits());
		assertEquals(serverName, chart.getFamily());
		assertEquals(serverName, chart.getContext());
		assertEquals(ChartType.LINE, chart.getChartType());
		// TODO: This should be dynamic.
		assertEquals(8000, chart.getPriority());
		assertNull(chart.getUpdateEvery());
	}

	@Test
	public void testInitializeDimension() {
		// Static Objects
		JmxChartConfiguration chartConfig = TestObjectBuilder.buildJmxChartConfiguration();
		JmxDimensionConfiguration dimensionConfig = TestObjectBuilder.buildJmxDimensionConfiguration();
		chartConfig.getDimensions().add(dimensionConfig);

		// Test
		Dimension dimension = mBeanServerCollector.initializeDimension(chartConfig, dimensionConfig, Long.class);

		// Verify
		assertEquals("name", dimension.getId());
		assertEquals("name", dimension.getName());
		assertEquals(DimensionAlgorithm.ABSOLUTE, dimension.getAlgorithm());
		assertEquals(1, dimension.getMultiplier());
		assertEquals(1, dimension.getDivisor());
		assertFalse(dimension.isHidden());
		assertNull(dimension.getCurrentValue());
	}

	@Test
	public void testInitializeMBeanQueryInfo() throws JmxMBeanServerQueryException, MalformedObjectNameException,
			AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		// Static Objects
		JmxDimensionConfiguration dimensionConfig = TestObjectBuilder.buildJmxDimensionConfiguration();
		// Add a valid object name.
		ObjectName name = new ObjectName("org.firehol.netdata.plugin.jmx", "key", "value");
		dimensionConfig.setFrom(name.toString());

		// Mock
		when(mBeanServer.getAttribute(name, "value")).thenReturn(new Long(1234));

		// Test
		MBeanQueryInfo queryInfo = mBeanServerCollector.initializeMBeanQueryInfo(dimensionConfig);

		// Verify
		assertEquals(name, queryInfo.getName());
		assertEquals("value", queryInfo.getAttribute());
		assertEquals(Long.class, queryInfo.getType());
	}

	@Test
	public void testGetAttribute() throws MalformedObjectNameException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException, JmxMBeanServerQueryException {
		// Static Objects
		ObjectName name = new ObjectName("org.firehol.netdata.plugin.jmx", "key", "value");
		String attribute = "attribute";

		// Mock
		when(mBeanServer.getAttribute(name, attribute)).thenReturn(new Long(1234));

		// Test
		Object value = mBeanServerCollector.getAttribute(name, attribute);

		// Verify
		assertEquals(1234L, value);
	}

	@Test(expected = JmxMBeanServerQueryException.class)
	public void testGetAttributeFailure() throws MalformedObjectNameException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException, JmxMBeanServerQueryException {
		// Static Objects
		ObjectName name = new ObjectName("org.firehol.netdata.plugin.jmx", "key", "value");
		String attribute = "attribute";

		// Mock
		when(mBeanServer.getAttribute(name, attribute)).thenThrow(new AttributeNotFoundException());

		// Test
		mBeanServerCollector.getAttribute(name, attribute);
	}

	@Test
	public void testToLongLong() {
		// Static Object
		long value = 1234;

		// Test
		long result = mBeanServerCollector.toLong(value);

		// Verify
		assertEquals(value, result);
	}

	@Test
	public void testToLongInteger() {
		// Static Object
		int value = 1234;

		// Test
		long result = mBeanServerCollector.toLong(value);

		// Verify
		assertEquals(1234, result);
	}

	@Test
	public void testToLongDouble() {
		// Static Object
		double value = 1234;

		// Test
		long result = mBeanServerCollector.toLong(value);

		// Verify
		assertEquals(1234 * 100, result);
	}

	@Test
	public void testClose() throws IOException {
		// Test
		mBeanServerCollector.close();
		// Verify
		verify(jmxConnector, times(1)).close();
	}

	@Test(expected = IOException.class)
	public void testCloseFailure() throws IOException {
		// Mock
		doThrow(new IOException()).when(jmxConnector).close();
		// Test
		mBeanServerCollector.close();
	}

}
