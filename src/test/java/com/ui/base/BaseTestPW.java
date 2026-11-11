package com.ui.base;

import java.util.List;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.WaitUntilState;
import com.ui.utilities.BrowserUtility;


public abstract class BaseTestPW {

	private Playwright playwright;
	private Browser browser;
	protected Page page;
	

	@BeforeMethod
	public void setup() {
		playwright = Playwright.create();
		boolean headless = Boolean.parseBoolean(System.getProperty("isHeadless",
				System.getProperty("meon.cloudRun", "false")));
		List<String> args = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux")
				? List.of("--no-sandbox", "--disable-dev-shm-usage") : List.of();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
				.setHeadless(headless).setArgs(args));
		page = browser.newPage();
		page.setDefaultTimeout(30_000);
	}

	@AfterMethod(alwaysRun = true)
	public void closePlaywright() {
		if (browser != null) browser.close();
		if (playwright != null) playwright.close();
		page = null;
		browser = null;
		playwright = null;
	}

	public Page getPage() {
		return page;
	}

}
