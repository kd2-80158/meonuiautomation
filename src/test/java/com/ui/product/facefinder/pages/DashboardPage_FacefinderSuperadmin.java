package com.ui.product.facefinder.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class DashboardPage_FacefinderSuperadmin extends BrowserUtility {

	public DashboardPage_FacefinderSuperadmin(WebDriver driver) {
		super(driver);
	}
	
	private static final By SEARCHBAR_SUPERADMIN = By.xpath("//input[@placeholder='Search...']");
	private static final By HEADING_SUPERADMIN = By.xpath("//img[@alt='Logo']");
	private static final By COMPANY_SUPERADMIN = By.xpath("//a[text()='Saurabh Chhimwal']");
    private static final By VISITADMIN_COMPANY_SUPERADMIN = By.xpath("//a[text()='Visit Admin']");
    
    public boolean isHeadingVisible()
    {
    	WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
    	WebElement heading=wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING_SUPERADMIN));
    	return heading.isDisplayed();
    }
    
    public DashboardPage_FacefinderSuperadmin clickOn(String text)
    {
    	By locator=null;
    	switch(text.toLowerCase())
    	{
    	case "searchbar":
    		locator = SEARCHBAR_SUPERADMIN;
    		break;
    	case "company_text":
    		locator = COMPANY_SUPERADMIN;
    		break;
    	case "visitadmin_button":
    		locator = VISITADMIN_COMPANY_SUPERADMIN;
    		break;
    	default:
    		throw new IllegalArgumentException("Unable to find the locator "+locator);
    	}
    	WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
    	WebElement option=wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    	option.click();
    	return this;
    }
    
    
}
