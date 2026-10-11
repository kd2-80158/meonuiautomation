package com.ui.pages.workflow2;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class LoginPage extends BrowserUtility{
		private static final By MOBILE_NUMBER = By.xpath("//input[@id='mobile_number']");
		private static final By SELECT_DEPENDENCY = By.xpath("//select[@id='select_dependancy']");
		private static final By OTP_BUTTON = By.xpath("//button[@id='SaveData']");
		private static final By SEND_OTP_BUTTON = By.xpath("//button[@id='SaveData']");
		private static final By OTP_BOX1 = By.xpath("//input[@id='txtmobotp0']");
		private static final By OTP_BOX2 = By.xpath("//input[@id='txtmobotp1']");
		private static final By OTP_BOX3 = By.xpath("//input[@id='txtmobotp2']");
		private static final By OTP_BOX4 = By.xpath("//input[@id='txtmobotp3']");
		private static final By TNC_CHECKBOX = By.xpath("//input[@id='exampleCheck1']");
		private static final By ERROR_MESSAGE_OTP = By.xpath("//div[@id='error']");
		private static final By OTP_EXPIRY_MESSAGE = By.xpath("//p[@id='successFalse_msg']");

		public LoginPage(WebDriver driver) {
			super(driver);
		}

		public LoginPage enterMobileNumber(String mobileNumber) {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
			WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(MOBILE_NUMBER));
			((JavascriptExecutor) getDriver()).executeScript(
				    "arguments[0].scrollIntoView(true);", mobile);
			((JavascriptExecutor) getDriver()).executeScript(
				    "arguments[0].click();", mobile);
			WebElement m = getDriver().findElement(By.cssSelector("label[for='mobile_number']"));
			m.click();
			mobile.sendKeys(mobileNumber);
//			mobile.sendKeys(mobileNumber);
			return this;
		}
		
		public LoginPage enterOTP()
		{
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
			WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(OTP_BOX1));
			((JavascriptExecutor) getDriver()).executeScript(
				    "arguments[0].scrollIntoView(true);", otp);
			((JavascriptExecutor) getDriver()).executeScript(
				    "arguments[0].click();", otp);
			WebElement o = getDriver().findElement(By.cssSelector("label[for='OTP']"));
			o.click();
			otp.sendKeys("1234");
			return this;
		}
		
		
		public LoginPage writeOTP() {
		    waitForOTPToBeEntered(OTP_BOX1);
		    waitForOTPToBeEntered(OTP_BOX2);
		    waitForOTPToBeEntered(OTP_BOX3);
		    waitForOTPToBeEntered(OTP_BOX4);
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
		
		public LoginPage clickOnTNCCheckbox()
		{
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
			WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(TNC_CHECKBOX));
			if (!checkbox.isSelected()) {
			    checkbox.click();
			}
			return this;
        }
		
		public String getOtpErrorMessage() {
		    WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		    WebElement errorDiv = wait.until(
		        ExpectedConditions.presenceOfElementLocated(ERROR_MESSAGE_OTP));
		    wait.until(driver -> !errorDiv.getText().trim().isEmpty());
		    return errorDiv.getText().trim();
		}
		
		public String getOtpExpiryErrorMessage() {
		    WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		    WebElement errorDiv = wait.until(
		        ExpectedConditions.presenceOfElementLocated(ERROR_MESSAGE_OTP));
		    wait.until(driver -> !errorDiv.getText().trim().isEmpty());
		    return errorDiv.getText().trim();
		}

		@Override
		public String toString() {
			return "LoginPage []";
		}
		
		
}
