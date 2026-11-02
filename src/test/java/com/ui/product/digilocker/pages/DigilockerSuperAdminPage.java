package com.ui.product.digilocker.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** DigiLocker-specific Google entry and tenant selection. */
public final class DigilockerSuperAdminPage {
	public static final String URL = "https://digilocker.meon.co.in/digilocker/superadmin";
	private static final By GOOGLE = By.cssSelector("div.google-signup-container[onclick*='googlesignin']");
	private static final By COMPANY_SEARCH = By.id("search__company");
	private static final By COMPANY_TABLE = By.cssSelector("#company-table tbody tr");
	private final WebDriver driver;
	private final WebDriverWait wait;

	public DigilockerSuperAdminPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
	}

	public void open() {
		driver.get(URL);
	}

	public boolean googleSignInVisible() {
		return driver.findElements(GOOGLE).stream().anyMatch(WebElement::isDisplayed);
	}

	public void clickGoogleSignIn() {
		wait.until(ExpectedConditions.elementToBeClickable(GOOGLE)).click();
	}

	public void waitForCompanyDirectory() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(COMPANY_SEARCH));
	}

	public void searchCompany(String company) {
		WebElement search = wait.until(ExpectedConditions.elementToBeClickable(COMPANY_SEARCH));
		search.clear();
		search.sendKeys(company);
		wait.until(d -> d.findElements(COMPANY_TABLE).stream().anyMatch(row -> row.getText().contains(company)));
	}

	public void visitCompany(String company) {
		By visit = By.xpath("//table[@id='company-table']//tr[.//td[contains(normalize-space(.), '" + company
				+ "')]]//button[contains(@class,'visit-admin')]");
		wait.until(ExpectedConditions.elementToBeClickable(visit)).click();
	}
}
