package org.firehol.netdata.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import org.firehol.netdata.plugin.configuration.ConfigurationService;
import org.firehol.netdata.plugin.configuration.PluginDaemonConfiguration;
import org.firehol.netdata.plugin.configuration.exception.ParseException;

/**
 * This singleton manages a collection of plugins.
 *
 * @author Simon Nagl
 */
public final class PluginHolder {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private final Collection<AbstractPlugin<?>> allPlugin = new LinkedList<>();

	private static final PluginHolder INSTANCE = new PluginHolder();

	/**
	 * Do not let anyone instantiate this class.
	 */
	private PluginHolder() {
	}

	public static PluginHolder getInstance() {
		return INSTANCE;
	}

	/**
	 * Add a Plugin which should be handeled by this classe.
	 *
	 * @param plugin
	 *            to add.
	 */
	public void add(final AbstractPlugin<?> plugin) {
		allPlugin.add(plugin);
	}

	public PluginDaemonConfiguration readConfiguration(final Path configDir) {
		log.info("Read configuration");
		Path globalConfigPath = configDir.resolve("java.d.conf");
		PluginDaemonConfiguration globalConfig;

		try {
			globalConfig = ConfigurationService.getInstance().readConfiguration(globalConfigPath.toFile(),
					PluginDaemonConfiguration.class);
		} catch (ParseException | IOException e) {
			log.warning("Could not read config file'" + globalConfigPath.toAbsolutePath().toString() + "'. Reason: "
					+ e.getMessage());
			globalConfig = new PluginDaemonConfiguration();
		}

		// Read Plugin specific configuration.
		Path javaPluginConfigPath = configDir.resolve("java.d");
		for (AbstractPlugin<?> plugin : allPlugin) {
			plugin.setBaseConfig(globalConfig);

			log.info("Read configuration for Java Plugin " + plugin.getName());
			try {
				plugin.readConfiguration(javaPluginConfigPath);
			} catch (ConfigurationException | IOException e) {
				// We could not read the configuration.
				// We do nothing here and give the plugin the change to
				// autoconfigure.
			}
		}

		return globalConfig;
	}

	public void initializeCharts() {
		// TODO Cleanup plugins which do not initialize correctly.

		allPlugin.parallelStream().map(AbstractPlugin::initialize).flatMap(Collection::stream)
				.forEach(Printer::initializeChart);
	}

	public int getAllPluginSize() {
		return allPlugin.size();
	}

	public void collectValues() {
		allPlugin.parallelStream().map(AbstractPlugin::collectValues).flatMap(Collection::stream)
				.forEach(Printer::collect);
	}
}