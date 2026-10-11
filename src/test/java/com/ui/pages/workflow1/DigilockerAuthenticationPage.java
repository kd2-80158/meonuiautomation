package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class DigilockerAuthenticationPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h5[text()='You are already registered with DigiLocker']");
	private static final By PIN_BOX1 = By.xpath("//input[@id='v_pin_1']");
	private static final By PIN_BOX2 = By.xpath("//input[@id='v_pin_2']");
	private static final By PIN_BOX3 = By.xpath("//input[@id='v_pin_3']");
	private static final By PIN_BOX4 = By.xpath("//input[@id='v_pin_4']");
	private static final By PIN_BOX5 = By.xpath("//input[@id='v_pin_5']");
	private static final By PIN_BOX6 = By.xpath("//input[@id='v_pin_6']");
	private static final By DONE_BUTTON = By.xpath("//button[@id='verify_pin_button']");

	public DigilockerAuthenticationPage(WebDriver driver) {
		super(driver);
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public DigilockerAuthenticationPage writeDigilockerPin() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX1)).sendKeys("2");
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX2)).sendKeys("5");
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX3)).sendKeys("8");
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX4)).sendKeys("3");
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX5)).sendKeys("6");
		wait.until(ExpectedConditions.elementToBeClickable(PIN_BOX6)).sendKeys("9");
		return this;
	}

	public DigilockerAuthenticationPage clickOnSubmitButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(DONE_BUTTON));
		otp.click();
		return this;
	}

}
