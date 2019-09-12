// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.model;

public enum DimensionAlgorithm {
	/**
	 * the value is to drawn as-is (interpolated to second boundary), if
	 * algorithm is empty, invalid or missing, absolute is used
	 */
	ABSOLUTE,
	/**
	 * the value increases over time, the difference from the last value is
	 * presented in the chart, the server interpolates the value and calculates
	 * a per second figure
	 */
	INCREMENTAL,
	/**
	 * the % of this value compared to the total of all dimensions
	 */
	PERCENTAGE_OF_ABSOLUTE_ROW,
	/**
	 * 
	 */
	PERCENTAGE_OF_INCREMENTAL_ROW;

	@Override
	public String toString() {
		return name().replace('_', '-').toLowerCase();
	}
}
