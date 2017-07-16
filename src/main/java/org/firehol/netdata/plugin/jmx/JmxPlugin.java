package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.AbstractPlugin;
import org.firehol.netdata.plugin.jmx.configuration.JmxPluginConfiguration;
import org.firehol.netdata.plugin.jmx.configuration.JmxServerConfiguration;
import org.firehol.netdata.plugin.jmx.exception.JmxMBeanServerConnectionException;
import org.firehol.netdata.utils.LoggingUtils;
import org.firehol.netdata.utils.ResourceUtils;

public class JmxPlugin extends AbstractPlugin<JmxPluginConfiguration> {

	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private final List<MBeanServerCollector> allMBeanCollector = new ArrayList<>();

	@Override
	public String getName() {
		return "jmx";
	}

	@Override
	protected Class<JmxPluginConfiguration> getConfigurationScheme() {
		return JmxPluginConfiguration.class;
	}

	@Override
	public Collection<Chart> initialize() {

		// Connect to MBeanServers of configuration.
		for (JmxServerConfiguration serverConfiguartion : getConfiguration().getJmxServers()) {
			MBeanServerCollector collector;
			try {
				collector = buildMBeanServerCollector(serverConfiguartion.getName(), serverConfiguartion.getPort());
			} catch (JmxMBeanServerConnectionException e) {
				log.warning(LoggingUtils.buildMessage(e));
				continue;
			}

			allMBeanCollector.add(collector);
		}

		// Connect to the local MBeanServer
		MBeanServerCollector collector = new MBeanServerCollector("NetdataJavaDaemon",
				ManagementFactory.getPlatformMBeanServer());
		allMBeanCollector.add(collector);

		// Initialize charts

		List<Chart> allChart = new LinkedList<>();
		Iterator<MBeanServerCollector> mBeanCollectorIterator = allMBeanCollector.iterator();

		while (mBeanCollectorIterator.hasNext()) {
			MBeanServerCollector mBeanCollector = mBeanCollectorIterator.next();
			try {
				// TODO Merge configuration of commenChats with server specific
				// configuration.
				allChart.addAll(mBeanCollector.initialize(getConfiguration().getCommonCharts()));
			} catch (InitializationException e) {
				log.warning("Could not initialize JMX plugin " + mBeanCollector.getMBeanServer().toString());
				ResourceUtils.close(mBeanCollector);
				mBeanCollectorIterator.remove();
			}
		}

		return allChart;
	}

	private MBeanServerCollector buildMBeanServerCollector(String name, int port)
			throws JmxMBeanServerConnectionException {
		JMXConnector connection = null;
		try {
			JMXServiceURL url = new JMXServiceURL(null, null, port);
			connection = JMXConnectorFactory.connect(url);
			MBeanServerConnection server = connection.getMBeanServerConnection();
			MBeanServerCollector collector = new MBeanServerCollector(name, server, connection);
			return collector;
		} catch (IOException e) {
			if (connection != null) {
				ResourceUtils.close(connection);
			}
			throw new JmxMBeanServerConnectionException("Faild to connect to JMX Server at port " + port + ".", e);
		}
	}

	public void cleanup() {
		try {
			CompletableFuture
					.allOf((CompletableFuture<?>[]) allMBeanCollector.stream().map(ResourceUtils::close).toArray())
					.get();
		} catch (InterruptedException | ExecutionException e) {
			log.fine("Could not close connection to at least one JMX Server");
		}

	}

	@Override
	public Collection<Chart> collectValues() {
		return allMBeanCollector.stream().map(MBeanServerCollector::collectValues).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

}
