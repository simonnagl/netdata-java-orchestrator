// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testIsBlankNull() {
		assertTrue(StringUtils.isBlank(null));
	}

	@Test
	public void testIsBlankEmpty() {
		assertTrue(StringUtils.isBlank(""));
	}

	@Test
	public void testIsBlankWhitespace() {
		assertTrue(StringUtils.isBlank(" "));
	}

	@Test
	public void testIsBlankEmptyFilled() {
		assertFalse(StringUtils.isBlank("bob"));
	}

	@Test
	public void testIsBlankEmptyFilledWithWhitespace() {
		assertFalse(StringUtils.isBlank("  bob  "));
	}

}
