package com.ui.pages.workflow1;

import java.io.File;
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
public class IncomeProofPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='Income Proof']");
	private static final By BANK_DOCUMENT_UPLOAD = By.xpath("//input[@id='upload_income_proof']");
	private static final By NEXT_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By ENTER_DATA_INCOME = By.xpath("//input[@id='enterdata']");
	private static final By DELETE_BUTTON = By
			.xpath("//button[contains(@class, 'btn-danger')]//i[@class='fa fa-trash']");

	public IncomeProofPage(WebDriver driver) {
		super(driver);
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}
	
	public IncomeProofPage writeIncome()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(ENTER_DATA_INCOME));
		otp.click();
		otp.clear();
		otp.sendKeys("500000");
		return this;
	}

	public IncomeProofPage uploadIncomeDocument(String fileName) {
		File file = new File(System.getProperty("user.dir") + "/src/test/resources/data/" + fileName);
		String absolutePath = file.getAbsolutePath();
		enterText(BANK_DOCUMENT_UPLOAD, absolutePath);
		return this;
	}

	public IncomeProofPage clickOnNextButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
		nextBtn.click();
		return this;
	}

	public IncomeProofPage clickOnDeleteButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(DELETE_BUTTON));
		deleteBtn.click();
		return this;
	}

	public IncomeProofPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Income proof page did not load");
		return this;
	}
	
	public IncomeProofPage waitForOverlayToDisappear() {
	    WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
	    wait.until(ExpectedConditions.invisibilityOfElementLocated(
	            By.cssSelector(".overlay-bg")));
	    return this;
	}

}
