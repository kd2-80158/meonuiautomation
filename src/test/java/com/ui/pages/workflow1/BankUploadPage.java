package com.ui.pages.workflow1;

import java.io.File;
import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class BankUploadPage extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());
	private static final By PAGE_HEADING = By.xpath("//h3[text()='Bank Upload']");
	private static final By BANK_DOCUMENT_UPLOAD = By.xpath("//input[@id='upload_bank_proof']");
	private static final By NEXT_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By DELETE_BUTTON = By.xpath("//button[contains(@class, 'btn-danger')]//i[@class='fa fa-trash']");

	public BankUploadPage(WebDriver driver) {
		super(driver);
	}	
	
	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}
	
	public BankUploadPage uploadBankDocument(String fileName) {
        File file = new File(System.getProperty("user.dir") + "/src/test/resources/data/"+fileName);
        String absolutePath = file.getAbsolutePath();
        logger.info("absolute path is: "+absolutePath);
        enterText(BANK_DOCUMENT_UPLOAD, absolutePath);
        return this;
    }
	
	public BankUploadPage clickOnNextButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
		nextBtn.click();
		return this;
	}
	
	public BankUploadPage clickOnDeleteButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(DELETE_BUTTON));
		deleteBtn.click();
		return this;
	}

	public BankUploadPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Bank Upload page did not load");
		return this;
	}

	
	
}
