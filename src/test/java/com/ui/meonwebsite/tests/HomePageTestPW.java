package com.ui.meonwebsite.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import com.ui.base.BaseTestPW;
import com.ui.meonwebsite.pages.base.HomePagePW;
import com.ui.meonwebsite.pages.base.RequestDemoPage;
import com.ui.meonwebsite.pages.products.AadhaarEKYCPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Listeners({ com.ui.listeners.TestListener.class })
public class HomePageTestPW extends BaseTestPW {

	HomePagePW homePage;
	AadhaarEKYCPage aadhaarEKYCPage;
	RequestDemoPage requestDemoPage;

	@BeforeMethod
	public void setup() {
        super.setup();
        homePage = new HomePagePW(page);
        aadhaarEKYCPage = new AadhaarEKYCPage(page);
		page.navigate("https://meon.co.in/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
	}
	@Test
	public void verifyAadhaarKYCAccessFromHomePage() {
	    homePage.goToAadhaarKYCOptionInDropdown();
	    if(aadhaarEKYCPage.isPageHeadingVisible())
	    	aadhaarEKYCPage.clickOnGetStartedButton();	
//	    assertThat();
	}
	
}
