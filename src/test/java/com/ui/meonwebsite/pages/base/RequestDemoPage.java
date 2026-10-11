package com.ui.meonwebsite.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.ui.utilities.BrowserUtility;

public class RequestDemoPage extends BrowserUtility {

	private static final By LOGO = By.xpath("//img[@alt='logo']");
	private static final By PAGE_HEADING = By.xpath("//h2[text()='Tell us your company info']");
	private static final By FULL_NAME = By.xpath("//input[@id='fname']");
	private static final By COMPANY_NAME = By.xpath("//input[@id='cname']");
	private static final By COMPANY_EMAIL = By.xpath("//input[@id='cemail']");
	private static final By PHONE_NUMBER = By.xpath("//input[@id='mnumber']");
	private static final By PRIVACY_STATEMENT = By.xpath("//a[text()='Privacy Statement.']");
	private static final By BOOK_DEMO_BUTTON = By.xpath("//button[@id='#submitbtn']");
	private static final By FULL_NAME_ERROR_MESSAGE = By.xpath("//small[@id='fnameErr']");
	private static final By PHONE_NUMBER_ERROR_MESSAGE = By.xpath("//small[@id='mnumberErr']");
	
	public RequestDemoPage(WebDriver driver) {
		super(driver);
	}

	public boolean isLogoVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGO));
		return logo.isDisplayed();
	}

	public boolean isFormHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement pageHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));

		return pageHeading.isDisplayed();
	}
	
	public boolean isFullNameErrorVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(FULL_NAME_ERROR_MESSAGE));

		return errorMsg.isDisplayed();
	}
	
	
	public String getFullNameErrorMessage()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(FULL_NAME_ERROR_MESSAGE));

		return errorMsg.getText();
	}
	
	public String getPhoneNumberErrorMessage()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(PHONE_NUMBER_ERROR_MESSAGE));

		return errorMsg.getText();
	}

	public RequestDemoPage writeFullName(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement fullName = wait.until(ExpectedConditions.visibilityOfElementLocated(FULL_NAME));
		fullName.click();
		fullName.clear();
		fullName.sendKeys(value);
		return this;
	}

	public RequestDemoPage writeCompanyName(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement companyName = wait.until(ExpectedConditions.visibilityOfElementLocated(COMPANY_NAME));
		companyName.click();
		companyName.clear();
		companyName.sendKeys(value);
		return this;
	}

	public RequestDemoPage writeCompanyEmail(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement companyEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(COMPANY_EMAIL));
		companyEmail.click();
		companyEmail.clear();
		companyEmail.sendKeys(value);
		return this;
	}

	public RequestDemoPage writePhoneNumber(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement phoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(PHONE_NUMBER));
		phoneNumber.click();
		phoneNumber.clear();
		phoneNumber.sendKeys(value);
		return this;
	}

	public RequestDemoPage clickOnPrivacyStatementLink() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement privacyLink = wait.until(ExpectedConditions.elementToBeClickable(PRIVACY_STATEMENT));
		privacyLink.click();
		return this;
	}

	public RequestDemoPage clickOnBookDemoButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement demoBtn = wait.until(ExpectedConditions.elementToBeClickable(BOOK_DEMO_BUTTON));
		demoBtn.click();
		return this;
	}

}
