package com.ui.admin.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class DashboardPage extends BrowserUtility {
	
	private static final By PAGE_HEADING = By.xpath("//h3[text()='KYC']");
	private static final By SEARCH_BAR = By.xpath("//input[@id='search__company']");
	
	
	public DashboardPage(WebDriver driver) {
		super(driver);
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement pageHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
	    return pageHeading.isDisplayed();
	}
	
	public String getTextInSearchBar()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement text = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_BAR));
        return text.getText();
	}

}
