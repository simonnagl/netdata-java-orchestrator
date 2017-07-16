package org.firehol.netdata.testutils;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.ChartType;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.plugin.jmx.configuration.JmxChartConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxDimensionConfiguration;

/**
 * Build standard Test Objects.
 * 
 * A standard Test Object is a instance of a Class where all Properties are set:
 * <ul>
 * <li>Properties with standard values get their standard values</li>
 * <li>String properties are set with the name of the property</li>
 * <li>For enum the most likely value is choosen.</li>
 * <li>Numbers are initialized with 1</li>
 * <li>Booleans get true</li>
 * </ul>
 * 
 * @author Simon Nagl
 */
public abstract class TestObjectBuilder {
	public static Chart buildChart() {
		Chart chart = new Chart();
		chart.setType("type");
		chart.setId("id");
		chart.setName("name");
		chart.setTitle("title");
		chart.setUnits("units");
		chart.setFamily("family");
		chart.setContext("context");
		chart.setChartType(ChartType.LINE);
		return chart;
	}

	public static Dimension buildDimension() {
		Dimension dim = new Dimension();
		dim.setId("id");
		dim.setName("name");
		dim.setHidden(true);
		dim.setCurrentValue(1L);
		return dim;
	}

	public static JmxChartConfiguration buildJmxChartConfiguration() {
		JmxChartConfiguration chartConfig = new JmxChartConfiguration();
		chartConfig.setId("id");
		chartConfig.setTitle("title");
		chartConfig.setUnits("units");
		return chartConfig;
	}

	public static JmxDimensionConfiguration buildJmxDimensionConfiguration() {
		JmxDimensionConfiguration dimensionConfig = new JmxDimensionConfiguration();
		dimensionConfig.setFrom("from");
		dimensionConfig.setValue("value");
		dimensionConfig.setName("name");
		return dimensionConfig;
	}
}
