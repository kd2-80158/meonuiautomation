
package com.ui.product.rekyc.pages;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.RekycNavigationUtility;

public class DocumentPage_AnnualIncomeModificationRekycUAT extends BrowserUtility {

	private static final Duration TIMEOUT = Duration.ofSeconds(30);

	private static final By PAGE_HEADING = By.xpath("//h2[text()='Upload Document']");

	private static final By ACCOUNT_DETAILS = By.xpath("//*[normalize-space()='Account Details']");

	private static final By PAN_DOCUMENT = By.xpath("//*[contains(normalize-space(text()),'Pan Image')]");

	private static final By FILE_UPLOAD = By.cssSelector("input[type='file']");

	private static final By UPLOADED_STATUS = By.xpath("//*[normalize-space()='Uploaded']");

	private static final By SHOW_PREVIEW = By
			.xpath("//button[normalize-space()='Show Preview'" + " or @aria-label='Show Preview']");

	private static final By PASSWORD_INPUT = By.cssSelector("input[type='password']");

	private static final By VERIFY_PASSWORD = By.xpath("//button[normalize-space()='Verify Password']");

	private static final By NEXT_BUTTON = By.xpath("//button[normalize-space()='Next']");

	private static final By PREVIOUS_BUTTON = By.xpath("//button[normalize-space()='Previous']");

	private static final By DOCUMENT_PREVIEW = By
			.xpath("//*[@role='dialog' and " + ".//*[normalize-space()='Document Preview']]");

	private static final By FINAL_CONFIRMATION = By.id("final_submission_confirmation");

	private static final By CONFIRM_BUTTON = By
			.xpath("//*[@id='final_submission_confirmation']" + "//button[normalize-space()='Confirm and continue']");

	private static final By REVIEW_PREVIOUS_BUTTON = By.xpath(
			"//*[@id='final_submission_confirmation']" + "//button[normalize-space()='Review previous section']");

	private static final By VALIDATION_ERROR = By.id("validationErrorPopup");

	private static final By HEADING_POPUPSCREEN = By.xpath("//h2[@id='final_submission_title']");
	private static final By REVIEWANDCONFIRMBUTTON_POPUPSCREEN = By.xpath("//button[@id='confirm_final_submission']");

	public DocumentPage_AnnualIncomeModificationRekycUAT(WebDriver driver) {
		super(driver);
	}

	private WebDriverWait waitFor() {
		return new WebDriverWait(getDriver(), TIMEOUT);
	}

	public boolean isDisplayed() {
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).isDisplayed();
	}

	public boolean isPopupHeadingDisplayed() {
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(HEADING_POPUPSCREEN)).isDisplayed();
	}

	public boolean isAccountDetailsDisplayed() {
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_DETAILS)).isDisplayed();
	}

	public boolean isPanDocumentDisplayed() {
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(PAN_DOCUMENT)).isDisplayed();
	}

	public boolean isPanDocumentUploaded() {
		return getDriver().findElements(UPLOADED_STATUS).stream().anyMatch(WebElement::isDisplayed);
	}

	public int getRequiredDocumentCount() {
		return isPanDocumentDisplayed() ? 1 : 0;
	}

	public boolean areAllRequiredDocumentsUploaded() {
		return getRequiredDocumentCount() > 0 && isPanDocumentUploaded();
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT uploadPanDocument(String filePath) {

		Path path = Path.of(filePath).toAbsolutePath();

		if (!Files.isRegularFile(path)) {
			throw new IllegalArgumentException("Document does not exist: " + path);
		}

		String fileName = path.getFileName().toString().toLowerCase();

		if (!(fileName.endsWith(".pdf") || fileName.endsWith(".png") || fileName.endsWith(".jpg")
				|| fileName.endsWith(".jpeg"))) {
			throw new IllegalArgumentException("Invalid document format");
		}

		try {
			if (Files.size(path) >= 5L * 1024 * 1024) {
				throw new IllegalArgumentException("Document must be less than 5 MB");
			}
		} catch (java.io.IOException e) {
			throw new IllegalStateException("Unable to validate file size", e);
		}

		WebElement upload = waitFor().until(ExpectedConditions.presenceOfElementLocated(FILE_UPLOAD));

		upload.sendKeys(path.toString());

		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT openDocumentPreview() {

		click(SHOW_PREVIEW);

		waitFor().until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_PREVIEW));

		return this;
	}

	public boolean isDocumentPreviewDisplayed() {
		return getDriver().findElements(DOCUMENT_PREVIEW).stream().anyMatch(WebElement::isDisplayed);
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT enterDocumentPassword(String password) {

		WebElement input = waitFor().until(ExpectedConditions.elementToBeClickable(PASSWORD_INPUT));

		input.clear();
		input.sendKeys(password);

		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT verifyDocumentPassword() {

		click(VERIFY_PASSWORD);
		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT openFinalSubmissionConfirmation() {

		click(NEXT_BUTTON);

		waitFor().until(ExpectedConditions.visibilityOfElementLocated(FINAL_CONFIRMATION));

		return this;
	}

	public boolean isFinalConfirmationDisplayed() {
		return getDriver().findElements(FINAL_CONFIRMATION).stream().anyMatch(WebElement::isDisplayed);
	}

	public String getFinalConfirmationMessage() {
		return waitFor().until(ExpectedConditions.visibilityOfElementLocated(FINAL_CONFIRMATION)).getText().trim();
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT reviewPreviousSection() {

		click(REVIEW_PREVIOUS_BUTTON);
		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT confirmFinalSubmission() {

		click(CONFIRM_BUTTON);
		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT goBackToPreviousPage() {

		String originalUrl = getDriver().getCurrentUrl();

		click(PREVIOUS_BUTTON);

		waitFor().until(ExpectedConditions.not(ExpectedConditions.urlToBe(originalUrl)));

		return this;
	}

	public boolean isValidationErrorDisplayed() {
		return getDriver().findElements(VALIDATION_ERROR).stream().anyMatch(WebElement::isDisplayed);
	}

	private void click(By locator) {

		WebElement element = waitFor().until(ExpectedConditions.elementToBeClickable(locator));

		((JavascriptExecutor) getDriver())
				.executeScript("arguments[0].scrollIntoView(" + "{block:'center',inline:'nearest'});", element);

		try {
			element.click();
		} catch (ElementClickInterceptedException e) {
			throw new IllegalStateException("Element click intercepted: " + locator, e);
		}
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT clickOnConfirmButton() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(REVIEWANDCONFIRMBUTTON_POPUPSCREEN));
		element.click();
		return this;
	}

	public DocumentPage_AnnualIncomeModificationRekycUAT clickNextToEsign() {

		RekycNavigationUtility.clickNextWithRetry(getDriver(), By.id("next_btn"), "/eSign/page");

		return this;
	}
}
