package com.ui.admin.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class SignInPage extends BrowserUtility {
	
	private static final By INVALID_CREDENTIALS_ERROR_MESSAGE = By.xpath("//div[@id='error-message']");
	private static final By PAGE_HEADING = By.xpath("//div[@class='superadmin-header-normal']");
    private static final By GOOGLE_SIGNIN_BUTTON = By.xpath("//div[@id='googleSignInButton']");
    private static final By CHOOSE_ACCOUNT = By.xpath("//div[@data-identifier='saurabh.chhimwal@meon.co.in']");
    private static final By EMAIL = By.xpath("//input[@type='email']");
    private static final By PASSWORD = By.xpath("//input[@type='password']");
    private static final By NEXT_BUTTON_PASSWORD = By.xpath("(//button[@type='button'])[2]");
    private static final By NEXT_BUTTON_EMAIL = By.xpath("(//button[@type='button'])[4]");
    private static final By GMAIL_ERROR_MESSAGE = By.xpath("//span[text()='Wrong password. Try again or click \"Try another way\" for more options.']");
	
	public SignInPage(WebDriver driver) {
		super(driver);
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement pageHeading = wait.until(ExpectedConditions.elementToBeClickable(PAGE_HEADING));
	    return pageHeading.isDisplayed();
	}
	
	public SignInPage writeEmail(String value)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement emailInputBox = wait.until(ExpectedConditions.elementToBeClickable(EMAIL));
		emailInputBox.click();
		emailInputBox.clear();
		emailInputBox.sendKeys(value);
        return this;
	}
	
	public SignInPage writePassword(String value)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement emailInputBox = wait.until(ExpectedConditions.elementToBeClickable(PASSWORD));
		emailInputBox.click();
		emailInputBox.clear();
		emailInputBox.sendKeys(value);
        return this;
	}
	
	public String getInvalidCredentialsErrorMessage()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(INVALID_CREDENTIALS_ERROR_MESSAGE));
		return errorMsg.getText();
	}
	
	public String getGmailErrorMessage()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(GMAIL_ERROR_MESSAGE));
		return errorMsg.getText();
	}
	
	public SignInPage clickOnSignInButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_SIGNIN_BUTTON));
		signInBtn.click();
		return this;
	}
	
	public SignInPage clickOnNextButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON_PASSWORD));
		nextBtn.click();
		return this;
	}
	
	public SignInPage clickOnNextButtonEmail()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON_EMAIL));
		nextBtn.click();
		return this;
	}
	
	public SignInPage clickOnSuperadminAccount()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement adminEmail = wait.until(ExpectedConditions.elementToBeClickable(CHOOSE_ACCOUNT));
		adminEmail.click();
		return this;
	}

}
