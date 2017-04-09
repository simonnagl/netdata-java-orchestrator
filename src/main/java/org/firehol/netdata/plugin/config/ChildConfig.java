package org.firehol.netdata.plugin.config;

import java.io.IOException;
import java.nio.file.Path;

import org.firehol.netdata.plugin.config.exception.ParsingException;

/**
 * This configuration tries to read a value first from its own configuration and
 * if it is not present form the fall back configuration.
 *
 * If new configuration values are set they always get set to this
 * configuration. The fall back configuration gets not changed.
 *
 * @author Simon
 */
public class ChildConfig extends BaseConfig implements Config {

	final BaseConfig fallbackConfig;

	public ChildConfig(final Path path, final BaseConfig fallbackConfig) throws ParsingException, IOException {
		this.fallbackConfig = fallbackConfig;
		read(path);
	}

	@Override
	public String get(final String section, final String option, final String default_value) {
		String value = this.get(section, option);

		if (value == null) {
			value = fallbackConfig.get(section, option);
			if (value == null) {
				this.set(section, option, default_value);
				return default_value;
			}
		}

		return value;
	}

	@Override
	public boolean isPresent(final String section, final String option) {
		if (!this.isPresent(section, option)) {
			return fallbackConfig.isPresent(section, option);
		}
		return true;
	}
}
