package com.ui.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.pages.base.AdminSignInPage;

@Listeners({ com.ui.listeners.TestListener.class })
public class AdminSignInTest extends BaseTest {
	
	AdminSignInPage adminSignInPage;
	
	@BeforeMethod
	public void setup()
	{
		super.setupBrowser();
		adminSignInPage = new AdminSignInPage(driver);
	}


	
	@Test(description = "verify login using username and password credentials")
	public void verifyLogin() {
		adminSignInPage.
		doAdminLogin().goToWorkflow().searchWorkFlow("individual").clickTestWorkflow("individual");
	}
}
