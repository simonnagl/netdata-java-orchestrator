package org.firehol.netdata.plugin;

import java.util.Collection;
import java.util.LinkedList;

import org.firehol.netdata.entity.Chart;

import lombok.Getter;

@Getter
public abstract class Plugin {
	
	private String name;
	
	private LinkedList<Chart> allChart;
	
	public abstract void readConfiguration();
	
	public abstract Collection<Chart> initialize();
	
	public abstract Collection<Chart> collectValues();
}
