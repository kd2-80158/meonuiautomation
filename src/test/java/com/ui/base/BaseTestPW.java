package com.ui.base;

import java.util.List;

import org.testng.annotations.BeforeMethod;

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

		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(List.of("--start-maximized")));
		page = browser.newPage();
	}

	public Page getPage() {
		return page;
	}

}
