package com.ui.product.rekyc.pages.superadmin;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class DashboardPage_REKYCSuperadmin extends BrowserUtility{

	public DashboardPage_REKYCSuperadmin(WebDriver driver) {
		super(driver);
	}
	
	private static final By SEARCH_LOCATOR = By.id("search__company");
	private static final By VISITADMIN_BUTTON = By.xpath("//tr[.//td[contains(normalize-space(.), 'meon_rekyc')]]//button[contains(concat(' ', normalize-space(@class), ' '), ' visit-admin ')]");
	private static final By WORKFLOWV2_SIDENAVIGATIONBAR = By.xpath("//a[@href='/v1/company/workflow_v2']");
	private static final By MAKERANDCHECKER_SIDENAVIGATIONBAR = By.xpath("//a[@href='#general-pages']");
	
	public DashboardPage_REKYCSuperadmin clickOn(String text)
	{
		By locator=null;
		switch(text.toLowerCase())
		{
		case "search":
			locator = SEARCH_LOCATOR;
			break;
		case "visitadmin":
			locator = VISITADMIN_BUTTON;
			break;
		case "workflowv2":
			locator = WORKFLOWV2_SIDENAVIGATIONBAR;
			break;
		case "makerchecker":
			locator = MAKERANDCHECKER_SIDENAVIGATIONBAR;
			break;
		default:
			throw new IllegalArgumentException("Unable to locate the locator: "+locator);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds("visitadmin".equalsIgnoreCase(text) ? 20 : 15));
		WebElement searchbar;
		try {
			searchbar = wait.until(ExpectedConditions.elementToBeClickable(locator));
		} catch (TimeoutException e) {
			if (!"visitadmin".equalsIgnoreCase(text)) {
				throw e;
			}
			String initialRow = getDriver().findElements(By.cssSelector("#company-table tbody tr")).stream()
					.findFirst().map(WebElement::getText).orElse("");
			if (initialRow.contains("Loading companies")) {
				getDriver().navigate().refresh();
				WebElement search = new WebDriverWait(getDriver(), Duration.ofSeconds(15))
						.until(ExpectedConditions.elementToBeClickable(SEARCH_LOCATOR));
				search.clear();
				search.sendKeys("meon");
				try {
					new WebDriverWait(getDriver(), Duration.ofSeconds(45))
							.until(ExpectedConditions.elementToBeClickable(VISITADMIN_BUTTON)).click();
					return this;
				} catch (TimeoutException ignored) {
					// Report the refreshed page state below.
				}
			}
			String searchValue = getDriver().findElements(SEARCH_LOCATOR).stream()
					.findFirst().map(element -> element.getAttribute("value")).orElse("<missing>");
			int rowCount = getDriver().findElements(By.cssSelector("#company-table tbody tr")).size();
			String firstRow = getDriver().findElements(By.cssSelector("#company-table tbody tr")).stream()
					.findFirst().map(WebElement::getText).orElse("<none>")
					.replaceAll("\\s+", " ");
			if (firstRow.length() > 160) {
				firstRow = firstRow.substring(0, 160);
			}
			int adminButtons = getDriver().findElements(By.cssSelector("button.visit-admin")).size();
			boolean sessionReady = Boolean.TRUE.equals(((JavascriptExecutor) getDriver())
					.executeScript("return !!sessionStorage.getItem('access_token')"));
			boolean noMatches = !getDriver().findElements(By.xpath("//*[contains(text(), 'No matching records')]")).isEmpty();
			throw new AssertionError("MEON Visit Admin unavailable: search='" + searchValue
					+ "', tableRows=" + rowCount + ", adminButtons=" + adminButtons
					+ ", firstRow='" + firstRow + "', sessionReady=" + sessionReady
					+ ", noMatchingRecords=" + noMatches + ", viewport="
					+ getDriver().manage().window().getSize(), e);
		}
		searchbar.click();
		return this;
	}
	
	public DashboardPage_REKYCSuperadmin writeService(String text, String input)
	{
		By locator=null;
		switch(text.toLowerCase())
		{
		case "search":
			locator = SEARCH_LOCATOR;
			break;
		default:
			throw new IllegalArgumentException("Unable to locate the locator: "+locator);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement textTo = wait.until(ExpectedConditions.elementToBeClickable(locator));
		textTo.click();
		textTo.clear();
		textTo.sendKeys(input);
		return this;
	}

}
