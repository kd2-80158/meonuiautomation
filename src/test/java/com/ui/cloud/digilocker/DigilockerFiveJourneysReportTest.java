package com.ui.cloud.digilocker;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.digilocker.pages.DigilockerReportsPage;
import com.ui.product.digilocker.pages.DigilockerReportsPage.ReportEntry;
import com.ui.product.digilocker.pages.DigilockerSuperAdminPage;
import com.ui.product.digilocker.pages.DigilockerTestPage;
import com.ui.product.digilocker.pages.DigilockerWebhookConsentPage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;

/** Five real portal journeys reconciled one-to-one with Retrieval reports. */
@Listeners(com.ui.listeners.TestListener.class)
public final class DigilockerFiveJourneysReportTest extends BaseTest {
	private static final String COMPANY = "EILISYS TECHNOLOGIES PRIVATE LTD";
	private DigilockerTestPage playground;
	private DigilockerReportsPage reports;
	private JourneyEvidence evidence;

	@BeforeClass(alwaysRun = true)
	public void signInAndVisitCompany() {

		DigilockerSuperAdminPage superAdmin = new DigilockerSuperAdminPage(driver);

		superAdmin.open();

		evidence = new JourneyEvidence(driver, "digilocker_five_journeys");

		evidence.capture("Superadmin entry");

		/*
		 * Google authentication
		 */
		if (superAdmin.googleSignInVisible()) {

			Set<String> before = new HashSet<>(driver.getWindowHandles());

			String source = driver.getWindowHandle();

			superAdmin.clickGoogleSignIn();

			String googleWindow = new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {

				for (String handle : d.getWindowHandles()) {

					if (!before.contains(handle)) {
						return handle;
					}
				}

				return d.getCurrentUrl().contains("accounts.google.com") ? d.getWindowHandle() : null;
			});

			driver.switchTo().window(googleWindow);

			new LoginPage_REKYCSuperadmin(driver).waitForEmailPage()
					.writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
					.clickOnButton("nextbtn").waitForPasswordPage()
					.writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
					.clickOnButton("nextbtn");

			/*
			 * Search all available windows for the Superadmin company directory instead of
			 * blindly switching back immediately.
			 */
			new WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {

				for (String handle : d.getWindowHandles()) {

					try {

						d.switchTo().window(handle);

						boolean companySearchVisible = d.findElements(By.id("search__company")).stream()
								.anyMatch(WebElement::isDisplayed);

						if (companySearchVisible) {
							return true;
						}

					} catch (Exception ignored) {
					}
				}

				return false;
			});
		}

		/*
		 * Company directory
		 */
		superAdmin.waitForCompanyDirectory();

		superAdmin.searchCompany("EILISYS TECHNOLOGIES");

		evidence.capture("EILISYS company result");

		/*
		 * Open EILISYS company
		 */
		superAdmin.visitCompany(COMPANY);

		/*
		 * Find actual EILISYS DigiLocker dashboard.
		 */
		new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {

			for (String handle : d.getWindowHandles()) {

				try {

					d.switchTo().window(handle);

					String url = d.getCurrentUrl();

					if (url != null && url.startsWith("https://digilocker.meon.co.in/") && url.contains("/dashboard")) {

						return true;
					}

				} catch (Exception ignored) {
				}
			}

			return false;
		});

		String dashboardUrl = driver.getCurrentUrl();

		ExtentReporterUtility.info("Company dashboard URL: " + dashboardUrl);

		/*
		 * URL validation
		 */
		Assert.assertTrue(dashboardUrl.startsWith("https://digilocker.meon.co.in/dashboard"),
				"Wrong DigiLocker dashboard." + "\nActual URL: " + dashboardUrl);

		/*
		 * Wait for dashboard to finish rendering.
		 */
		WebDriverWait dashboardWait = new WebDriverWait(driver, Duration.ofSeconds(30));

		dashboardWait.until(d -> {

			String body = d.findElement(By.tagName("body")).getText();

			return body.contains("DigiLocker Overview") && body.contains("DigiLocker Test") && body.contains("Reports");
		});

		String dashboardBody = driver.findElement(By.tagName("body")).getText();

		String normalizedBody = dashboardBody.replaceAll("\\s+", " ").trim().toUpperCase();

		/*
		 * Company validation using stable identifier.
		 */
		Assert.assertTrue(normalizedBody.contains("EILISYS TECHNOLOGIES"),
				"EILISYS company context not found." + "\nCurrent URL: " + dashboardUrl);

		/*
		 * Application-level validation.
		 */
		Assert.assertTrue(normalizedBody.contains("DIGILOCKER OVERVIEW"), "DigiLocker Overview missing");

		Assert.assertTrue(normalizedBody.contains("DIGILOCKER TEST"), "DigiLocker Test module missing");

		Assert.assertTrue(normalizedBody.contains("REPORTS"), "Reports module missing");

		ExtentReporterUtility.info("EILISYS DigiLocker dashboard " + "verified successfully");

		evidence.capture("EILISYS dashboard");

		playground = new DigilockerTestPage(driver);

		reports = new DigilockerReportsPage(driver);
	}

	@Test(description = "DL_AAD_10 - Five completed DigiLocker Test journeys each appear once in Reports", groups = {
			"critical", "digilocker", "correlation", "manual-otp" })
	public void fivePortalJourneysProduceFiveDistinctCompletedReports() {
		String aadhaar = System.getenv("DIGILOCKER_TEST_AADHAAR");
		String pin = System.getenv("DIGILOCKER_TEST_PIN");
		Assert.assertNotNull(aadhaar, "Set DIGILOCKER_TEST_AADHAAR for live journeys");
		Assert.assertNotNull(pin, "Set DIGILOCKER_TEST_PIN for live journeys");

		Assert.assertTrue(aadhaar.matches("[0-9]{12}"), "Aadhaar must contain exactly 12 digits");

		Assert.assertTrue(pin.matches("[0-9]{6}"), "DigiLocker PIN must contain exactly 6 digits");

		List<String> ids = new ArrayList<>();
		Set<String> unique = new HashSet<>();
		for (int run = 1; run <= 5; run++) {
			playground.openFromSidebar();
			evidence.capture("Run " + run + " DigiLocker Test page");
			String sourceWindow = driver.getWindowHandle();
			Set<String> before = new HashSet<>(driver.getWindowHandles());
			String id = playground.generateAndRedirect();
			Assert.assertTrue(unique.add(id), "Generated URL repeated transaction ID on run " + run);
			ids.add(id);
			ExtentReporterUtility.info("DigiLocker journey " + run + " generated transaction " + id);
			evidence.capture("Run " + run + " URL generated");

			new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
				for (String handle : d.getWindowHandles()) {
					if (!before.contains(handle)) {
						d.switchTo().window(handle);
						return true;
					}
				}
				d.switchTo().window(sourceWindow);
				return !d.getCurrentUrl().contains("/api-playground");
			});
			DigilockerWebhookConsentPage consent = new DigilockerWebhookConsentPage(driver);
			consent.enterAadhaarAndContinue(aadhaar);
			evidence.capture("Run " + run + " OTP checkpoint");
			// The user enters the actual OTP in the open browser; the test never reads it
			// from logs.
			consent.waitForOtpAndContinue();
			consent.enterPinAndContinue(pin);
			evidence.capture("Run " + run + " document consent");
			consent.selectAllAvailableDocumentsAndAllow();
			new WebDriverWait(driver, Duration.ofMinutes(2))
					.until(d -> d.getCurrentUrl().startsWith("https://digilocker.meon.co.in/")
							&& !d.getCurrentUrl().contains("/redirect/digilocker/"));
			evidence.capture("Run " + run + " returned from DigiLocker");
			if (!driver.getWindowHandle().equals(sourceWindow)) {
				driver.close();
				driver.switchTo().window(sourceWindow);
			}
		}

		// The report can be eventually consistent. Poll all five IDs together, not an
		// unrelated total.
		long deadline = System.nanoTime() + Duration.ofMinutes(15).toNanos();
		List<String> failures = new ArrayList<>();
		do {
			failures.clear();
			for (String id : ids) {
				reports.openFresh();
				List<ReportEntry> matches = reports.findOnRecentPages(id, 10);
				if (matches.size() != 1)
					failures.add(id + ": expected one row, found " + matches.size());
				else if (!matches.get(0).status.equalsIgnoreCase("Complete"))
					failures.add(id + ": status=" + matches.get(0).status);
			}
			if (failures.isEmpty())
				break;
			if (System.nanoTime() >= deadline)
				break;
			try {
				Thread.sleep(15000L);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
				throw new AssertionError("Interrupted while waiting for report reconciliation", interrupted);
			}
		} while (true);
		evidence.capture("Five-journey Reports reconciliation");
		Assert.assertTrue(failures.isEmpty(), "Report discrepancies: " + failures);
		Assert.assertEquals(ids.size(), 5);
		ExtentReporterUtility.info("Five unique DigiLocker transactions completed and each appeared once in Reports");
	}

	@AfterClass(alwaysRun = true)
	public void finishEvidenceAndBrowser() {
		if (evidence != null) {
			try {
				evidence.capture("Final browser state");
			} catch (RuntimeException unavailable) {
				ExtentReporterUtility.warning("Final screenshot unavailable");
			}
			evidence.finish();
		}
		if (driver != null)
			driver.quit();
	}
}
