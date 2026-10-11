package com.ui.tests.workflow1;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.pages.workflow1.AadhaarAuthentication;
import com.ui.pages.workflow1.AadhaarVerificationPage;
import com.ui.pages.workflow1.BankDetailsPage;
import com.ui.pages.workflow1.BankUploadPage;
import com.ui.pages.workflow1.BrokerageSelectionPage;
import com.ui.pages.workflow1.CustomerDetailsVerificationPage;
import com.ui.pages.workflow1.DigilockerAuthenticationPage;
import com.ui.pages.workflow1.DigilockerDocumentConsentPage;
import com.ui.pages.workflow1.EmailOTPPage;
import com.ui.pages.workflow1.EmailVerificationPage;
import com.ui.pages.workflow1.IncomeProofPage;
import com.ui.pages.workflow1.LoginPage;
import com.ui.pages.workflow1.NomineePage;
import com.ui.pages.workflow1.OTPPage;
import com.ui.pages.workflow1.PANAuthenticationPage;
import com.ui.pages.workflow1.PersonalDetailsPage;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class DemoCapitalWorkflow_Individual extends BaseTest {

	String url = JSONUtility.getWorkflow1().getUrl();
	LoginPage loginPage;
	OTPPage otpPage;
	EmailVerificationPage emailVerificationPage;
	EmailOTPPage emailOTPPage;
	BrokerageSelectionPage brokerageSelectionPage;
	AadhaarAuthentication aadhaarAuthentication;
	AadhaarVerificationPage aadhaarVerificationPage;
	DigilockerAuthenticationPage digilockerAuthenticationPage;
	DigilockerDocumentConsentPage consentPage;
	CustomerDetailsVerificationPage customerDetailsVerificationPage;
	PANAuthenticationPage panAuthenticationPage;
	PersonalDetailsPage personalDetailsPage;
	NomineePage nomineePage;
	BankDetailsPage bankDetailsPage;
	BankUploadPage bankUploadPage;
	IncomeProofPage incomeProofPage;


	@BeforeMethod
	public void setup() {
		super.setupBrowser();
		driver.get(url);
		loginPage = new LoginPage(driver);
		otpPage = new OTPPage(driver);
		emailVerificationPage = new EmailVerificationPage(driver);
		emailOTPPage = new EmailOTPPage(driver);
		brokerageSelectionPage = new BrokerageSelectionPage(driver);
		aadhaarAuthentication = new AadhaarAuthentication(driver);
		aadhaarVerificationPage = new AadhaarVerificationPage(driver);
		consentPage = new DigilockerDocumentConsentPage(driver);
		customerDetailsVerificationPage = new CustomerDetailsVerificationPage(driver);
		panAuthenticationPage = new PANAuthenticationPage(driver);
		personalDetailsPage = new PersonalDetailsPage(driver);
		nomineePage = new NomineePage(driver);
		bankDetailsPage = new BankDetailsPage(driver);
		bankUploadPage = new BankUploadPage(driver);
		incomeProofPage = new IncomeProofPage(driver);
		digilockerAuthenticationPage = new DigilockerAuthenticationPage(driver);
	}
     //workflow - individual - mandot securities
	@Test(description = "Verify the happy flow for mandot securities.")
	public void verifyWorkflow() {

		loginPage
		.enterMobileNumber("8810619472")
		.selectDependency()
		.clickOnOTPButton();

		otpPage.writeOTP();

		emailVerificationPage.writeEmail().clickOnSendOTPButton();

		emailOTPPage.writeOTP().clickOnVerifyButton();

		brokerageSelectionPage
		.clickOnBrokeragePlan()
		.clickOnSelectButton()
		.selectTradingPreference("Only Demat")
		.clickOnNextButton();

		assertStillOnPage("/oauth_partner", "Aadhaar Authentication Page");

		aadhaarAuthentication
		.writeAadhaar(JSONUtility.getWorkflow1().getAdhaarno())
		.clickOnNextButton();

		assertStillOnPage("/send_otp", "Aadhaar Verification Page");

		aadhaarVerificationPage.writeOTP().clickOnSubmitButton();

		assertStillOnPage("/verify_otp", "Digilocker Authentication Page");

		digilockerAuthenticationPage.writeDigilockerPin().clickOnSubmitButton();

		assertStillOnPage("/consent-form", "Digilocker Consent Page");

		consentPage
		.selectConsentCheckboxByValue("issued/in.gov.pan-PANCR-AZIPC1120C")
		.selectConsentCheckboxByValue("issued/in.gov.uidai-ADHAR-dab2350fd12224d719d8a10e8444fac4")
		.clickOnAllowButton();

		if (!ensureOnPage("/individual", "Digilocker Consent Page")) {
			return;
		}

		customerDetailsVerificationPage
		.assertCustomerName("Saurabh Chhimwal")
		.assertFatherName("Harish Chandra Chhimwal")
		.assertDOB("28-06-1995")
		.clickOnProceedButton();

		customerDetailsVerificationPage.clickOnOKButton();

		panAuthenticationPage
		.writePANName("Saurabh Chhimwal")
		.writePANNumber(JSONUtility.getWorkflow1().getPanno()).clickOnSubmitButton();

		personalDetailsPage
		.writeMotherName("Deepa Chhimwal")
		.selectGenderDropdown("Male")
		.selectMaritalStatusDropdown("Single")
		.selectEducationDropdown("Post Graduate")
		.selectAnnualIncomeDropdown("Below 1 Lac")
		.writeTradingExperience("0")
		.selectPoliticallyExposedDropdown("No")
		.selectOccupationDropdown("Private Sector")
		.selectCitizenshipDropdown("Yes")
		.selectBSDADropdown("No")
		.clickOnSubmitButton();

		nomineePage
		.selectNomineeDropdown("No")
		.selectConsentCheckboxByValue()
		.clickOnSubmitButton();

		bankDetailsPage
		.writeIfscCode(JSONUtility.getWorkflow1().getIfsc())
		.writeAccountNumber(JSONUtility.getWorkflow1().getAccountno())
		.writeConfirmAccountNumber(JSONUtility.getWorkflow1().getAccountno())
		.selectAccountTypeDropdown("Saving Account")
		.clickOnSubmitButton()
		.clickOnProceedButton();

		bankUploadPage
		.uploadBankDocument("BankStatementPaytm.pdf")
		.clickOnNextButton();

		incomeProofPage
		.uploadIncomeDocument("BankStatementPaytm.pdf")
		.waitForOverlayToDisappear()
		.writeIncome()
		.clickOnNextButton();
	}

}
