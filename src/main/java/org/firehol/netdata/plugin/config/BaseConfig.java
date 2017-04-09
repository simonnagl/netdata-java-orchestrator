package org.firehol.netdata.plugin.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.firehol.netdata.plugin.config.exception.ParsingException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Handle Configuration properties.
 *
 * The configuration file can contain three elements. 1. Lines starting with '#'
 * are treated as comments and ignored. 2. Lines starting with '[' and ending
 * with ']' are section headers. 3. Lines containing one '=' are treated as key
 * values pairs.
 *
 * The configuration file must start with one section.
 *
 * @author Simon Nagl
 */
// TODO: Proper logging.
@NoArgsConstructor
public class BaseConfig implements Config {

	private final Logger log = Logger.getLogger("org.firehol.netdata.configuration");

	@Getter
	private class Section {
		private final String name;
		private final SortedMap<String, Option> allOption = new TreeMap<>();

		public Section(final String name) {
			this.name = name;
		}
	}

	@Getter
	@AllArgsConstructor
	private class Option {
		private final String key;
		@Setter
		private String value;
		@Setter
		private boolean fromFile;
	}

	private final SortedMap<String, Section> allSection = new TreeMap<>();

	public BaseConfig(final Path path) throws ParsingException, IOException {
		read(path);
	}

	@Override
	public void read(final Path path) throws ParsingException, IOException {
		BufferedReader reader = Files.newBufferedReader(path);
		String line = null;
		String currentSection = null;

		while ((line = reader.readLine()) != null) {
			String rawLine = line;
			line = line.trim();

			// Skip empty lines
			if (line.isEmpty()) {
				continue;
			}

			// Skip comments
			if (line.charAt(0) == '#') {
				continue;
			}

			// Parse section header
			if (line.charAt(0) == '[') {
				currentSection = line.substring(1, line.length() - 1);
				continue;
			}

			// Parse configuration option
			int equalSignIndex = line.indexOf('=');
			if (equalSignIndex == 0) {
				throw new ParsingException("Could not parse line '" + rawLine + "'");
			}
			set(currentSection, line.substring(0, equalSignIndex).trim(), line.substring(equalSignIndex + 1).trim(),
					true);
		}
	}

	@Override
	public void write(final Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			for (Section section : allSection.values()) {
				// Write section name.
				writer.write('[');
				writer.write(section.getName());
				writer.write(']');
				writer.newLine();

				// Write section options.
				for (Option option : section.allOption.values()) {
					if (!option.fromFile) {
						writer.write("# ");
					}
					writer.write(option.getKey());
					writer.write(" = ");
					writer.write(option.getValue());
					writer.newLine();
				}

				// Add newline between two sections.
				writer.newLine();
			}
			writer.close();
		} catch (IOException x) {
			log.severe("Could not write configuration to " + path.toAbsolutePath().toString());
		}
	}

	public String get(final String section, final String option) {
		// Find section
		Section thisSection = allSection.get(section);
		if (thisSection == null) {
			return null;
		}
		// Find option
		Option thisOption = thisSection.getAllOption().get(option);
		if (thisOption == null) {
			return null;
		}
		// Find value
		return thisOption.getValue();
	}

	@Override
	public String get(final String section, final String option, final String default_value) {
		String value = this.get(section, option);

		if (value == null) {
			set(section, option, default_value);
			return default_value;
		}

		return value;
	}

	public long getNumber(final String section, final String option) {
		return Long.valueOf(get(section, option));
	}

	@Override
	public long getNumber(final String section, final String option, final long default_value) {
		return Long.valueOf(get(section, option, String.valueOf(default_value)));
	}

	public boolean getBoolean(final String section, final String option) {
		return Boolean.valueOf(get(section, option));
	}

	@Override
	public boolean getBoolean(final String section, final String option, final boolean default_value) {
		return Boolean.valueOf(get(section, option, String.valueOf(default_value)));
	}

	@Override
	public boolean isPresent(final String section, final String option) {
		Section thisSecion = allSection.get(section);
		if (section == null) {
			return false;
		}
		if (thisSecion.getAllOption().get(option) == null) {
			return false;
		}
		return true;
	}

	private void set(final String section, final String option, final String value, final boolean fromFile) {
		// Find or create section
		Section thisSection = allSection.get(section);
		if (thisSection == null) {
			thisSection = new Section(section);
			allSection.put(section, thisSection);
		}
		// Find or set create
		Option thisOption = thisSection.getAllOption().get(option);
		if (thisOption == null) {
			thisOption = new Option(option, value, fromFile);
			thisSection.getAllOption().put(option, thisOption);
		} else {
			log.warning("Overwrite configuration option '" + option + " = " + thisOption.getValue() + "' at section '"
					+ section + "'");
			thisOption.setValue(value);
			thisOption.setFromFile(fromFile);
		}
	}

	@Override
	public void set(final String section, final String option, final String value) {
		set(section, option, value, false);
	}

	@Override
	public void setNumber(final String section, final String option, final int value) {
		set(section, option, "" + value);
	}

	@Override
	public void setBoolean(final String section, final String option, final boolean value) {
		set(section, option, "" + value);
	}

}
