package com.mariocairone.cucumbersome.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssertionUtils {

	
	public static void compareCounts(String comparison, int expected, int actual) {
		switch (comparison) {
		case "at least":
			assertTrue("Expected to have at least " + expected + " but has " + actual, actual >= expected);
			break;
		case "at most":
			assertTrue("Expected to have at most " + expected + " but has " + actual, actual <= expected);
			break;
		case "more than":
			assertTrue("Expected to have more than " + expected + " but has " + actual, actual > expected);
			break;
		case "less than":
			assertTrue("Expected to have less than " + expected + " but has " + actual, actual < expected);
			break;
		default:
			assertEquals("Expected to have " + expected + " but has " + actual, expected, actual);
		}
	}
}
