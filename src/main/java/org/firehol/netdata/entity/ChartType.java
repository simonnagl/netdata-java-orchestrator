package org.firehol.netdata.entity;

public enum ChartType {
	LINE, AREA, STACKED;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
