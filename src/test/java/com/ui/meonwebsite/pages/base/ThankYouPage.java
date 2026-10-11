package com.ui.meonwebsite.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class ThankYouPage extends BrowserUtility {
	
	private static final By PAGE_HEADING = By.xpath("//h1[text()='Thank You']");

	public ThankYouPage(WebDriver driver) {
		super(driver);
	}
	
	public boolean isPageHeadingVisible() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement pageHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));

		return pageHeading.isDisplayed();
	}

}
