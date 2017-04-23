package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
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

public class JmxPlugin extends AbstractPlugin<JmxPluginConfiguration> {

	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin.jmx");

	private final List<MBeanServerCollector> allMBeanCollector = new ArrayList<>();
	/**
	 * We maintain a list of all connections to enable disconnecting.
	 */
	private final List<JMXConnector> allJMXConnector = new ArrayList<>();

	@Override
	public String getName() {
		return "jmx";
	}

	@Override
	protected Class<JmxPluginConfiguration> getConfigurationScheme() {
		return JmxPluginConfiguration.class;
	}

	/**
	 * Close a JMXConnector asynchrony.
	 * 
	 * @param connection
	 * @return
	 */
	protected static CompletableFuture<Boolean> close(JMXConnector connection) {

		return CompletableFuture.supplyAsync(new Supplier<Boolean>() {

			@Override
			public Boolean get() {
				try {
					connection.close();
					return true;
				} catch (IOException e) {
					return false;
				}
			}
		});
	}

	@Override
	public Collection<Chart> initialize() {

		// Connect to MBeanServers of configuration.

		for (JmxServerConfiguration serverConfiguartion : getConfiguration().getJmxServers()) {
			JMXServiceURL url;
			try {
				url = new JMXServiceURL(null, null, serverConfiguartion.getPort());
			} catch (MalformedURLException e) {
				log.warning("Could not connect to JMX Server at port " + serverConfiguartion.getPort() + " Reason: "
						+ e.getMessage());
				continue;
			}

			JMXConnector connection;
			try {
				connection = JMXConnectorFactory.connect(url);
				allJMXConnector.add(connection);
			} catch (IOException e) {
				log.warning("Could not connect to JMX Server at port " + serverConfiguartion.getPort() + " Reason: "
						+ e.getMessage());
				continue;
			}
			MBeanServerConnection mBeanServer;
			try {
				mBeanServer = connection.getMBeanServerConnection();
			} catch (IOException e) {
				log.warning("Could not connect to JMX Server at port " + serverConfiguartion.getPort() + " Reason: "
						+ e.getMessage());
				close(connection);

				continue;
			}

			MBeanServerCollector collector = new MBeanServerCollector(mBeanServer);
			allMBeanCollector.add(collector);
		}

		// Connect to the local MBeanServer

		MBeanServerCollector collector = new MBeanServerCollector(ManagementFactory.getPlatformMBeanServer());
		allMBeanCollector.add(collector);

		// Initialize MBeanServer

		List<Chart> allChart = new LinkedList<>();
		Iterator<MBeanServerCollector> mBeanCollectorIterator = allMBeanCollector.iterator();

		while (mBeanCollectorIterator.hasNext()) {
			MBeanServerCollector mBeanCollector = mBeanCollectorIterator.next();
			try {
				allChart.addAll(mBeanCollector.initialize());
			} catch (InitializationException e) {
				log.warning("Could not initialize JMX plugin " + mBeanCollector.getmBeanServer().toString());
				mBeanCollectorIterator.remove();
			}
		}

		return allChart;
	}

	public void cleanup() {
		try {
			CompletableFuture.allOf((CompletableFuture<?>[]) allJMXConnector.stream().map(JmxPlugin::close).toArray())
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
