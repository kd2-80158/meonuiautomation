
package com.ui.cloud.rekyc.corporateworkflow;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.DocumentPage_AnnualIncomeModificationRekycUAT;
import com.ui.product.rekyc.pages.EsignPage_AnnualIncomeModificationRekycUAT;
import com.ui.product.rekyc.pages.LoginPage_AnnualIncomeModificationRekycUAT;
import com.ui.product.rekyc.pages.OthersPage_AnnualIncomeModificationRekycUAT;
import com.ui.product.rekyc.pages.admin.meontechnologies.MakerCheckerPage_MeonTechnologies;
import com.ui.product.rekyc.pages.superadmin.DashboardPage_REKYCSuperadmin;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class RekycAnnualIncomeModificationWorkflowTest extends BaseTest {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	private static final String LOGIN_URL = "https://rekycuat.meon.co.in/v1/company/"
			+ "meon_rekyc/annual_income_modification/login";

	private static final String SUPERADMIN_LOGIN_URL = "https://rekycuat.meon.co.in/v1/company/superadmin";

	private static final String CLIENT_CODE = "MEON1";
	private static final String VALID_OTP = "444444";

	private LoginPage_AnnualIncomeModificationRekycUAT loginPage;

	private LoginPage_REKYCSuperadmin loginREKYCPage;

	private OthersPage_AnnualIncomeModificationRekycUAT othersPage;

	private DocumentPage_AnnualIncomeModificationRekycUAT documentPage;

	private EsignPage_AnnualIncomeModificationRekycUAT esignPage;

	private DashboardPage_REKYCSuperadmin dashboardPage;
	
	private MakerCheckerPage_MeonTechnologies makerCheckerPage;

	private WebDriverWait wait;

	@BeforeMethod(alwaysRun = true)
	public void prepareIndependentTest() {

		if (driver == null) {
			super.setupBrowser();
		}

		wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		driver.manage().deleteAllCookies();

		driver.get(LOGIN_URL);

		loginPage = new LoginPage_AnnualIncomeModificationRekycUAT(driver);

		othersPage = new OthersPage_AnnualIncomeModificationRekycUAT(driver);

		documentPage = new DocumentPage_AnnualIncomeModificationRekycUAT(driver);

		esignPage = new EsignPage_AnnualIncomeModificationRekycUAT(driver);

		loginREKYCPage = new LoginPage_REKYCSuperadmin(driver);

		dashboardPage = new DashboardPage_REKYCSuperadmin(driver);

		System.out.println("[SETUP] Current URL: " + driver.getCurrentUrl());
	}

	@Test(description = "tc_01 - Verify valid client code and OTP login", priority = 1, groups = { "critical", "smoke",
			"functional" })
	public void verifyOtpValidationAndValidLogin() {

		loginWithValidCredentials();

		softAssert.assertTrue(othersPage.isDisplayed(), "Others page should be displayed after login");

		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify Annual Income Modification Others page", priority = 2, groups = { "critical",
			"smoke", "functional" })
	public void verifyAnnualIncomeModificationOthersPage() {

		loginAndOpenOthersPage();

		softAssert.assertTrue(othersPage.isDisplayed(), "Others page should be displayed");

		softAssert.assertTrue(othersPage.isAccountDetailsDisplayed(), "Account Details should be visible");

		softAssert.assertTrue(othersPage.isAnnualIncomeDisplayed(),
				"Annual Income as on date field should be displayed");

		softAssert.assertTrue(othersPage.isNextButtonEnabled(), "Next button should be enabled");

		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify Annual Income selection", priority = 3, groups = { "functional" })
	public void verifyAnnualIncomeSelection() {

		loginAndOpenOthersPage();

		String selectedIncome = othersPage.getSelectedAnnualIncome();

		softAssert.assertNotNull(selectedIncome, "Annual Income should not be null");

		softAssert.assertFalse(selectedIncome.isBlank(), "Annual Income should not be empty");

		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify navigation from Others to Document", priority = 4, groups = { "critical",
			"smoke", "functional" })
	public void verifyOthersToDocumentNavigation() {

		loginAndOpenDocumentPage();

		softAssert.assertTrue(documentPage.isDisplayed(), "assertion 2:Document Upload page should be displayed");

		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify Document Upload page", priority = 5, groups = { "critical", "smoke",
			"functional" })
	public void verifyDocumentUploadPage() {

		loginAndOpenDocumentPage();

		softAssert.assertTrue(documentPage.isDisplayed(), "Document Upload page should be displayed");

		softAssert.assertTrue(documentPage.isAccountDetailsDisplayed(), "Account Details should be displayed");

		softAssert.assertTrue(documentPage.isPanDocumentDisplayed(), "PAN Document field should be displayed");

		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify PAN document upload status", priority = 6, groups = { "functional" })
	public void verifyPanDocumentUploadStatus() {

		loginAndOpenDocumentPage();

		softAssert.assertTrue(documentPage.isPanDocumentUploaded(), "PAN document should already be uploaded");

		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify all required documents uploaded", priority = 7, groups = { "critical",
			"functional" })
	public void verifyAllRequiredDocumentsUploaded() {

		loginAndOpenDocumentPage();

		softAssert.assertTrue(documentPage.areAllRequiredDocumentsUploaded(),
				"All required documents should be uploaded");

		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify PAN document preview", priority = 8, groups = { "functional" })
	public void verifyPanDocumentPreview() {

		loginAndOpenDocumentPage();

		documentPage.openDocumentPreview();

		softAssert.assertTrue(documentPage.isDocumentPreviewDisplayed(), "PAN document preview should be displayed");

		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify final submission confirmation", priority = 9, groups = { "critical",
			"functional" })
	public void verifyFinalSubmissionConfirmation() {

		loginAndOpenDocumentPage();

		documentPage.openFinalSubmissionConfirmation();

		softAssert.assertTrue(documentPage.isFinalConfirmationDisplayed(),
				"Final submission confirmation should be displayed");

		String message = documentPage.getFinalConfirmationMessage();

		softAssert.assertTrue(message.contains("Review before final submission"),
				"Final submission review message should be displayed");

		documentPage.reviewPreviousSection();

		softAssert.assertAll();
	}

	@Test(description = "tc_10 - Verify end to end user journey", priority = 10, groups = { "critical", "functional" })
	public void verifyEndToEndJourney() {

		loginAndOpenDocumentPage();

		documentPage.openFinalSubmissionConfirmation();

		softAssert.assertTrue(documentPage.isFinalConfirmationDisplayed(),
				"Final submission confirmation should be displayed");

		String message = documentPage.getFinalConfirmationMessage();

		softAssert.assertTrue(message.contains("Review before final submission"),
				"Final submission review message should be displayed");

		documentPage.clickOnConfirmButton();

		esignPage.isSuccessMsgDisplayed();

		softAssert.assertAll();
	}

	@Test(description = "tc_11 - Verify generated PDF is downloaded and readable", priority = 11, groups = { "critical",
			"functional" })
	public void verifyDocumentGenerated() {

		// Step 1: Superadmin login
		driver.get(SUPERADMIN_LOGIN_URL);

		loginAndOpenSuperadminPage();

		// Step 2: Search and visit MEON Admin
		dashboardPage.clickOn("search").writeService("search", "meon").clickOn("visitadmin");

		// Step 3: Initialize Maker-Checker page
		makerCheckerPage = new MakerCheckerPage_MeonTechnologies(driver);

		// Step 4: Navigate to Maker-Checker > Verify
		makerCheckerPage.clickOn("verify_btn_mc");

		// Step 5: Select Annual Income Modification workflow
		makerCheckerPage.selectWorkflow("annual_income_modification");

		// Step 6: Set download location
		String downloadDirectory = System.getProperty("user.home") + "/Downloads";

		// Step 7: Record download start time
		long downloadStartTime = System.currentTimeMillis();

		// Step 8: Click Generate PDF for the required client
		makerCheckerPage.clickGeneratePdfForClient("MEON1");

		// Step 9: Verify generated PDF
		boolean pdfVerified = makerCheckerPage.isPdfDownloadedAndOpenable(downloadDirectory,
				"AnnualIncomeModificationPDF", downloadStartTime);

		softAssert.assertTrue(pdfVerified,
				"Generated Annual Income Modification PDF " + "should download and open successfully");

		softAssert.assertAll();
	}

	@Test(description = "Verify Annual Income dropdown options", priority = 3, groups = { "functional" })
	public void verifyAnnualIncomeDropdownOptions() {

		loginAndOpenOthersPage();

		List<String> expectedOptions = Arrays.asList("Below 1 Lac", "1-5 Lac", "5-10 Lac", "10-25 Lac",
				"More Than 25 Lac");

		List<String> actualOptions = othersPage.getAvailableIncomeSlabs();

		softAssert.assertEquals(actualOptions, expectedOptions, "Annual Income dropdown options mismatch");

		othersPage.selectAnnualIncomeByValue(OthersPage_AnnualIncomeModificationRekycUAT.TEN_TO_TWENTY_FIVE_LAC);

		softAssert.assertEquals(othersPage.getSelectedAnnualIncomeValue(), "10_25_lac",
				"Annual Income selection failed");

		softAssert.assertAll();
	}

	private void loginWithValidCredentials() {

		String currentUrl = driver.getCurrentUrl();

		System.out.println("[LOGIN] Starting URL: " + currentUrl);

		if (currentUrl.contains("/Others/page")) {

			othersPage.waitUntilReady();

			return;
		}

		wait.until(ExpectedConditions.elementToBeClickable(By.id("clientid")));

		loginPage.enterClientCode(CLIENT_CODE).clickOnSendOtpButton().enterOtp(VALID_OTP).clickOnVerifyButton();

		wait.until(ExpectedConditions.urlContains("/Others/page"));

		othersPage.waitUntilReady();

		System.out.println("[LOGIN] Login successful. URL: " + driver.getCurrentUrl());
	}

	private void loginAndOpenOthersPage() {

		loginWithValidCredentials();

		Assert.assertTrue(othersPage.isDisplayed(), "Annual Income Modification Others page " + "should be displayed");

		othersPage.waitUntilReady();
	}

	private void loginAndOpenDocumentPage() {

		loginAndOpenOthersPage();

		othersPage.selectAnnualIncomeByValue(OthersPage_AnnualIncomeModificationRekycUAT.TEN_TO_TWENTY_FIVE_LAC);

		softAssert.assertEquals(othersPage.getSelectedAnnualIncomeValue(), "10_25_lac",
				"Selected annual income should be 10-25 Lac");

		othersPage.clickOnNextButton();
	}

	private void loginAndOpenSuperadminPage() {
		By GOOGLE_SIGNIN_BUTTON = By.xpath("//button[@class='google-btn']");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		WebElement e1 = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_SIGNIN_BUTTON));
		e1.click();
		String email = JSONUtility.getCredentials().getKycAdmin().getEmail();

		String password = JSONUtility.getCredentials().getKycAdmin().getPassword();
		long uniqueValue = System.currentTimeMillis();
		String testUserMobile = "8" + String.format("%09d", uniqueValue % 1_000_000_000L);
		String testUserEmail = "saurabhtest+" + uniqueValue + "@gmail.com";

		logger.info("Opening Google Sign In");

		// loginREKYCPage.clickOnButton("googlesignin");

		logger.info("Waiting for email page");

		loginREKYCPage.waitForEmailPage();

		logger.info("Entering email");

		loginREKYCPage.writeService("email", email);

		logger.info("Clicking email Next");

		loginREKYCPage.clickOnButton("nextbtn");

		logger.info("Waiting for password page");

		loginREKYCPage.waitForPasswordPage();

		logger.info("Entering password");

		loginREKYCPage.writeService("password", password);

		logger.info("Clicking password Next");

		loginREKYCPage.clickOnButton("nextbtn");
	}

}
