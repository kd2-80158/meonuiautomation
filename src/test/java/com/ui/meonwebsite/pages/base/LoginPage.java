package com.ui.meonwebsite.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class LoginPage extends BrowserUtility{
	
	public LoginPage(WebDriver driver) {
		super(driver);
	}
	private static final By PAGE_HEADING = By.xpath("//h5[text()='Let’s get you in!']");
	private static final By GOOGLE_SIGNIN_BUTTON = By.xpath("(//button[@tabindex='0'])[2]");
	private static final By EMAIL_ADDRESS = By.xpath("//input[@type='email']");
	private static final By PASSWORD = By.xpath("//input[@type='password']");
	private static final By LOGIN_BUTTON = By.xpath("(//button[@tabindex='0'])[3]");
	private static final By FORGET_PASSWORD_LINK = By.xpath("//p[text()='Forgot Password?']");
	private static final By SIGNUP_BUTTON = By.xpath("(//button[@tabindex='0'])[1]");
	private static final By LOGO = By.xpath("x//img[@alt='logo']");
	
	public LoginPage writeEmail(String userEmail)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(EMAIL_ADDRESS));
		email.click();
		email.clear();
		email.sendKeys(userEmail);
		return this;
	}
	
	public LoginPage writePassword(String userPassword)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement password = wait.until(ExpectedConditions.elementToBeClickable(PASSWORD));
		password.click();
		password.clear();
		password.sendKeys(userPassword);
		return this;
	}
	
	public boolean isLogoVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGO));
		return logo.isDisplayed();
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		return heading.isDisplayed();
	}
	
	public LoginPage clickOnLoginButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
		signInBtn.click();
		return this;
	}
	
	public LoginPage clickOnGoogleSignInButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_SIGNIN_BUTTON));
		signInBtn.click();
		return this;
	}
	
	public LoginPage clickOnSignupButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(SIGNUP_BUTTON));
		signInBtn.click();
		return this;
	}
	
	public LoginPage clickOnForgetPasswordLink()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement forgetPasswordLink = wait.until(ExpectedConditions.elementToBeClickable(FORGET_PASSWORD_LINK));
		forgetPasswordLink.click();
		return this;
	}
	
	
}
