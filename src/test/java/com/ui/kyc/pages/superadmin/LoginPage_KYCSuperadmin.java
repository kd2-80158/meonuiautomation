package com.ui.kyc.pages.superadmin;

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
public class LoginPage_KYCSuperadmin extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	public LoginPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}

	private static final By GOOGLESIGNIN_BUTTON = By.xpath("//div[@id='googleSignInButton']");
	private static final By GACCOUNT_SELECTION = By.xpath("(//div[@class='VV3oRb YZVTmd SmR8'])[3]");
	private static final By EMAIL_INPUTBOX = By.xpath("//input[@id='identifierId']");
	private static final By PASSWORD_INPUTBOX = By.xpath("//input[@type='password']");
	private static final By EMAIL_NEXT_BUTTON = By.id("identifierNext");
	private static final By PASSWORD_NEXT_BUTTON = By.id("passwordNext");

	public LoginPage_KYCSuperadmin clickOnButton(String text) {
		By locator;
		switch (text.toLowerCase()) {
		case "googlesignin":
			locator = GOOGLESIGNIN_BUTTON;
			break;

		case "googleaccountselection":
			locator = GACCOUNT_SELECTION;
			break;

		case "email":
			locator = EMAIL_INPUTBOX;
			break;
		case "password":
			locator = PASSWORD_INPUTBOX;
			break;
		case "nextbtn":
			clickGoogleNext();
			return this;

		default:
			throw new IllegalArgumentException("Invalid error message locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		logger.info("Clicking on button: " + text);
		element.click();
		return this;
	}

	private void clickGoogleNext() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		By locator = hasVisiblePasswordField() ? PASSWORD_NEXT_BUTTON : EMAIL_NEXT_BUTTON;
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		logger.info("Clicking Google {} Next button", locator.equals(PASSWORD_NEXT_BUTTON) ? "password" : "email");
		element.click();
	}

	private boolean hasVisiblePasswordField() {
		return getDriver().findElements(PASSWORD_INPUTBOX).stream().anyMatch(WebElement::isDisplayed);
	}

	public LoginPage_KYCSuperadmin writeService(String text, String input) {
		By locator;
		switch (text.toLowerCase()) {
		case "email":
			locator = EMAIL_INPUTBOX;
			break;
		case "password":
			locator = PASSWORD_INPUTBOX;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement sendtext = wait.until(ExpectedConditions.elementToBeClickable(locator));
		sendtext.clear();
		sendtext.sendKeys(input);
		return this;
	}

	public LoginPage_KYCSuperadmin waitForPasswordPage() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(40));

		logger.info("Waiting for Google password page");

		wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUTBOX));

		logger.info("Google password input is visible");

		return this;
	}

	public LoginPage_KYCSuperadmin waitForEmailPage() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		logger.info("Waiting for Google email page");

		wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUTBOX));

		logger.info("Google email input is visible");

		return this;
	}
}
