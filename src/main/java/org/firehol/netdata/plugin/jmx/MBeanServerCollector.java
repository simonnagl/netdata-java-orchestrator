package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.plugin.Collector;

public class MBeanServerCollector implements Collector {
	private MBeanServerConnection mBeanServer;

	private List<MBeanCollector> allMBeanCollector = new ArrayList<>();

	public MBeanServerCollector(MBeanServerConnection mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerConnection getmBeanServer() {
		return mBeanServer;
	}

	@Override
	public Collection<Chart> initialize() throws InitializationException {
		// Step 1
		// Get all MBeans
		Set<ObjectName> allMBeanName;
		try {
			allMBeanName = mBeanServer.queryNames(null, null);
		} catch (IOException e) {
			throw new InitializationException("Could not initialize mBeanServer", e);
		}

		Iterator<ObjectName> allMBeanNameIterator = allMBeanName.iterator();
		while (allMBeanNameIterator.hasNext()) {
			ObjectName mBeanName = allMBeanNameIterator.next();

			System.out.println(mBeanName.toString());

			try {
				MBeanAttributeInfo[] mBeanInfo = mBeanServer.getMBeanInfo(mBeanName).getAttributes();
			} catch (InstanceNotFoundException | IntrospectionException | ReflectionException | IOException e) {
				// TODO : Proper error handling
				allMBeanNameIterator.remove();
			}

		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Chart> collectValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
