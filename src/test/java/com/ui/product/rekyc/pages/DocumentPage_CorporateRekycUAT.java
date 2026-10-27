package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

public class DocumentPage_CorporateRekycUAT extends BrowserUtility {

	private static final By PAGE_HEADING = By.xpath("//h1[normalize-space()='Document Upload']");
	private static final By COMPLETION_PERCENTAGE = By.id("huf_doc_percent");
	private static final By COMPLETION_COUNT = By.id("huf_doc_count");
	private static final By REQUIRED_DOCUMENTS = By.cssSelector("#show_document .huf-doc-require[mandatory='true']");
	private static final By NEXT_BUTTON = By.id("next_btn");
	private static final By GENERATING_OVERLAY = By.cssSelector(".overlay-bg");
	private static final By PREVIOUS_BUTTON = By.id("previous_btn");
	private static final By SUBHEADING_IPV = By.xpath("//h2[text()='IPV']");
	private static final By SUBHEADING_IPV_TEXT = By.xpath("//p[text()='Face verification']");


	public DocumentPage_CorporateRekycUAT(WebDriver driver) {
		super(driver);
	}

	public boolean isDisplayed() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).isDisplayed();
	}

	public boolean isSubheadingDisplayed() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.visibilityOfElementLocated(SUBHEADING_IPV)).isDisplayed();
	}

	public boolean isSubheadingTextDisplayed() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.visibilityOfElementLocated(SUBHEADING_IPV_TEXT)).isDisplayed();
	}

	public String getCompletionPercentage() {
		return visibleText(COMPLETION_PERCENTAGE);
	}

	public DocumentPage_CorporateRekycUAT waitForCompletionData() {
		new WebDriverWait(getDriver(), Duration.ofSeconds(60)).until(driver ->
				!"0 of 0 Uploaded".equals(driver.findElement(COMPLETION_COUNT).getText().trim())
						&& !driver.findElements(REQUIRED_DOCUMENTS).isEmpty());
		return this;
	}

	public String getCompletionCount() {
		return visibleText(COMPLETION_COUNT);
	}

	public boolean isCompletionCountFullyUploaded() {
		String count = getCompletionCount();
		java.util.regex.Matcher matcher = java.util.regex.Pattern
				.compile("^(\\d+) of (\\d+) Uploaded$").matcher(count);
		return matcher.matches() && Integer.parseInt(matcher.group(2)) > 0
				&& matcher.group(1).equals(matcher.group(2));
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

	public ESignPage_CorporateRekycUAT goToESignPage() {
		WebDriverWait ready = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebDriverWait navigation = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		for (int attempt = 1; attempt <= 3; attempt++) {
			if (getDriver().getCurrentUrl().contains("/eSign/page")) {
				return new ESignPage_CorporateRekycUAT(getDriver());
			}
			ready.until(ExpectedConditions.invisibilityOfElementLocated(GENERATING_OVERLAY));
			WebElement next = ready.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
			((JavascriptExecutor) getDriver()).executeScript(
					"arguments[0].scrollIntoView({block:'center'});", next);
			if (attempt == 1) {
				next.click();
			} else {
				// Firefox can report a successful WebDriver click without firing this button's handler.
				((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", next);
			}
			try {
				navigation.until(driver -> driver.getCurrentUrl().contains("/eSign/page")
						|| isGeneratingOverlayVisible());
			} catch (TimeoutException noTransition) {
				// No loading state or navigation: re-locate the button and retry.
			}
			if (getDriver().getCurrentUrl().contains("/eSign/page")) {
				return new ESignPage_CorporateRekycUAT(getDriver());
			}
			if (isGeneratingOverlayVisible()) {
				new WebDriverWait(getDriver(), Duration.ofSeconds(180))
						.until(ExpectedConditions.urlContains("/eSign/page"));
				return new ESignPage_CorporateRekycUAT(getDriver());
			}
		}
		throw new IllegalStateException("Document Next did not start PDF generation after 3 clicks. URL: "
				+ getDriver().getCurrentUrl() + ", completion: " + getCompletionCount());
	}

	private boolean isGeneratingOverlayVisible() {
		for (WebElement overlay : getDriver().findElements(GENERATING_OVERLAY)) {
			if (overlay.isDisplayed()) {
				return true;
			}
		}
		return false;
	}

	public DocumentPage_CorporateRekycUAT goBackToPersonalPage() {
		click(PREVIOUS_BUTTON, "previous");
		new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(ExpectedConditions.urlContains("/Personal/page"));
		return this;
	}

	private String visibleText(By locator) {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
	}

	private void click(By locator, String description) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		((JavascriptExecutor) getDriver()).executeScript(
				"arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		try {
			element.click();
		} catch (ElementClickInterceptedException intercepted) {
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
		}
	}
	
}
