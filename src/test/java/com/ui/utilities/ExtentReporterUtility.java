
package com.ui.utilities;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class ExtentReporterUtility {

	private static final String DEFAULT_PATH = "logs/report.html";

	private static final int MAX_LOG_LENGTH = 30000;

	private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

	private static final ThreadLocal<Instant> startTime = new ThreadLocal<>();

	private static final Map<String, ExtentTest> createdTests = new ConcurrentHashMap<>();

	private static volatile ExtentReports extent;
	private static volatile String reportPath;
	private static volatile boolean shutdownHookRegistered;

	private ExtentReporterUtility() {
	}

	// ==========================================
	// REPORT INITIALIZATION
	// ==========================================

	public static synchronized void setupSparkReporter(String path) {

		if (extent != null) {
			return;
		}

		String configuredPath = path == null || path.trim().isEmpty() ? DEFAULT_PATH : path.trim();
		Path configured = Paths.get(configuredPath);
		Path resolved = configured.isAbsolute() ? configured
				: Paths.get(System.getProperty("user.dir")).resolve(configured);
		String targetPath = resolved.normalize().toAbsolutePath().toString();

		File reportFile = new File(targetPath);
		File parent = reportFile.getAbsoluteFile().getParentFile();

		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Cannot create report directory: " + parent);
		}

		ExtentSparkReporter spark = new ExtentSparkReporter(targetPath);

		spark.config().setDocumentTitle("UI Automation Test Results");

		spark.config().setReportName("UI Automation Execution Report");

		spark.config().setTheme(Theme.DARK);

		spark.config().setTimeStampFormat("dd-MM-yyyy HH:mm:ss");

		ExtentReports reports = new ExtentReports();

		reports.attachReporter(spark);

		reports.setSystemInfo("Automation Framework", "Java | TestNG | Selenium");

		reports.setSystemInfo("Environment", System.getProperty("env", "QA"));

		reports.setSystemInfo("Operating System", System.getProperty("os.name", "Unknown"));

		reports.setSystemInfo("Java Version", System.getProperty("java.version", "Unknown"));

		reports.setSystemInfo("Execution Mode", System.getProperty("executionMode", "Local"));
		reports.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
		reports.setSystemInfo("Headless", System.getProperty("isHeadless", "false"));

		reportPath = targetPath;
		extent = reports;

		if (!shutdownHookRegistered) {
			Runtime.getRuntime().addShutdownHook(new Thread(ExtentReporterUtility::flushReport,
					"extent-report-shutdown-hook"));
			shutdownHookRegistered = true;
		}
	}

	// ==========================================
	// TEST CREATION
	// ==========================================

	public static ExtentTest createExtentTest(String uniqueId, String displayName) {

		Objects.requireNonNull(uniqueId, "uniqueId must not be null");

		if (extent == null) {
			setupSparkReporter(DEFAULT_PATH);
		}

		synchronized (createdTests) {

			ExtentTest test = createdTests.get(uniqueId);

			if (test == null) {

				test = extent.createTest(displayName == null ? uniqueId : displayName);

				createdTests.put(uniqueId, test);
			}

			currentTest.set(test);
			startTime.set(Instant.now());

			return test;
		}
	}

	public static ExtentTest createExtentTest(String displayName) {

		String uid = displayName == null ? "unnamed-test" : displayName;

		return createExtentTest(uid, displayName);
	}

	/**
	 * Creates a separate entry for an invocation.
	 *
	 * Use a stable invocation identifier for DataProvider-driven and repeated
	 * tests.
	 */
	public static ExtentTest createInvocationTest(String methodId, String invocationId, String displayName) {

		return createExtentTest(methodId + "#" + invocationId, displayName);
	}

	public static ExtentTest getTest() {
		return currentTest.get();
	}

	public static void activateTest(ExtentTest test) {
		if (test != null) {
			currentTest.set(test);
			startTime.set(Instant.now());
		}
	}

	// ==========================================
	// COMMON LOGGING
	// ==========================================

	private static void log(Status status, String message) {

		ExtentTest test = currentTest.get();

		if (test != null) {
			test.log(status, message == null ? "" : message);
		}
	}

	public static void info(String message) {
		log(Status.INFO, message);
	}

	public static void pass(String message) {
		log(Status.PASS, message);
	}

	public static void fail(String message) {
		log(Status.FAIL, message);
	}

	public static void skip(String message) {
		log(Status.SKIP, message);
	}

	public static void warning(String message) {
		log(Status.WARNING, message);
	}

	public static void fail(Throwable throwable) {

		ExtentTest test = getTest();

		if (test != null && throwable != null) {
			test.fail(throwable);
		}
	}

	// ==========================================
	// TEST CATEGORIZATION
	// ==========================================

	public static void assignCategory(String... categories) {

		ExtentTest test = getTest();

		if (test != null && categories != null && categories.length > 0) {

			test.assignCategory(categories);
		}
	}

	public static void assignAuthor(String author) {

		ExtentTest test = getTest();

		if (test != null && author != null) {
			test.assignAuthor(author);
		}
	}

	// ==========================================
	// STRUCTURED JSON REPORTING
	// ==========================================

	public static void logJson(String title, String json) {

		ExtentTest test = getTest();

		if (test == null) {
			return;
		}

		test.info(title == null ? "JSON" : title);

		if (json == null) {
			test.info("No JSON content");
			return;
		}

		String formatted = json;

		try {
			formatted = new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(json));
		} catch (Exception ignored) {
			// Log non-JSON content as plain text.
		}

		if (formatted.length() > MAX_LOG_LENGTH) {
			formatted = formatted.substring(0, MAX_LOG_LENGTH) + "\n... [Content truncated]";
		}

		test.info(MarkupHelper.createCodeBlock(formatted));
	}

	// ==========================================
	// API REQUEST / RESPONSE REPORTING
	// ==========================================

	public static void logApiRequest(String method, String url, String requestBody) {

		info("HTTP Method: " + method);
		info("Request URL: " + url);

		if (requestBody != null && !requestBody.isEmpty()) {

			logJson("Request Payload", requestBody);
		}
	}

	public static void logApiResponse(int statusCode, long responseTimeMs, String responseBody) {

		info("HTTP Status Code: " + statusCode);

		info("API Response Time: " + responseTimeMs + " ms");

		if (responseBody != null && !responseBody.isEmpty()) {

			logJson("Response Body", responseBody);
		}
	}

	// ==========================================
	// EXPECTED VS ACTUAL VALIDATION
	// ==========================================

	public static void logComparison(String field, Object expected, Object actual, boolean matched) {

		String message = "Field: " + field + " | Expected: " + expected + " | Actual: " + actual;

		if (matched) {
			pass(message);
		} else {
			fail(message);
		}
	}

	// ==========================================
	// EXECUTION DURATION
	// ==========================================

	public static void logExecutionTime() {

		Instant start = startTime.get();

		if (start != null) {

			long duration = Duration.between(start, Instant.now()).toMillis();

			info("Test Execution Time: " + duration + " ms");
		}
	}

	// ==========================================
	// SCREENSHOT ATTACHMENT
	// ==========================================

	public static void addImageToReport(String title, String imagePath) {

		ExtentTest test = getTest();

		if (test == null) {
			return;
		}

		try {

			if (imagePath == null || imagePath.trim().isEmpty()) {

				test.warning("Screenshot path is empty");
				return;
			}

			File file = new File(imagePath);

			if (!file.isFile()) {

				test.warning("Screenshot not found: " + imagePath);
				return;
			}

			test.addScreenCaptureFromPath(file.getAbsolutePath(), title);

		} catch (Exception e) {

			test.warning("Unable to attach image: " + e.getMessage());
		}
	}

	// ==========================================
	// REPORT MANAGEMENT
	// ==========================================

	public static synchronized void flushReport() {

		if (extent != null) {
			extent.flush();
		}
	}

	public static void clearCurrentTest() {

		currentTest.remove();
		startTime.remove();
	}

	public static String getReportPath() {
		return reportPath;
	}

	public static String getSimpleSummary(String suiteName, int total, int passed, int failed, int skipped) {

		return String.format("%s - Tests: total=%d, passed=%d, " + "failed=%d, skipped=%d",
				suiteName == null ? "Tests" : suiteName, total, passed, failed, skipped);
	}
}
