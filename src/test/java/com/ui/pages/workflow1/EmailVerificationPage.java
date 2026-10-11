package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class EmailVerificationPage extends BrowserUtility {
	
	private static final By PAGE_HEADING = By.xpath("//h3[text()='Email Verification']");
	private static final By EMAIL = By.xpath("//input[@id='email']");
	private static final By SEND_OTP = By.xpath("//button[@id='SaveData']");

	public EmailVerificationPage(WebDriver driver) {
		super(driver);
	}

	public EmailVerificationPage writeEmail()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(EMAIL));
		email.click();
		email.clear();
		email.sendKeys("saurabh.chhimwal@meon.co.in");
		return this;
	}
	
	public EmailVerificationPage clickOnSendOTPButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(SEND_OTP));
		otp.click();
	
		return this;
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if(title.isDisplayed())
			return true;
		return false;
	}

	public EmailVerificationPage assertPageLoaded() {
		Assert.assertTrue(
		        isPageHeadingVisible(),
		        "Email Verification page did not load"
		    );
		    return this;
	}
	
	

}
