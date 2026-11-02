package com.ui.cloud.rekyc.corporateworkflow;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.DocumentPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.ESignPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.LoginPage_CorporateRekycUAT;
import com.ui.product.rekyc.pages.PersonalPage_CorporateRekycUAT;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;
import com.ui.utilities.ExtentReporterUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class RekycCorporateWorkflowTest extends BaseTest {

	private static final String INVALID_OTP = "123456";
	private static final DateTimeFormatter FORM_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private String url;
	private LoginPage_CorporateRekycUAT loginPage;
	private PersonalPage_CorporateRekycUAT personalPage;
	private DocumentPage_CorporateRekycUAT documentPage;
	private JourneyEvidence evidence;
	private String currentTestName;

	@BeforeMethod(alwaysRun = true)
	public void prepareIndependentTest(Method method) {
		currentTestName = method.getName();
		evidence = null;
		url = JSONUtility.getRekycUrl().getUrl();
		driver.get(url);
		loginPage = new LoginPage_CorporateRekycUAT(driver);
		personalPage = new PersonalPage_CorporateRekycUAT(driver);
		documentPage = new DocumentPage_CorporateRekycUAT(driver);
	}

	@AfterMethod(alwaysRun = true)
	public void finishEvidence() {
		try {
			captureEvidence("Final browser state");
		} catch (RuntimeException error) {
			ExtentReporterUtility.warning("Final evidence screenshot unavailable: " + error.getMessage());
		} finally {
			if (evidence != null) {
				evidence.finish();
				evidence = null;
			}
		}
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
		captureEvidence("Corporate login page");
		loginPage.enterClientCode("12345").clickOnSendOtpButton();
		captureEvidence("Invalid client code rejected");
		softAssert.assertEquals(loginPage.getErrorMessage("clientcode"),
				"PAN not found in the back-office response mapping.");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_001/003/006 - invalid OTP is rejected and valid OTP opens Personal", priority = 2,
			groups = { "critical", "smoke", "functional", "negative" })
	public void verifyOtpValidationAndValidLogin() {
		captureEvidence("Corporate login page");
		loginPage.enterClientCode(clientCode()).clickOnSendOtpButton().enterOtp(INVALID_OTP).clickOnVerifyButton();
		captureEvidence("Invalid OTP rejected");
		softAssert.assertEquals(loginPage.getErrorMessage("otp"), "invalid otp");
		loginPage.enterOtp(validOtp()).clickOnVerifyButton();
		ensureOnPage("/Personal/page", "Personal Details");
		personalPage.waitUntilReady();
		captureEvidence("Personal Details opened after valid OTP");
		softAssert.assertTrue(loginPage.isPersonalPageVisible(), "Personal Details should be visible");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_010/060 - critical corporate KYC data is fetched", priority = 4,
			groups = { "critical", "smoke", "functional" })
	public void verifyCriticalCorporateKycDataIsFetched() {
		loginToPersonalPage();
		softAssert.assertEquals(personalPage.getActualText("heading_ad"), "Account Details");
		softAssert.assertEquals(personalPage.getActualText("c_id"), clientCode());
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

	@Test(description = "tc_corp_rekyc_036a - invalid incorporation date is rejected", priority = 5,
			groups = { "critical", "functional", "edit", "negative" })
	public void verifyInvalidIncorporationDateIsRejected() {
		loginToPersonalPage();
		String originalDate = personalPage.getActualText("doi");
		String validation = personalPage.validateInvalidIncorporationDateEdit("31/02/2020");
		captureEvidence("Invalid incorporation date validation");
		softAssert.assertFalse(validation.isBlank(), "Invalid incorporation date must show a validation message");
		softAssert.assertEquals(personalPage.getActualText("doi"), originalDate,
				"Invalid incorporation date must not be saved");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_036b - future incorporation date is rejected", priority = 5,
			groups = { "critical", "functional", "edit", "negative" })
	public void verifyFutureIncorporationDateIsRejected() {
		loginToPersonalPage();
		String originalDate = personalPage.getActualText("doi");
		String futureDate = LocalDate.now().plusDays(1).format(FORM_DATE);
		String validation = personalPage.validateInvalidIncorporationDateEdit(futureDate);
		captureEvidence("Future incorporation date validation");
		softAssert.assertFalse(validation.isBlank(), "Future incorporation date must show a validation message");
		softAssert.assertEquals(personalPage.getActualText("doi"), originalDate,
				"Future incorporation date must not be saved");
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

	@Test(description = "tc_corp_rekyc_062a - alphabetic corporate mobile is rejected", priority = 6,
			groups = { "functional", "edit", "negative" })
	public void verifyAlphabeticCorporateMobileIsRejected() {
		loginToPersonalPage();
		softAssert.assertFalse(personalPage.validateInvalidMobileEdit("abcdefghij").isBlank());
		captureEvidence("Alphabetic mobile validation");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_063a - email without a domain is rejected", priority = 6,
			groups = { "functional", "edit", "negative" })
	public void verifyCorporateEmailWithoutDomainIsRejected() {
		loginToPersonalPage();
		softAssert.assertFalse(personalPage.validateInvalidEmailEdit("test@").isBlank());
		captureEvidence("Invalid email domain validation");
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

	@Test(description = "tc_corp_rekyc_032a - invalid authorized signatory DOB is rejected", priority = 7,
			groups = { "critical", "functional", "edit", "negative" })
	public void verifyInvalidAuthorizedSignatoryDobIsRejected() {
		loginToPersonalPage();
		String originalDob = personalPage.getActualText("dob_as");
		personalPage.openFirstAuthorizedSignatoryIdentityEditor();
		String validation = personalPage.validateInvalidAuthorizedSignatoryDob("31/02/1990");
		captureEvidence("Invalid signatory DOB validation");
		personalPage.cancelAuthorizedSignatoryIdentityEditor();
		softAssert.assertFalse(validation.isBlank(), "Invalid signatory DOB must show a validation message");
		softAssert.assertEquals(personalPage.getActualText("dob_as"), originalDob,
				"Invalid signatory DOB must not be saved");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_032c - future authorized signatory DOB is rejected", priority = 7,
			groups = { "critical", "functional", "edit", "negative" })
	public void verifyFutureAuthorizedSignatoryDobIsRejected() {
		loginToPersonalPage();
		String originalDob = personalPage.getActualText("dob_as");
		String futureDate = LocalDate.now().plusDays(1).format(FORM_DATE);
		personalPage.openFirstAuthorizedSignatoryIdentityEditor();
		String validation = personalPage.validateInvalidAuthorizedSignatoryDob(futureDate);
		captureEvidence("Future signatory DOB validation");
		personalPage.cancelAuthorizedSignatoryIdentityEditor();
		softAssert.assertFalse(validation.isBlank(), "Future signatory DOB must show a validation message");
		softAssert.assertEquals(personalPage.getActualText("dob_as"), originalDob,
				"Future signatory DOB must not be saved");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_032b - malformed authorized signatory PAN is rejected", priority = 7,
			groups = { "functional", "edit", "negative" })
	public void verifyMalformedAuthorizedSignatoryPanIsRejected() {
		loginToPersonalPage();
		String originalPan = personalPage.getActualText("pan_as");
		personalPage.openFirstAuthorizedSignatoryIdentityEditor();
		String validation = personalPage.validateInvalidAuthorizedSignatoryPan("12345ABCDE");
		captureEvidence("Malformed signatory PAN validation");
		personalPage.cancelAuthorizedSignatoryIdentityEditor();
		softAssert.assertFalse(validation.isBlank(), "Malformed PAN must show a validation message");
		softAssert.assertEquals(personalPage.getActualText("pan_as"), originalPan,
				"Malformed PAN must not be saved");
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
		captureEvidence("Document Upload opened");
		softAssert.assertTrue(documentPage.isDisplayed(), "Document Upload should be displayed");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_052 - complete documents generate a readable eSign PDF preview", priority = 13,
			groups = { "critical", "functional" })
	public void verifyDocumentCompletionAndFinalSubmissionGate() {
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		documentPage.waitForCompletionData();
		captureEvidence("Document Upload completion");
		softAssert.assertEquals(documentPage.getCompletionPercentage(), "100%");
		softAssert.assertTrue(documentPage.isCompletionCountFullyUploaded(),
				"Completion count should show every configured document uploaded: "
						+ documentPage.getCompletionCount());
		softAssert.assertTrue(documentPage.getRequiredDocumentCount() > 0,
				"At least one mandatory document should be configured");
		softAssert.assertTrue(documentPage.areAllRequiredDocumentsUploaded(),
				"Every mandatory document must be uploaded and not rejected");
		ESignPage_CorporateRekycUAT eSignPage = documentPage.goToESignPage();
		softAssert.assertTrue(eSignPage.isDisplayed(), "Generated eSign document link should be visible");
		captureEvidence("Aadhaar eSign page with generated document link");
		eSignPage.openCorporatePdfPreview();
		softAssert.assertTrue(eSignPage.isCorporatePdfRendered(),
				"Corporate PDF should render in the eSign gateway preview");
		captureEvidence("Generated corporate PDF opened in preview");
		softAssert.assertAll();
	}

	@Test(description = "tc_corp_rekyc_066 - Previous preserves corporate Personal data", priority = 14,
			groups = { "critical", "functional" })
	public void verifyPreviousNavigationPreservesPersonalData() {
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		captureEvidence("Document Upload before Previous");
		documentPage.goBackToPersonalPage();
		softAssert.assertEquals(personalPage.getActualText("c_id"), clientCode());
		softAssert.assertEquals(personalPage.getAuthorizedSignatoryCount(), 2);
		softAssert.assertAll();
	}

	@Test(description="tc_corp_rekyc_067 - Confirm the sub-heading for Corporate IPV",priority=15, groups= {"smoke","functional"})
	public void verifySubHeadingForIPV()
	{
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		softAssert.assertTrue(documentPage.isSubheadingDisplayed());
		softAssert.assertAll();
	}

	@Test(description="tc_corp_rekyc_068 - Confirm the sub-heading for Corporate IPV",priority=15, groups= {"smoke","functional"})
	public void verifySubHeadingTextForIPV()
	{
		loginToPersonalPage();
		personalPage.goToDocumentPage();
		softAssert.assertTrue(documentPage.isSubheadingTextDisplayed());
		softAssert.assertAll();
	}

	private void loginToPersonalPage() {
		if (driver.getCurrentUrl().contains("/Document/page")) {
			documentPage.goBackToPersonalPage();
		}
		if (!driver.getCurrentUrl().contains("/Personal/page")
				&& !loginPage.isClientCodeInputReady() && loginPage.isBackToHomeAvailable()) {
			loginPage.returnToLoginFromBackToHome();
		}
		if (driver.getCurrentUrl().contains("/Personal/page")) {
			personalPage.waitUntilReady();
			captureEvidence("Corporate Personal Details loaded");
			return;
		}
		if (!loginPage.isClientCodeInputReady()) {
			captureEvidence("Corporate login entry state unavailable");
			throw new IllegalStateException("Neither client-code login nor Back to Home is available. URL: "
					+ driver.getCurrentUrl() + ". Visible message: " + loginPage.getVisibleLoginMessage());
		}
		loginPage.enterClientCode(clientCode()).clickOnSendOtpButton().enterOtp(validOtp()).clickOnVerifyButton();
		if (!loginPage.waitForPersonalPageOrLoginError()) {
			String message = loginPage.getVisibleLoginMessage();
			if (message.contains("being processed")) {
				throw new SkipException("Corporate ReKYC fixture is unavailable: " + message
						+ " Use a reset, ACTIVE fixture for this scheduled run.");
			}
			throw new IllegalStateException("Corporate ReKYC login did not reach Personal Details. URL: "
					+ driver.getCurrentUrl() + ". Login message: " + message);
		}
		personalPage.waitUntilReady();
		captureEvidence("Corporate Personal Details loaded");
	}

	private void captureEvidence(String step) {
		if (evidence == null) {
			evidence = new JourneyEvidence(driver, currentTestName);
		}
		evidence.capture(step);
	}

	private String clientCode() {
		return "MEON1";
	}

	private String validOtp() {
		return "444444";
	}

	private String requiredFixtureValue(String name) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			value = System.getProperty(name);
		}
		if (value == null || value.isBlank()) {
			throw new SkipException("Corporate ReKYC test fixture is not configured. Set " + name
					+ " in the scheduler environment or Maven system properties.");
		}
		return value.trim();
	}
}
