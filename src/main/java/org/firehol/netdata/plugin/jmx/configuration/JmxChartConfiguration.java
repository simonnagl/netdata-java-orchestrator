package org.firehol.netdata.plugin.jmx.configuration;

import java.util.ArrayList;
import java.util.List;

import org.firehol.netdata.entity.ChartType;
import org.firehol.netdata.entity.DimensionAlgorithm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmxChartConfiguration {
	private String id;
	private String title;
	private String units;
	private ChartType chartType = ChartType.LINE;

	private DimensionAlgorithm dimType = DimensionAlgorithm.ABSOLUTE;

	private List<JmxDimensionConfiguration> dimensions = new ArrayList<>();
}
