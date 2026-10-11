package com.ui.product.rekyc.pages;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
	private static final By CLIENTID_ACCOUNTDETAILS = By.id("cus_id");
	private static final By ACCOUNTSTATUS_ACCOUNTDETAILS = By.id("cus_supcode");
	private static final By PAN_ACCOUNTDETAILS = By.id("cus_pan");
	private static final By CKYCNO_ACCOUNTDETAILS = By.id("cus_kycn");
	private static final By HEADING_ACCOUNTDETAILS = By.id("account_details_heading");
	private static final By HEADING_PERSONALDETAILS = By.xpath("//h2[contains(text(), 'Personal Details')]");
	private static final By DOI_PERSONALDETAILS = By.id("c_dob");
	private static final By EDITDOI_PERSONALDETAILS = By.xpath("//div[contains(@id, 'edit_dob')]");
	private static final By CINNUMBER_PERSONALDETAILS = By.id("c_cinNumber");
	private static final By EMAIL_PERSONALDETAILS = By.id("c_email");
	private static final By EMAILDEPENDENCY_PERSONALDETAILS = By.id("c_email_dependency");
	private static final By EDITEMAIL_PERSONALDETAILS = By.xpath("//div[contains(@id, 'email_modify')]");
	private static final By MOBILE_PERSONALDETAILS = By.id("c_mob");
	private static final By MOBILEDEPENDENCY_PERSONALDETAILS = By.id("c_mob_dependency");
	private static final By EDITMOBILE_PERSONALDETAILS = By.xpath("//div[contains(@id, 'mobile_modify')]");
	private static final By ADDRESS_PERSONALDETAILS = By.id("c_add");
	private static final By EDITADDRESS_PERSONALDETAILS = By.xpath("//div[contains(@id, 'address_modify')]");
	private static final By HEADING_AUTHORIZEDSIGNATORY = By.id("authorizedSignatoryTitle");
	private static final By ADDBUTTON_AUTHORIZEDSIGNATORY = By.id("authorized_signatory_add_btn");
	private static final By NAME_AUTHORIZEDSIGNATORY = By.xpath("//h4[@class='authorized-signatory-name']");
	private static final By ROLE_AUTHORIZEDSIGNATORY = By.xpath("//span[@class='authorized-signatory-role']");
	private static final By FULLNAME_AUTHORIZEDSIGNATORY = By.xpath("(//div[@class='authorized-signatory-value'])[1]");
	private static final By EDITFULLNAME_AUTHORIZEDSIGNATORY = By
			.xpath("(//button[contains(@class, 'authorized-signatory-edit')])[2]");
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
	private static final By PREVIOUS_BUTTON = By.id("previous_btn");
	private static final By NEXT_BUTTON = By.id("next_btn");

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

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		logger.info("Clicking on button: " + text);
		element.click();
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
}
