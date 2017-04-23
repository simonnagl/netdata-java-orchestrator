package org.firehol.netdata.plugin.jmx.configuration;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmxPluginConfiguration {
	private List<JmxServerConfiguration> jmxServers = new ArrayList<>();
}
