package org.firehol.netdata.plugin.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.firehol.netdata.plugin.config.exception.ParsingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationTest {

	Configuration config;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void init() {
		config = new Configuration();
	}

	@Test
	public void testWrite() throws IOException, ParsingException {
		// Add configuration Options.
		config.set("section", "option", "value");
		config.set("section", "option2", "value2");
		config.set("section2", "option", "value", true);
		
		// write File
		Path configPath = folder.newFile("config").toPath();
		config.write(configPath);
		
		String fileString = new String(Files.readAllBytes(configPath));
		String expected = "[section]\n# option = value\n# option2 = value2\n\n[section2]\noption = value\n\n";
		
		assertEquals(expected, fileString);
	}
	
	@Test
	public void testRead() throws IOException, ParsingException {
		Path file = folder.newFile("config").toPath();
		String lines = "[section]\noption = value\n option2 = value2\n#Comment\n\n[section2]\noption = value\n\n";
		Files.write(file, lines.getBytes());
		
		Configuration readConfig = new Configuration(file);
		
		assertEquals("section.option", "value", readConfig.get("section", "option", "false"));
		assertEquals("section.option2", "value2", readConfig.get("section", "option2", "false"));
		assertEquals("section2.option", "value", readConfig.get("section", "option", "false"));


	}

	@Test
	public void testIsPresent() {
		// Set Option
		config.set("section", "option", "value");
		
		// Test Present
		assertTrue(config.isPresent("section","option"));
		assertFalse(config.isPresent("section", "option2"));
	}

	@Test
	public void testGetAndSet() {
		// Set Option and read it.
		config.set("section", "option", "value");
		assertEquals("value", config.get("section", "option", "default"));

		// Overwrite option
		config.set("section", "option", "newValue");
		assertEquals("newValue", config.get("section", "option", "default"));

		// Get default.
		assertEquals("defaultValue", config.get("section", "newOption", "defaultValue"));

		// Do not overwrite option set with get.
		assertEquals("defaultValue", config.get("section", "newOption", "newValue"));
	}

	@Test
	public void testSetAndGetNumber() {
		// Set Option and read it.
		config.setNumber("section", "one", 1);
		assertEquals(1, config.getNumber("section", "one", -1));
		
		// Set negative option
		config.setNumber("section", "negative", -2);
		assertEquals(-2, config.getNumber("section", "negative", -3));

		// Overwrite option
		config.setNumber("section", "option", 2);
		assertEquals(2, config.getNumber("section", "option", 3));

		// Get default.
		assertEquals(2, config.getNumber("section", "newOption", 2));

		// Do not overwrite option set with get.
		assertEquals(2, config.getNumber("section", "newOption", 3));
	}

	@Test
	public void testGetAndSetBoolean() {
		// Set Option and read it.
		config.setBoolean("section", "true", true);
		assertEquals(true, config.getBoolean("section", "true", false));
		
		// Overwrite option
		config.setBoolean("section", "true", false);
		assertEquals(false, config.getBoolean("section", "true", true));

		// Get default.
		assertEquals(true, config.getBoolean("section", "newOption", true));

		// Do not overwrite option set with get.
		assertEquals(true, config.getBoolean("section", "newOption", false));	}

}
