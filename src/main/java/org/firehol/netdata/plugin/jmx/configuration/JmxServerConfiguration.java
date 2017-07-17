package org.firehol.netdata.plugin.jmx.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JmxServerConfiguration {
	private int port;
	private String name;
}
