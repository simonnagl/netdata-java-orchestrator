// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.query;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.firehol.netdata.model.Dimension;
import org.junit.Test;

public class MBeanValueStoreTest {

	@Test
	public void testNewInstance() {
		final long valueToHandle = 1234;
		final MBeanValueStore store = MBeanValueStore.newInstance(valueToHandle);

		assertEquals(MBeanLongStore.class, store.getClass());
	}

	@Test
	public void testNewInstanceDouble() {
		final double valueToHandle = 12.34;
		final MBeanValueStore store = MBeanValueStore.newInstance(valueToHandle);

		assertEquals(MBeanDoubleStore.class, store.getClass());
	}

	@Test
	public void addDimension() {
		final MBeanValueStore store = MBeanValueStore.newInstance(1234);
		final Dimension dimension = new Dimension();

		store.addDimension(dimension);

		assertEquals(dimension, store.streamAllDimension().findFirst().orElse(null));
	}

	@Test
	public void streamAllDimension() {
		final MBeanValueStore store = MBeanValueStore.newInstance(1234);
		final Dimension dimension = new Dimension();
		store.addDimension(dimension);

		final List<Dimension> allDimension = store.streamAllDimension().collect(Collectors.toList());

		assertEquals(1, allDimension.size());
		assertEquals(dimension, allDimension.get(0));
	}

	@Test
	public void testUpdateValueLong() {
		final MBeanValueStore store = MBeanValueStore.newInstance(1234L);
		final Dimension dimension = new Dimension();
		store.addDimension(dimension);

		store.updateValue(1234L);

		assertEquals(1234L, (long) dimension.getCurrentValue());
	}

	@Test
	public void testUpdateValueInteger() {
		final MBeanValueStore store = MBeanValueStore.newInstance(1234);
		final Dimension dimension = new Dimension();
		store.addDimension(dimension);

		store.updateValue(1234);

		assertEquals(1234L, (long) dimension.getCurrentValue());
	}

	@Test
	public void testUpdateValueDouble() {
		final MBeanValueStore store = MBeanValueStore.newInstance(12.34);
		final Dimension dimension = new Dimension();
		store.addDimension(dimension);

		store.updateValue(12.34);

		assertEquals(1234L, (long) dimension.getCurrentValue());
		assertEquals("Add Dimension should set divisor", 100, dimension.getDivisor());
	}
}
