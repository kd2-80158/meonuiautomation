package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class LoginPage extends BrowserUtility {

	private static final By MOBILE_NUMBER = By.xpath("//input[@id='mobile_number']");
	private static final By SELECT_DEPENDENCY = By.xpath("//select[@id='select_dependancy']");
	private static final By OTP_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By SEND_OTP_BUTTON = By.xpath("//button[@id='setOtpAPI_mobile_number']");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public LoginPage enterMobileNumber(String mobileNumber) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(MOBILE_NUMBER));
		mobile.click();
		mobile.clear();
		mobile.sendKeys(mobileNumber);
		return this;
	}
	
	public LoginPage selectDependency()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(SELECT_DEPENDENCY));
		Select select = new Select(dropdown);
		select.selectByValue("Self"); //or you can change this: select.selectByIndex(1) or select.selectByVisibleText("Self");
		return this;
	}
	
	public LoginPage clickOnOTPButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otpButton = wait.until(ExpectedConditions.elementToBeClickable(OTP_BUTTON));
		otpButton.click();
		return this;
	}
	
	public LoginPage clickOnSendOTPButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otpButton = wait.until(ExpectedConditions.elementToBeClickable(SEND_OTP_BUTTON));
		otpButton.click();
		return this;
	}

}
