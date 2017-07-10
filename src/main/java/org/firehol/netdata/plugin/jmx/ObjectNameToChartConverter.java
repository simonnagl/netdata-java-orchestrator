package org.firehol.netdata.plugin.jmx;

import javax.management.ObjectName;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.ChartType;

public class ObjectNameToChartConverter {

	private static final ObjectNameToChartConverter INSTANCE = new ObjectNameToChartConverter();

	/**
	 * Do not let anyone instantiate this class.
	 */
	private ObjectNameToChartConverter() {
	}

	public static ObjectNameToChartConverter getInstance() {
		return INSTANCE;
	}

	public Chart convert(ObjectName jmxBean, String name) {
		String jmxBeanName = jmxBean.getKeyProperty("name");
		if (jmxBeanName != null) {
			jmxBeanName = jmxBeanName.replaceAll(" ", "_");
		}
		String jmxBeanType = jmxBean.getKeyProperty("type");
		String jmxBeanDomain = jmxBean.getDomain();

		String id = jmxBean.getCanonicalName().replaceAll("[.]", "/").replaceAll(":", "-").replaceAll(",", "-")
				.replaceAll(" ", "_").replaceAll("=", "-");
		String beanName = jmxBeanName;
		if (name == null) {
			name = jmxBeanDomain + jmxBeanType;
		}
		String title = new StringBuilder(jmxBean.getDomain()).append('.').append(jmxBeanName).toString();
		String family = name;
		String context = jmxBeanType;
		if (context == null) {
			context = jmxBeanDomain;
		}

		return new Chart("jmx", id, beanName, title, "num", family, context, ChartType.LINE, 15000, 1);
	}

}
