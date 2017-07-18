package org.firehol.netdata;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class MainTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Test
	public void testExit() throws Exception {

		// Expect
		exit.expectSystemExitWithStatus(1);

		// Test
		Main.exit("Test");

		// Verify
		assertEquals("DISABLE", systemOutRule.getLog());
	}

	@Test
	public void testGetUpdateEveryFailFast() {

		// Test
		long result = Main.getUpdateEveryFailFast("1");

		// Verify
		assertEquals(1, result);
	}

	@Test
	public void testGetUpdateEveryFailFastFailure() {
		// Expect
		exit.expectSystemExitWithStatus(1);

		// Test
		long result = Main.getUpdateEveryFailFast("wrong input");
		
		// Verify
		assertEquals("DISABLE", systemOutRule.getLog());
		assertEquals(1, result);
	}

}
