package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;


public class EsignPage_AnnualIncomeModificationRekycUAT extends BrowserUtility {

	public EsignPage_AnnualIncomeModificationRekycUAT(WebDriver driver) {
		super(driver);
	}
	private static final Duration TIMEOUT = Duration.ofSeconds(30);
	
	private static final By SUCCESSMSG_ESIGNPAGE = By.xpath("//h1[@class='esign-success-title']");
	
	public boolean isSuccessMsgDisplayed()
	{
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(SUCCESSMSG_ESIGNPAGE)).isDisplayed();
	}
	
	private WebDriverWait waitFor() {
		return new WebDriverWait(getDriver(), TIMEOUT);
	}
	

}
