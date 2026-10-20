package com.ui.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
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

		String browser = System.getProperty("browser", "chrome");

		boolean isHeadless = Boolean.parseBoolean(System.getProperty("isHeadless", "false"));

		if (browser.equalsIgnoreCase("chrome") || browser.equalsIgnoreCase("chromium")) {

			ChromeOptions options = new ChromeOptions();

			options.addArguments("--disable-notifications");

			options.addArguments("--disable-popup-blocking");

			options.addArguments("--disable-extensions");

			options.addArguments("--window-size=1920,1080");

			if (isHeadless) {
				options.addArguments("--headless=new");
			}

			driver = new ChromeDriver(options);

		} else if (browser.equalsIgnoreCase("firefox")) {

			FirefoxOptions options = new FirefoxOptions();

			options.addPreference("dom.webnotifications.enabled", false);

			options.addPreference("dom.disable_open_during_load", true);

			if (isHeadless) {
				options.addArguments("-headless");
			}

			driver = new FirefoxDriver(options);

		} else {

			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}

		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

		if (!isHeadless) {
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
