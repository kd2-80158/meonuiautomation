package com.ui.utilities;

import org.testng.asserts.SoftAssert;

public class SeveritySoftAssert extends SoftAssert {

	// =========================================================
	// ASSERT EQUALS - OBJECT
	// Supports JsonElement, POJO, List, Map, etc.
	// =========================================================

	public void assertEquals(Object actual, Object expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT EQUALS - STRING
	// =========================================================

	public void assertEquals(String actual, String expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT EQUALS - INT
	// =========================================================

	public void assertEquals(int actual, int expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT EQUALS - LONG
	// =========================================================

	public void assertEquals(long actual, long expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT EQUALS - DOUBLE
	// =========================================================

	public void assertEquals(double actual, double expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT EQUALS - BOOLEAN
	// =========================================================

	public void assertEquals(boolean actual, boolean expected, Severity severity, String message) {

		super.assertEquals(actual, expected, "[" + severity + "] " + message);
	}

	// =========================================================
	// SEVERITY FIRST VERSION
	// Example:
	// softAssert.assertEquals(
	// Severity.CRITICAL,
	// actual,
	// expected
	// );
	// =========================================================

	public void assertEquals(Severity severity, Object actual, Object expected) {

		super.assertEquals(actual, expected, "[" + severity + "] Expected value should match");
	}

	public void assertEquals(Severity severity, int actual, int expected) {

		super.assertEquals(actual, expected, "[" + severity + "] Expected value should match");
	}

	// =========================================================
	// ASSERT NOT NULL
	// =========================================================

	public void assertNotNull(Object actual, Severity severity, String message) {

		super.assertNotNull(actual, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT NULL
	// =========================================================

	public void assertNull(Object actual, Severity severity, String message) {

		super.assertNull(actual, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT TRUE
	// =========================================================

	public void assertTrue(boolean condition, Severity severity, String message) {

		super.assertTrue(condition, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT FALSE
	// =========================================================

	public void assertFalse(boolean condition, Severity severity, String message) {

		super.assertFalse(condition, "[" + severity + "] " + message);
	}

	// =========================================================
	// ASSERT FAIL
	// =========================================================

	public void assertFail(Severity severity, String message) {

		super.fail("[" + severity + "] " + message);
	}
}