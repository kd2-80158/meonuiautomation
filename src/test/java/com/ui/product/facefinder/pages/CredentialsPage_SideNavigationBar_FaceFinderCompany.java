package com.ui.product.facefinder.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class CredentialsPage_SideNavigationBar_FaceFinderCompany extends BrowserUtility {

	public CredentialsPage_SideNavigationBar_FaceFinderCompany(WebDriver driver) {
		super(driver);
	}

	private static final By HEADING_CREDENTIALSPAGE = By.xpath("//h1[text()='Credentials']");
	private static final By ADDCREDENTIALS_CREDENTIALSPAGE = By.xpath("//button[text()='Add Credentials']");
	private static final By THRESHOLD_CREDENTIALSPAGE = By.xpath("//button[@title='Setup threshold']");
	private static final By EDITCREDENTIALS_CREDENTIALSPAGE = By.xpath("//button[@title='Edit credentials']");
	private static final By HEADING_NODE1_CREDENTIALSPAGE = By.xpath("//p[text()='Credential Node #']");
	private static final By NODES_VIEW = By.cssSelector("button[aria-label='Nodes view']");
	private static final By TABLE_VIEW = By.cssSelector("button[aria-label='Table view']");
	private static final By REFRESH = By.cssSelector("button[aria-label='Refresh credentials']");
	private static final By COPY_ID = By.cssSelector("button[aria-label='Copy Client ID']");
	private static final By COPY_SECRET = By.cssSelector("button[aria-label='Copy Client Secret']");

	public boolean isHeadingVisible() {
		return !getDriver().findElements(HEADING_CREDENTIALSPAGE).isEmpty();
	}

	public boolean hasAction(String action) {
		return !getDriver().findElements(actionLocator(action)).isEmpty();
	}

	public void clickAction(String action) {
		By locator = actionLocator(action);
		new org.openqa.selenium.support.ui.WebDriverWait(getDriver(), java.time.Duration.ofSeconds(20))
				.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(locator)).click();
	}

	private By actionLocator(String action) {
		switch (action.toLowerCase()) {
		case "add":
			return ADDCREDENTIALS_CREDENTIALSPAGE;
		case "edit":
			return EDITCREDENTIALS_CREDENTIALSPAGE;
		case "threshold":
			return THRESHOLD_CREDENTIALSPAGE;
		case "nodes":
			return NODES_VIEW;
		case "table":
			return TABLE_VIEW;
		case "refresh":
			return REFRESH;
		case "copy_id":
			return COPY_ID;
		case "copy_secret":
			return COPY_SECRET;
		default:
			throw new IllegalArgumentException("Unknown credential action: " + action);
		}
	}
}
