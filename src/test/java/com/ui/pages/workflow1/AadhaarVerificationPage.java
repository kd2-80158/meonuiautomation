package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class AadhaarVerificationPage extends BrowserUtility {
	
	private static final By PAGE_HEADING = By.xpath("//h5[text()='Verify Aadhaar OTP']");
	private static final By OTP = By.xpath("//input[@id='a_otp_1']");
	private static final By SEND_OTP = By.xpath("//button[@id='otp_button']");
	private static final By RESEND_BUTTON = By.xpath("//span[text()='Resend OTP']");

	public AadhaarVerificationPage(WebDriver driver) {
		super(driver);
	}


	public AadhaarVerificationPage writeOTP()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(60));
	    
	    wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	            String value = d.findElement(OTP).getAttribute("value");
	            return value != null && value.length() == 6;
	        }
	    });
	    
	    return this;
	}
	
	public AadhaarVerificationPage clickOnSubmitButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(SEND_OTP));
		otp.click();
	
		return this;
	}
	
	public AadhaarVerificationPage clickOnResendOTPButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(RESEND_BUTTON));
		otp.click();
		return this;
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if(title.isDisplayed())
			return true;
		return false;
	}


	public AadhaarVerificationPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Aadhaar Verification page did not load");
		return this;
	}
	
	

}
