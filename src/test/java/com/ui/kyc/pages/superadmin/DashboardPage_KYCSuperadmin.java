package com.ui.kyc.pages.superadmin;

import java.time.Duration;

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
public class DashboardPage_KYCSuperadmin extends BrowserUtility {

	public DashboardPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}

	private static final By USERS_SIDENAVIGATIONBAR = By.xpath("//a[contains(@href, '/superadmin/users')]");
	private static final By EMAILEDITOR_SIDENAVIGATIONBAR = By
			.xpath("//a[contains(@href, '/superadmin/email-editor')]");
	private static final By SEARCHBAR_KYCDASHBOARD = By.cssSelector("input#search__company");
	private static final By SEARCHBUTTON_KYCDASHBOARD = By.id("search-superadmin-company-btn");
	private static final By VISITADMINBUTTON_KYCDASHBOARD = By.xpath("(//button[@class='visit-admin'])[1]");
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
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement user = wait.until(ExpectedConditions.elementToBeClickable(SEARCHBUTTON_KYCDASHBOARD));
		user.click();
		return this;
	}

	public DashboardPage_KYCSuperadmin writeOnSearchBar(String value) {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));

		WebElement searchBar = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCHBAR_KYCDASHBOARD));

		JavascriptExecutor js = (JavascriptExecutor) getDriver();

		js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", searchBar);

		wait.until(driver -> searchBar.isDisplayed() && searchBar.isEnabled());

		searchBar.click();

		try {

			searchBar.clear();
			searchBar.sendKeys(value);

		} catch (Exception e) {

			js.executeScript(
					"arguments[0].value='';" + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
					searchBar);

			searchBar.sendKeys(value);
		}

		return this;
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
