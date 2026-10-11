package com.ui.product.apimanager.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class LoginPage_APIManager extends BrowserUtility{
	
	public LoginPage_APIManager(WebDriver driver) {
		super(driver);
	}

	public static By EMAIL_LOCATOR = By.xpath("//input[@name='email']");
	public static By PASSWORD_LOCATOR = By.xpath("//input[@name='password']");
	public static By SIGN_IN_BUTTON_LOCATOR = By.xpath("//button[contains(normalize-space(), 'Sign in')]");
	public static By ERROR_MSG_INVALID_CREDENTIALS = By.xpath("//div[contains(text(),'Invalid credentials')]");
	public static By DASHBOARD_HEADING_LOCATOR = By.xpath("//aside[@class='border-r bg-card/60 p-5 backdrop-blur-md']");
	
	
	public LoginPage_APIManager writeEmail(String email1)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_LOCATOR));
		email.click();
		email.clear();
		email.sendKeys(email1);
		return this;
	}
	
	public LoginPage_APIManager writePassword(String password1)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_LOCATOR));
		password.click();
		password.clear();
		password.sendKeys(password1);
		return this;
	}
	
	public LoginPage_APIManager clickOnSignInButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON_LOCATOR));
		signInBtn.click();
		return this;
	}
	
	public boolean isErrorMessageVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MSG_INVALID_CREDENTIALS));
		if(errorMsg.isDisplayed())
			return true;
		return false;
	}
	
	public boolean isDashboardVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement dashboard = wait.until(ExpectedConditions.visibilityOfElementLocated(DASHBOARD_HEADING_LOCATOR));
		if(dashboard.isDisplayed())
			return true;
		return false;
	}
	
	
	

}
