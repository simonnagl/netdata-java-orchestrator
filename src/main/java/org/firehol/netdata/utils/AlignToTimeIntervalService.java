// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AlignToTimeIntervalService {
	private Logger log = Logger.getLogger("org.firehol.netdata.utils.aligntotimeintervalservice");

	private final long intervalInNSec;
	private long lastTimestamp;

	public AlignToTimeIntervalService(long intervalInNSec, final TimeUnit timeUnit) {
		this.intervalInNSec = timeUnit.toNanos(intervalInNSec);
		this.lastTimestamp = ClockService.nowMonotonicNSec();
	}

	public long alignToNextInterval() {
		long now = ClockService.nowMonotonicNSec();
		long next = now - (now % intervalInNSec) + intervalInNSec;

		while (now < next) {
			try {
				long delta = next - now;

				Thread.sleep(delta / UnitConversion.MILI_PER_NANO,
						Math.toIntExact(delta % UnitConversion.MILI_PER_NANO));
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
