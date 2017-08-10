/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.firehol.netdata.plugin.configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.firehol.netdata.Main;
import org.firehol.netdata.plugin.configuration.exception.ConfigurationSchemeInstantiationException;
import org.firehol.netdata.utils.LoggingUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
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

	private final ObjectMapper mapper;

	private EnvironmentConfigurationService environmentConfigurationService = EnvironmentConfigurationService
			.getInstance();

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

		log.fine("Initialize object mapper for reading configuration files.");
		mapper = new ObjectMapper();
		configureObjectMapper(mapper);
	}

	private void configureObjectMapper(ObjectMapper mapper) {
		mapper.enable(Feature.ALLOW_COMMENTS);
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

		// First try to read the value.
		try {
			configuration = mapper.readValue(file, clazz);
		} catch (JsonParseException | JsonMappingException e) {
			log.warning(LoggingUtils.getMessageSupplier("Could not read malformed configuration file.", e));
		} catch (Exception e) {
			log.warning(LoggingUtils
					.getMessageSupplier("Could not read configuration file '" + file.getAbsolutePath() + "'.", e));
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

	public PluginDaemonConfiguration readGlobalConfiguration() throws ConfigurationSchemeInstantiationException {
		final Path configDir = environmentConfigurationService.getConfigDir();
		Path globalConfigPath = configDir.resolve("java.d.conf");
		PluginDaemonConfiguration globalConfig;

		globalConfig = readConfiguration(globalConfigPath.toFile(), PluginDaemonConfiguration.class);
		return globalConfig;
	}

	public <T> T readPluginConfiguration(String pluginName, Class<T> clazz)
			throws ConfigurationSchemeInstantiationException {
		Path configDir = environmentConfigurationService.getConfigDir().resolve("java.d");
		Path configFile = configDir.resolve(pluginName + ".conf");

		return this.readConfiguration(configFile.toFile(), clazz);
	}
}
