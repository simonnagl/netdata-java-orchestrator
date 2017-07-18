package org.firehol.netdata.plugin;

import java.util.Collection;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;

public interface Collector {
	 Collection<Chart> initialize() throws InitializationException;
	 
	 Collection<Chart> collectValues();
	 
	 void cleanup();
}
