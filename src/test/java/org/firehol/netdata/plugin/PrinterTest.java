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
		assertEquals("CHART type.id name 'title' units family context line 1000", sb.toString());
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
