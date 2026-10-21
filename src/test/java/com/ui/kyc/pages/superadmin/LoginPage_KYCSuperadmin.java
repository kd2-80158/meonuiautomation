package com.ui.kyc.pages.superadmin;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
	private static final By NEXT_BUTTON = By.xpath("(//button[@jsname='LgbsSe'])[2]");

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
			locator = NEXT_BUTTON;
			break;

		default:
			throw new IllegalArgumentException("Invalid error message locator key: " + text);
		}
		logger.info("Clicking on button: " + text);

		/*
		 * The Google sign-in control is a div rendered by Google's identity widget.
		 * Firefox headless can report that wrapper as clickable, then block in the
		 * native WebDriver click command while the widget handles the redirect. A
		 * DOM click dispatches the widget's handler without that driver-level stall.
		 */
		if ("googlesignin".equalsIgnoreCase(text)) {
			clickGoogleSignIn(locator);
		} else {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
			element.click();
		}
		return this;
	}

	private void clickGoogleSignIn(By locator) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		Set<String> windowsBeforeClick = new HashSet<>(getDriver().getWindowHandles());

		((JavascriptExecutor) getDriver()).executeScript(
				"arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);

		// Google may redirect in the current tab or open a separate authentication tab.
		wait.until(driver -> driver.getWindowHandles().size() > windowsBeforeClick.size()
				|| driver.getCurrentUrl().contains("accounts.google.com")
				|| !driver.findElements(EMAIL_INPUTBOX).isEmpty());

		for (String window : getDriver().getWindowHandles()) {
			if (!windowsBeforeClick.contains(window)) {
				getDriver().switchTo().window(window);
				break;
			}
		}
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
}
