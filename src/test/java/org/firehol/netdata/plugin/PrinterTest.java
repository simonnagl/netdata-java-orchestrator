package org.firehol.netdata.plugin;

import static org.junit.Assert.assertEquals;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.testutils.TestObjectBuilder;
import org.junit.Test;

public class PrinterTest {

	@Test
	public void testAppendInitializeChart() {

		// Static Objects
		Chart chart = TestObjectBuilder.buildChart();
		StringBuilder sb = new StringBuilder();

		// Test
		Printer.appendInitializeChart(sb, chart);

		// Verify
		assertEquals("CHART type.id name title units family context line 1000 -1", sb.toString());
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

}
