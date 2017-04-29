package org.firehol.netdata.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.plugin.configuration.ConfigurationService;
import org.firehol.netdata.plugin.configuration.PluginDaemonConfiguration;
import org.firehol.netdata.plugin.configuration.exception.ParseException;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AbstractPlugin<C> implements Collector {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	@Setter
	private PluginDaemonConfiguration baseConfig;

	private C configuration;

	private LinkedList<Chart> allChart;

	public C readConfiguration(final Path pluginConfigurationFolder) throws ConfigurationException, IOException {
		Path configPath = pluginConfigurationFolder.resolve(getName() + ".conf");

		try {
			configuration = ConfigurationService.getInstance().readConfiguration(configPath.toFile(),
					getConfigurationScheme());
			return configuration;
		} catch (ParseException e) {
			log.warning("Could not read malformed configuration file '" + configPath.toAbsolutePath().toString() + "'");
			throw e;
		} catch (IOException e) {
			log.warning("Could not read configuration file '" + configPath.toAbsolutePath().toString() + "'");
			throw e;
		} finally {
			if (configuration == null) {
				try {
					configuration = getConfigurationScheme().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					log.severe("Could not instancize default Configuration");
				}
			}
		}
	}

	protected abstract Class<C> getConfigurationScheme();

	public abstract String getName();

	public abstract Collection<Chart> initialize();

	public abstract void cleanup();

	public abstract Collection<Chart> collectValues();
}
