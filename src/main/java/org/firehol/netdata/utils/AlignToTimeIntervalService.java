package org.firehol.netdata.utils;

import java.util.logging.Logger;

/**
 * Service to align execution of a command to an specified time interval.
 * 
 * @author Simon Nagl
 */
public class AlignToTimeIntervalService {
	private Logger log = Logger.getLogger("org.firehol.netdata.utils.aligntotimeintervalservice");

	private long intervalInNSec;
	private long lastTimestamp;

	public AlignToTimeIntervalService(long intervalInNSec) {
		this.intervalInNSec = intervalInNSec;
	}

	public long alignToNextInterval() {
		long now = ClockService.nowMonotonicNSec();
		long next = now - (now % intervalInNSec) + intervalInNSec;

		while (now < next) {
			try {
				Thread.sleep((next - now) / UnitConversion.MILI_PER_NANO,
						(int) (next - now) % UnitConversion.MILI_PER_NANO);
			} catch (InterruptedException e) {
				log.warning("Interrupted while waiting for next tick.");
				// We try again here. The worst might happen is a busy wait
				// instead of sleeping.
			}
			now = ClockService.nowMonotonicNSec();
		}

		long delta = now - lastTimestamp;
		lastTimestamp = now;
		if (delta / intervalInNSec > 1) {
			log.warning("At least one tick missed since last call of alignToNextInterval()");
		}
		return delta;
	}

}
