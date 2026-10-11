package com.ui.pages.workflow1;

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
public class CustomerDetailsVerificationPage extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//div[text()='Confirm your details']");
	private static final By PROCEED_BUTTON = By.xpath("//button[text()='Confirm & Proceed']");
	private static final By OK_BUTTON = By.xpath("//button[text()='OK']");

	public CustomerDetailsVerificationPage(WebDriver driver) {
		super(driver);
	}

	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if (title.isDisplayed())
			return true;
		return false;
	}

	public CustomerDetailsVerificationPage clickOnProceedButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement proceedBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(PROCEED_BUTTON));
		proceedBtn.click();
		return this;
	}

	public CustomerDetailsVerificationPage clickOnOKButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(40));
		WebElement okBtn = wait.until(ExpectedConditions.elementToBeClickable(OK_BUTTON));
		okBtn.click();
		return this;
	}

	public boolean isCustomerNameCorrect(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		By NAME = By.xpath("//div[text()='" + value + "']");
		String expectedCustomerName = value;
		WebElement element= wait.until(
	            ExpectedConditions.visibilityOfElementLocated(NAME));				
		String actualCustomerName = element.getText();;
		System.out.println("Expected: " + expectedCustomerName);
		System.out.println("Actual:   " + actualCustomerName);
		return expectedCustomerName.trim().equals(actualCustomerName.trim());
	}

	public boolean isFatherNameCorrect(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		By FATHER_NAME = By.xpath("//div[text()='" + value + "']");
		String expectedFatherName = value;
		WebElement element= wait.until(
	            ExpectedConditions.visibilityOfElementLocated(FATHER_NAME));
		 String actualFatherName = element.getText();
		System.out.println("Expected: " + expectedFatherName);
		System.out.println("Actual:   " + actualFatherName);
		return expectedFatherName.trim().equals(actualFatherName.trim());

	}

	public boolean isDOBCorrect(String value) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		By DOB = By.xpath("//div[text()='" + value + "']");
		String expectedDOB = value;
		WebElement element = wait.until(
	            ExpectedConditions.visibilityOfElementLocated(DOB));
		String actualDOB = element.getText();
		System.out.println("Expected: " + expectedDOB);
		System.out.println("Actual:   " + actualDOB);
		return expectedDOB.trim().equals(actualDOB.trim());
	}

	public CustomerDetailsVerificationPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Customer Details Verification page did not load");
		return this;
	}

	public CustomerDetailsVerificationPage assertCustomerName(String value) {
		Assert.assertTrue(isCustomerNameCorrect(value), "Customer name did not match");
		return this;
	}

	public CustomerDetailsVerificationPage assertFatherName(String value) {
		Assert.assertTrue(isFatherNameCorrect(value), "Father name did not match");
		return this;
	}

	public CustomerDetailsVerificationPage assertDOB(String value) {
		Assert.assertTrue(isDOBCorrect(value), "Father name did not match");
		return this;
	}

}
