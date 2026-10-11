package com.ui.utilities;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporterUtility {
		
		private static ExtentReports extentReports;
		static ExtentSparkReporter extentSparkReporter;
		
		private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
		
		public static void setupSparkReporter(String reportName)
		{
		    // Define the directory path
		    String dirPath = System.getProperty("user.dir") + File.separator + "screenshots";
		    
		    // Create directory if it doesn't exist
		    new File(dirPath).mkdirs();
		    
		    // Define full file path
		    String filePath = dirPath + File.separator + reportName;
		    
		    try {
		        extentSparkReporter = new ExtentSparkReporter(filePath);
		        extentReports = new ExtentReports();
		        extentReports.attachReporter(extentSparkReporter);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		public static void createExtentTest(String testName)
		{
			ExtentTest test = extentReports.createTest(testName);
			extentTest.set(test);
		}
		public static ExtentTest getTest()
		{
			return extentTest.get();
		}
		public static void flushReport()
		{
			extentReports.flush();
		}

	}
