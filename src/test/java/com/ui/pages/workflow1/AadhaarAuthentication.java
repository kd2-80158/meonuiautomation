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
public class AadhaarAuthentication extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h5[text()='Sign up']");
	private static final By AADHAAR = By.xpath("//input[@id='aadhaar_1']");
	private static final By NEXT_BUTTON = By.xpath("//button[text()='Next']");

	public AadhaarAuthentication(WebDriver driver) {
		super(driver);
	}

	public AadhaarAuthentication writeAadhaar(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(120));
		WebElement aadhaar = wait.until(ExpectedConditions.visibilityOfElementLocated(AADHAAR));
		aadhaar.clear();
		aadhaar.sendKeys(value);
		return this;
	}
	
	public AadhaarAuthentication clickOnNextButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement aadhaar = wait
				.until(ExpectedConditions.visibilityOfElementLocated(NEXT_BUTTON));
		aadhaar.click();
		return this;
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if(title.isDisplayed())
			return true;
		return false;
	}

	public AadhaarAuthentication assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Aadhaar Authentication page did not load");
		return this;
	}

}
