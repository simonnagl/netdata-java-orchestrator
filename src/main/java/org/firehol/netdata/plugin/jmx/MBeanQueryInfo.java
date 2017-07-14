package org.firehol.netdata.plugin.jmx;

import java.util.LinkedList;
import java.util.List;

import javax.management.ObjectName;

import org.firehol.netdata.entity.Dimension;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBeanQueryInfo {
	private ObjectName name;
	private String attribute;
	private Class<?> type;
	private List<Dimension> dimensions = new LinkedList<>();
}