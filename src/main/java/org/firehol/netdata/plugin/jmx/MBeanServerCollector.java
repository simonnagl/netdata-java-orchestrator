package org.firehol.netdata.plugin.jmx;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.ChartType;
import org.firehol.netdata.entity.Dimension;
import org.firehol.netdata.entity.DimensionAlgorithm;
import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.exception.NotImplementedException;
import org.firehol.netdata.plugin.Collector;

public class MBeanServerCollector implements Collector {
	private class MBeanCollector implements Collector {

		private ObjectName mBeanName;
		
		public MBeanCollector(ObjectName mBeanName) {
			this.mBeanName = mBeanName;
		}
		
		@Override
		public Collection<Chart> initialize() throws InitializationException {
			List<Chart> allChart = new LinkedList<>();
			
			Chart chart = new Chart("jmx", mBeanName.getCanonicalName(), mBeanName.getCanonicalName(), mBeanName.getCanonicalName(), "number", mBeanServer.toString(), mBeanName.getKeyProperty("type"), ChartType.LINE, 10000, 1);

			allChart.add(chart);
			
			return allChart;
		}

		@Override
		public Collection<Chart> collectValues() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private MBeanServerConnection mBeanServer;

	
	private List<MBeanCollector> allMBeanCollector = new LinkedList<>();

	public MBeanServerCollector(MBeanServerConnection mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	public MBeanServerConnection getmBeanServer() {
		return mBeanServer;
	}

	@Override
	public Collection<Chart> initialize() throws InitializationException {
		List<Chart> allChart = new LinkedList<>();
		
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

			MBeanCollector collector = new MBeanCollector(mBeanName);
			
			allChart.addAll(collector.initialize());
			MBeanAttributeInfo[] mBeanInfo;
			try {
				 mBeanInfo = mBeanServer.getMBeanInfo(mBeanName).getAttributes();
				
			} catch (InstanceNotFoundException | IntrospectionException | ReflectionException | IOException e) {
				// TODO : Proper error handling
				allMBeanNameIterator.remove();
				continue;
			}
			
			
			 for(MBeanAttributeInfo info : mBeanInfo) {
				 switch(info.getType()) {
				 case "long":
					 try {
						long value = (long) mBeanServer.getAttribute(mBeanName, info.getName());
						
						Dimension dim = new Dimension(info.getName(), info.getName(), DimensionAlgorithm.ABSOLUTE, 1, 1, false, value);
						
					} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException
							| ReflectionException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 break;
				 }
			 }
		}

		// TODO Auto-generated method stub
		return allChart;
	}

	@Override
	public Collection<Chart> collectValues() {
		throw new NotImplementedException("Collect the values of all MBeans in this MBeanServer");
	}

}
