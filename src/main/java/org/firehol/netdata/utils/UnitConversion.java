// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import java.util.concurrent.TimeUnit;

public abstract class UnitConversion {
	public static long NANO_PER_PLAIN = TimeUnit.SECONDS.toNanos(1);

	public static long MILI_PER_PLAIN = TimeUnit.SECONDS.toMillis(1);

	public static long MILI_PER_NANO = TimeUnit.MILLISECONDS.toNanos(1);
}
