package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class DocumentPage_CorporateRekycUAT extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h1[normalize-space()='Document Upload']");
	private static final By COMPLETION_PERCENTAGE = By.id("huf_doc_percent");
	private static final By COMPLETION_COUNT = By.id("huf_doc_count");
	private static final By REQUIRED_DOCUMENTS = By.cssSelector("#show_document .huf-doc-require[mandatory='true']");
	private static final By NEXT_BUTTON = By.id("next_btn");
	private static final By PREVIOUS_BUTTON = By.id("previous_btn");

	public DocumentPage_CorporateRekycUAT(WebDriver driver) {
		super(driver);
	}

	public boolean isDisplayed() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).isDisplayed();
	}

	public String getCompletionPercentage() {
		return visibleText(COMPLETION_PERCENTAGE);
	}

	public String getCompletionCount() {
		return visibleText(COMPLETION_COUNT);
	}

	public int getRequiredDocumentCount() {
		return getDriver().findElements(REQUIRED_DOCUMENTS).size();
	}

	public boolean areAllRequiredDocumentsUploaded() {
		for (WebElement document : getDriver().findElements(REQUIRED_DOCUMENTS)) {
			if (!"true".equals(document.getAttribute("data-uploaded"))
					|| "true".equals(document.getAttribute("data-rejected"))) {
				return false;
			}
		}
		return getRequiredDocumentCount() > 0;
	}

	public String openFinalSubmissionConfirmationAndDismiss() {
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
		Alert confirmation = new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.alertIsPresent());
		String message = confirmation.getText();
		confirmation.dismiss();
		return message;
	}

	public DocumentPage_CorporateRekycUAT goBackToPersonalPage() {
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(PREVIOUS_BUTTON)).click();
		new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.urlContains("/Personal/page"));
		return this;
	}

	private String visibleText(By locator) {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
	}
}
