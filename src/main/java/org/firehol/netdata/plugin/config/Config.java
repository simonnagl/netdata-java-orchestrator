package org.firehol.netdata.plugin.config;

import java.io.IOException;
import java.nio.file.Path;

import org.firehol.netdata.plugin.config.exception.ParsingException;

/**
 * Handle Configuration properties.
 *
 * A configuration property is identified by a section name and an option name.
 *
 * @author Simon Nagl
 */
public interface Config {

	/**
	 * Reads configuration options from a file.
	 *
	 * The configuration file can contain three elements.
	 *
	 * 1. Lines starting with '#' are treated as comments and ignored.
	 *
	 * 2. Lines starting with '[' and ending with ']' are section headers.
	 *
	 * 3. Lines containing one '=' are treated as key values pairs.
	 *
	 * In the configuration file there must be a section header before the first
	 * key value pair.
	 *
	 * @param path
	 *            to a File.
	 * @throws ParsingException
	 *             if something goes wrong.
	 */
	public void read(Path path) throws ParsingException, IOException;

	/**
	 * Writes the configuration to path.
	 *
	 * @see read(Path path)
	 * @param path
	 *            to file to write configuration to.
	 */
	public void write(Path path);

	/**
	 * Get a configuration option as string.
	 *
	 * If configuration option was not present create a new one with
	 * default_value.
	 *
	 * @param section
	 *            of the configuration
	 * @param option
	 *            of the configuration
	 * @param default_value
	 *            if configuration was not present.
	 * @return configuration value
	 */
	public String get(String section, String option, String default_value);

	/**
	 * Get a configuration option as long.
	 *
	 * If the configuration option is not present create a new one with value
	 * default_value.
	 *
	 * @param section
	 *            of the configuration option
	 * @param option
	 *            name
	 * @param default_value
	 *            to set if configuration option was not present
	 * @return configuration value
	 */
	public long getNumber(String section, String option, long default_value);

	/**
	 * Get a configuration option as boolean.
	 *
	 * If the configuration option is not present create a new one with value
	 * default_value.
	 *
	 * @param section
	 *            of the configuration option
	 * @param option
	 *            name
	 * @param default_value
	 *            to set if configuration option was not present
	 * @return configuration value
	 */
	public boolean getBoolean(String section, String option, boolean default_value);

	/**
	 * Check if a configuration option is present.
	 *
	 * @param section
	 *            of the configuration option
	 * @param option
	 *            name
	 * @return if the configuration option is present
	 */
	public boolean isPresent(String section, String option);

	/**
	 * Set a configuration option
	 *
	 * @param section
	 *            name
	 * @param option
	 *            name
	 * @param value
	 *            to set
	 */
	public void set(String section, String option, String value);

	/**
	 * Set a configuration option
	 *
	 * @param section
	 *            name
	 * @param option
	 *            name
	 * @param value
	 *            to set
	 */
	public void setNumber(String section, String option, int value);

	/**
	 * Set a configuration option
	 *
	 * @param section
	 *            name
	 * @param option
	 *            name
	 * @param value
	 *            to set
	 */
	public void setBoolean(String section, String option, boolean value);
}
