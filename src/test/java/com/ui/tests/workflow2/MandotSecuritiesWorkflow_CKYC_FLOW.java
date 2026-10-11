package com.ui.tests.workflow2;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.pages.workflow2.LoginPage;
import com.ui.utilities.LoggerUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class MandotSecuritiesWorkflow_CKYC_FLOW extends BaseTest {
	
	String url = "https://livetest.meon.co.in/mandotsecurities/individual_rm_anmol";
	LoginPage loginPage;
	Logger logger;
	
	@BeforeMethod
	public void setup()
	{
		super.setupBrowser();
		driver.get(url);
		loginPage = new LoginPage(driver);
		logger = LoggerUtility.getLogger(this.getClass());
	}
	
	@Test(description = "Verify the happy flow.")
	public void verifyWorkflow()
	{
		loginPage
		.enterMobileNumber("8920944825")
		.selectDependency()
		.clickOnSendOTPButton();
		
		if(loginPage.getOtpErrorMessage() != null)
		{
			logger.info("Error message: "+loginPage.getOtpErrorMessage());
			Assert.assertFalse(loginPage.getOtpErrorMessage().contains("OTP sending limit"));
			return;
		}		
		loginPage
		.enterOTP()
		.clickOnTNCCheckbox()
		.clickOnOTPButton();
		if(loginPage.getOtpExpiryErrorMessage()!=null)
		{
			logger.info("OTP Expiry message: "+loginPage.getOtpExpiryErrorMessage());
			return;
		}
	}

}
