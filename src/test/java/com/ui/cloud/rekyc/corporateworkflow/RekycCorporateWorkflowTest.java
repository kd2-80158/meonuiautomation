package com.ui.cloud.rekyc.corporateworkflow;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.DocumentPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.LoginPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.PersonalPage_CorporateRekycUAT;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class RekycCorporateWorkflowTest extends BaseTest {

	private static final String CLIENT_CODE = "MEON1";
	private static final String VALID_OTP = "444444";
	private static final String INVALID_OTP = "123456";

	private String url;
	private LoginPage_CorporateRekycUAT loginPage;
	private PersonalPage_CorporateRekycUAT personalPage;
	private DocumentPage_CorporateRekycUAT documentPage;

	@BeforeMethod(alwaysRun = true)
	public void prepareIndependentTest() {
		url = JSONUtility.getRekycUrl().getUrl();
		driver.get(url);
		loginPage = new LoginPage_CorporateRekycUAT(driver);
		personalPage = new PersonalPage_CorporateRekycUAT(driver);
		documentPage = new DocumentPage_CorporateRekycUAT(driver);
	}

	@AfterClass(alwaysRun = true)
	public void closeBrowser() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	@Test(description = "tc_corp_rekyc_002 - invalid client code is rejected", priority = 1,
			groups = { "critical", "functional", "negative" })
	public void verifyInvalidClientCodeIsRejected() {
		loginPage.enterClientCode("12345").clickOnSendOtpButton();
		softAssert.assertEquals(loginPage.getErrorMessage("clientcode"),
				"PAN not found in the back-office response mapping.");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_001/003/006 - invalid OTP is rejected and valid OTP opens Personal", priority = 2,
			groups = { "critical", "smoke", "functional", "negative" })
	public void verifyOtpValidationAndValidLogin() {
		loginPage.enterClientCode(CLIENT_CODE).clickOnSendOtpButton().enterOtp(INVALID_OTP).clickOnVerifyButton();
		softAssert.assertEquals(loginPage.getErrorMessage("otp"), "invalid otp");
		loginPage.enterOtp(VALID_OTP).clickOnVerifyButton();
		ensureOnPage("/Personal/page", "Personal Details");
		personalPage.waitUntilReady();
		softAssert.assertTrue(loginPage.isPersonalPageVisible(), "Personal Details should be visible");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_010/060 - critical corporate KYC data is fetched", priority = 4,
			groups = { "critical", "smoke", "functional" })
	public void verifyCriticalCorporateKycDataIsFetched() {
		loginToPersonalPage();
		softAssert.assertEquals(personalPage.getActualText("heading_ad"), "Account Details");
		softAssert.assertEquals(personalPage.getActualText("c_id"), CLIENT_CODE);
		softAssert.assertEquals(personalPage.getActualText("c_status"), "ACTIVE");
		softAssert.assertEquals(personalPage.getActualText("c_pan"), "KAEPK1365J");
		softAssert.assertFalse(personalPage.getActualText("c_email").isBlank(), "Email should be populated");
		softAssert.assertFalse(personalPage.getActualText("c_mobile").isBlank(), "Mobile should be populated");
		softAssert.assertFalse(personalPage.getActualText("c_address").isBlank(), "Address should be populated");
		softAssert.assertEquals(personalPage.getActualText("heading_as"), "Authorized Signatories & Key People");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_011/036 - entity name and incorporation date edits enforce mandatory values",
			priority = 5, groups = { "critical", "functional", "edit", "negative" })
	public void verifyEntityIdentityEditsEnforceMandatoryValues() {
		loginToPersonalPage();
		softAssert.assertEquals(personalPage.validateBlankEntityNameEdit(),
				"Please enter name as per income tax");
		softAssert.assertEquals(personalPage.validateBlankIncorporationDateEdit(), "Please enter dob");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_062/063 - mobile and email edits reject invalid values before OTP",
			priority = 6, groups = { "critical", "functional", "edit", "negative" })
	public void verifyCorporateContactEditsRejectInvalidValues() {
		loginToPersonalPage();
		softAssert.assertTrue(personalPage.validateInvalidMobileEdit("12345").contains("mobile number is not valid"),
				"Invalid mobile must be rejected before sending OTP");
		softAssert.assertTrue(personalPage.validateInvalidEmailEdit("invalid-email").contains("email address is not valid"),
				"Invalid email must be rejected before sending OTP");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_032 - authorized signatory identity editor is prefilled and validates PAN",
			priority = 7, groups = { "critical", "functional", "edit", "negative" })
	public void verifyAuthorizedSignatoryIdentityEditValidation() {
		loginToPersonalPage();
		personalPage.openFirstAuthorizedSignatoryIdentityEditor();
		softAssert.assertTrue(personalPage.areAuthorizedSignatoryIdentityFieldsPrefilled(),
				"Signatory name, DOB, PAN and ownership should be prefilled for editing");
		softAssert.assertEquals(personalPage.validateInvalidAuthorizedSignatoryPan("BADPAN"),
				"Please enter valid PAN");
		personalPage.cancelAuthorizedSignatoryIdentityEditor();
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_032/061 - authorized signatory contact edits validate before OTP",
			priority = 8, groups = { "critical", "functional", "edit", "negative" })
	public void verifyAuthorizedSignatoryContactEditValidation() {
		loginToPersonalPage();
		softAssert.assertEquals(personalPage.validateInvalidFirstSignatoryMobile("12345"),
				"Please enter valid mobile number");
		softAssert.assertEquals(personalPage.validateInvalidFirstSignatoryEmail("invalid-email"),
				"Please enter valid email address");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_061 - configured signatory limit is enforced", priority = 9,
			groups = { "critical", "functional" })
	public void verifyConfiguredAuthorizedSignatoryLimitIsEnforced() {
		loginToPersonalPage();
		softAssert.assertEquals(personalPage.getAuthorizedSignatoryCount(), 2,
				"The UAT corporate account should contain its two configured signatories");
		softAssert.assertFalse(personalPage.isAddSignatoryEnabled(),
				"Add must remain disabled after the configured signatory/ownership limit is reached");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_062 - mobile change requires mandatory values", priority = 10,
			groups = { "critical", "functional", "negative" })
	public void verifyMobileChangeRequiresMandatoryValues() {
		loginToPersonalPage();
		String validation = personalPage.openMobileEditor().submitBlankMobileChange();
		softAssert.assertTrue(validation.contains("New mobile number is missing"),
				"A blank mobile change should show the mandatory-field validation");
		personalPage.cancelMobileEditor();
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_063 - email change requires mandatory values", priority = 11,
			groups = { "critical", "functional", "negative" })
	public void verifyEmailChangeRequiresMandatoryValues() {
		loginToPersonalPage();
		String validation = personalPage.openEmailEditor().submitBlankEmailChange();
		softAssert.assertTrue(validation.contains("New email is missing"),
				"A blank email change should show the mandatory-field validation");
		personalPage.cancelEmailEditor();
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_068 - unchanged data routes from Personal to Document", priority = 12,
			groups = { "critical", "smoke", "functional" })
	public void verifyNoChangeRouteOpensDocumentPage() {
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		softAssert.assertTrue(documentPage.isDisplayed(), "Document Upload should be displayed");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_052 - complete documents reach the final submission gate", priority = 13,
			groups = { "critical", "functional" })
	public void verifyDocumentCompletionAndFinalSubmissionGate() {
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		softAssert.assertEquals(documentPage.getCompletionPercentage(), "100%");
		softAssert.assertEquals(documentPage.getCompletionCount(), "7 of 7 Uploaded");
		softAssert.assertEquals(documentPage.getRequiredDocumentCount(), 7);
		softAssert.assertTrue(documentPage.areAllRequiredDocumentsUploaded(),
				"Every mandatory document must be uploaded and not rejected");
		String confirmation = documentPage.openFinalSubmissionConfirmationAndDismiss();
		softAssert.assertFalse(confirmation.isBlank(), "Final submission must require explicit confirmation");
		softAssert.assertTrue(driver.getCurrentUrl().contains("/Document/page"),
				"Dismissing confirmation must keep the user on Document Upload");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_066 - Previous preserves corporate Personal data", priority = 14,
			groups = { "critical", "functional" })
	public void verifyPreviousNavigationPreservesPersonalData() {
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		documentPage.goBackToPersonalPage();
		softAssert.assertEquals(personalPage.getActualText("c_id"), CLIENT_CODE);
		softAssert.assertEquals(personalPage.getAuthorizedSignatoryCount(), 2);
		softAssert.assertAll();
	}

	private void loginToPersonalPage() {
		if (driver.getCurrentUrl().contains("/Personal/page")) {
			personalPage.waitUntilReady();
			return;
		}
		loginPage.enterClientCode(CLIENT_CODE).clickOnSendOtpButton().enterOtp(VALID_OTP).clickOnVerifyButton();
		ensureOnPage("/Personal/page", "Personal Details");
		personalPage.waitUntilReady();
	}
}
