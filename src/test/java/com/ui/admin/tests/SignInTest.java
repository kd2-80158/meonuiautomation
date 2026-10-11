package com.ui.admin.tests;

import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.admin.pages.base.DashboardPage;
import com.ui.admin.pages.base.SignInPage;
import com.ui.base.BaseTest;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class SignInTest extends BaseTest {
	
	String url = JSONUtility.getSuperAdminWebsite().getUrl();
	SignInPage signInPage;
	DashboardPage dashboardPage;
	
	@BeforeMethod
	public void setup(Method method)
	{
		if (method.getName().equals("checkVisiblityOfHorizontalScrollBar")) {
	        return; 
	    }
		super.setupBrowser();;
		
		driver.get(url);
		signInPage = new SignInPage(driver);
		dashboardPage = new DashboardPage(driver);
	}
	
	@Test(priority=1, description = "tc_01 - Verify Superadmin login page loads successfully", groups= {"smoke","sanity","regresssion"})
	public void verifySuccessfulLoginWithValidCredentials()
	{
		signInPage
		.clickOnSignInButton()
		.writeEmail("saurabh.chhimwal@meon.co.in")
		.clickOnNextButtonEmail()
		.writePassword(JSONUtility.getSuperAdminWebsite().getPassword())
		.clickOnNextButton();
		
		Assert.assertTrue(dashboardPage.isPageHeadingVisible(),"Something went wrong while login with valid credentials");
	}
	
	@Test(priority=2, description = "tc_05 - Verify unauthorized Google account is blocked", groups= {"sanity","regresssion"})
	public void verifyDisplayingErrorMessageWithInvalidCredentials()
	{
		signInPage
		.clickOnSignInButton()
		.writeEmail("saurabh.chhimwal@meon.co.in")
		.clickOnNextButtonEmail()
		.writePassword("InvalidPassword")
		.clickOnNextButton();
		
		Assert.assertEquals(signInPage.getGmailErrorMessage(),"Wrong password. Try again or click \"Try another way\" for more options.");
	}
	
	@Test(priority=3,description="tc_07 - Verify horizontal scrollbar is not visible", groups= {"sanity","regresssion"})
	public void checkVisiblityOfHorizontalScrollBar()
	{
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(d -> ((JavascriptExecutor) d)
		        .executeScript("return document.readyState").equals("complete"));

		JavascriptExecutor js = (JavascriptExecutor) driver;

		Boolean hasHorizontalScroll = (Boolean) js.executeScript(
		    "return Math.max(" +
		    "document.body.scrollWidth, document.documentElement.scrollWidth) > " +
		    "Math.max(document.body.clientWidth, document.documentElement.clientWidth);"
		);

		Assert.assertTrue(hasHorizontalScroll,
		        "Expected horizontal scrollbar, but none found");

	}

}
