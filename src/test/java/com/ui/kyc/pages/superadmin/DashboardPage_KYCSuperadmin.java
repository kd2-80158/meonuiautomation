package com.ui.kyc.pages.superadmin;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class DashboardPage_KYCSuperadmin extends BrowserUtility {

	public DashboardPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}

	private static final By USERS_SIDENAVIGATIONBAR = By.xpath("//a[contains(@href, '/superadmin/users')]");
	private static final By EMAILEDITOR_SIDENAVIGATIONBAR = By
			.xpath("//a[contains(@href, '/superadmin/email-editor')]");
	private static final By SEARCHBAR_KYCDASHBOARD = By.cssSelector("input#search__company");
	private static final By SEARCHBUTTON_KYCDASHBOARD = By.id("search-superadmin-company-btn");
	private static final By VISITADMINBUTTON_KYCDASHBOARD = By.xpath("//tr[.//td[normalize-space()='DEMO CAPITAL MARKET']]//button[contains(@class,'visit-admin')]");
	private static final By COMPANYNAME_TABLE_KYCDASHBOARD = By.xpath("//td[normalize-space()='DEMO CAPITAL MARKET']");
	Logger logger = LoggerUtility.getLogger(this.getClass());

	public DashboardPage_KYCSuperadmin clickOnButton(String text) {
		By locator;
		switch (text.toLowerCase()) {
		case "visitadmin_button":
			locator = VISITADMINBUTTON_KYCDASHBOARD;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		button.click();
		return this;
	}

	public DashboardPage_KYCSuperadmin waitForDashboard() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(60));
		wait.until(driver -> driver.getCurrentUrl().contains("/superadmin")
				&& driver.findElements(SEARCHBAR_KYCDASHBOARD).stream().anyMatch(WebElement::isDisplayed));
		return this;
	}

	public boolean isTextVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(COMPANYNAME_TABLE_KYCDASHBOARD));
		logger.info("The element value is: " + element.getText());
		System.out.println("The element value is: " + element.getText());
		return element.getText().equalsIgnoreCase("DEMO CAPITAL MARKET");
	}

	public DashboardPage_KYCSuperadmin clickOnUsers_KYCSuperadmin() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement user = wait.until(ExpectedConditions.elementToBeClickable(USERS_SIDENAVIGATIONBAR));
		user.click();
		return this;
	}

	public DashboardPage_KYCSuperadmin clickOnSearchButton_KYCSuperadmin() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		// Some deployments have a Search button; others filter while typing.
		boolean submitted = false;
		for (WebElement button : getDriver().findElements(SEARCHBUTTON_KYCDASHBOARD)) {
			if (button.isDisplayed() && button.isEnabled()) {
				wait.until(ExpectedConditions.elementToBeClickable(button)).click();
				submitted = true;
				break;
			}
		}
		if (!submitted) {
			logger.info("No Search button is visible; submitting company query with Enter");
			wait.until(ExpectedConditions.elementToBeClickable(SEARCHBAR_KYCDASHBOARD)).sendKeys(Keys.ENTER);
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(COMPANYNAME_TABLE_KYCDASHBOARD));
		} catch (TimeoutException error) {
			String query = getDriver().findElement(SEARCHBAR_KYCDASHBOARD).getAttribute("value");
			String rows = getDriver().findElements(By.cssSelector("table tbody tr")).stream()
					.limit(5).map(WebElement::getText).reduce((left, right) -> left + " | " + right)
					.orElse("<no table rows>");
			throw new IllegalStateException("Company search returned no DEMO CAPITAL MARKET row. URL="
					+ getDriver().getCurrentUrl() + ", query='" + query + "', first rows=" + rows, error);
		}
		return this;
	}

	public DashboardPage_KYCSuperadmin writeOnSearchBar(String value) {
		waitForDashboard();
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		wait.ignoring(StaleElementReferenceException.class);
		for (int attempt = 1; attempt <= 3; attempt++) {
			WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCHBAR_KYCDASHBOARD));
			((JavascriptExecutor) getDriver()).executeScript(
					"arguments[0].scrollIntoView({block:'center', inline:'nearest'});", searchBar);
			searchBar.click();
			searchBar.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			searchBar.sendKeys(value);
			String actual = getDriver().findElement(SEARCHBAR_KYCDASHBOARD).getAttribute("value");
			if (value.equalsIgnoreCase(actual == null ? "" : actual.trim())) {
				logger.info("Company search accepted on attempt {}", attempt);
				return this;
			}
			logger.warn("Company search attempt {} did not persist; current value='{}', URL={}",
					attempt, actual, getDriver().getCurrentUrl());
		}
		throw new IllegalStateException("Company search input did not retain the query on "
				+ getDriver().getCurrentUrl() + "; current value='"
				+ getDriver().findElement(SEARCHBAR_KYCDASHBOARD).getAttribute("value") + "'");
	}

	public DashboardPage_KYCSuperadmin clickOnEmailEditor_KYCSuperadmin() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(EMAILEDITOR_SIDENAVIGATIONBAR));
		email.click();
		return this;
	}

	public DashboardPage_KYCSuperadmin switchToNewAdminTab() {

		String currentWindow = getDriver().getWindowHandle();

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));

		wait.until(driver -> driver.getWindowHandles().size() > 1);

		for (String windowHandle : getDriver().getWindowHandles()) {

			if (!windowHandle.equals(currentWindow)) {

				getDriver().switchTo().window(windowHandle);

				break;
			}
		}

		return this;
	}

}
