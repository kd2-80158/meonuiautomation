package com.ui.cloud.digilocker;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.base.BaseTest;
import com.ui.product.digilocker.pages.DigilockerReportsPage;
import com.ui.product.digilocker.pages.DigilockerReportsPage.ReportEntry;
import com.ui.product.digilocker.pages.DigilockerSuperAdminPage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;

/**
 * EILISYS Aadhaar report completeness, uniqueness, latency and backend
 * rendering checks.
 */
@Listeners(com.ui.listeners.TestListener.class)
public final class DigilockerAadhaarReportsTest extends BaseTest {
	private static final String COMPANY = "EILISYS TECHNOLOGIES PRIVATE LTD";
	private static final String SEARCH = "EILISYS TECHNOLOGIES";
	private DigilockerReportsPage reports;
	private JourneyEvidence evidence;
	private JourneyEvidence setupEvidence;

	@BeforeClass(alwaysRun = true)
	public void signInAndVisitTenant() {
		DigilockerSuperAdminPage superAdmin = new DigilockerSuperAdminPage(driver);
		superAdmin.open();
		setupEvidence = new JourneyEvidence(driver, "digilocker_eilisys_signin_and_tenant_selection");
		setupEvidence.capture("DigiLocker superadmin opened");
		String sourceWindow = driver.getWindowHandle();
		if (superAdmin.googleSignInVisible()) {
			Set<String> before = new HashSet<>(driver.getWindowHandles());
			superAdmin.clickGoogleSignIn();
			setupEvidence.capture("Google sign-in opened");
			String googleWindow = new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
				for (String handle : d.getWindowHandles())
					if (!before.contains(handle))
						return handle;
				return d.getCurrentUrl().contains("accounts.google.com") ? d.getWindowHandle() : null;
			});
			driver.switchTo().window(googleWindow);
			LoginPage_REKYCSuperadmin google = new LoginPage_REKYCSuperadmin(driver);
			google.waitForEmailPage().writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
					.clickOnButton("nextbtn").waitForPasswordPage()
					.writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
					.clickOnButton("nextbtn");
			setupEvidence.capture("Google sign-in submitted");
			driver.switchTo().window(sourceWindow);
		}
		try {
			Thread.sleep(7000L);
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while allowing DigiLocker sign-in to settle", interrupted);
		}
		try {
			new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
				for (String handle : d.getWindowHandles()) {
					d.switchTo().window(handle);
					if (d.findElements(By.id("search__company")).stream().anyMatch(WebElement::isDisplayed))
						return true;
				}
				return false;
			});
		} catch (org.openqa.selenium.TimeoutException signInFailure) {
			List<String> locations = new ArrayList<>();
			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				URI location = URI.create(driver.getCurrentUrl());
				locations.add(location.getHost() + location.getPath());
			}
			throw new AssertionError(
					"Google sign-in did not reach DigiLocker company directory. Window paths: " + locations,
					signInFailure);
		}
		superAdmin.waitForCompanyDirectory();
		setupEvidence.capture("Company directory ready");
		superAdmin.searchCompany(SEARCH);
		setupEvidence.capture("EILISYS search results");
		Set<String> beforeVisit = new HashSet<>(driver.getWindowHandles());
		superAdmin.visitCompany(COMPANY);
		new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
			for (String handle : d.getWindowHandles()) {
				if (!beforeVisit.contains(handle)) {
					d.switchTo().window(handle);
					return d.getCurrentUrl().contains("/dashboard");
				}
			}
			return d.getCurrentUrl().contains("/dashboard");
		});
		new WebDriverWait(driver, Duration.ofSeconds(30))
				.until(d -> d.findElement(By.tagName("body")).getText().contains(COMPANY));
		setupEvidence.capture("EILISYS dashboard opened");
		reports = new DigilockerReportsPage(driver);
	}

	@Test(description = "DL_AAD_01 - EILISYS admin opens Retrieval reports", groups = { "critical", "digilocker",
			"read" })
	public void reportsOpenForCorrectTenant() {
		evidence = new JourneyEvidence(driver, "digilocker_eilisys_reports_navigation");
		evidence.capture("EILISYS dashboard");
		reports.openFromSidebar();
		evidence.capture("Retrieval reports opened");
		Assert.assertTrue(driver.getCurrentUrl().endsWith("/reports"));
		Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains(COMPANY));
	}

	@Test(description = "DL_AAD_02 - Recent Aadhaar report IDs are unique", groups = { "critical", "digilocker",
			"read" })
	public void recentAadhaarTransactionsHaveUniqueIds() {
		reports.openFromSidebar();
		evidence = new JourneyEvidence(driver, "digilocker_aadhaar_unique_report_rows");
		evidence.capture("Recent Aadhaar report rows");
		Set<String> ids = new HashSet<>();
		int checked = 0;
		for (ReportEntry entry : reports.visibleEntries()) {
			if (!entry.document.toLowerCase().contains("aadhaar"))
				continue;
			Assert.assertFalse(entry.transactionId.isBlank(), "Aadhaar row has no transaction ID");
			Assert.assertTrue(ids.add(entry.transactionId), "Duplicate Aadhaar report row: " + entry.transactionId);
			checked++;
		}
		Assert.assertTrue(checked > 0, "No Aadhaar rows on the latest report page");
		ExtentReporterUtility.info("Checked " + checked + " recent Aadhaar rows for duplicate transaction IDs");
	}

	@Test(description = "DL_AAD_03 - Report rendering APIs return successful responses", groups = { "critical",
			"digilocker", "network", "read" })
	public void reportBackendResponsesAreHealthy() throws IOException {
		if (!(driver instanceof ChromeDriver))
			throw new SkipException("Use Chrome for network evidence");
		driver.manage().logs().get(LogType.PERFORMANCE);
		driver.manage().logs().get(LogType.BROWSER);
		reports.openFromSidebar();
		reports.refresh();
		evidence = new JourneyEvidence(driver, "digilocker_reports_backend");
		evidence.capture("Reports rendered after backend requests");
		Assert.assertFalse(reports.visibleEntries().isEmpty(), "Reports rendered with no transaction rows");
		List<String> responses = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
			for (LogEntry entry : d.manage().logs().get(LogType.PERFORMANCE)) {
				captureReportResponse(entry, responses, errors);
			}
			return !responses.isEmpty();
		});
		for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
			if (entry.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue())
				errors.add("Console error (details redacted)");
		}
		Path file = Paths.get(System.getProperty("user.dir"), "logs", "evidence",
				"digilocker-report-api-" + Instant.now().toEpochMilli() + ".txt");
		Files.createDirectories(file.getParent());
		Files.write(file, responses);
		ExtentReporterUtility.info("Sanitized report API evidence: " + file.toAbsolutePath());
		Assert.assertTrue(errors.isEmpty(), "Report backend or console errors: " + errors);
	}

	private void captureReportResponse(LogEntry entry, List<String> responses, List<String> errors) {
			try {
				JsonObject event = JsonParser.parseString(entry.getMessage()).getAsJsonObject()
						.getAsJsonObject("message");
				if (!"Network.responseReceived".equals(event.get("method").getAsString()))
					return;
				JsonObject params = event.getAsJsonObject("params");
				String type = params.get("type").getAsString();
				if (!"XHR".equals(type) && !"Fetch".equals(type))
					return;
				JsonObject response = params.getAsJsonObject("response");
				URI uri = URI.create(response.get("url").getAsString());
				if (uri.getHost() == null || !uri.getHost().endsWith("meon.co.in"))
					return;
				int status = response.get("status").getAsInt();
				String safe = status + " " + uri.getHost() + uri.getPath();
				responses.add(safe);
				if (status >= 400)
					errors.add(safe);
			} catch (RuntimeException unrelated) {
				/* Ignore browser events that are not response metadata. */ }
	}

	@Test(description = "DL_AAD_05 - Refresh reports retains visible transaction data", groups = { "critical", "digilocker", "read" })
	public void refreshReportsKeepsTransactionRows() {
		reports.openFresh();
		evidence = new JourneyEvidence(driver, "digilocker_reports_refresh");
		evidence.capture("Reports before refresh");
		reports.refresh();
		evidence.capture("Reports after refresh");
		Assert.assertFalse(reports.visibleEntries().isEmpty(), "Refresh removed all report rows");
	}

	@Test(description = "DL_AAD_06 - Search and reset a transaction ID", groups = { "critical", "digilocker", "read" })
	public void searchAndResetTransactionId() {
		reports.openFresh();
		String id = reports.visibleEntries().get(0).transactionId;
		evidence = new JourneyEvidence(driver, "digilocker_reports_search_reset");
		evidence.capture("Reports before transaction search");
		reports.searchFor(id);
		evidence.capture("Exact transaction search applied");
		Assert.assertTrue(reports.visibleEntries().stream().allMatch(row -> row.transactionId.equals(id)));
		reports.resetFilters();
		evidence.capture("Search filters reset");
		Assert.assertFalse(reports.visibleEntries().isEmpty());
	}

	@Test(description = "DL_AAD_07 - Complete status filter returns only completed records", groups = { "critical", "digilocker", "read" })
	public void completeStatusFilter() {
		reports.openFresh();
		evidence = new JourneyEvidence(driver, "digilocker_reports_status_filter");
		evidence.capture("Reports before status filter");
		reports.filterStatus("Complete");
		evidence.capture("Complete status filter applied");
		Assert.assertTrue(reports.visibleEntries().stream().allMatch(row -> row.status.equalsIgnoreCase("Complete")));
		reports.resetFilters();
		evidence.capture("Status filter reset");
	}

	@Test(description = "DL_AAD_08 - Next and previous report pages show different rows", groups = { "critical", "digilocker", "read" })
	public void reportPaginationNavigatesBothWays() {
		reports.openFresh();
		evidence = new JourneyEvidence(driver, "digilocker_reports_pagination");
		String first = reports.visibleEntries().get(0).transactionId;
		evidence.capture("First report page");
		String second = reports.nextPage();
		evidence.capture("Second report page");
		Assert.assertNotEquals(second, first);
		reports.previousPage();
		evidence.capture("Returned to first report page");
		Assert.assertEquals(reports.visibleEntries().get(0).transactionId, first);
	}

	@Test(description = "DL_AAD_09 - View documents displays transaction-specific Aadhaar files", groups = { "critical", "digilocker", "read" })
	public void viewDocumentsShowsCorrectTransactionFiles() throws IOException {
		reports.openFresh();
		evidence = new JourneyEvidence(driver, "digilocker_reports_view_documents");
		evidence.capture("Completed transaction in Reports");
		String id = reports.openFirstCompletedDocuments();
		evidence.capture("Transaction document details opened");
		List<String> documentTypes = reports.availableDocumentTypes();
		Assert.assertFalse(documentTypes.isEmpty(), "No fetched document tabs were displayed");
		for (String type : documentTypes) {
			reports.selectDocumentType(type);
			evidence.capture(type + " fetched document details");
			Assert.assertTrue(reports.documentDialogShowsFiles(id, type),
					"Document panel must show the matching transaction, document information, and file links for " + type);
			List<String> actions = reports.availableFileActions();
			for (String action : actions) {
				int status = reports.clickFileAndVerifyResponse(action, id);
				Assert.assertEquals(status, 200, type + " " + action + " file response");
				evidence.capture(type + " " + action + " link verified");
			}
		}
		reports.closeDocuments();
		evidence.capture("Transaction document details closed");
	}

	@Test(description = "DL_AAD_04 - Portal transaction appears once within reporting SLA", groups = { "critical",
			"digilocker", "correlation" })
	public void portalTransactionIsPromptAndUnique() throws InterruptedException {
		String id = System.getProperty("digilocker.transactionId", "").trim();
		String started = System.getProperty("digilocker.startedAtEpochMs", "").trim();
		if (id.isEmpty() || started.isEmpty())
			throw new SkipException(
					"Supply the portal API hit's transaction ID and start epoch via -Ddigilocker.transactionId and -Ddigilocker.startedAtEpochMs");
		long startedAt = Long.parseLong(started);
		long maxWaitMs = Long.getLong("digilocker.maxReportWaitMs", 900000L);
		long slaMs = Long.getLong("digilocker.reportSlaMs", 120000L);
		long deadline = System.currentTimeMillis() + maxWaitMs;
		evidence = new JourneyEvidence(driver, "digilocker_portal_transaction_reconciliation");
		while (System.currentTimeMillis() <= deadline) {
			driver.get("https://digilocker.meon.co.in/reports");
			List<ReportEntry> matches = reports.findOnRecentPages(id, 5);
			if (!matches.isEmpty()) {
				evidence.capture("Portal transaction found in reports");
				Assert.assertEquals(matches.size(), 1, "One portal API hit produced duplicate report rows");
				Assert.assertTrue(matches.get(0).document.toLowerCase().contains("aadhaar"),
						"Matched report row is not Aadhaar");
				long delay = System.currentTimeMillis() - startedAt;
				ExtentReporterUtility
						.info("Report appearance delay: " + delay + " ms; status=" + matches.get(0).status);
				Assert.assertTrue(delay <= slaMs, "Report appeared after configured SLA: " + delay + " ms");
				return;
			}
			Thread.sleep(Math.min(30000L, Math.max(1L, deadline - System.currentTimeMillis())));
		}
		evidence.capture("Portal transaction missing after reporting wait");
		Assert.fail(
				"Portal transaction was not found in the five most recent report pages within " + maxWaitMs + " ms");
	}

	@AfterMethod(alwaysRun = true)
	public void finishEvidence() {
		if (evidence != null) {
			try {
				evidence.capture("Final browser state");
			} catch (RuntimeException unavailable) {
				ExtentReporterUtility.warning("Final screenshot unavailable");
			}
			evidence.finish();
			evidence = null;
		}
	}

	@AfterClass(alwaysRun = true)
	public void closeBrowser() {
		if (setupEvidence != null) {
			setupEvidence.finish();
			setupEvidence = null;
		}
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
}
