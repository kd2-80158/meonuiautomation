package com.ui.product.facefinder.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.ui.utilities.BrowserUtility;

/**
 * Public superadmin login surface; does not bypass the application's disabled
 * state.
 */
public class LoginPage_FaceFinderSuperadmin extends BrowserUtility {
	private static final By GOOGLE = By.xpath("//button[contains(normalize-space(.),'Continue with Google')]");

	public LoginPage_FaceFinderSuperadmin(WebDriver driver) {
		super(driver);
	}

	public boolean hasGoogleButton() {
		return !getDriver().findElements(GOOGLE).isEmpty();
	}

	public void clickGoogleWhenEnabled(Duration timeout) {
		WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE));
		button.click();
	}

	public boolean isGoogleButtonEnabled() {
		return hasGoogleButton() && getDriver().findElement(GOOGLE).isEnabled();
	}
}
