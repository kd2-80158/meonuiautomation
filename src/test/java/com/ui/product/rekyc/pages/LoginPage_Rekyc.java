package com.ui.product.rekyc.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class LoginPage_Rekyc extends BrowserUtility{

	public LoginPage_Rekyc(WebDriver driver) {
		super(driver);
	}

	private static final By LOGIN_BUTTON = By.xpath("//button[contains(@aria-label,'Login options')]");

	public WebElement getLoginButton()
	{
		return getDriver().findElement(LOGIN_BUTTON);
	}
}
