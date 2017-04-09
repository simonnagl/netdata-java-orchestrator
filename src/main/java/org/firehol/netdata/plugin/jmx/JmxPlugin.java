package org.firehol.netdata.plugin.jmx;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.ChartType;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.entity.DimensionAlgorithm;
import org.firehol.netdata.plugin.AbstractPlugin;

public class JmxPlugin extends AbstractPlugin {

	private final List<Chart> allChart = new LinkedList<>();

	@Override
	public String setName() {
		return "jmx";
	}

	@Override
	public Collection<Chart> initialize() {
		getConfig().get("global", "server", "");

		// Connect

		Chart test = new Chart("test", "example", null, "Java Test chart", "num", "test", "test", ChartType.LINE, 1,
				Integer.valueOf((getConfig().get("global", "update every", "1"))));
		Dimension dim = new Dimension("one", "one", DimensionAlgorithm.ABSOLUTE, 1, 1, false, 1);
		test.getAllDimension().add(dim);
		allChart.add(test);

		return allChart;
	}

	@Override
	public Collection<Chart> collectValues() {
		return allChart;
	}

}
