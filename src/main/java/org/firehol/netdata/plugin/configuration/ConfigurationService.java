package org.firehol.netdata.plugin.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.firehol.netdata.Main;
import org.firehol.netdata.plugin.configuration.exception.ConfigurationSchemeInstantiationException;
import org.firehol.netdata.utils.LoggingUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 * Service for reading configuration files.
 * 
 * @author Simon Nagl
 */
public final class ConfigurationService {
	private final Logger log = Logger.getLogger("org.firehol.netdata.plugin.configuration");

	private final ObjectMapper mapper = new ObjectMapper();

	private EnvironmentConfigurationService environmentConfigurationService = EnvironmentConfigurationService.getInstance();

	@Getter
	private PluginDaemonConfiguration globalConfiguration;

	private static final ConfigurationService INSTANCE = new ConfigurationService();

	/**
	 * Do not let anyone instantiate this class.
	 */
	private ConfigurationService() {
		log.info("Read configuration");
		try {
			this.globalConfiguration = readGlobalConfiguration();
		} catch (ConfigurationSchemeInstantiationException e) {
			Main.exit(LoggingUtils.buildMessage("Could not initialize ConfigurationService", e));
		}
	}

	/**
	 * Get an instance of the configuration service.
	 * 
	 * @return the ConfigurationService
	 */
	public static ConfigurationService getInstance() {
		return INSTANCE;
	}

	/**
	 * Read a configuration file.
	 * 
	 * If the file cannot be parsed for some reason this methods tries to use a
	 * default configuration. This is the default instance of the configuration
	 * scheme.
	 *
	 * @param file
	 *            to read.
	 * @param clazz
	 *            The scheme of the configuration.
	 * @return The configuration read from file, or if it was invalid the default
	 *         configuration.
	 * @throws ConfigurationSchemeInstantiationException
	 *             if it was not possible to instantiate clazz
	 */
	protected <T> T readConfiguration(File file, Class<T> clazz) throws ConfigurationSchemeInstantiationException {
		T configuration = null;
	
		try {
			configuration = mapper.readValue(file, clazz);
		} catch (JsonParseException | JsonMappingException e) {
			log.warning(LoggingUtils.buildMessage("Could not read malformed configuration file.", e));
		} catch (IOException e) {
			log.warning(LoggingUtils.buildMessage("Could not read configuration file '" + file.getAbsolutePath() + "'.", e));
		} finally {
			if (configuration == null) {
				try {
					configuration = clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new ConfigurationSchemeInstantiationException("Could not instancize default Configuration.");
				} 
			}
		}
		return configuration;
	}

	public PluginDaemonConfiguration readGlobalConfiguration() throws ConfigurationSchemeInstantiationException {
		final Path configDir = environmentConfigurationService.getConfigDir();
		Path globalConfigPath = configDir.resolve("java.d.conf");
		PluginDaemonConfiguration globalConfig;

		globalConfig = readConfiguration(globalConfigPath.toFile(), PluginDaemonConfiguration.class);
		return globalConfig;
	}

	public <T> T readPluginConfiguration(String pluginName, Class<T> clazz) throws ConfigurationSchemeInstantiationException {
		Path configDir = environmentConfigurationService.getConfigDir().resolve("java.d");
		Path configFile = configDir.resolve(pluginName + ".conf");

		return this.readConfiguration(configFile.toFile(), clazz);
	}
}
