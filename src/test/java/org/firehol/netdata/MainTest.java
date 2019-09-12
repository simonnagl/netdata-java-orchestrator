// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class MainTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();

	@Rule
	public final SystemErrRule systemerrRule = new SystemErrRule().muteForSuccessfulTests();

	@Test
	public void testGetUpdateEveryInSecondsFomCommandLineFailFast() {
		final String[] args = { "3" };

		int updateEvery = Main.getUpdateEveryInSecondsFomCommandLineFailFast(args);

		assertEquals(3, updateEvery);
	}

	@Test
	public void testGetUpdateEveryInSecondsFomCommandLineFailFastFailToMany() {
		exit.expectSystemExitWithStatus(1);
		final String[] args = { "to", "many" };

		exit.expectSystemExitWithStatus(1);

		Main.getUpdateEveryInSecondsFomCommandLineFailFast(args);
	}

	@Test
	public void testGetUpdateEveryInSecondsFomCommandLineFailFastFailNoNumber() {
		exit.expectSystemExitWithStatus(1);
		final String[] args = { "String" };

		exit.expectSystemExitWithStatus(1);

		Main.getUpdateEveryInSecondsFomCommandLineFailFast(args);
	}

	@Test
	public void testExit() throws Exception {
		exit.expectSystemExitWithStatus(1);

		Main.exit("Test");

		assertEquals("DISABLE", systemOutRule.getLog());
	}
}
