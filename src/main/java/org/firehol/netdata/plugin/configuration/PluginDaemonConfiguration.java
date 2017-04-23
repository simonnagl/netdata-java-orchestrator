package org.firehol.netdata.plugin.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PluginDaemonConfiguration {
	private long updateEvery = 1;
}
