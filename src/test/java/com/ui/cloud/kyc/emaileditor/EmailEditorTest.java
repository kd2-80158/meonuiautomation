package com.ui.cloud.kyc.emaileditor;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.kyc.pages.superadmin.DashboardPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.EmailEditorPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.LoginPage_KYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class EmailEditorTest extends BaseTest {

	LoginPage_KYCSuperadmin loginPage;
	DashboardPage_KYCSuperadmin dashboardPage;
	EmailEditorPage_KYCSuperadmin emailEditorPage;
	Logger logger;
	String url;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setupBrowser();
		loginPage = new LoginPage_KYCSuperadmin(driver);
		dashboardPage = new DashboardPage_KYCSuperadmin(driver);
		emailEditorPage = new EmailEditorPage_KYCSuperadmin(driver);
		logger = LoggerUtility.getLogger(this.getClass());
	}

	private void openKYCSuperadminLoginPage() {

		url = JSONUtility.getSuperAdminWebsite().getUrl();
		driver.get(url);
		softAssert.assertTrue(ensureOnPage("/superadmin", "KYC Superadmin"),
				"Corporate Re-KYC login page should be displayed");
	}

	@Test(description = "Verify add email template functionality in KYC superadmin", priority = 1)
	public void verifyAddEmailTemplateFunctionality_KYCSuperadmin() {

		openKYCSuperadminLoginPage();

		String email = JSONUtility.getCredentials().getKycAdmin().getEmail();
		String password = JSONUtility.getCredentials().getKycAdmin().getPassword();

		loginPage.clickOnButton("googlesignin").writeService("email", email)
				.clickOnButton("nextbtn").writeService("password", password)
				.clickOnButton("nextbtn");

		dashboardPage.clickOnEmailEditor_KYCSuperadmin();

		String uniqueValue = "AutoTemplate_" + System.currentTimeMillis();

		String subject = uniqueValue;
		String templateName = uniqueValue;
		String body = "Automated email body";

		int previousRowCount = emailEditorPage.getEmailTemplateRowCount();

		emailEditorPage.clickOnButton("addtemplate_button").writeService("emailsubject", subject)
				.writeService("emailtemplate", templateName).writeService("emailbody", body).clickSaveButton();

		softAssert.assertTrue(emailEditorPage.waitForNewEmailTemplateRow(previousRowCount),
				"New email template row should be added after clicking Save");

		softAssert.assertTrue(emailEditorPage.isEmailTemplatePresent(subject, templateName),
				"Created email template should be visible in the dashboard table");

		softAssert.assertAll();
	}

}
