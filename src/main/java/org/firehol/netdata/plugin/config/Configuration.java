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
@NoArgsConstructor
public class Configuration {

	private Logger log = Logger.getLogger("org.firehol.netdata.configuration");

	@Getter
	private class Section {
		private String name;
		private SortedMap<String, Option> allOption = new TreeMap<>();

		public Section(String name) {
			this.name = name;
		}
	}

	@Getter
	@AllArgsConstructor
	private class Option {
		private String key;
		@Setter
		private String value;
		@Setter
		private boolean fromFile;
	}

	private SortedMap<String, Section> allSection = new TreeMap<>();
	
	public Configuration(Path path) throws ParsingException {
		read(path);
	}

	public void read(Path path) throws ParsingException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line = null;
			String currentSection = null;

			while ((line = reader.readLine()) != null) {
				String rawLine = line;
				line = line.trim();
				
				// Skip empty lines
				if(line.isEmpty()) {
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
				set(currentSection, line.substring(0, equalSignIndex).trim(), line.substring(equalSignIndex + 1).trim(), true);
			}
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	public void write(Path path) {
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

	public String get(String section, String option, String default_value) {
		// Find Section
		Section thisSection = allSection.get(section);
		if (thisSection == null) {
			set(section, option, default_value);
			return default_value;
		}
		// Find option
		Option thisOption = thisSection.getAllOption().get(option);
		if (thisOption == null) {
			set(section, option, default_value);
			return default_value;
		}
		return thisOption.getValue();
	}

	public long getNumber(String section, String option, long default_value) {
		return Long.valueOf(get(section, option, String.valueOf(default_value)));
	}

	public boolean getBoolean(String section, String option, boolean default_value) {
		return Boolean.valueOf(get(section, option, String.valueOf(default_value)));
	}

	public boolean isPresent(String section, String option) {
		Section thisSecion = allSection.get(section);
		if (section == null) {
			return false;
		}
		if (thisSecion.getAllOption().get(option) == null) {
			return false;
		}
		return true;
	}

	void set(String section, String option, String value, boolean fromFile) {
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

	public void set(String section, String option, String value) {
		set(section, option, value, false);
	}

	public void setNumber(String section, String option, int value) {
		set(section, option, "" + value);
	}

	public void setBoolean(String section, String option, boolean value) {
		set(section, option, "" + value);
	}

}
