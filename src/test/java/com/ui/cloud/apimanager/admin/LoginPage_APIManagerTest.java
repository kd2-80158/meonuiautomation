package com.ui.cloud.apimanager.admin;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.SkipException;

import com.ui.base.BaseTest;
import com.ui.product.apimanager.pages.LoginPage_APIManager;
import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class LoginPage_APIManagerTest extends BaseTest {
	private static final String BASE = System.getProperty("apiManager.baseUrl", "https://api.meon.co.in");

	LoginPage_APIManager loginPage_APIManager;
	Logger logger;

	@BeforeClass
	public void setup() {
		super.setupBrowser();
		loginPage_APIManager = new LoginPage_APIManager(driver);
		logger = LoggerUtility.getLogger(this.getClass());
	}

	@AfterClass(alwaysRun = true)
	public void closeLoginBrowser() {
		if (driver != null) {
			try { driver.quit(); } finally { driver = null; browserUtility = null; }
		}
	}

	private String credential(String property, String environment) {
		String value = System.getProperty(property, System.getenv(environment));
		if (value == null || value.isBlank()) {
			throw new SkipException("Set " + environment + " to run API Manager authentication tests");
		}
		return value;
	}

	private void requireNegativeLoginTarget() {
		if (!Boolean.parseBoolean(System.getProperty("apiManager.allowNegativeLogin", "false"))) {
			throw new SkipException("Negative login tests are disabled on the live manager by default");
		}
	}

	@Test(description = "tc_login_02 - Verify successful login with valid credentials", priority = 1, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithValidCredentials() {
		String email = "meontest@meon.co.in";
		String password = "meon@123";
		driver.get(BASE + "/login");
		loginPage_APIManager.writeEmail(email).writePassword(password).clickOnSignInButton();
		Assert.assertTrue(loginPage_APIManager.isDashboardVisible());
		BrowserUtility.doLogout(driver);
	}

	@Test(description = "tc_login_03 - Verify login fails with invalid password", priority = 2, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithInvalidPassword() {
		String email = "meontest@meon.co.in";
		String invalidPassword = "Invalid@123";
		driver.get(BASE + "/login");
		loginPage_APIManager.writeEmail(email).writePassword(invalidPassword).clickOnSignInButton();
		Assert.assertTrue(loginPage_APIManager.isErrorMessageVisible());
	}
	
	@Test(description = "tc_login_04 - Verify login fails with unregistered email", priority = 3, groups = { "e2e", "smoke" })
	public void verifyLoginFunctionalityWithUnregisteredEmail() {
		String invalidEmail = "test@testing.com";
		String password = "meon@123";
		driver.get(BASE + "/login");
		loginPage_APIManager.writeEmail(invalidEmail)
		                    .writePassword(password)
		                    .clickOnSignInButton();
		Assert.assertTrue(loginPage_APIManager.isErrorMessageVisible());
	}
	
	@Test(description="tc_login_09 - Verify password masking",priority=4,groups= {"e2e","sanity"})
	public void verifyPasswordMaskingInputValidation()
	{
		driver.get(BASE + "/login");
		By PASSWORD_LOCATOR = By.xpath("//input[@name='password']");
		WebElement pass = driver.findElement(PASSWORD_LOCATOR);
		String passwordAttribute = pass.getAttribute("type");
		Assert.assertEquals(passwordAttribute, "password");
	}

}
