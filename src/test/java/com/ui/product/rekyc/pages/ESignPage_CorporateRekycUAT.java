package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

/** Stops at the PDF preview; it never submits the Aadhaar eSign flow. */
public class ESignPage_CorporateRekycUAT extends BrowserUtility {

	private static final By ESIGN_LINK = By.xpath("//a[contains(normalize-space(.), 'CORPORATE KRA eSign')]");

	public ESignPage_CorporateRekycUAT(WebDriver driver) {
		super(driver);
	}

	public boolean isDisplayed() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.visibilityOfElementLocated(ESIGN_LINK)).isDisplayed();
	}

	public ESignPage_CorporateRekycUAT openCorporatePdfPreview() {
		WebElement link = new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.elementToBeClickable(ESIGN_LINK));
		link.click();
		new WebDriverWait(getDriver(), Duration.ofSeconds(120)).until(driver ->
				driver.getCurrentUrl().startsWith("https://esignuat.meon.co.in/EsignServices/NeweSigngateway/"));
		return this;
	}

	public boolean isCorporatePdfRendered() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(120)).until(driver -> {
			Object rendered = ((JavascriptExecutor) driver).executeScript(
					"const pages = Array.from(document.querySelectorAll('canvas')).filter(c => c.width > 0 && c.height > 0);"
					+ "return pages.length > 0 && /\\b[1-9]\\d*\\s*\\/\\s*[1-9]\\d*\\b/.test(document.body.innerText);");
			return Boolean.TRUE.equals(rendered);
		});
	}
}
