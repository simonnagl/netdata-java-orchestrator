package org.firehol.netdata.module.jmx.utils;

import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;

import javax.management.*;
import java.io.IOException;

public final class MBeanServerUtils {

	private MBeanServerUtils() {
	}

	public static Object getAttribute(MBeanServerConnection mBeanServer, ObjectName name, String attribute)
			throws JmxMBeanServerQueryException {
		try {
			return mBeanServer.getAttribute(name, attribute);
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			throw new JmxMBeanServerQueryException(
					"Could not query attribute '" + attribute + "' of MBean '" + name + "'", e);
		}
	}
}
