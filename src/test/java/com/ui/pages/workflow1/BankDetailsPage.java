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
public class BankDetailsPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='Bank']");
	private static final By IFSC = By.xpath("//input[@id='ifsc']");
	private static final By MICR = By.xpath("//input[@id='micr1']");
	private static final By ACCOUNT_NUMBER = By.xpath("//input[@id='account_number']");
	private static final By CONFIRM_ACCOUNT_NUMBER = By.xpath("//input[@id='reenter_account_number']");
	private static final By BANK_ADDRESS = By.xpath("//input[@id='bank_address']");
	private static final By ACCOUNT_TYPE_DROPDOWN = By.xpath("//select[@id='account_type']");
	private static final By SUBMIT_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By PROCEED_BUTTON_PENNY = By.xpath("(//button[normalize-space(.)='Proceed'])[2]");

	public BankDetailsPage(WebDriver driver) {
		super(driver);
	}

	public BankDetailsPage writeIfscCode(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement ifscCode = wait.until(ExpectedConditions.visibilityOfElementLocated(IFSC));
		ifscCode.click();
		ifscCode.clear();
		ifscCode.sendKeys(value);
		return this;
	}

	public BankDetailsPage writeMicrCode(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement micrCode = wait.until(ExpectedConditions.visibilityOfElementLocated(MICR));
		micrCode.click();
		micrCode.clear();
		micrCode.sendKeys(value);
		return this;
	}

	public BankDetailsPage writeAccountNumber(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement accountNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_NUMBER));
		accountNumber.click();
		accountNumber.clear();
		accountNumber.sendKeys(value);
		return this;
	}

	public BankDetailsPage writeConfirmAccountNumber(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement accountNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRM_ACCOUNT_NUMBER));
		accountNumber.click();
		accountNumber.clear();
		accountNumber.sendKeys(value);
		return this;
	}

	public BankDetailsPage writeBankAddress(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement bankAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(BANK_ADDRESS));
		bankAddress.click();
		bankAddress.clear();
		bankAddress.sendKeys(value);
		return this;
	}

	public BankDetailsPage selectAccountTypeDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(ACCOUNT_TYPE_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value);
		return this;
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public BankDetailsPage clickOnSubmitButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
		otp.click();
		return this;
	}
	
	public BankDetailsPage clickOnProceedButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_BUTTON_PENNY));
		otp.click();
		return this;
	}

	public BankDetailsPage assertPageLoaded() {
			Assert.assertTrue(isPageHeadingVisible(), "Bank Details page did not load");
			return this;
		}
	}

