package com.ui.meonwebsite.pages.base;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

public class HomePage extends BrowserUtility {
    Logger logger = LoggerUtility.getLogger(this.getClass());
	public HomePage(WebDriver driver) {
		super(driver);
	}

	private static final By LOGO = By.xpath("//img[@alt='logo']");
	private static final By SIGN_IN_BUTTON = By.xpath("//button[text()='Sign In']");
	private static final By GET_DEMO_BUTTON = By.xpath("//button[text()='Get Demo']");
	private static final By ESIGN_BUTTON = By.xpath("//button[@class='btn-try']");
	private static final By API_DOCUMENTATION_BUTTON = By.xpath("//button[@class='btn-demo']");
	private static final By PRODUCTS_DROPDOWN = By.xpath("//a[@id='productsDropdown']");
	private static final By AadhaarEKYC_OPTION = By.xpath("(//a[@class='list-group-item listhover'])[1]");
    private static final By KYC_SOLUTION = By.xpath("//p[normalize-space()='KYC Solutions']");
	public boolean isLogoVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGO));
		return logo.isDisplayed();
	}
	
	public boolean isHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(KYC_SOLUTION));
		return heading.isDisplayed();
	}

	public HomePage clickOnSignInButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON));
		signInBtn.click();
		return this;
	}

	public HomePage clickOnGetDemoButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement demoBtn = wait.until(ExpectedConditions.elementToBeClickable(GET_DEMO_BUTTON));
		demoBtn.click();
		return this;
	}

	public HomePage clickOnESignButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement esignBtn = wait.until(ExpectedConditions.elementToBeClickable(ESIGN_BUTTON));
		esignBtn.click();
		return this;
	}

	public HomePage clickOnApiDocumentationButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement apiBtn = wait.until(ExpectedConditions.elementToBeClickable(API_DOCUMENTATION_BUTTON));
		apiBtn.click();
		return this;
	}

	public HomePage goToAadhaarKYCOptionInDropdown() {
		clickDropdown(PRODUCTS_DROPDOWN);
        waitForProductsDropdown(KYC_SOLUTION);
		if(isHeadingVisible())
		{
			logger.info("KYC Solutions is visible!!!");
		}
		scrollIntoView(AadhaarEKYC_OPTION);
		jsClick(AadhaarEKYC_OPTION);
		return this;
	}

}
