
package com.ui.base;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.SeveritySoftAssert;

public class BaseTest {

	protected WebDriver driver;
	protected BrowserUtility browserUtility;
	protected SeveritySoftAssert softAssert;

	@BeforeClass(alwaysRun = true)
	public void setupBrowser() {
		if (driver != null) {
			return;
		}

		String browser = System.getProperty("browser", "firefox");

		boolean isHeadless = Boolean.parseBoolean(System.getProperty("isHeadless", "false"));
		Path downloadDirectory = Paths.get(System.getProperty("meon.download.dir",
				isHeadless && System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux")
						? "/work/downloads" : System.getProperty("user.home") + "/Downloads"));
		try {
			Files.createDirectories(downloadDirectory);
		} catch (IOException e) {
			throw new IllegalStateException("Could not create PDF download directory: " + downloadDirectory, e);
		}
		System.setProperty("meon.download.dir", downloadDirectory.toString());

		if (browser.equalsIgnoreCase("chrome") || browser.equalsIgnoreCase("chromium")) {

			ChromeOptions options = new ChromeOptions();

			Map<String, Object> chromePreferences = new HashMap<>();
			chromePreferences.put("signin.allowed", false);
			chromePreferences.put("signin.allowed_on_next_startup", false);
			chromePreferences.put("signin.interception_enabled", false);
			chromePreferences.put("credentials_enable_service", false);
			chromePreferences.put("profile.password_manager_enabled", false);
			chromePreferences.put("download.default_directory", downloadDirectory.toString());
			chromePreferences.put("download.prompt_for_download", false);
			chromePreferences.put("plugins.always_open_pdf_externally", true);
			options.setExperimentalOption("prefs", chromePreferences);

			options.addArguments("--disable-notifications");

			options.addArguments("--disable-popup-blocking");

			options.addArguments("--disable-extensions");

			// Keep WebDriver's disposable profile from presenting Chrome's native
			// first-run/sign-in UI over the application during local runs.
			options.addArguments("--no-first-run");

			options.addArguments("--no-default-browser-check");

			options.addArguments("--disable-sync");

			options.addArguments("--disallow-signin");

			options.addArguments("--disable-features=ChromeSignin,ChromeSigninIntercept");

			options.addArguments("--window-size=1920,1080");

			if (isHeadless) {
				options.addArguments("--headless=new");
				if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux")) {
					options.addArguments("--no-sandbox");
					options.addArguments("--disable-dev-shm-usage");
					if (Files.isExecutable(Paths.get("/usr/bin/chromium"))) {
						options.setBinary("/usr/bin/chromium");
					}
				}
			}

			driver = new ChromeDriver(options);

		} else if (browser.equalsIgnoreCase("firefox")) {

			FirefoxOptions options = new FirefoxOptions();

			options.addPreference("dom.webnotifications.enabled", false);

			options.addPreference("dom.disable_open_during_load", true);
			options.addPreference("browser.download.folderList", 2);
			options.addPreference("browser.download.dir", downloadDirectory.toString());
			options.addPreference("browser.download.useDownloadDir", true);
			options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/octet-stream");
			options.addPreference("pdfjs.disabled", true);

			if (isHeadless) {
				options.addArguments("-headless");
			}

			driver = new FirefoxDriver(options);

		} else {

			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}

		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

		if (isHeadless) {
			driver.manage().window().setSize(new Dimension(1920, 1080));
		} else {
			driver.manage().window().maximize();
		}

		browserUtility = new BrowserUtility(driver) {
		};
	}

	@BeforeMethod(alwaysRun = true)
	public void setupAssertion() {

		softAssert = new SeveritySoftAssert();
	}

//	@AfterClass(alwaysRun = true)
//	public void tearDownBrowser() {
//
//		if (driver != null) {
//
//			driver.quit();
//			driver = null;
//			browserUtility = null;
//		}
//	}

	public BrowserUtility getInstance() {
		return browserUtility;
	}

	/** Exposes the active driver to cross-cutting test listeners (reporting/screenshots). */
	public WebDriver getWebDriver() {
		return driver;
	}

	protected void assertStillOnPage(String expectedUrlPart, String pageName) {
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> d.getCurrentUrl().contains(expectedUrlPart));
		if (!driver.getCurrentUrl().contains(expectedUrlPart)) {
			throw new IllegalStateException(
					"❌ Flow broken! Expected: " + pageName + " | Actual URL: " + driver.getCurrentUrl());
		}
	}

	protected boolean ensureOnPage(String expectedUrlPart, String pageName) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> d.getCurrentUrl().contains(expectedUrlPart));
			return driver.getCurrentUrl().contains(expectedUrlPart);
		} catch (Exception e) {

			throw new IllegalStateException("❌ FLOW BROKEN at: " + pageName + " | Expected URL contains: "
					+ expectedUrlPart + " | Actual URL: " + driver.getCurrentUrl(), e);
		}
	}

//	@AfterClass(alwaysRun = true)
//	public void tearDownBrowser() {
//
//		if (driver != null) {
//			driver.quit();
//			driver = null;
//			browserUtility = null;
//		}
//	}
}
