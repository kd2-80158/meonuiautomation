package com.ui.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.SeveritySoftAssert;

@Listeners({ com.ui.listeners.TestListener.class })
public class BaseTest {

	protected WebDriver driver;
	private BrowserUtility browserUtility;
	protected SeveritySoftAssert softAssert;

	@BeforeClass(alwaysRun = true)
	public void setupBrowser() {
		if (driver == null) {
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			// BrowserUtility instance for Listener / Screenshot
			browserUtility = new BrowserUtility(driver) {
			};
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setupAssertion() {
		softAssert = new SeveritySoftAssert();
	}

	public BrowserUtility getInstance() {
		return browserUtility;
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

	@AfterClass(alwaysRun = true)
	public void tearDownBrowser() {

		if (driver != null) {
			driver.quit();
			driver = null;
			browserUtility = null;
		}
	}
}