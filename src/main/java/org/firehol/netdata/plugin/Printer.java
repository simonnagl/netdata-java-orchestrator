package org.firehol.netdata.plugin;

import org.firehol.netdata.entity.Chart;
import org.firehol.netdata.entity.Dimension;

/**
 * The class {@code Printer} contains methods to communicate with the caller.
 * 
 * The format of the communication is defined <a href="https://github.com/firehol/netdata/wiki/External-Plugins#netdata-plugins">here</a> 
 * 
 * @author Simon Nagl
 * @since 1.0.0
 * @
 */
public final class Printer {

	/**
     * Don't let anyone instantiate this class.
	 */
	private Printer() {
	}

	private static void print(final String command) {
		System.out.println(command);
	}

	/**
	 * Print the initialization of a chart.
	 * 
	 * @param chart
	 *            to initialize
	 */
	public static void initializeChart(final Chart chart) {
		StringBuilder sb = new StringBuilder();
		appendInitializeChart(sb, chart);

		for (Dimension dimension : chart.getAllDimension()) {
			sb.append(System.lineSeparator());
			appendInitializeDimension(sb, dimension);
		}

		print(sb.toString());
	}

	protected static void appendInitializeChart(StringBuilder sb, final Chart chart) {
		// Start new chart
		sb.append("CHART ");
		// Append identifier
		sb.append(chart.getType());
		sb.append('.');
		sb.append(chart.getId());
		sb.append(' ');
		// Append name
		if (chart.hasName()) {
			sb.append(chart.getName());
		} else {
			sb.append("null");
		}
		sb.append(' ');
		// Append title
		sb.append(chart.getTitle());
		sb.append(' ');
		// Append untis
		sb.append(chart.getUnits());
		sb.append(' ');
		// Append familiy
		if (chart.hasFamily()) {
			sb.append(chart.getFamily());
		} else {
			sb.append(chart.getId());
		}
		sb.append(' ');
		// Append context
		if (chart.hasContext()) {
			sb.append(chart.getContext());
		} else {
			sb.append(chart.getId());
		}
		sb.append(' ');
		// Append chart type
		sb.append(chart.getChartType());
		sb.append(' ');
		// Append priority
		sb.append(chart.getPriority());
		// Append update_every
		if (chart.hasUpdateEvery()) {
			sb.append(' ');
			sb.append(chart.getUpdateEvery());
		}
	}

	protected static void appendInitializeDimension(StringBuilder sb, final Dimension dimension) {
		// Start new dimension
		sb.append("DIMENSION ");
		// Append ID
		sb.append(dimension.getId());
		sb.append(' ');
		// Append name
		if (dimension.hasName()) {
			sb.append(dimension.getName());
		} else {
			sb.append(dimension.getId());
		}
		sb.append(' ');
		// Append algorithm
		sb.append(dimension.getAlgorithm());
		sb.append(' ');
		// Append multiplier
		sb.append(dimension.getMultiplier());
		sb.append(' ');
		// Append divisor
		sb.append(dimension.getDivisor());
		// Append hidden
		if (dimension.isHidden()) {
			sb.append(" hidden");
		}
	}

	public static void collect(final Chart chart) {
		StringBuilder sb = new StringBuilder();
		appendCollectBegin(sb, chart);

		for (Dimension dim : chart.getAllDimension()) {
			if (dim.getCurrentValue() != null) {
				sb.append(System.lineSeparator());
				appendCollectDimension(sb, dim);
				dim.setCurrentValue(null);
			}
		}

		sb.append(System.lineSeparator());
		appendCollectEnd(sb);

		print(sb.toString());
	}

	protected static void appendCollectBegin(StringBuilder sb, Chart chart) {
		// TODO Add microseconds to the output.
		sb.append("BEGIN ");
		sb.append(chart.getType());
		sb.append('.');
		sb.append(chart.getId());
	}

	protected static void appendCollectDimension(StringBuilder sb, Dimension dim) {
		sb.append("SET ");
		sb.append(dim.getId());
		sb.append(" = ");
		sb.append(dim.getCurrentValue());
	}

	protected static void appendCollectEnd(StringBuilder sb) {
		sb.append("END");
	}

	/**
	 * Tell the caller to disable the plugin. This will prevent it from
	 * restarting the plugin.
	 */
	public static void disable() {
		print("DISABLE");
	}
}
