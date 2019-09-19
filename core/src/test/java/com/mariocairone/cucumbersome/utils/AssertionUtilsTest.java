package com.mariocairone.cucumbersome.utils;

import org.junit.Test;

import com.mariocairone.cucumbersome.utils.AssertionUtils;

public class AssertionUtilsTest {

	@Test
	public void testAssertion() throws Exception {
	
		
		AssertionUtils.compareCounts("at least", 3, 4);
		AssertionUtils.compareCounts("at most", 3, 2);
		AssertionUtils.compareCounts("more than", 3, 4);
		AssertionUtils.compareCounts("less than", 3, 2);
		
	}
	
}
