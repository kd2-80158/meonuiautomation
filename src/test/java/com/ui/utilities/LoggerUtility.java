package com.ui.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtility {

	static {
		String logDirPath = System.getProperty("user.dir") + "/logs";
		java.io.File logDir = new java.io.File(logDirPath);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	}

	private static Logger logger;

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = null;
		if (logger == null) {
			logger = LogManager.getLogger(clazz);
		}
		return logger;
	}
}
