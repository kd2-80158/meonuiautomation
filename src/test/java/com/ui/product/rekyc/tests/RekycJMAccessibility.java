//package com.ui.product.rekyc.tests;
//
//import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.WebElement;
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
//import com.aventstack.extentreports.MediaEntityBuilder;
//import com.aventstack.extentreports.Status;
//import com.google.gson.Gson;
//import com.ui.base.BaseTest;
//import com.ui.product.rekyc.pages.LoginPage_Rekyc;
//import com.ui.utilities.ExtentReporterUtility;
//import com.ui.utilities.LoggerUtility;
//
//@Listeners({com.ui.listeners.TestListener.class})
//public class RekycJMAccessibility extends BaseTest {
//	
//	private Gson gson;
//	private Logger logger;
//
//	@BeforeMethod(alwaysRun=true)
//	public void setup()
//	{
//		super.setup();
//		gson = new Gson();
//		logger = LoggerUtility.getLogger(this.getClass());
//	}
//	
//	@Test(description="tc_01 - Verify the login button contains accessible name",priority=1)
//	public void verifyLoginAccessibleName()
//	{
//		String url = "https://rekycuat.jmfonline.in/rekyc/diy";
//		this.driver.get(url);
//		
//		LoginPage_Rekyc obj = new LoginPage_Rekyc(driver);
//
//		logger.info("The aria label is: "+obj.getLoginButton().getAttribute("aria-label"));
//		String ariaLabel = obj.getLoginButton().getAttribute("aria-label");
//		Assert.assertEquals(ariaLabel, "Login options");
//		
//		String elementPath = getInstance().takeElementScreenshot(obj.getLoginButton(), "LoginButton");
//		
//		ExtentReporterUtility.getTest().log(
//		        Status.PASS,
//		        "Login button element captured with aria-label: " + ariaLabel,
//		        MediaEntityBuilder.createScreenCaptureFromPath(elementPath).build()
//		    );
//	}
//}
