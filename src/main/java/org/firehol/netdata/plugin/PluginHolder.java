package org.firehol.netdata.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.firehol.netdata.plugin.config.BaseConfig;
import org.firehol.netdata.plugin.config.exception.ParsingException;

/**
 * This singleton manages a collection of plugins.
 *
 * @author Simon Nagl
 */
public final class PluginHolder {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin");

	private final Collection<AbstractPlugin> allPlugin = new LinkedList<>();

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
	public void add(final AbstractPlugin plugin) {
		allPlugin.add(plugin);
	}

	public BaseConfig readConfiguration(final Path configDir) {
		log.info("Read configuration");
		Path globalConfigPath = configDir.resolve("java.d.conf");
		BaseConfig globalConfig;
		try {
			globalConfig = new BaseConfig(globalConfigPath);
		} catch (ParsingException | IOException e) {
			log.warning("Could not read config file'" + globalConfigPath.toAbsolutePath().toString() + "'. Reason: "
					+ e.getMessage());
			globalConfig = new BaseConfig();
		}

		// Read Plugin specific configuration.
		Path javaPluginConfigPath = configDir.resolve("java.d");
		for (AbstractPlugin plugin : allPlugin) {
			log.info("Read configuration for Java Plugin " + plugin.getName());
			plugin.readConfig(javaPluginConfigPath, globalConfig);
		}

		return globalConfig;
	}

	public void initializeCharts() {
		allPlugin.parallelStream().map(AbstractPlugin::initialize).flatMap(Collection::stream)
				.forEach(Printer::initializeChart);
	}

	public int getAllPluginSize() {
		return allPlugin.size();
	}

	public void collectValues() {
		allPlugin.parallelStream().map(AbstractPlugin::collectValues).flatMap(Collection::stream).forEach(Printer::collect);
	}
}
