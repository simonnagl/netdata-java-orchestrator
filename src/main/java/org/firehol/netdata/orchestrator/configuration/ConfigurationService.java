// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.orchestrator.configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.firehol.netdata.Main;
import org.firehol.netdata.orchestrator.configuration.exception.ConfigurationSchemeInstantiationException;
import org.firehol.netdata.orchestrator.configuration.schema.OrchestratorConfiguration;
import org.firehol.netdata.utils.LoggingUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

public final class ConfigurationService {
	private final Logger log = Logger.getLogger("org.firehol.netdata.orchestrator.configuration");

	private final ObjectMapper mapper;

	private EnvironmentConfigurationService environmentConfigurationService = EnvironmentConfigurationService
			.getInstance();

	@Getter
	private OrchestratorConfiguration globalConfiguration;

	private static final ConfigurationService INSTANCE = new ConfigurationService();

	private ConfigurationService() {
		log.fine("Initialize object mapper for reading configuration files.");
		mapper = buildObjectMapper();

		log.info("Read configuration");
		try {
			this.globalConfiguration = readGlobalConfiguration();
		} catch (ConfigurationSchemeInstantiationException e) {
			Main.exit(LoggingUtils.buildMessage("Could not initialize ConfigurationService", e));
		}

	}

	private ObjectMapper buildObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(Feature.ALLOW_COMMENTS);
		return mapper;
	}

	public static ConfigurationService getInstance() {
		return INSTANCE;
	}

	/**
	 * Read a configuration file.
	 * 
	 * <p>
	 * If the file cannot be parsed for some reason this methods tries to use a
	 * default configuration. This is the default instance of the configuration
	 * scheme.
	 * </p>
	 * 
	 * @param <T>
	 *            Configuration Schema.
	 * 
	 * @param file
	 *            to read
	 * @param clazz
	 *            The schema of the configuration.
	 * @return The configuration read from file, or if it was invalid the
	 *         default configuration.
	 * @throws ConfigurationSchemeInstantiationException
	 *             if it was not possible to instantiate clazz
	 */
	protected <T> T readConfiguration(File file, Class<T> clazz) throws ConfigurationSchemeInstantiationException {
		T configuration = null;

		// First try to read the value.
		try {
			configuration = mapper.readValue(file, clazz);
		} catch (JsonParseException | JsonMappingException e) {
			log.warning(LoggingUtils.getMessageSupplier(
					"Could not read malformed configuration file '" + file.getAbsolutePath() + ".", e));
		} catch (Exception e) {
			log.warning(LoggingUtils.getMessageSupplier("Could not read configuration file '" + file.getAbsolutePath()
					+ "', " + clazz + ", " + mapper + ".", e));
		}

		// If we still have no configuration try to read the default one.
		if (configuration == null) {
			try {
				configuration = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new ConfigurationSchemeInstantiationException(
						"Could not instanciate default configuration for class " + clazz.getName() + ".");
			}
		}

		return configuration;
	}

	public OrchestratorConfiguration readGlobalConfiguration() throws ConfigurationSchemeInstantiationException {
		final Path configDir = environmentConfigurationService.getConfigDir();
		Path globalConfigPath = configDir.resolve("java.d.conf");
		OrchestratorConfiguration globalConfig;

		globalConfig = readConfiguration(globalConfigPath.toFile(), OrchestratorConfiguration.class);
		return globalConfig;
	}

	public <T> T readModuleConfiguration(String moduleName, Class<T> clazz)
			throws ConfigurationSchemeInstantiationException {
		Path configDir = environmentConfigurationService.getConfigDir().resolve("java.d");
		Path configFile = configDir.resolve(moduleName + ".conf");

		log.info(": Reading '" + moduleName + "' module configuration file '" + configFile.toFile().getAbsolutePath()
				+ "'");
		return this.readConfiguration(configFile.toFile(), clazz);
	}
}
