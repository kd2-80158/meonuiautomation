package com.ui.meonwebsite.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.meonwebsite.pages.base.HomePage;
import com.ui.meonwebsite.pages.base.RequestDemoPage;
import com.ui.meonwebsite.pages.base.ThankYouPage;
import com.ui.utilities.GmailUtility;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class RequestDemoTest extends BaseTest {

	String url = JSONUtility.getMainWebsite().getUrl();
	HomePage homePage;
	RequestDemoPage requestDemoPage;
	ThankYouPage thankYouPage;
	private static final String EXPECTED_EMAIL_CONTENT = "Hi Saurabh Chhimwal,\n"
			+ "We have received your query and are excited to explore how Meon Technologies can help grow your business.\n"
			+ "One of our team members will reach out to you shortly.\n" + "Best regards,\n" + "Team Meon Technologies";

	@BeforeMethod
	public void setup() {
		super.setupBrowser();;
		driver.get(url);
		homePage = new HomePage(driver);
		requestDemoPage = new RequestDemoPage(driver);
		thankYouPage = new ThankYouPage(driver);
	}

	private String normalizeEmailContent(String content) {
		return content.replaceAll("\\r", "").replaceAll("\\n+", "\n").replaceAll("[ \\t]+", " ").trim();
	}

	@Test(priority = 1, description = "tc_01 - Verify demo form submits successfully with all valid inputs", groups = {
			"smoke", "sanity", "regression" })
	public void verifySuccessfulFormSubmission() {
		homePage.clickOnGetDemoButton();

		requestDemoPage.writeFullName("Saurabh Test").writeCompanyName("Meon")
				.writeCompanyEmail("saurabh.chhimwal@meon.co.in").writePhoneNumber("8810619472")
				.clickOnBookDemoButton();

		Assert.assertTrue(thankYouPage.isPageHeadingVisible(), "Form Submission is unsuccessful");
	}

	@Test(priority = 2, description = "tc_04 - Verify error shown when Full Name is empty", groups = { "smoke",
			"sanity", "regression" })
	public void verifyErrorMessageForEmptyFullName() {
		homePage.clickOnGetDemoButton();

		requestDemoPage.writeCompanyName("Meon").writeCompanyEmail("saurabh.chhimwal@meon.co.in")
				.writePhoneNumber("8810619472").clickOnBookDemoButton();

		Assert.assertEquals(requestDemoPage.getFullNameErrorMessage(), "Please enter your full name");
	}

	@Test(priority = 3, description = "tc_05 - Verify error shown when Phone Number is empty", groups = { "sanity",
			"regression" })
	public void verifyErrorMessageForEmptyPhoneNumber() {
		homePage.clickOnGetDemoButton();

		requestDemoPage.writeFullName("Saurabh Test").writeCompanyName("Meon")
				.writeCompanyEmail("saurabh.chhimwal@meon.co.in").clickOnBookDemoButton();

		Assert.assertEquals(requestDemoPage.getFullNameErrorMessage(), "Please enter your mobile number");
	}

	@Test(priority = 4, description = "tc_ui_12 - Verify invalid email format is rejected", groups = { "sanity",
			"regression", "negative" })
	public void verifyInvalidEmailFormatIsRejected() {

		homePage.clickOnGetDemoButton();

		requestDemoPage.writeFullName("Saurabh Test").writeCompanyName("Meon").writeCompanyEmail("saurabh@gmail.co")
				.writePhoneNumber("8810619472").clickOnBookDemoButton();

		Assert.assertFalse(thankYouPage.isPageHeadingVisible(),
				"Form was submitted with invalid email format — validation is missing");
		Assert.assertTrue(requestDemoPage.isFormHeadingVisible(),
				"Form is not visible; expected to stay on Request Demo page");
	}

	@Test(priority = 5, description = "tc_ui_28 - Verify confirmation email content", groups = { "regression",
			"sanity" })
	public void verifyLeadingAndTrailingSpacesAreTrimmed() throws Exception {

		homePage.clickOnGetDemoButton();

		requestDemoPage
		.writeFullName("    Saurabh Chhimwal      ")
		.writeCompanyName("    Meon Technologies    ")
		.writeCompanyEmail("samchhimwal28@gmail.com")
		.writePhoneNumber("8810619472")
		.clickOnBookDemoButton();

		Assert.assertTrue(thankYouPage.isPageHeadingVisible(), "Demo request submission failed");

		Thread.sleep(10000);

		String emailBody = GmailUtility.getLatestEmailBody();

		Assert.assertTrue(emailBody.contains("Hi Saurabh Chhimwal"));

	}

	@Test(priority = 6, description = "tc_ui_22 - Verify error messages disappear after correction", groups = {
			"sanity", "regression"})
	public void verifyErrorMessagesDisappearAfterCorrection() {

		homePage.clickOnGetDemoButton();

		requestDemoPage.writeCompanyName("Meon").writeCompanyEmail("saurabh.chhimwal@meon.co.in")
				.writePhoneNumber("8810619472").clickOnBookDemoButton();

		Assert.assertTrue(requestDemoPage.isFullNameErrorVisible(),
				"Expected Full Name error message was not displayed");

		requestDemoPage.writeFullName("Saurabh Chhimwal");

		Assert.assertFalse(requestDemoPage.isFullNameErrorVisible(),
				"Expected Full Name error message was not displayed");
	}

	@Test(priority = 7, description = "tc_ui_25 - Verify Privacy Statement link opens correctly", groups = { "sanity",
			"regression"})
	public void verifyNavigationOfPrivacyStatementLink() {

		homePage.clickOnGetDemoButton();

		requestDemoPage.clickOnPrivacyStatementLink();

		Assert.assertFalse(requestDemoPage.isFormHeadingVisible(),
				"Form is not visible; expected to stay on Request Demo page");
	}

	@Test(priority = 8, description = "tc_ui_28 - Verify confirmation email content", groups = { "regression",
			"sanity" })
	public void verifyConfirmationEmailContent() throws Exception {

		homePage.clickOnGetDemoButton();

		requestDemoPage.writeFullName("Saurabh Chhimwal").writeCompanyName("Meon Technologies")
				.writeCompanyEmail("samchhimwal28@gmail.com").writePhoneNumber("8810619472").clickOnBookDemoButton();

		Assert.assertTrue(thankYouPage.isPageHeadingVisible(), "Demo request submission failed");

		Thread.sleep(10000);

		String emailBody = GmailUtility.getLatestEmailBody();

		Assert.assertTrue(emailBody.contains("Hi Saurabh Chhimwal"));
		Assert.assertTrue(emailBody.contains("We have received your query"));
		Assert.assertTrue(emailBody.contains("Team Meon Technologies"));

		Assert.assertFalse(emailBody.contains("{{"));
		Assert.assertFalse(emailBody.toLowerCase().contains("lorem"));

		String normalizedActual = normalizeEmailContent(emailBody);
		String normalizedExpected = normalizeEmailContent(EXPECTED_EMAIL_CONTENT);

		Assert.assertEquals(normalizedActual, normalizedExpected,
				"Confirmation email content does not match the approved template. "
						+ "Grammar, wording, or formatting may be incorrect.");
	}

}
