package com.ui.listeners;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.ui.base.BaseTest;
import com.ui.utilities.BrowserUtility;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.LoggerUtility;

public class TestListener implements ITestListener {

	Logger logger = LoggerUtility.getLogger(this.getClass()); // Logger for logging test details
	ExtentReports extentReports; // ExtentReports object to generate the HTML report

	// This method runs before each test starts
	public void onTestStart(ITestResult result) {
		logger.info(result.getMethod().getMethodName());
		logger.info(result.getMethod().getDescription());
		logger.info(Arrays.toString(result.getMethod().getGroups()));

		ExtentReporterUtility.createExtentTest(result.getMethod().getMethodName());
	}

	public void onTestSuccess(ITestResult result) {

		logger.info(result.getMethod().getMethodName() + " " + "PASSED");
		ExtentReporterUtility.getTest().log(Status.PASS, result.getMethod().getMethodName() + " " + "PASSED");
	}

	@Override
	public void onTestFailure(ITestResult result) {

		logger.error(result.getMethod().getMethodName() + " FAILED");
		logger.error(result.getThrowable());

		ExtentReporterUtility.getTest().log(Status.FAIL, result.getMethod().getMethodName() + " FAILED");
		ExtentReporterUtility.getTest().log(Status.FAIL, result.getThrowable());

		Object testInstance = result.getInstance();

		try {
			// ================= SELENIUM =================
			if (testInstance instanceof BaseTest) {

				BaseTest baseTest = (BaseTest) testInstance;
				BrowserUtility browserUtility = baseTest.getInstance();

				logger.info("Taking Selenium screenshot");
				String screenshotPath = BrowserUtility.takeScreenShot("SELENIUM_FAIL");

				ExtentReporterUtility.getTest().addScreenCaptureFromPath(screenshotPath);
			}

			// ================= PLAYWRIGHT =================
			else if (testInstance instanceof com.ui.base.BaseTestPW) {

				com.ui.base.BaseTestPW pwTest = (com.ui.base.BaseTestPW) testInstance;

				logger.info("Taking Playwright screenshot");

				String screenshotPath = "./screenshots/PLAYWRIGHT_FAIL.png";

				pwTest.getPage().screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
						.setPath(java.nio.file.Paths.get(screenshotPath)).setFullPage(true));

				ExtentReporterUtility.getTest().addScreenCaptureFromPath(screenshotPath);
			}

			else {
				logger.warn("Unknown test type – no screenshot captured");
			}

		} catch (Exception e) {
			logger.error("Error while capturing screenshot", e);
		}
	}

	public void onTestSkipped(ITestResult result) {
		logger.warn(result.getMethod().getMethodName() + " " + "SKIPPED");
		ExtentReporterUtility.getTest().log(Status.SKIP, result.getMethod().getMethodName() + " " + "SKIPPED");
	}

	public void onStart(ITestContext context) {
		logger.info("Test Suite Started..");
		ExtentReporterUtility.setupSparkReporter("report.html");
	}

	public void onFinish(ITestContext context) {
		logger.info("Test Suite Completed!!");
		ExtentReporterUtility.flushReport();
	}
}
