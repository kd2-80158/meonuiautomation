
package com.ui.listeners;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.testng.IResultMap;
import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.ui.base.BaseTest;
import com.ui.base.BaseTestPW;
import com.microsoft.playwright.Page;
import com.ui.utilities.EmailUtility;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.LoggerUtility;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener, IConfigurationListener, IExecutionListener {

	private static final String REPORT_PATH = "logs/report.html";

	private static final String EXTENT_TEST_KEY = "extentTest";

	private final Logger logger = LoggerUtility.getLogger(this.getClass());

	@Override
	public void onExecutionStart() {
		ExtentReporterUtility.setupSparkReporter(REPORT_PATH);
		logger.info("Extent reporting started: {}", ExtentReporterUtility.getReportPath());
	}

	@Override
	public void onExecutionFinish() {
		ExtentReporterUtility.flushReport();
		logger.info("Extent report written to: {}", ExtentReporterUtility.getReportPath());
	}

	// ==========================================
	// TEST CONTEXT INITIALIZATION
	// ==========================================

	@Override
	public void onStart(ITestContext context) {

		logger.info("Test Context Started: {}", context.getName());

		ExtentReporterUtility.setupSparkReporter(REPORT_PATH);

		logger.info("Extent Report Initialized: {}", ExtentReporterUtility.getReportPath());
	}

	// ==========================================
	// TEST START
	// ==========================================

	@Override
	public void onTestStart(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		Object existing = result.getAttribute(EXTENT_TEST_KEY);
		if (existing instanceof ExtentTest) {
			ExtentReporterUtility.activateTest((ExtentTest) existing);
			return;
		}

		String methodName = result.getMethod().getMethodName();

		String className = result.getTestClass().getName();

		String description = result.getMethod().getDescription();

		logger.info("Starting Test: {}#{}", className, methodName);

		String uniqueId = uniqueIdFor(result.getMethod());

		/*
		 * Every invocation receives its own ID.
		 *
		 * Important for: - DataProvider tests - Parallel execution - TestNG
		 * invocationCount - Retry executions
		 */
		String invocationId = UUID.randomUUID().toString();

		String displayName = result.getTestClass().getRealClass().getSimpleName() + " - " + methodName;

		ExtentTest test = ExtentReporterUtility.createInvocationTest(uniqueId, invocationId, displayName);

		/*
		 * Save the ExtentTest on this ITestResult. Completion callbacks will reuse it.
		 */
		result.setAttribute(EXTENT_TEST_KEY, test);

		ExtentReporterUtility.assignCategory(result.getTestClass().getRealClass().getSimpleName());

		String[] groups = result.getMethod().getGroups();

		if (groups != null && groups.length > 0) {
			ExtentReporterUtility.assignCategory(groups);
		}

		test.log(Status.INFO, "Test execution started");

		test.log(Status.INFO, "Test Class: " + className);

		test.log(Status.INFO, "Test Method: " + methodName);

		if (description != null && !description.trim().isEmpty()) {

			test.log(Status.INFO, "Description: " + description);
		}

		Object[] parameters = result.getParameters();

		if (parameters != null && parameters.length > 0) {

			test.log(Status.INFO, "Test Parameters Count: " + parameters.length);
		}

		logBrowserState(result, test, "Initial browser state");
	}

	// ==========================================
	// TEST SUCCESS
	// ==========================================

	@Override
	public void onTestSuccess(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.info("{} PASSED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);
			logBrowserState(result, test, "Final browser state");
			attachScreenshot(result, test, "Pass evidence");

			test.log(Status.PASS, testName + " PASSED");

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	// ==========================================
	// TEST FAILURE
	// ==========================================

	@Override
	public void onTestFailure(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.error("{} FAILED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);
			logBrowserState(result, test, "Browser state at failure");
			attachScreenshot(result, test, "Failure evidence");

			test.log(Status.FAIL, testName + " FAILED");

			Throwable throwable = result.getThrowable();

			if (throwable != null) {

				logger.error("Test Failure: " + testName, throwable);

				/*
				 * ExtentReports automatically formats exception and stack trace details.
				 */
				test.fail(throwable);
			}

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	// ==========================================
	// TEST SKIPPED
	// ==========================================

	@Override
	public void onTestSkipped(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.warn("{} SKIPPED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);
			logBrowserState(result, test, "Browser state when skipped");

			test.log(Status.SKIP, testName + " SKIPPED");

			Throwable throwable = result.getThrowable();

			if (throwable != null) {

				logger.warn("Skip Reason: {}", throwable.toString());

				test.skip(throwable);
			}

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	@Override
	public void onConfigurationFailure(ITestResult result) {
		String methodName = result != null && result.getMethod() != null
				? result.getMethod().getQualifiedName() : "Unknown configuration";
		ExtentTest test = ExtentReporterUtility.createInvocationTest("configuration#" + methodName,
				UUID.randomUUID().toString(), "Configuration failure - " + methodName);
		test.fail("Test configuration failed");
		if (result != null && result.getThrowable() != null) {
			test.fail(result.getThrowable());
		}
		logBrowserState(result, test, "Browser state at configuration failure");
		attachScreenshot(result, test, "Configuration failure evidence");
		ExtentReporterUtility.clearCurrentTest();
	}

	private void logBrowserState(ITestResult result, ExtentTest test, String label) {
		WebDriver webDriver = resolveDriver(result);
		if (webDriver != null) {
			try {
				if (webDriver instanceof RemoteWebDriver) {
					Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
					test.info("Browser: " + capabilities.getBrowserName() + " " + capabilities.getBrowserVersion());
				}
				test.info(label + " | URL: " + webDriver.getCurrentUrl());
				test.info("Page title: " + webDriver.getTitle());
			} catch (Exception e) {
				test.warning("Unable to read Selenium browser state: " + e.getMessage());
			}
			return;
		}

		Page page = resolvePage(result);
		if (page != null) {
			try {
				test.info("Browser: Playwright Chromium");
				test.info(label + " | URL: " + page.url());
				test.info("Page title: " + page.title());
			} catch (Exception e) {
				test.warning("Unable to read Playwright browser state: " + e.getMessage());
			}
			return;
		}
		test.info(label + ": browser session is not available");
	}

	private void attachScreenshot(ITestResult result, ExtentTest test, String title) {
		// API Manager pages can display live API keys and vendor credentials.
		// Never persist full-page screenshots of that suite in local or cloud reports.
		if (result != null && result.getTestClass() != null
				&& result.getTestClass().getName().startsWith("com.ui.cloud.apimanager.")) {
			test.info("Screenshot omitted for API Manager to avoid capturing credentials or API keys");
			return;
		}
		WebDriver webDriver = resolveDriver(result);
		try {
			Path screenshotDirectory = Paths.get(System.getProperty("user.dir"), "logs", "screenshots");
			Files.createDirectories(screenshotDirectory);
			String method = result != null && result.getMethod() != null
					? result.getMethod().getMethodName() : "configuration";
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
			Path screenshot = screenshotDirectory.resolve(sanitize(method) + "_" + timestamp + ".png");
			if (webDriver instanceof TakesScreenshot) {
				Files.write(screenshot, ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES));
			} else {
				Page page = resolvePage(result);
				if (page == null) {
					return;
				}
				page.screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));
			}
			test.addScreenCaptureFromPath(screenshot.toAbsolutePath().toString(), title);
			test.info("Screenshot saved: " + screenshot.toAbsolutePath());
		} catch (Exception e) {
			test.warning("Unable to capture screenshot: " + e.getMessage());
		}
	}

	private WebDriver resolveDriver(ITestResult result) {
		if (result == null) {
			return null;
		}
		Object instance = result.getInstance();
		if (instance instanceof BaseTest) {
			return ((BaseTest) instance).getWebDriver();
		}
		return null;
	}

	private Page resolvePage(ITestResult result) {
		if (result == null) {
			return null;
		}
		Object instance = result.getInstance();
		return instance instanceof BaseTestPW ? ((BaseTestPW) instance).getPage() : null;
	}

	private String sanitize(String value) {
		return value == null ? "test" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
	}

	// ==========================================
	// EXECUTION TIME
	// ==========================================

	private void logExecutionTime(ITestResult result, ExtentTest test) {

		long startTime = result.getStartMillis();

		long endTime = result.getEndMillis();

		if (startTime <= 0 || endTime < startTime) {
			return;
		}

		long duration = endTime - startTime;

		test.log(Status.INFO, "Test Execution Time: " + duration + " ms");
	}

	// ==========================================
	// EXTENT TEST RECOVERY
	// ==========================================

	private ExtentTest getOrCreateExtentTest(ITestResult result) {

		Object existing = result.getAttribute(EXTENT_TEST_KEY);

		if (existing instanceof ExtentTest) {

			return (ExtentTest) existing;
		}

		/*
		 * Fallback for skipped tests where onTestStart might not have been called.
		 *
		 * A fresh invocation identifier prevents accidental reuse of another test.
		 */
		String uniqueId = uniqueIdFor(result.getMethod());

		String invocationId = UUID.randomUUID().toString();

		String displayName = result.getMethod().getMethodName();

		ExtentTest test = ExtentReporterUtility.createInvocationTest(uniqueId, invocationId, displayName);

		result.setAttribute(EXTENT_TEST_KEY, test);

		return test;
	}

	// ==========================================
	// VALID TEST CHECK
	// ==========================================

	private boolean isValidTest(ITestResult result) {

		return result != null && result.getMethod() != null && result.getMethod().isTest();
	}

	// ==========================================
	// UNIQUE TEST IDENTIFIER
	// ==========================================

	private String uniqueIdFor(ITestNGMethod method) {

		if (method == null) {
			return "unknown#unknown";
		}

		String className = "unknown";

		try {

			if (method.getTestClass() != null && method.getTestClass().getName() != null) {

				className = method.getTestClass().getName();

			} else if (method.getRealClass() != null) {

				className = method.getRealClass().getName();
			}

		} catch (Exception e) {

			logger.warn("Unable to resolve test class: {}", e.getMessage());
		}

		return className + "#" + method.getMethodName();
	}

	// ==========================================
	// UNIQUE METHOD COUNTING
	// ==========================================

	private Set<String> uniqueTestIdsFrom(IResultMap results) {

		Set<String> ids = new HashSet<>();

		if (results == null) {
			return ids;
		}

		for (ITestResult result : results.getAllResults()) {

			if (isValidTest(result)) {

				ids.add(uniqueIdFor(result.getMethod()));
			}
		}

		return ids;
	}

	// ==========================================
	// EXECUTION SUMMARY
	// ==========================================

	private TestSummary buildSummary(ITestContext context) {

		Set<String> passed = uniqueTestIdsFrom(context.getPassedTests());

		Set<String> failed = uniqueTestIdsFrom(context.getFailedTests());

		Set<String> skipped = uniqueTestIdsFrom(context.getSkippedTests());

		/*
		 * Count unique methods with mutually exclusive status precedence:
		 *
		 * FAILED > SKIPPED > PASSED
		 *
		 * A method with any failed invocation is counted as failed.
		 */
		skipped.removeAll(failed);

		passed.removeAll(failed);
		passed.removeAll(skipped);

		Set<String> all = new HashSet<>();

		all.addAll(passed);
		all.addAll(failed);
		all.addAll(skipped);

		return new TestSummary(all.size(), passed.size(), failed.size(), skipped.size());
	}

	// ==========================================
	// TEST CONTEXT FINISH
	// ==========================================

	@Override
	public void onFinish(ITestContext context) {

		logger.info("Test Context Finished: {}", context.getName());

		TestSummary summary = buildSummary(context);

		String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), summary.total, summary.passed,
				summary.failed, summary.skipped);

		/*
		 * Flush before checking or attaching the HTML report.
		 */
		try {

			ExtentReporterUtility.flushReport();

			logger.info("Extent Report Flushed Successfully");

		} catch (Exception e) {

			logger.error("Unable to flush Extent Report", e);
		}

		String reportPath = ExtentReporterUtility.getReportPath();

		Path path = reportPath == null ? null : Paths.get(reportPath);

		boolean reportAvailable = false;

		if (path != null) {

			try {

				reportAvailable = Files.isRegularFile(path) && Files.size(path) > 0;

			} catch (Exception e) {

				logger.warn("Unable to verify report file", e);
			}
		}

		if (!reportAvailable) {

			logger.warn("Extent Report unavailable: {}", reportPath);
			if (Boolean.parseBoolean(System.getProperty("sendReportEmail", "false"))) {
				sendSummaryOnly(context, summary);
			}
			return;
		}

		if (!Boolean.parseBoolean(System.getProperty("sendReportEmail", "false"))) {
			logger.info("Report email disabled. Enable with -DsendReportEmail=true");
			return;
		}

		String body = buildEmailBody(context.getName(), summary, true, path.getFileName().toString());

		try {

			EmailUtility.sendReportEmail(subject, body, path.toAbsolutePath().toString(),
					EmailUtility.loadRecipientsFromPropsOrDefault());

			logger.info("Extent Report Email Sent: {}", path.toAbsolutePath());

		} catch (Exception e) {

			logger.error("Unable to send report attachment", e);

			sendSummaryOnly(context, summary);
		}
	}

	// ==========================================
	// EMAIL BODY
	// ==========================================

	private String buildEmailBody(String suiteName, TestSummary summary, boolean attached, String attachmentName) {

		StringBuilder body = new StringBuilder();

		body.append("<html><body>");

		body.append("<h2>UI Automation Execution Report</h2>");

		body.append("<p>Hi Team,</p>");

		body.append("<p>Automation execution has " + "been completed.</p>");

		body.append("<p><b>Test Context:</b> ").append(escapeHtml(suiteName)).append("</p>");

		body.append(
				"<table border='1' " + "cellpadding='10' " + "cellspacing='0' " + "style='border-collapse:collapse;'>");

		appendRow(body, "Total Tests", summary.total);

		appendRow(body, "Passed", summary.passed);

		appendRow(body, "Failed", summary.failed);

		appendRow(body, "Skipped", summary.skipped);

		body.append("</table>");

		double passPercentage = summary.total == 0 ? 0.0 : (summary.passed * 100.0) / summary.total;

		body.append("<p><b>Pass Percentage:</b> ");

		body.append(String.format(java.util.Locale.ROOT, "%.2f%%", passPercentage));

		body.append("</p>");

		body.append("<p><b>Environment:</b> ").append(escapeHtml(System.getProperty("env", "QA"))).append("</p>");

		if (attached) {

			body.append("<p>Detailed Extent Report Attached: " + "<b>");

			body.append(escapeHtml(attachmentName));

			body.append("</b></p>");

		} else {

			body.append("<p>Detailed HTML report " + "is not available.</p>");
		}

		body.append("<p>Regards,<br/>");
		body.append("UI Automation Framework</p>");

		body.append("</body></html>");

		return body.toString();
	}

	private void appendRow(StringBuilder body, String label, int value) {

		body.append("<tr><td>").append(escapeHtml(label)).append("</td><td>").append(value).append("</td></tr>");
	}

	private String escapeHtml(String value) {

		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	// ==========================================
	// SUMMARY-ONLY EMAIL FALLBACK
	// ==========================================

	private void sendSummaryOnly(ITestContext context, TestSummary summary) {

		String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), summary.total, summary.passed,
				summary.failed, summary.skipped);

		String body = buildEmailBody(context.getName(), summary, false, null);

		try {

			EmailUtility.sendReportEmail(subject, body, null, EmailUtility.loadRecipientsFromPropsOrDefault());

			logger.info("Summary-only email sent");

		} catch (Exception e) {

			logger.error("Unable to send summary email", e);
		}
	}

	// ==========================================
	// SUMMARY DATA HOLDER
	// ==========================================

	private static final class TestSummary {

		private final int total;
		private final int passed;
		private final int failed;
		private final int skipped;

		private TestSummary(int total, int passed, int failed, int skipped) {

			this.total = total;
			this.passed = passed;
			this.failed = failed;
			this.skipped = skipped;
		}
	}
}
