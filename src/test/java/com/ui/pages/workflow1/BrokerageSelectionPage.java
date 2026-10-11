package com.ui.pages.workflow1;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.ui.utilities.BrowserUtility;

public class BrokerageSelectionPage extends BrowserUtility{
	
	private static final By PAGE_HEADING = By.xpath("//h3[text()='Segments']");
	private static final By TRADING_PREFERENCE_DROPDOWN = By.xpath("//select[@id='select_trading_preferences']");
	private static final By NEXT_BUTTON = By.xpath("//button[@id='SaveData']");
	private static final By BROKERAGE_PLAN = By.xpath("//input[@class='ButtonMain brokerage']");
    private static final By SELECT_BUTTON = By.xpath("//button[@class='btn continue select_btn']");
	
    public BrokerageSelectionPage(WebDriver driver) {
		super(driver);
	}
	
	public boolean isPageHeadingVisible()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
		if(title.isDisplayed())
			return true;
		return false;
	}
	
	public BrokerageSelectionPage selectTradingPreference(String value)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(TRADING_PREFERENCE_DROPDOWN));
		Select select = new Select(dropdown);
		select.selectByValue(value); //or you can change this: select.selectByIndex(1) or select.selectByVisibleText("Only Trading");
		return this;
	}
	
	public BrokerageSelectionPage clickOnNextButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement resendBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
		resendBtn.click();
		return this;
	}
	
	public BrokerageSelectionPage clickOnBrokeragePlan()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		WebElement brokeragePlan = wait.until(ExpectedConditions.elementToBeClickable(BROKERAGE_PLAN));
		brokeragePlan.click();
		return this;
	}
	
	public BrokerageSelectionPage clickOnSelectButton()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement selectBtn = wait.until(ExpectedConditions.elementToBeClickable(SELECT_BUTTON));
		selectBtn.click();
		return this;
	}

	public BrokerageSelectionPage assertPageLoaded() {
		Assert.assertTrue(isPageHeadingVisible(), "Brokerage Selection page did not load");
		return this;
	}

}
