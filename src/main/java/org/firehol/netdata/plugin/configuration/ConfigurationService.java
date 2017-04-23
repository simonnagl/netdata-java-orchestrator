package org.firehol.netdata.plugin.configuration;

import java.io.File;
import java.io.IOException;

import org.firehol.netdata.plugin.configuration.exception.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for reading configuration files.
 * 
 * @author Simon Nagl
 */
public class ConfigurationService {

	private ObjectMapper mapper = new ObjectMapper();

	private static final ConfigurationService INSTANCE = new ConfigurationService();

	/**
	 * Do not let anyone instantiate this class.
	 */
	private ConfigurationService() {
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
	 * @param file
	 *            An existing configruation file.
	 * @param clazz
	 *            The scheme of the configuration.
	 * @return The read configuration.
	 * @throws IOException
	 *             if a low-level I/O problem (unexpected end-of-input, network
	 *             error) occurs (passed through as-is without additional
	 *             wrapping -- note that this is one case where
	 * @throws ParseException
	 *             If the content of the configuration file is not valid.
	 */
	public <T> T readConfiguration(File file, Class<T> clazz) throws IOException, ParseException {
		try {
			return mapper.readValue(file, clazz);
		} catch (JsonParseException | JsonMappingException e) {
			throw new ParseException("Errors in configuration. Reason: " + e.getMessage());
		}
	}
}
