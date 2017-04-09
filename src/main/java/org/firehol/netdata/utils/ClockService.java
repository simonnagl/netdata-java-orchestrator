package org.firehol.netdata.utils;

/**
 * Proxy to access differen system clocs.
 * 
 * @author simonnagl
 *
 */
public abstract class ClockService {

	/**
	 * Get the current Monotonic time.
	 * 
	 * This clock is not affected by
	 * discontinuous jumps in the system time.
	 * 
	 * @return Monotonic Clock in Microseconds
	 */
	public static long nowMonotonicNSec() {
		return System.nanoTime();
	}
}
