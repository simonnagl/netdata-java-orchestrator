// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.orchestrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.firehol.netdata.model.Chart;
import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.testutils.TestObjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class PrinterTest {

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

	@Test
	public void testInitializeChart() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		chart.setUpdateEvery(1);
		Dimension dim = TestObjectBuilder.buildDimension();
		chart.getAllDimension().add(dim);

		// Test
		Printer.initializeChart(chart);

		// Verify
		assertEquals(
				"CHART type.id name 'title' units family context line 1000 1\nDIMENSION id name absolute 1 1 hidden\n",
				systemOutRule.getLog());
	}

	@Test
	public void testAppendInitializeChart() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeChart(sb, chart);

		// Verify
		assertEquals("CHART type.id name 'title' units family context line 1000", sb.toString());
	}

	@Test
	public void testAppendInitializeChartNoName() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		chart.setName(null);
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeChart(sb, chart);

		// Verify
		assertEquals("CHART type.id null 'title' units family context line 1000", sb.toString());
	}

	@Test
	public void testAppendInitializeChartNoFamily() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		chart.setFamily(null);
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeChart(sb, chart);

		// Verify
		assertEquals("CHART type.id name 'title' units id context line 1000", sb.toString());
	}

	@Test
	public void testAppendInitializeChartNoContext() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		chart.setContext(null);
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeChart(sb, chart);

		// Verify
		assertEquals("CHART type.id name 'title' units family id line 1000", sb.toString());
	}

	@Test
	public void testAppendInitializeDimension() {

		// Static Objects
		Dimension dimension = TestObjectBuilder.buildDimension();
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeDimension(sb, dimension);

		// Verify
		assertEquals("DIMENSION id name absolute 1 1 hidden", sb.toString());
	}

	@Test
	public void testAppendInitializeDimensionNotHidden() {

		// Static Objects
		Dimension dimension = TestObjectBuilder.buildDimension();
		dimension.setHidden(false);
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeDimension(sb, dimension);

		// Verify
		assertEquals("DIMENSION id name absolute 1 1", sb.toString());
	}

	@Test
	public void testAppendInitializeDimensionNoName() {

		// Static Objects
		Dimension dimension = TestObjectBuilder.buildDimension();
		dimension.setName(null);
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeDimension(sb, dimension);

		// Verify
		assertEquals("DIMENSION id id absolute 1 1 hidden", sb.toString());
	}

	@Test
	public void testCollect() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		Dimension dim = TestObjectBuilder.buildDimension();
		dim.setCurrentValue(1L);
		chart.getAllDimension().add(dim);

		// Test
		Printer.collect(chart);

		// Verify
		assertEquals("BEGIN type.id\nSET id = 1\nEND\n", systemOutRule.getLog());
		// collect should delete currentValue after printing.
		assertNull(dim.getCurrentValue());
	}

	@Test
	public void testCollectNoValue() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		Dimension dim = TestObjectBuilder.buildDimension();
		dim.setCurrentValue(null);
		chart.getAllDimension().add(dim);

		// Test
		Printer.collect(chart);

		// Verify
		assertEquals("BEGIN type.id\nEND\n", systemOutRule.getLog());
		// collect should delete currentValue after printing.
		assertNull(dim.getCurrentValue());
	}

	@Test
	public void testAppendCollectBegin() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendCollectBegin(sb, chart);

		// Verify
		assertEquals("BEGIN type.id", sb.toString());
	}

	@Test
	public void testAppendCollectDimension() {

		// Static Objects
		Dimension dimension = TestObjectBuilder.buildDimension();
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendCollectDimension(sb, dimension);

		// Verify
		assertEquals("SET id = 1", sb.toString());
	}

	@Test
	public void testAppendCollectEnd() {
		// Static Objects
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendCollectEnd(sb);

		// Verify
		assertEquals("END", sb.toString());
	}

	@Test
	public void testDisable() {
		// Test
		Printer.disable();

		// Verify
		assertEquals("DISABLE\n", systemOutRule.getLog());
	}

}
