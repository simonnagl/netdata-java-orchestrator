package org.firehol.netdata.plugin.jmx.configuration;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmxServerConfiguration {
	private int port;
	private String name;
	
	private List<JmxChartConfiguration> charts;
}
