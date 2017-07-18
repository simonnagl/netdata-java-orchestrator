package org.firehol.netdata.plugin.configuration;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.firehol.netdata.plugin.configuration.exception.EnvironmentConfigurationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class EnvironmentConfigurationServiceTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

	@Test
	public void testReadNetdataConfigDir() throws EnvironmentConfigurationException {
		Path path = Paths.get("/test/folder");
		environmentVariables.set("NETDATA_CONFIG_DIR", path.toString());

		Path result = EnvironmentConfigurationService.getInstance().readNetdataConfigDir();
		
		assertEquals(path, result);
	}
}
