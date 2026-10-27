
package com.ui.product.rekyc.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import com.ui.utilities.RekycNavigationUtility;

import com.ui.utilities.BrowserUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class OthersPage_AnnualIncomeModificationRekycUAT extends BrowserUtility {

	private static final Duration TIMEOUT = Duration.ofSeconds(30);

	private static final By PAGE_HEADING = By.xpath("//h2[normalize-space()='Other Details']");

	private static final By ACCOUNT_DETAILS = By.id("account-details-card");

	private static final By ANNUAL_INCOME_CONTAINER = By.id("addannual_income");

	private static final By ANNUAL_INCOME_LABEL = By.xpath("//label[@for='select_currency']");

	private static final By ANNUAL_INCOME_DROPDOWN = By.id("select_currency");

	private static final By NEXT_BUTTON = By.id("next_btn");

	private static final By OVERLAY = By.cssSelector(".overlay-bg");

	public static final String BELOW_1_LAC = "below_1_lac";
	public static final String ONE_TO_FIVE_LAC = "1_5_lac";
	public static final String FIVE_TO_TEN_LAC = "5_10_lac";
	public static final String TEN_TO_TWENTY_FIVE_LAC = "10_25_lac";
	public static final String ABOVE_TWENTY_FIVE_LAC = "more_than_25_lac";

	public OthersPage_AnnualIncomeModificationRekycUAT(WebDriver driver) {
		super(driver);
	}

	private WebDriverWait waitFor() {
		return new WebDriverWait(getDriver(), TIMEOUT);
	}

	public boolean isDisplayed() {

		waitFor().until(ExpectedConditions.urlContains("/Others/page"));

		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).isDisplayed();
	}

	public void waitUntilReady() {

		waitFor().until(ExpectedConditions.urlContains("/Others/page"));

		waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_DROPDOWN));
	}

	public boolean isAccountDetailsDisplayed() {

		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_DETAILS)).isDisplayed();
	}

	public boolean isAnnualIncomeDisplayed() {

		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_CONTAINER)).isDisplayed()
				&& waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_DROPDOWN)).isDisplayed();
	}

	public String getSelectedAnnualIncome() {

		WebElement dropdown = waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_DROPDOWN));

		return new Select(dropdown).getFirstSelectedOption().getText().trim();
	}

	public String getSelectedAnnualIncomeValue() {

		WebElement dropdown = waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_DROPDOWN));

		return new Select(dropdown).getFirstSelectedOption().getAttribute("value");
	}

	public OthersPage_AnnualIncomeModificationRekycUAT selectAnnualIncome(String income) {

		WebElement dropdown = waitFor().until(ExpectedConditions.elementToBeClickable(ANNUAL_INCOME_DROPDOWN));

		new Select(dropdown).selectByVisibleText(income);

		waitFor().until(driver -> income.equals(getSelectedAnnualIncome()));

		return this;
	}

	public OthersPage_AnnualIncomeModificationRekycUAT selectAnnualIncomeByValue(String value) {

		WebElement dropdown = waitFor().until(ExpectedConditions.elementToBeClickable(ANNUAL_INCOME_DROPDOWN));

		new Select(dropdown).selectByValue(value);

		waitFor().until(driver -> value.equals(getSelectedAnnualIncomeValue()));

		return this;
	}

	public List<String> getAvailableIncomeSlabs() {

		WebElement dropdown = waitFor().until(ExpectedConditions.visibilityOfElementLocated(ANNUAL_INCOME_DROPDOWN));

		List<String> values = new ArrayList<>();

		for (WebElement option : new Select(dropdown).getOptions()) {

			String value = option.getAttribute("value");

			// Exclude the empty placeholder option
			if (value != null && !value.isBlank()) {
				values.add(option.getText().trim());
			}
		}

		return values;
	}

	public boolean isAnnualIncomeSelected() {

		String value = getSelectedAnnualIncomeValue();

		return value != null && !value.isBlank();
	}

	public boolean isNextButtonEnabled() {

		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(NEXT_BUTTON)).isEnabled();
	}

//	public DocumentPage_AnnualIncomeModificationRekycUAT clickOnNextButton() {
//
//		if (!isAnnualIncomeSelected()) {
//			throw new IllegalStateException("Annual Income must be selected before " + "navigating to Document page");
//		}
//
//		WebElement next = waitFor().until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
//
//		((JavascriptExecutor) getDriver())
//				.executeScript("arguments[0].scrollIntoView(" + "{block:'center',inline:'nearest'});", next);
//
//		try {
//			next.click();
//		} catch (ElementClickInterceptedException e) {
//			throw new IllegalStateException("Next button click intercepted", e);
//		}
//
//		waitFor().until(ExpectedConditions.urlContains("/Document/page"));
//
//		DocumentPage_AnnualIncomeModificationRekycUAT page = new DocumentPage_AnnualIncomeModificationRekycUAT(
//				getDriver());
//
//		if (!page.isDisplayed()) {
//			throw new IllegalStateException("Document Upload page did not appear");
//		}
//
//		return page;
//	}

//	public OthersPage_AnnualIncomeModificationRekycUAT clickOnNextButton() {
//
//		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
//
//		// Wait until the overlay is no longer visible.
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(OVERLAY));
//
//		WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
//
//		nextBtn.click();
//
//		return this;
//	}

	public OthersPage_AnnualIncomeModificationRekycUAT clickOnNextButton() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		JavascriptExecutor js = (JavascriptExecutor) getDriver();

		WebElement nextBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(NEXT_BUTTON));

		js.executeScript("arguments[0].scrollIntoView({block:'center'});", nextBtn);

		wait.until(driver -> {
			WebElement button = driver.findElement(NEXT_BUTTON);

			return button.isEnabled() && Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
					"const b = arguments[0];" + "const r = b.getBoundingClientRect();"
							+ "const x = r.left + r.width / 2;" + "const y = r.top + r.height / 2;"
							+ "const top = document.elementFromPoint(x, y);" + "return top === b || b.contains(top);",
					button));
		});

		wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();

		return this;
	}

	public OthersPage_AnnualIncomeModificationRekycUAT clickOnNextButtonNew() {

		RekycNavigationUtility.clickNextWithRetry(getDriver(), By.id("next_btn"), "/Document/page");

		return this;
	}
}
