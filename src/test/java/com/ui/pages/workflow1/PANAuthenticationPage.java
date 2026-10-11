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
public class PANAuthenticationPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='PAN']");
	private static final By PAN_NAME = By.xpath("//input[@id='name']");
	private static final By PAN_NUMBER = By.xpath("//input[@id='pan_number']");
	private static final By SUBMIT_BUTTON = By.xpath("//button[@id='SaveData']");

	public PANAuthenticationPage(WebDriver driver) {
		super(driver);
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public PANAuthenticationPage writePANName(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(120));
		WebElement panName = wait.until(ExpectedConditions.visibilityOfElementLocated(PAN_NAME));
		panName.click();
		panName.clear();
		panName.sendKeys(value);
		return this;
	}

	public PANAuthenticationPage writePANNumber(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(120));
		WebElement panNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(PAN_NUMBER));
		panNumber.click();
		panNumber.clear();
		panNumber.sendKeys(value);
		return this;
	}

	public PANAuthenticationPage clickOnSubmitButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BUTTON));
		submitBtn.click();
		submitBtn.click();
		return this;
	}

	public PANAuthenticationPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "PAN authentication page did not load");
		return this;
	}
}
