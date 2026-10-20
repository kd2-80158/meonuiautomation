package com.ui.product.facefinder.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class DashboardPage_FaceFinderCompany extends BrowserUtility {

	public DashboardPage_FaceFinderCompany(WebDriver driver) {
		super(driver);
	}

	private static final By LOGGEDINUSER_COMPANY = By.xpath("//p[text()='Saurabh Chhimwal']");
	private static final By HEADING_COMPANY = By.xpath("//H1[text()='IPV Logs']");
	private static final By IPVLOGS_SIDENAVIGATIONBAR = By.xpath("//a[@href='/dashboard/ipv-logs']");
	private static final By ASSISTEDIPV_SIDENAVIGATIONBAR = By.xpath("//a[@href='/dashboard/assisted-ipv']");
	private static final By CREDENTIALS_SIDENAVIGATIONBAR = By.xpath("//a[@href='/dashboard/credentials']");
	private static final By ADDCREDITS_SIDENAVIGATIONBAR = By.xpath("//a[@href='/dashboard/addCredits']");
	private static final By AUDITLOGS_SIDENAVIGATIONBAR = By.xpath("//a[@href='/dashboard/audit-logs']");

	public boolean isHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING_COMPANY));
		return heading.isDisplayed();
	}

	public String getText(String text) {
		By locator = null;
		switch (text.toLowerCase()) {
		case "username":
			locator = LOGGEDINUSER_COMPANY;
			break;
		default:
			throw new IllegalArgumentException("Key not found " + locator);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement l1 = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return l1.getText();
	}

	public DashboardPage_FaceFinderCompany clickOn(String text) {
		By locator = null;
		switch (text.toLowerCase()) {
		case "ipv":
			locator = IPVLOGS_SIDENAVIGATIONBAR;
			break;
		case "assisted_ipv":
			locator = ASSISTEDIPV_SIDENAVIGATIONBAR;
			break;
		case "credentials":
			locator = CREDENTIALS_SIDENAVIGATIONBAR;
			break;
		case "addcredits":
			locator = ADDCREDITS_SIDENAVIGATIONBAR;
			break;
		case "audit_logs":
			locator = AUDITLOGS_SIDENAVIGATIONBAR;
			break;

		default:
			throw new IllegalArgumentException("Key not found " + locator);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		option.click();
		return this;
	}
}
