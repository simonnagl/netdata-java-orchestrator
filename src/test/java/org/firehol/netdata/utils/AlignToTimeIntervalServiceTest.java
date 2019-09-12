// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import org.firehol.netdata.testutils.ReflectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

public class AlignToTimeIntervalServiceTest {

	@Rule
	public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

	@Test
	public void testAlignToTimeIntervalService()
			throws NoSuchFieldException, IllegalAccessException, SecurityException {

		// Test
		AlignToTimeIntervalService service = new AlignToTimeIntervalService(100, TimeUnit.NANOSECONDS);

		// Verify
		assertEquals(100L, ReflectionUtils.getPrivateField(service, "intervalInNSec"));
	}

	@Test
	public void testAlignToTimeIntervalServiceSeconds()
			throws NoSuchFieldException, IllegalAccessException, SecurityException {

		// Test
		AlignToTimeIntervalService service = new AlignToTimeIntervalService(100, TimeUnit.SECONDS);

		// Verify
		assertEquals(TimeUnit.SECONDS.toNanos(100), ReflectionUtils.getPrivateField(service, "intervalInNSec"));
	}

	@Test(timeout = 2000)
	// Just test it does not fail.
	public void testAlignToNextInterval() throws InterruptedException {
		// Build object under test
		AlignToTimeIntervalService service = new AlignToTimeIntervalService(UnitConversion.NANO_PER_PLAIN / 100,
				TimeUnit.NANOSECONDS);

		for (int i = 0; i < 100; i++) {
			service.alignToNextInterval();
		}
	}

}
