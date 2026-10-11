package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class NomineePage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='Nominee']");
	private static final By NOMINEE_DROPDOWN = By.xpath("//select[@id='add_nominee']");
	private static final By SUBMIT_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By NUMBER_OF_NOMINEES = By.xpath("//select[@id='total_nominees']");
	private static final By SELECT_CHECKBOX = By.xpath("//input[@id='opt_out_terms']/following-sibling::span[1]");

	public NomineePage(WebDriver driver) {
		super(driver);
	}

	public NomineePage selectConsentCheckboxByValue() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(SELECT_CHECKBOX));
		if (!checkbox.isSelected()) {
			checkbox.click();
		}
		return this;
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public NomineePage selectNomineeDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(NOMINEE_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value);
		return this;
	}

	public NomineePage selectNumberOfNomineeDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(NUMBER_OF_NOMINEES));
		Select select = new Select(dropdown);
		select.selectByValue(value);
		return this;
	}

	public NomineePage clickOnSubmitButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
		otp.click();
		return this;
	}

	public NomineePage assertPageLoaded() {
			Assert.assertTrue(isPageHeadingVisible(), "Nominee page did not load");
			return this;
		}
	}

