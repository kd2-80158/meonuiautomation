package com.ui.kyc.pages.superadmin;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class DashboardPage_DemoCapital extends BrowserUtility {

	public DashboardPage_DemoCapital(WebDriver driver) {
		super(driver);
	}

	/* The current admin UI no longer assigns id="userList-tab" to this item. */
	private static final By USERS_SIDENAVIGATIONBAR = By.xpath(
			"//*[self::a or self::button or @role='button'][normalize-space()='User List' "
					+ "or .//*[normalize-space()='User List']]");
	private static final By LOADING_OVERLAY = By.cssSelector("div.overlay-bg");

	public DashboardPage_DemoCapital clickOnUsersList_DemoCapital() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement userTab = wait.until(ExpectedConditions.visibilityOfElementLocated(USERS_SIDENAVIGATIONBAR));
		((JavascriptExecutor) getDriver()).executeScript(
				"arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", userTab);
		wait.until(ExpectedConditions.elementToBeClickable(userTab));
		try {
			userTab.click();
		} catch (ElementClickInterceptedException intercepted) {
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", userTab);
		}
		wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
		return this;
	}
}
