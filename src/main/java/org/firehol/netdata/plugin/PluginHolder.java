package org.firehol.netdata.plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public class PluginHolder {
	private Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private Collection<Plugin> allPlugin = new LinkedList<Plugin>();

	public void add(Plugin plugin) {
		allPlugin.add(plugin);
	}

	public void readConfiguration() {
		allPlugin.parallelStream().peek(plugin -> log.info("Read configuration for Java Plugin " + plugin.getName()))
				.forEach(Plugin::readConfiguration);
	}

	public void initializeCharts() {
		log.warning("Initialize Charts");
	}
	
	public int getAllPluginSize() {
		return allPlugin.size();
	}

	public void collect() {
		allPlugin.parallelStream().map(Plugin::collectValues).flatMap(Collection::stream).forEach(Printer::collect);
	}
}
