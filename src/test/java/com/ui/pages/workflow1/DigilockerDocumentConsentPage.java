package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class DigilockerDocumentConsentPage extends BrowserUtility {
	
	private static final By DENY_BUTTON  = By.xpath("//input[@value='Deny']");
	private static final By ALLOW_BUTTON = By.xpath("//input[@value='Allow']");

	public DigilockerDocumentConsentPage(WebDriver driver) {
		super(driver);
	}

	public DigilockerDocumentConsentPage selectConsentCheckboxByValue(String value) {

		By checkboxLocator = By.xpath("//input[@type='checkbox' and @name='consent' and @value='" + value + "']");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));

		if (!checkbox.isSelected()) {
			checkbox.click();
		}
		return this;
	}
	
	public DigilockerDocumentConsentPage clickOnAllowButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement allowBtn = wait.until(ExpectedConditions.elementToBeClickable(ALLOW_BUTTON));
		allowBtn.click();
		return this;
	}
	
	public DigilockerDocumentConsentPage clickOnDenyButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement denyBtn = wait.until(ExpectedConditions.elementToBeClickable(DENY_BUTTON));
		denyBtn.click();
		return this;
	}

}
