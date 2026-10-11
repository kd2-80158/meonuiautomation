package com.ui.cloud.apimanager.admin;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ui.base.BaseTest;
import com.ui.product.apimanager.pages.LoginPage_APIManager;
import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class LoginPageTest_APIManager extends BaseTest {

	LoginPage_APIManager loginPage_APIManager;
	Logger logger;
	SoftAssert softAssert;

	@BeforeTest
	public void setup() {
		super.setupBrowser();
		loginPage_APIManager = new LoginPage_APIManager(driver);
		logger = LoggerUtility.getLogger(this.getClass());
		softAssert = new SoftAssert();
	}

	@Test(description = "tc_login_02 - Verify successful login with valid credentials", priority = 1, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithValidCredentials() {
		String email = "test@meon.co.in";
		String password = "test@1234";
		driver.get("https://api.meon.co.in/login");
		loginPage_APIManager.writeEmail(email).writePassword(password).clickOnSignInButton();
		softAssert.assertTrue(loginPage_APIManager.isDashboardVisible());
		BrowserUtility.doLogout(driver);
	}

	@Test(description = "tc_login_03 - Verify login fails with invalid password", priority = 2, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithInvalidPassword() {
		String email = "test@meon.co.in";
		String invalidPassword = "test@12345";
		if (driver == null)
			driver.get("https://api.meon.co.in/login");
		loginPage_APIManager.writeEmail(email).writePassword(invalidPassword).clickOnSignInButton();
		softAssert.assertTrue(loginPage_APIManager.isErrorMessageVisible());
	}
	
	@Test(description = "tc_login_04 - Verify login fails with unregistered email", priority = 3, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithUnregisteredEmail() {
		String invalidEmail = "invalid@meon.co.in";
		String password = "test@1234";
		if (driver == null)
			driver.get("https://api.meon.co.in/login");
		loginPage_APIManager.writeEmail(invalidEmail)
		                    .writePassword(password)
		                    .clickOnSignInButton();
		softAssert.assertTrue(loginPage_APIManager.isErrorMessageVisible());
	}
	
	@Test(description="tc_login_09 - Verify password masking",priority=4,groups= {"e2e","sanity"})
	public void verifyPasswordMaskingInputValidation()
	{
		String email = "test@meon.co.in";
		String password = "test@1234";
		if (driver == null)
			driver.get("https://api.meon.co.in/login");
		By PASSWORD_LOCATOR = By.xpath("//input[@name='password']");
		WebElement pass = driver.findElement(PASSWORD_LOCATOR);
		String passwordAttribute = pass.getAttribute("type");
		softAssert.assertEquals(passwordAttribute, "password");
	}

}
