package org.firehol.netdata.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.plugin.config.BaseConfig;
import org.firehol.netdata.plugin.config.ChildConfig;
import org.firehol.netdata.plugin.config.Config;
import org.firehol.netdata.plugin.config.exception.ParsingException;

import lombok.Getter;

@Getter
public abstract class AbstractPlugin implements Collector {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private final String name;

	private Config config;

	private LinkedList<Chart> allChart;

	public AbstractPlugin() {
		this.name = this.setName();
	}

	public void readConfig(final Path javaPluginConfigPath, final BaseConfig baseConfig) {
		Path configPath = javaPluginConfigPath.resolve(getName()).resolve(".conf");

		try {
			config = new ChildConfig(configPath, baseConfig);
		} catch (ParsingException | IOException e) {
			log.warning("Could not read config file'" + configPath.toAbsolutePath().toString() + "' of plugin "
					+ getName() + ". Reason: " + e.getMessage());
			config = baseConfig;
		}
	}

	public abstract String setName();

	public abstract Collection<Chart> initialize();

	public abstract Collection<Chart> collectValues();
}
