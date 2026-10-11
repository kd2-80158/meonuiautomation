package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class OTPPage extends BrowserUtility {
	
	private static final By OTP = By.xpath("//input[@id='txtmobotp0']");
	private static final By PAGE_HEADING = By.xpath("//h3[text()='Mobile OTP']");
	private static final By RESEND_BUTTON = By.xpath("//button[@id='resent_counterBtn']");
	private static final By OTP_EXPIRY_HEADING = By.xpath("//p[@id='successFalse_msg']");
	private static final By OTP_EXPIRY_OK_BUTTON = By.xpath("//button[text()='OK']");

	public OTPPage(WebDriver driver) {
		super(driver);
	}

	public OTPPage writeOTP()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));//wait for 30 sec to input
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(OTP));
		otp.click();
		otp.clear();
		otp.sendKeys("1234");;
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
	
	public boolean isOTPExpiryHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(OTP_EXPIRY_HEADING));
		if(title.isDisplayed())
			return true;
		return false;
	}
	
	public OTPPage clickOnResendButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement resendBtn = wait.until(ExpectedConditions.elementToBeClickable(RESEND_BUTTON));
		resendBtn.click();
		return this;
	}
	
	public OTPPage clickOnOkButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement okBtn = wait.until(ExpectedConditions.elementToBeClickable(OTP_EXPIRY_OK_BUTTON));
		okBtn.click();
		return this;
	}
	
}
