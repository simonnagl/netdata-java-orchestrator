// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.model;

public enum ChartType {
	LINE, AREA, STACKED;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
