/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
