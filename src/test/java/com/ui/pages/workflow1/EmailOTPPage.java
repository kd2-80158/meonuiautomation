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
public class EmailOTPPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='Email OTP']");
	private static final By OTP = By.xpath("//input[@id='otp']");
	private static final By VERIFY_OTP_BUTTON = By.xpath("//button[@id='SaveData']");

	public EmailOTPPage(WebDriver driver) {
		super(driver);
	}

	public EmailOTPPage writeOTP() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(OTP));
		otp.click();
		otp.clear();
		otp.sendKeys("1234");
		return this;
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public EmailOTPPage clickOnVerifyButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement resendBtn = wait.until(ExpectedConditions.elementToBeClickable(VERIFY_OTP_BUTTON));
		resendBtn.click();
		return this;
	}

	public EmailOTPPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Email OTP page did not load");
		return this;
	}

}
