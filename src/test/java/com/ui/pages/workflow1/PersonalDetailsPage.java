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
public class PersonalDetailsPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h3[text()='Personal Details']");
	private static final By MOTHER_NAME = By.xpath("//input[@id='mother_name']");
	private static final By GENDER_DROPDOWN = By.xpath("//select[@id='gender']");
	private static final By MARITAL_STATUS_DROPDOWN = By.xpath("//select[@id='marital_status']");
	private static final By EDUCATION_DROPDOWN = By.xpath("//select[@id='education']");
	private static final By ANNUAL_INCOME_DROPDOWN = By.xpath("//select[@id='annual_income']");
	private static final By TRADING_EXPERIENCE = By.xpath("//input[@id='trading_experience']");
	private static final By POLITICALLY_EXPOSED_DROPDOWN = By.xpath("//select[@id='politically_exposed']");
	private static final By OCCUPATION_DROPDOWN = By.xpath("//select[@id='occupation']");
	private static final By CITIZENSHIP_DROPDOWN = By.xpath("//select[@id='are_ypu_citizen_of_india']");
	private static final By BSDA_DROPDOWN = By.xpath("//select[@id='bsda']");
	private static final By SUBMIT_BUTTON = By.xpath("//button[@id='SaveData']");

	public PersonalDetailsPage(WebDriver driver) {
		super(driver);
	}

	public PersonalDetailsPage writeMotherName(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement motherName = wait.until(ExpectedConditions.visibilityOfElementLocated(MOTHER_NAME));
		motherName.click();
		motherName.clear();
		motherName.sendKeys(value);
		return this;
	}

	public PersonalDetailsPage writeTradingExperience(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement tradingExp = wait.until(ExpectedConditions.visibilityOfElementLocated(TRADING_EXPERIENCE));
		tradingExp.click();
		tradingExp.clear();
		tradingExp.sendKeys(value);
		return this;
	}

	public PersonalDetailsPage selectGenderDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(GENDER_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectMaritalStatusDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(MARITAL_STATUS_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectEducationDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(EDUCATION_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectAnnualIncomeDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(ANNUAL_INCOME_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectPoliticallyExposedDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(POLITICALLY_EXPOSED_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectOccupationDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(OCCUPATION_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectCitizenshipDropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(CITIZENSHIP_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage selectBSDADropdown(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(BSDA_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); // or you can change this: select.selectByIndex(1) or
										// select.selectByVisibleText("Only Trading");
		return this;
	}

	public PersonalDetailsPage clickOnSubmitButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement otp = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
		otp.click();
		return this;
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public PersonalDetailsPage assertPageLoaded() {
			Assert.assertTrue(isPageHeadingVisible(), "Personal Details page did not load");
			return this;
		}
	}
