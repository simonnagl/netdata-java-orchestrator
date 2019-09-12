// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

public abstract class ClockService {

	/**
	 * Get the current Monotonic time.
	 * 
	 * This clock is not affected by discontinuous jumps in the system time.
	 * 
	 * @return Monotonic Clock in Microseconds
	 */
	public static long nowMonotonicNSec() {
		return System.nanoTime();
	}
}
