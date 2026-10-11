package com.ui.meonwebsite.pages.base;

import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.ui.utilities.LoggerUtility;
import com.ui.utilities.PlaywrightUtility;

public class HomePagePW extends PlaywrightUtility {
	private final Logger logger = LoggerUtility.getLogger(this.getClass());
	private final Locator logo;
	private final Locator signInButton;
	private final Locator getDemoButton;
	private final Locator eSignButton;
	private final Locator apiDocumentationButton;
	private final Locator navbar;
	private final Locator productsDropdownTrigger;
	private final Locator productsDropdownContainer;
	private final Locator kycSolutionHeading;
	private final Locator aadhaarEKYCOption;

	public HomePagePW(Page page) {
		super(page);
		this.page = page;
		this.logo = page.locator("img[alt='logo']");
		this.signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));
		this.getDemoButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get Demo"));
		this.eSignButton = page.locator("button.btn-try");
		this.apiDocumentationButton = page.locator("button.btn-demo");
		this.navbar = page.locator("#navbarNav");
		this.productsDropdownTrigger = navbar.locator("#productsDropdown");
		this.productsDropdownContainer = navbar.locator("div.dropdown-menu-product");
		this.kycSolutionHeading = productsDropdownContainer.locator("p",
				new Locator.LocatorOptions().setHasText("KYC Solutions"));
		this.aadhaarEKYCOption = navbar.getByRole(AriaRole.LINK,
				new Locator.GetByRoleOptions().setName("Aadhaar eKYC"));
	}
	public boolean isLogoVisible() {
		return logo.isVisible();
	}

	public boolean isHeadingVisible() {
		productsDropdownContainer.waitFor();
		String headingText = kycSolutionHeading.innerText();
		logger.info("Dropdown heading text: {}", headingText);
		return kycSolutionHeading.isVisible();
	}
	public HomePagePW clickOnSignInButton() {
		signInButton.click();
		return this;
	}

	public HomePagePW clickOnGetDemoButton() {
		getDemoButton.click();
		return this;
	}

	public HomePagePW clickOnESignButton() {
		eSignButton.click();
		return this;
	}

	public HomePagePW clickOnApiDocumentationButton() {
		apiDocumentationButton.click();
		return this;
	}

	public HomePagePW goToAadhaarKYCOptionInDropdown() {
		productsDropdownTrigger.hover();
		aadhaarEKYCOption.waitFor();
		aadhaarEKYCOption.click();
		logger.info("Clicked Aadhaar eKYC from Products dropdown");
		return this;
	}
}
