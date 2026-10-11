package com.ui.meonwebsite.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.meonwebsite.pages.base.HomePage;

@Listeners({com.ui.listeners.TestListener.class})
public class HomePageTest extends BaseTest {

	String url = "https://meon.co.in/";
	HomePage homePage;
	
	@BeforeMethod
	public void setup()
	{
		super.setupBrowser();;
		homePage = new HomePage(driver);
		driver.get(url);
		
	}
	
	@Test
	public void verifyAadhaarKYCAccessFromHomePage() {
		homePage.goToAadhaarKYCOptionInDropdown();
	}
}
