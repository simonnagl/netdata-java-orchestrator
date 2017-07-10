package org.firehol.netdata.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dimension {
	/**
	 * Identifier of this dimension (it is a text value, not numeric), this will
	 * be needed later to add values to the dimension.
	 */
	private String id;
	/**
	 * Name of the dimension as it will appear at the legend of the chart, if
	 * empty or missing the id will be used.
	 */
	private String name;
	private DimensionAlgorithm algorithm = DimensionAlgorithm.ABSOLUTE;
	/**
	 * Multiply the collected value.
	 */
	private int multiplier = 1;
	/**
	 * Divide the collected value.
	 */
	private int divisor = 1;
	/**
	 * Make this dimension hidden, it will take part in the calculations but
	 * will not be presented in the chart.
	 */
	private boolean hidden;

	/**
	 * Current collected value
	 */
	private long currentValue;

	public boolean hasName() {
		return getName() != null;
	}
}
