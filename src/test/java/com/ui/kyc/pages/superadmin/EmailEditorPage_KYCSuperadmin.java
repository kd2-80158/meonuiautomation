package com.ui.kyc.pages.superadmin;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class EmailEditorPage_KYCSuperadmin extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	public EmailEditorPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}

	private static final By EMAIL_TEMPLATE_TABLE = By.id("email_template_TableData");
	private static final By EMAIL_TEMPLATE_ROWS = By.cssSelector("#email_template_TableData tr");
	private static final By ADDTEMPLATE_BUTTON = By.xpath("(//button[@class='btn btn-primary ml-auto'])[1]");
	// add email template
	private static final By EMAILSUBJECT_ADDEMAILTEMPLATE = By.id("email_subject");
	private static final By TEMPLATENAME_ADDEMAILTEMPLATE = By.id("template_name");
	private static final By EMAILBODY_ADDEMAILTEMPLATE = By
			.xpath("//div[contains(@class,'note-editable') and @contenteditable='true']");
	private static final By SAVEBUTTON_ADDEMAILTEMPLATE = By.id("save_template");
	private static final By HEADING_ADDEMAILTEMPLATE = By.xpath("//h6[@class='m-2']");

	public EmailEditorPage_KYCSuperadmin writeService(String text, String input) {
		By locator;
		switch (text.toLowerCase()) {
		case "emailsubject":
			locator = EMAILSUBJECT_ADDEMAILTEMPLATE;
			break;
		case "emailtemplate":
			locator = TEMPLATENAME_ADDEMAILTEMPLATE;
			break;
		case "emailbody":
			locator = EMAILBODY_ADDEMAILTEMPLATE;
			break;
		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement sendtext = wait.until(ExpectedConditions.elementToBeClickable(locator));
		sendtext.clear();
		sendtext.sendKeys(input);
		return this;
	}

	public boolean isHeadingVisible(String text) {
		By locator;
		switch (text.toLowerCase()) {

		case "emailtemplate_heading":
			locator = HEADING_ADDEMAILTEMPLATE;
			break;
		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		if (element.isDisplayed())
			return true;
		return false;
	}

	public EmailEditorPage_KYCSuperadmin clickOnButton(String text) {
		By locator;
		switch (text.toLowerCase()) {
		case "addtemplate_button":
			locator = ADDTEMPLATE_BUTTON;
			break;
		case "save_button":
			locator = SAVEBUTTON_ADDEMAILTEMPLATE;
			break;
		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));
		button.click();
		return this;
	}

	public EmailEditorPage_KYCSuperadmin clickSaveButton() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));

		WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(SAVEBUTTON_ADDEMAILTEMPLATE));

		JavascriptExecutor js = (JavascriptExecutor) getDriver();

		// First scroll the actual button into view
		js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", saveButton);

		// Also scroll nearest scrollable parent/modal
		js.executeScript("let el = arguments[0];" + "let p = el.parentElement;" + "while (p) {"
				+ "  const style = window.getComputedStyle(p);"
				+ "  if ((style.overflowY === 'auto' || style.overflowY === 'scroll') "
				+ "      && p.scrollHeight > p.clientHeight) {" + "    p.scrollTop = p.scrollHeight;" + "    break;"
				+ "  }" + "  p = p.parentElement;" + "}", saveButton);

		try {

			wait.until(ExpectedConditions.elementToBeClickable(saveButton));

			saveButton.click();

		} catch (Exception e) {

			logger.warn("Normal Save click failed. Using JavaScript click.", e);

			js.executeScript("arguments[0].click();", saveButton);
		}

		return this;
	}

	public int getEmailTemplateRowCount() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));

		wait.until(ExpectedConditions.presenceOfElementLocated(EMAIL_TEMPLATE_TABLE));

		List<WebElement> rows = getDriver().findElements(EMAIL_TEMPLATE_ROWS);

		logger.info("Current email template row count: " + rows.size());

		return rows.size();
	}

	public boolean waitForNewEmailTemplateRow(int previousRowCount) {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		try {

			Boolean isAdded = wait.until(driver -> {

				int currentRowCount = driver.findElements(EMAIL_TEMPLATE_ROWS).size();

				logger.info("Previous row count: " + previousRowCount + " | Current row count: " + currentRowCount);

				return currentRowCount > previousRowCount;
			});

			return Boolean.TRUE.equals(isAdded);

		} catch (Exception e) {

			logger.error("New email template row was not added. " + "Previous row count: " + previousRowCount);

			return false;
		}
	}

	public boolean isEmailTemplatePresent(String subject, String templateName) {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		By templateRow = By.xpath("//tbody[@id='email_template_TableData']" + "/tr[" + "td[1][normalize-space()=\""
				+ subject + "\"]" + " and " + "td[2][normalize-space()=\"" + templateName + "\"]" + "]");

		try {

			WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(templateRow));

			logger.info("Email template found successfully. " + "Subject: " + subject + " | Template Name: "
					+ templateName);

			return row.isDisplayed();

		} catch (Exception e) {

			logger.error("Email template not found. " + "Subject: " + subject + " | Template Name: " + templateName);

			return false;
		}
	}

}
