package org.firehol.netdata.plugin;

import java.util.Collection;

import org.firehol.netdata.entity.Chart;

public interface Collector {
	public Collection<Chart> initialize();
	
	public Collection<Chart> collect(Collection<Chart> allChart);
}
