// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module.jmx.configuration;

import java.util.ArrayList;
import java.util.List;

import org.firehol.netdata.model.ChartType;
import org.firehol.netdata.model.DimensionAlgorithm;
import org.firehol.netdata.module.jmx.JmxModule;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration scheme of a chart created by the {@link JmxModule}.
 */
@Getter
@Setter
public class JmxChartConfiguration {

	/**
	 * uniquely identifies the chart
	 */
	private String id;
	/**
	 * the text above the chart
	 */
	private String title;
	/**
	 * the label of the vertical axis of the chart, all dimensions added to a
	 * chart should have the same units of measurement
	 */
	private String units;
	/**
	 * the sub-menu on the dashboard
	 */
	private String family;

	/**
	 * the relative priority of the charts as rendered on the web page. Lower
	 * numbers make the charts appear before the ones with higher numbers.
	 */
	private Integer priority;

	/**
	 * the chart type used on the web page.
	 */
	private ChartType chartType = ChartType.LINE;
	/**
	 * how to interpret collected values.
	 */
	private DimensionAlgorithm dimensionAlgorithm = DimensionAlgorithm.ABSOLUTE;

	/**
	 * dimensions this chart displays.
	 */
	private List<JmxDimensionConfiguration> dimensions = new ArrayList<>();
}
