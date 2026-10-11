package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class LoginPage_CorporateRekycUAT extends BrowserUtility {

	public LoginPage_CorporateRekycUAT(WebDriver driver) {
		super(driver);
	}

	private static final By CLIENTCODE_INPUTBOX = By.id("clientid"); // xpath->//input[@id='clientid']
	private static final By SENDOTP_BUTTON = By.id("sendotp"); // xpath->//button[@id="sendotp"]
	private static final By OTP_INPUTBOX = By.id("otpsign"); // xpath->//input[@id='otpsign']
	private static final By VERIFYANDCONTINUE_BUTTON = By.id("client_signup"); // xpath->//button[@id='client_signup']
	private static final By PERSONALPAGE_TEXT = By.xpath("//h2[contains(text(), 'Personal Details')]");
	private static final By ERRORMESSAGE_CLIENTCODE = By.id("otp-msg-loginsend");
	private static final By ERRORMESSAGE_OTP = By.id("otp-msg-send");
    private static final By SUCCESSMESSAGE_CLIENTCODE = By.id("otp-msg-send");
	
	Logger logger = LoggerUtility.getLogger(this.getClass());

	public LoginPage_CorporateRekycUAT enterClientCode(String cc) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement clientCode = wait.until(ExpectedConditions.elementToBeClickable(CLIENTCODE_INPUTBOX));
		clientCode.click();
		clientCode.clear();
		clientCode.sendKeys(cc);
		return this;
	}

	public LoginPage_CorporateRekycUAT clickOnSendOtpButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement otpButton = wait.until(ExpectedConditions.elementToBeClickable(SENDOTP_BUTTON));
		otpButton.click();
		return this;
	}

	public LoginPage_CorporateRekycUAT enterOtp(String otp1) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(OTP_INPUTBOX));
		otp.click();
		otp.clear();
		otp.sendKeys(otp1);
		return this;
	}

	public LoginPage_CorporateRekycUAT clickOnVerifyButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement verifyButton = wait.until(ExpectedConditions.elementToBeClickable(VERIFYANDCONTINUE_BUTTON));
		verifyButton.click();
		return this;
	}

	public boolean isPersonalPageVisible() {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PERSONALPAGE_TEXT));

			String actualText = element.getText();

			logger.info("Actual text found for PERSONALPAGE_TEXT: '" + actualText + "'");
			return actualText.contains("Personal Details");

		} catch (Exception e) {
			logger.error("Failed to find or read text for PERSONALPAGE_TEXT: " + e.getMessage());
			return false;
		}
	}

	public String getErrorMessage(String text) {

		By locator;

		switch (text.toLowerCase()) {

		case "otp":
			locator = ERRORMESSAGE_OTP;
			break;

		case "clientcode":
			locator = ERRORMESSAGE_CLIENTCODE;
			break;
			
		case "validclientcode":
			locator= SUCCESSMESSAGE_CLIENTCODE;
			break;

		default:
			throw new IllegalArgumentException("Invalid error message locator key: " + text);
		}

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		return element.getText();
	}
}
