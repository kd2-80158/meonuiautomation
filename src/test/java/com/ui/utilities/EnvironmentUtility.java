package com.ui.utilities;

public class EnvironmentUtility {

	private EnvironmentUtility() {
	}

	public static String getRequiredVariable(String name) {
		String value = System.getenv(name);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("Required environment variable is not configured: " + name);
		}
		return value;
	}
}