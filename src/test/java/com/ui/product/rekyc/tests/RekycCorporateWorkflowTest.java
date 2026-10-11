package com.ui.product.rekyc.tests;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.LoginPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.PersonalPage_CorporateRekycUAT;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class RekycCorporateWorkflowTest extends BaseTest {

	String url;
	Gson gson;
	Logger logger;
	LoginPage_CorporateRekycUAT corporateRekycUAT;
	PersonalPage_CorporateRekycUAT personalPage;

	@BeforeClass(alwaysRun = true)
	public void setupClass() {
		super.setupBrowser();
		gson = new Gson();
		logger = LoggerUtility.getLogger(this.getClass());
		corporateRekycUAT = new LoginPage_CorporateRekycUAT(driver);
		personalPage = new PersonalPage_CorporateRekycUAT(driver);
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() {

		if (driver != null) {
			driver.quit();
		}
	}

	private void openCorporateRekycLoginPage() {
		url = JSONUtility.getRekycUrl().getUrl();
		driver.get(url);
		softAssert.assertTrue(ensureOnPage("/corporate_workflow/login", "Corporate Workflow"),
				"Corporate Re-KYC login page should be displayed");
	}

	@Test(description = "tc_corp_rekyc_001-Verify registered mobile OTP re-verification succeeds with valid OTP", priority = 4, groups = {
			"smoke", "e2e" })
	public void verifyEndtoEndCorporateWorkflowJourney() {
		if (!ensureOnPage("/corporate_workflow/login", "Corporate Workflow")) {
			url = JSONUtility.getRekycUrl().getUrl();
			this.driver.get(url);
		}
		corporateRekycUAT.enterClientCode("MEON1").clickOnSendOtpButton().enterOtp("444444").clickOnVerifyButton();
		softAssert.assertEquals(corporateRekycUAT.isPersonalPageVisible(), true);
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_002-Verify invalid clientcode is rejected", priority = 1, groups = { "e2e" })
	public void verifyRejectionOfInvalidClientCode() {
		url = JSONUtility.getRekycUrl().getUrl();
		this.driver.get(url);
		if (ensureOnPage("/corporate_workflow/login", "Corporate Workflow")) {
			corporateRekycUAT.enterClientCode("12345").clickOnSendOtpButton();
			String expectedErrMessage = "PAN not found in the back-office response mapping.";
			String actualErrMsg = corporateRekycUAT.getErrorMessage("clientcode");
			softAssert.assertEquals(actualErrMsg, expectedErrMessage);
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_corp_rekyc_003-Verify invalid mobile OTP is rejected", priority = 2, groups = { "e2e" })
	public void verifyRejectionOfInvalidMobileOTP() {
		url = JSONUtility.getRekycUrl().getUrl();
		this.driver.get(url);
		if (ensureOnPage("/corporate_workflow/login", "Corporate Workflow")) {
			corporateRekycUAT.enterClientCode("MEON1").clickOnSendOtpButton().enterOtp("123456").clickOnVerifyButton();
			String expectedErrMessage = "invalid otp";
			String actualErrMsg = corporateRekycUAT.getErrorMessage("otp");
			softAssert.assertEquals(actualErrMsg, expectedErrMessage);
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_corp_rekyc_006 - Verify registered email OTP re-verification succeeds with valid OTP", priority = 3, groups = {
			"e2e" })
	public void verifyVerificationOfValidOtp() {
		url = JSONUtility.getRekycUrl().getUrl();
		this.driver.get(url);
		if (ensureOnPage("/corporate_workflow/login", "Corporate Workflow")) {
			corporateRekycUAT.enterClientCode("MEON1").clickOnSendOtpButton();
			String expectedErrMessage = "OTP sent on your registered mobile number & email Id";
			String actualErrMsg = corporateRekycUAT.getErrorMessage("validclientcode");
			softAssert.assertEquals(actualErrMsg, expectedErrMessage);
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_corp_rekyc_010 - Verify existing KYC record is fetched successfully from KRA/CKYCR", priority = 5, groups = {
			"e2e", "smoke" })
	public void verifyValidExistingKYCRecordFetch() {

		openCorporateRekycLoginPage();

		corporateRekycUAT.enterClientCode("MEON1").clickOnSendOtpButton().enterOtp("444444").clickOnVerifyButton();

		softAssert.assertTrue(personalPage.getActualText("heading_ad").equalsIgnoreCase("Account Details"),
				"Personal Details page should be displayed");

		softAssert.assertTrue(personalPage.getActualText("c_name").equalsIgnoreCase("Amit Kumar Kannaujia"),
				"Client name should be available");

		softAssert.assertTrue(personalPage.getActualText("c_id").equalsIgnoreCase("MEON1"),
				"Client ID should be available");

		softAssert.assertTrue(personalPage.getActualText("c_status").equalsIgnoreCase("ACTIVE"),
				"Account status should be available");

		softAssert.assertTrue(personalPage.getActualText("c_pan").equalsIgnoreCase("KAEPK1365J"),
				"PAN should be available");

//		softAssert.assertTrue(personalPage.getActualText("doi").equalsIgnoreCase("04/08/2013"),
//				"Date of Incorporation should be available");

		softAssert.assertTrue(personalPage.getActualText("cin").equalsIgnoreCase("U45209MH2015PTC834199"),
				"CIN should be available");

		softAssert.assertTrue(personalPage.getActualText("c_email").equalsIgnoreCase("amit.kannaujia@meon.co.in"),
				"Email should be available");

		softAssert.assertTrue(personalPage.getActualText("c_mobile").equalsIgnoreCase("+919520199148"),
				"Mobile number should be available");

		softAssert.assertTrue(
				personalPage.getActualText("c_address")
						.equalsIgnoreCase("RZ 55 MANAS KUNJ, UTTAM NAGAR, NEW DELHI, 07-Delhi, 110059, India"),
				"Registered address should be available");

		softAssert.assertTrue(
				personalPage.getActualText("heading_as").equalsIgnoreCase("Authorized Signatories & Key People"),
				"Authorized Signatory heading should be displayed");

		softAssert.assertTrue(personalPage.getActualText("name_as").equalsIgnoreCase("SHUBHAM SAINI"),
				"Authorized Signatory name should be available");

		softAssert.assertTrue(personalPage.getActualText("role_as").equalsIgnoreCase("(Authorized Signatory)"),
				"Authorized Signatory role should be available");

		softAssert.assertTrue(personalPage.getActualText("fullname_as").equalsIgnoreCase("SHUBHAM SAINI"),
				"Authorized Signatory full name should be available");

		softAssert.assertTrue(personalPage.getActualText("pan_as").equalsIgnoreCase("KKAPS3025P"),
				"Authorized Signatory PAN should be available");

		softAssert.assertTrue(personalPage.getActualText("dob_as").equalsIgnoreCase("15-07-1994"),
				"Authorized Signatory DOB should be available");

		softAssert.assertTrue(personalPage.getActualText("mobno_as").equalsIgnoreCase("8433114231"),
				"Authorized Signatory mobile number should be available");

		softAssert.assertTrue(personalPage.getActualText("email_as").equalsIgnoreCase("shubham@meon.co.in"),
				"Authorized Signatory email should be available");

		softAssert.assertTrue(personalPage.getActualText("ownership_as").equalsIgnoreCase("50%"),
				"Authorized Signatory ownership percentage should be available");

		softAssert.assertTrue(personalPage.getActualText("address_as").equalsIgnoreCase(
				"Chandra Vihar Colony Devsaini, Ramghat Road Quarsi, Aligarh, Aligarh, Aligarh, Uttar Pradesh, 202001, India"),
				"Authorized Signatory address should be available");

		softAssert.assertAll();

	}

	@Test(description = "tc_corp_rekyc_011-Verify edit functionality for name in personal details section is working correctly", priority = 6, groups = {
			"e2e" })
	public void verifyEditFunctionalityForNameInPersonalDetailsSection() {
		corporateRekycUAT.enterClientCode("MEON1").clickOnSendOtpButton().enterOtp("444444").clickOnVerifyButton();

		personalPage.clickOnButton("editname_ad");
	}
}
