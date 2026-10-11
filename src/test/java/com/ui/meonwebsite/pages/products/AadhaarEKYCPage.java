package com.ui.meonwebsite.pages.products;

import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.ui.utilities.LoggerUtility;
import com.ui.utilities.PlaywrightUtility;

public class AadhaarEKYCPage extends PlaywrightUtility {

	private Page page;
	private Logger logger = LoggerUtility.getLogger(this.getClass());
	private Locator pageHeading;
	private Locator getStartedButton;

	public AadhaarEKYCPage(Page page) {
		super(page);
		this.page = page;
		this.pageHeading = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Aadhaar eKYC for Secure,"));
	}

	public AadhaarEKYCPage clickOnGetStartedButton() {

		Locator getStarted = page.locator("a[target='_blank']")
				.filter(new Locator.FilterOptions().setHasText("Get Started"));

		Page newPage = page.waitForPopup(() -> {
			getStarted.scrollIntoViewIfNeeded();
			getStarted.click();
		});

		newPage.waitForLoadState();
		return new AadhaarEKYCPage(newPage);
	}

	public boolean isPageHeadingVisible() {
		logger.info("Page Heading is {}" + pageHeading.innerText());
		return pageHeading.isVisible();
	}

}
