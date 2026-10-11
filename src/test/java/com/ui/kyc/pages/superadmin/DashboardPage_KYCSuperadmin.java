package com.ui.kyc.pages.superadmin;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class DashboardPage_KYCSuperadmin extends BrowserUtility{

	public DashboardPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}
	
	private static final By USERS_SIDENAVIGATIONBAR = By.xpath("//a[contains(@href, '/superadmin/users')]");
	private static final By EMAILEDITOR_SIDENAVIGATIONBAR = By.xpath("//a[contains(@href, '/superadmin/email-editor')]");
	
	public DashboardPage_KYCSuperadmin clickOnUsers_KYCSuperadmin()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement user = wait.until(ExpectedConditions.elementToBeClickable(USERS_SIDENAVIGATIONBAR));
		user.click();
		return this;
	}
	
	public DashboardPage_KYCSuperadmin clickOnEmailEditor_KYCSuperadmin()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(EMAILEDITOR_SIDENAVIGATIONBAR));
		email.click();
		return this;
	}

}
