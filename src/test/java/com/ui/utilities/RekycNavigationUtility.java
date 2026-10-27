
package com.ui.utilities;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class RekycNavigationUtility {

	private static final By OVERLAY = By.cssSelector(".overlay-bg");

	private RekycNavigationUtility() {
	}

	public static void clickNextWithRetry(WebDriver driver, By nextButton, String expectedUrl) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

		for (int attempt = 1; attempt <= 2; attempt++) {

			// Never click again after successful navigation.
			if (driver.getCurrentUrl().contains(expectedUrl)) {
				return;
			}

			try {
				// Wait for the loading overlay.
				wait.until(ExpectedConditions.invisibilityOfElementLocated(OVERLAY));

				// Re-locate Next before each attempt.
				WebElement next = wait.until(ExpectedConditions.elementToBeClickable(nextButton));

				next.click();

				System.out.println("[NAVIGATION] Next click attempt: " + attempt);

			} catch (ElementClickInterceptedException e) {

				System.out.println("[NAVIGATION] Next click intercepted " + "on attempt " + attempt);

			} catch (org.openqa.selenium.TimeoutException e) {

				System.out.println("[NAVIGATION] Page not ready " + "on attempt " + attempt);
			}

			// Check whether navigation succeeded.
			try {
				wait.until(ExpectedConditions.urlContains(expectedUrl));

				System.out.println("[NAVIGATION] Successfully navigated: " + driver.getCurrentUrl());

				return;

			} catch (org.openqa.selenium.TimeoutException e) {

				if (attempt == 2) {
					throw new AssertionError("Unable to navigate after 2 attempts. " + "Expected: " + expectedUrl
							+ ", Actual: " + driver.getCurrentUrl(), e);
				}
			}
		}
	}
}
