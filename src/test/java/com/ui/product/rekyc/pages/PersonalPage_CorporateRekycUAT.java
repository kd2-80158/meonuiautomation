package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class PersonalPage_CorporateRekycUAT extends BrowserUtility {

	public PersonalPage_CorporateRekycUAT(WebDriver driver) {
		super(driver);
	}

	Logger logger = LoggerUtility.getLogger(this.getClass());
	private static final By NAME_ACCOUNTDETAILS = By.id("cus_name");
	private static final By EDITNAME_ACCOUNTDETAILS = By.xpath("//div[contains(@aria-label, 'Edit Name')]");
	private static final By EDITNAME_INPUTBOX_AD = By.id("panname");
	private static final By EDITNAME_SAVEBUTTON_AD = By.id("check_cName");
	private static final By EDITNAME_ERROR_AD = By.id("error_new_panname");
	private static final By EDITNAME_MODAL_AD = By.id("edit_clientName");
	private static final By EDITNAME_CANCEL_AD = By.cssSelector("#edit_clientName [aria-label='Close dialog']");
	private static final By CLIENTID_ACCOUNTDETAILS = By.id("cus_id");
	private static final By ACCOUNTSTATUS_ACCOUNTDETAILS = By.id("cus_supcode");
	private static final By PAN_ACCOUNTDETAILS = By.id("cus_pan");
	private static final By CKYCNO_ACCOUNTDETAILS = By.id("cus_kycn");
	private static final By HEADING_ACCOUNTDETAILS = By.id("account_details_heading");
	private static final By HEADING_PERSONALDETAILS = By.xpath("//h2[contains(text(), 'Personal Details')]");

	// date of incorporation
	private static final By DOI_PERSONALDETAILS = By.id("c_dob");
	private static final By EDITDOI_PERSONALDETAILS = By.xpath("//div[contains(@id, 'edit_dob')]");
	private static final By INPUTBOX_EDITDOI_PERSONALDETAILS = By.id("new_dob");
	private static final By UPDATEBUTTON_EDITDOI_PERSONALDETAILS = By.id("save_personal");
	private static final By ERROR_EDITDOI_PERSONALDETAILS = By.id("error_new_dob");
	private static final By CANCEL_EDITDOI_PERSONALDETAILS = By.id("close_personal");
	private static final By EDIT_PERSONAL_MODAL = By.id("edit_personal");

	private static final By CINNUMBER_PERSONALDETAILS = By.id("c_cinNumber");

	// email
	private static final By EMAIL_PERSONALDETAILS = By.id("c_email");
	private static final By EMAILDEPENDENCY_PERSONALDETAILS = By.id("c_email_dependency");
	private static final By EDITEMAIL_PERSONALDETAILS = By.xpath("//div[contains(@id, 'email_modify')]");
	private static final By INPUTBOXFOREMAILID_EDITEMAIL_PERSONALDETAILS = By.id("edit_newemail");
	private static final By INPUTBOXFORCONFIRMEMAILID_EDITEMAIL_PERSONALDETAILS = By.id("edit_newemail_conf");
	private static final By DROPDOWN_EDITEMAIL_PERSONALDETAILS = By.id("email_dependency");
	private static final By CHECKBOX_EDITEMAIL_PERSONALDETAILS = By.id("email_editconsent_check");
	private static final By CONTINUEBUTTON_EDITEMAIL_PERSONALDETAILS = By.id("email_update_cont");
	private static final By INVALID_EMAIL_ERROR = By.id("error_emailmsg");
	
    //mobile
	private static final By MOBILE_PERSONALDETAILS = By.id("c_mob");
	private static final By MOBILEDEPENDENCY_PERSONALDETAILS = By.id("c_mob_dependency");
	private static final By EDITMOBILE_PERSONALDETAILS = By.xpath("//div[contains(@id, 'mobile_modify')]");
	private static final By INPUTBOXFORMOBILE_EDITMOBILE_PERSONALDETAILS = By.id("edit_newmob");
	private static final By INPUTBOXFORCONFIRMMOBILE_EDITMOBILE_PERSONALDETAILS = By.id("edit_newmob_conf");
	private static final By DROPDOWN_EDITEMOBILE_PERSONALDETAILS = By.id("mob_dependency");
	private static final By CHECKBOX_EDITMOBILE_PERSONALDETAILS = By.id("mob_editconsent_check");
	private static final By CONTINUEBUTTON_EDITEMOBILE_PERSONALDETAILS = By.id("mob_update_cont");
	private static final By INVALID_MOBILE_ERROR = By.id("error_mobmsg");
	
	//address
	private static final By ADDRESS_PERSONALDETAILS = By.id("c_add");
	private static final By EDITADDRESS_PERSONALDETAILS = By.xpath("//div[contains(@id, 'address_modify')]");
	private static final By DIGILOCKERBUTTON_ADDRESS_PERSONALDETAILS = By.id("digilocker");
	private static final By INPUTBOX1_DIGILOCKERADDRESS_PERSONALDETAILS = By.id("aadhaar_1");
	private static final By INPUTBOX2_DIGILOCKERADDRESS_PERSONALDETAILS = By.id("aadhaar_2");
	private static final By INPUTBOX3_DIGILOCKERADDRESS_PERSONALDETAILS = By.id("aadhaar_3");
	private static final By NEXTBUTTON_DIGILOCKERADDRESS_PERSONALDETAILS = By.cssSelector(".btn.btn-success.btn-lg.btn-block");
	private static final By INPUTBOXFOROTP_DIGILOCKERADDRESS_PERSONALDETAILS = By.id("a_otp_1");
	private static final By SUBMITBUTTON_DIGILOCKERADDRESS_PERSONALDETAILS = By.id("otp_button");
	
	//authorized signatory
	private static final By HEADING_AUTHORIZEDSIGNATORY = By.id("authorizedSignatoryTitle");
	private static final By ADDBUTTON_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_add_btn");
	private static final By DELETEBUTTON_AUTHORIZEDSIGNATORY = By.xpath("//button[@class='authorized-signatory-delete is-active']");
	private static final By NAME_AUTHORIZEDSIGNATORY = By.xpath("//h4[@class='authorized-signatory-name']");
	private static final By ROLE_AUTHORIZEDSIGNATORY = By.xpath("//span[@class='authorized-signatory-role']");
	private static final By FULLNAME_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[1]");
	private static final By EDITFULLNAME_AUTHORIZEDSIGNATORY = By.cssSelector(
			"#authorized_signatory_list article:first-of-type button[aria-label='Edit authorized signatory details']");
	private static final By PAN_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[2]");
	private static final By EDITPAN_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[3]");
	private static final By DOB_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[3]");
	private static final By EDITDOB_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[4]");
	private static final By MOBILENO_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[4]");
	private static final By EDITMOBILE_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[5]");
	private static final By EMAILADDRESS_AUTHORIZEDSIGNATORY = By
			.xpath("(//div[@class='authorized-signatory-value'])[5]");
	private static final By EDITEMAILADDRESS_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[6]");
	private static final By OWNERSHIPPERCENTAGE_AUTHORIZEDSIGNATORY = By
			.xpath("(//div[@class='authorized-signatory-value'])[6]");
	private static final By EDITOWNERSHIPPERCENTAGE_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[7]");
	private static final By ADDRESS_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[7]");
	private static final By EDITADDRESS_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[8]");
	
	//ADD SIGNATORY
	private static final By FULLNAME_ADDSIGNATORY_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_name");
	private static final By DOB_ADDSIGNATORY_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_dob");
	private static final By PAN_ADDSIGNATORY_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_pan");
	private static final By PERCENTAGE_ADDSIGNATORY_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_percentage");
	private static final By SAVEBUTTON_ADDSIGNATORY_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_form_save");
	private static final By AUTHORIZED_SIGNATORY_FORM_MODAL = By.id("authorized_signatory_form_popup");
	private static final By AUTHORIZED_SIGNATORY_FORM_CANCEL = By.id("authorized_signatory_form_cancel");
	private static final By AUTHORIZED_SIGNATORY_PAN_ERROR = By.id("error_authorized_signatory_pan");
	private static final By FIRST_SIGNATORY_EDIT_MOBILE = By.cssSelector(
			"#authorized_signatory_list article:first-of-type button[aria-label='Edit mobile number']");
	private static final By FIRST_SIGNATORY_EDIT_EMAIL = By.cssSelector(
			"#authorized_signatory_list article:first-of-type button[aria-label='Edit email address']");
	private static final By AUTHORIZED_SIGNATORY_CONTACT_MODAL = By.id("authorized_signatory_contact_popup");
	private static final By AUTHORIZED_SIGNATORY_CONTACT_VALUE = By.id("authorized_signatory_contact_value");
	private static final By AUTHORIZED_SIGNATORY_CONTACT_SEND_OTP = By.id("authorized_signatory_send_otp");
	private static final By AUTHORIZED_SIGNATORY_CONTACT_ERROR = By.id("error_authorized_signatory_contact_value");
	private static final By AUTHORIZED_SIGNATORY_CONTACT_CANCEL = By.id("authorized_signatory_contact_cancel");
	private static final By EMAIL_REQUIRED_ERROR = By.id("error_emailmsg");
	private static final By MOBILE_REQUIRED_ERROR = By.id("error_mobmsg");
	private static final By EMAIL_CANCEL_BUTTON = By.id("email_update_cancel");
	private static final By MOBILE_CANCEL_BUTTON = By.id("mob_update_cancel");
	private static final By AUTHORIZED_SIGNATORY_CARDS = By.cssSelector("#authorized_signatory_list article, #authorized_signatory_list .authorized-signatory-card");
	
	private static final By PREVIOUS_BUTTON = By.id("previous_btn");
	private static final By NEXT_BUTTON = By.id("next_btn");
	private static final By VALIDATION_ERROR_POPUP = By.id("validationErrorPopup");
	private static final By VALIDATION_ERROR_MESSAGE = By.id("validationErrorMessage");
	private static final By LOADING_OVERLAY = By.cssSelector(".overlay-bg");

	public PersonalPage_CorporateRekycUAT clickOnButton(String text) {

		By locator;

		switch (text.toLowerCase()) {

		case "editname_ad":
			locator = EDITNAME_ACCOUNTDETAILS;
			break;

		case "editdoi_pd":
			locator = EDITDOI_PERSONALDETAILS;
			break;

		case "editemail_pd":
			locator = EDITEMAIL_PERSONALDETAILS;
			break;

		case "editmobile_pd":
			locator = EDITMOBILE_PERSONALDETAILS;
			break;

		case "editaddress_pd":
			locator = EDITADDRESS_PERSONALDETAILS;
			break;

		case "add_as":
			locator = ADDBUTTON_AUTHORIZEDSIGNATORY;
			break;

		case "editfullname_as":
			locator = EDITFULLNAME_AUTHORIZEDSIGNATORY;
			break;

		case "editpan_as":
			locator = EDITPAN_AUTHORIZEDSIGNATORY;
			break;

		case "editdob_as":
			locator = EDITDOB_AUTHORIZEDSIGNATORY;
			break;

		case "editmobile_as":
			locator = EDITMOBILE_AUTHORIZEDSIGNATORY;
			break;

		case "editemail_as":
			locator = EDITEMAILADDRESS_AUTHORIZEDSIGNATORY;
			break;

		case "editownership_as":
			locator = EDITOWNERSHIPPERCENTAGE_AUTHORIZEDSIGNATORY;
			break;

		case "editaddress_as":
			locator = EDITADDRESS_AUTHORIZEDSIGNATORY;
			break;

		case "previous":
			locator = PREVIOUS_BUTTON;
			break;

		case "next":
			locator = NEXT_BUTTON;
			break;

		case "update_doi":
			locator = UPDATEBUTTON_EDITDOI_PERSONALDETAILS;
			break;
			
		case "checkbox_email":
			locator = CHECKBOX_EDITEMAIL_PERSONALDETAILS;
			break;
			
		case "continuebutton_email":
			locator = CONTINUEBUTTON_EDITEMAIL_PERSONALDETAILS;
			break;
			
		case "checkbox_mobile":
			locator = CHECKBOX_EDITMOBILE_PERSONALDETAILS;
			break;
			
		case "continuebutton_mobile":
			locator = CONTINUEBUTTON_EDITEMOBILE_PERSONALDETAILS;
			break;
			
		case "savebutton_addsignatory":
			locator = SAVEBUTTON_ADDSIGNATORY_AUTHORIZEDSIGNATORY;
			break;
			
		case "delete_signatory":
			locator = DELETEBUTTON_AUTHORIZEDSIGNATORY;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		click(locator, text);
		return this;
	}

	public String getActualText(String text) {

		By locator;

		switch (text.toLowerCase()) {

		case "c_name":
			locator = NAME_ACCOUNTDETAILS;
			break;

		case "c_id":
			locator = CLIENTID_ACCOUNTDETAILS;
			break;

		case "c_status":
			locator = ACCOUNTSTATUS_ACCOUNTDETAILS;
			break;

		case "c_pan":
			locator = PAN_ACCOUNTDETAILS;
			break;

		case "c_kycno":
			locator = CKYCNO_ACCOUNTDETAILS;
			break;

		case "doi":
			locator = DOI_PERSONALDETAILS;
			break;

		case "cin":
			locator = CINNUMBER_PERSONALDETAILS;
			break;

		case "c_email":
			locator = EMAIL_PERSONALDETAILS;
			break;

		case "c_emaildependency":
			locator = EMAILDEPENDENCY_PERSONALDETAILS;
			break;

		case "c_mobile":
			locator = MOBILE_PERSONALDETAILS;
			break;

		case "c_mobiledependency":
			locator = MOBILEDEPENDENCY_PERSONALDETAILS;
			break;

		case "c_address":
			locator = ADDRESS_PERSONALDETAILS;
			break;

		case "heading_ad":
			locator = HEADING_ACCOUNTDETAILS;
			break;

		case "heading_pd":
			locator = HEADING_PERSONALDETAILS;
			break;

		case "heading_as":
			locator = HEADING_AUTHORIZEDSIGNATORY;
			break;

		case "name_as":
			locator = NAME_AUTHORIZEDSIGNATORY;
			break;

		case "role_as":
			locator = ROLE_AUTHORIZEDSIGNATORY;
			break;

		case "fullname_as":
			locator = FULLNAME_AUTHORIZEDSIGNATORY;
			break;

		case "pan_as":
			locator = PAN_AUTHORIZEDSIGNATORY;
			break;

		case "dob_as":
			locator = DOB_AUTHORIZEDSIGNATORY;
			break;

		case "mobno_as":
			locator = MOBILENO_AUTHORIZEDSIGNATORY;
			break;

		case "email_as":
			locator = EMAILADDRESS_AUTHORIZEDSIGNATORY;
			break;

		case "ownership_as":
			locator = OWNERSHIPPERCENTAGE_AUTHORIZEDSIGNATORY;
			break;

		case "address_as":
			locator = ADDRESS_AUTHORIZEDSIGNATORY;
			break;

		case "success_msg":
			locator = EDITNAME_ACCOUNTDETAILS;
			break;

		default:
			throw new IllegalArgumentException("Invalid error message locator key: " + text);
		}

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		logger.info("The element value is: " + element.getText());
		System.out.println("The element value is: " + element.getText());
		return element.getText();
	}

	public PersonalPage_CorporateRekycUAT openEmailEditor() {
		return clickOnButton("editemail_pd");
	}

	public String validateBlankEntityNameEdit() {
		clickOnButton("editname_ad");
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(EDITNAME_MODAL_AD));
		WebElement input = getDriver().findElement(EDITNAME_INPUTBOX_AD);
		input.clear();
		click(EDITNAME_SAVEBUTTON_AD, "entity name save");
		String validation = visibleText(EDITNAME_ERROR_AD);
		click(EDITNAME_CANCEL_AD, "entity name editor close");
		return validation;
	}

	public String validateBlankIncorporationDateEdit() {
		clickOnButton("editdoi_pd");
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(EDIT_PERSONAL_MODAL));
		WebElement input = new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(INPUTBOX_EDITDOI_PERSONALDETAILS));
		input.clear();
		click(UPDATEBUTTON_EDITDOI_PERSONALDETAILS, "incorporation date update");
		String validation = visibleText(ERROR_EDITDOI_PERSONALDETAILS);
		click(CANCEL_EDITDOI_PERSONALDETAILS, "incorporation date editor cancel");
		return validation;
	}

	public String validateInvalidEmailEdit(String invalidEmail) {
		openEmailEditor();
		replaceValue(INPUTBOXFOREMAILID_EDITEMAIL_PERSONALDETAILS, invalidEmail);
		replaceValue(INPUTBOXFORCONFIRMEMAILID_EDITEMAIL_PERSONALDETAILS, invalidEmail);
		clickOnButton("continuebutton_email");
		String validation = visibleText(INVALID_EMAIL_ERROR);
		cancelEmailEditor();
		return validation;
	}

	public String validateInvalidMobileEdit(String invalidMobile) {
		openMobileEditor();
		replaceValue(INPUTBOXFORMOBILE_EDITMOBILE_PERSONALDETAILS, invalidMobile);
		replaceValue(INPUTBOXFORCONFIRMMOBILE_EDITMOBILE_PERSONALDETAILS, invalidMobile);
		clickOnButton("continuebutton_mobile");
		String validation = visibleText(INVALID_MOBILE_ERROR);
		cancelMobileEditor();
		return validation;
	}

	public PersonalPage_CorporateRekycUAT openFirstAuthorizedSignatoryIdentityEditor() {
		click(EDITFULLNAME_AUTHORIZEDSIGNATORY, "first authorized signatory identity editor");
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(AUTHORIZED_SIGNATORY_FORM_MODAL));
		return this;
	}

	public boolean areAuthorizedSignatoryIdentityFieldsPrefilled() {
		return !valueOf(FULLNAME_ADDSIGNATORY_AUTHORIZEDSIGNATORY).isBlank()
				&& !valueOf(DOB_ADDSIGNATORY_AUTHORIZEDSIGNATORY).isBlank()
				&& !valueOf(PAN_ADDSIGNATORY_AUTHORIZEDSIGNATORY).isBlank()
				&& !valueOf(PERCENTAGE_ADDSIGNATORY_AUTHORIZEDSIGNATORY).isBlank();
	}

	public String validateInvalidAuthorizedSignatoryPan(String invalidPan) {
		replaceValue(PAN_ADDSIGNATORY_AUTHORIZEDSIGNATORY, invalidPan);
		click(SAVEBUTTON_ADDSIGNATORY_AUTHORIZEDSIGNATORY, "authorized signatory save");
		return visibleText(AUTHORIZED_SIGNATORY_PAN_ERROR);
	}

	public PersonalPage_CorporateRekycUAT cancelAuthorizedSignatoryIdentityEditor() {
		click(AUTHORIZED_SIGNATORY_FORM_CANCEL, "authorized signatory identity editor cancel");
		return this;
	}

	public String validateInvalidFirstSignatoryMobile(String invalidMobile) {
		return validateInvalidFirstSignatoryContact(FIRST_SIGNATORY_EDIT_MOBILE, invalidMobile);
	}

	public String validateInvalidFirstSignatoryEmail(String invalidEmail) {
		return validateInvalidFirstSignatoryContact(FIRST_SIGNATORY_EDIT_EMAIL, invalidEmail);
	}

	public PersonalPage_CorporateRekycUAT openMobileEditor() {
		return clickOnButton("editmobile_pd");
	}

	public String submitBlankEmailChange() {
		clickOnButton("continuebutton_email");
		return visibleText(EMAIL_REQUIRED_ERROR);
	}

	public String submitBlankMobileChange() {
		clickOnButton("continuebutton_mobile");
		return visibleText(MOBILE_REQUIRED_ERROR);
	}

	public PersonalPage_CorporateRekycUAT cancelEmailEditor() {
		click(EMAIL_CANCEL_BUTTON, "email editor cancel");
		return this;
	}

	public PersonalPage_CorporateRekycUAT cancelMobileEditor() {
		click(MOBILE_CANCEL_BUTTON, "mobile editor cancel");
		return this;
	}

	public boolean isAddSignatoryEnabled() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(ADDBUTTON_AUTHORIZEDSIGNATORY));
		try {
			wait.until(driver -> !driver.findElement(ADDBUTTON_AUTHORIZEDSIGNATORY).isEnabled());
		} catch (TimeoutException ignored) {
			// Return the final UI state so the test reports a useful assertion failure.
		}
		return button.isEnabled();
	}

	public int getAuthorizedSignatoryCount() {
		new WebDriverWait(getDriver(), Duration.ofSeconds(30))
				.until(driver -> driver.findElements(AUTHORIZED_SIGNATORY_CARDS).size() > 0);
		return getDriver().findElements(AUTHORIZED_SIGNATORY_CARDS).size();
	}

	public PersonalPage_CorporateRekycUAT goToDocumentPage() {
		waitUntilReady();
		clickOnButton("next");
		String result = new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(driver -> {
			if (driver.getCurrentUrl().contains("/Document/page")) {
				return "document";
			}
			for (WebElement popup : driver.findElements(VALIDATION_ERROR_POPUP)) {
				if (popup.isDisplayed()) {
					return driver.findElement(VALIDATION_ERROR_MESSAGE).getText().trim();
				}
			}
			return null;
		});
		if (!"document".equals(result)) {
			throw new AssertionError("Personal page blocked navigation: " + result);
		}
		return this;
	}

	public PersonalPage_CorporateRekycUAT waitUntilReady() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING_PERSONALDETAILS));
		wait.until(driver -> !driver.findElement(CLIENTID_ACCOUNTDETAILS).getText().trim().isEmpty());
		wait.until(driver -> driver.findElements(AUTHORIZED_SIGNATORY_CARDS).size() > 0);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
		wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
		return this;
	}

	private String visibleText(By locator) {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
	}

	private String validateInvalidFirstSignatoryContact(By editButton, String invalidValue) {
		click(editButton, "authorized signatory contact editor");
		new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(AUTHORIZED_SIGNATORY_CONTACT_MODAL));
		replaceValue(AUTHORIZED_SIGNATORY_CONTACT_VALUE, invalidValue);
		click(AUTHORIZED_SIGNATORY_CONTACT_SEND_OTP, "authorized signatory send OTP");
		String validation = visibleText(AUTHORIZED_SIGNATORY_CONTACT_ERROR);
		click(AUTHORIZED_SIGNATORY_CONTACT_CANCEL, "authorized signatory contact editor cancel");
		return validation;
	}

	private String valueOf(By locator) {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator)).getAttribute("value").trim();
	}

	private void replaceValue(By locator, String value) {
		WebElement input = new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
		input.clear();
		input.sendKeys(value);
	}

	private void click(By locator, String description) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		((JavascriptExecutor) getDriver()).executeScript(
				"arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		logger.info("Clicking on " + description);
		try {
			element.click();
		} catch (ElementClickInterceptedException intercepted) {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
		}
	}

	public PersonalPage_CorporateRekycUAT writeService(String text, String input) {
		By locator;
		switch (text.toLowerCase()) {
		case "doi":
			locator = INPUTBOX_EDITDOI_PERSONALDETAILS;
			break;
		case "emailid":
			locator = INPUTBOXFOREMAILID_EDITEMAIL_PERSONALDETAILS;
			break;
		case "confirmemail":
			locator = INPUTBOXFORCONFIRMEMAILID_EDITEMAIL_PERSONALDETAILS;
			break;
			
		case "mobile":
			locator = INPUTBOXFORMOBILE_EDITMOBILE_PERSONALDETAILS;
			break;
			
		case "confirmmobile":
			locator = INPUTBOXFORCONFIRMMOBILE_EDITMOBILE_PERSONALDETAILS;
			break;
			
		case "fullname":
			locator = FULLNAME_ADDSIGNATORY_AUTHORIZEDSIGNATORY;
			break;
			
		case "dob":
			locator = DOB_ADDSIGNATORY_AUTHORIZEDSIGNATORY;
			break;
			
		case "pan":
			locator = PAN_ADDSIGNATORY_AUTHORIZEDSIGNATORY;
			break;
			
		case "percentage":
			locator = PERCENTAGE_ADDSIGNATORY_AUTHORIZEDSIGNATORY;
			break;
			
		case "mobile_as":
			locator = EDITMOBILE_AUTHORIZEDSIGNATORY;
			break;
			
		case "email_as":
			locator = EDITEMAILADDRESS_AUTHORIZEDSIGNATORY;
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

	public PersonalPage_CorporateRekycUAT clickDropdown(String text) {
		By locator = null;
		switch (text.toLowerCase()) {
		case "username":
			locator = DROPDOWN_EDITEMAIL_PERSONALDETAILS;
			break;
		case "accessgranted":
			locator = DROPDOWN_EDITEMOBILE_PERSONALDETAILS;
			break;
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(locator));
		dropdown.click();
		return this;
	}
}
