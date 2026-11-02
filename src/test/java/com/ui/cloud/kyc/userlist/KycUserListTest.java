package com.ui.cloud.kyc.userlist;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.kyc.pages.superadmin.AdminUserPage_DemoCapital;
import com.ui.kyc.pages.superadmin.AdminUsersPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.DashboardPage_DemoCapital;
import com.ui.kyc.pages.superadmin.DashboardPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.LoginPage_KYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;
import com.ui.utilities.LoggerUtility;
import com.ui.utilities.ExtentReporterUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class KycUserListTest extends BaseTest {

	LoginPage_KYCSuperadmin loginPage;
	DashboardPage_KYCSuperadmin dashboardPage;
	DashboardPage_DemoCapital dashboardPage_DemoCapital;
	AdminUsersPage_KYCSuperadmin adminUserPage;
	AdminUserPage_DemoCapital adminUserPage_DemoCapital;
	Logger logger;
	String url;
	JourneyEvidence evidence;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setupBrowser();
		loginPage = new LoginPage_KYCSuperadmin(driver);
		dashboardPage = new DashboardPage_KYCSuperadmin(driver);
		adminUserPage = new AdminUsersPage_KYCSuperadmin(driver);
		dashboardPage_DemoCapital = new DashboardPage_DemoCapital(driver);
		adminUserPage_DemoCapital = new AdminUserPage_DemoCapital(driver);
		logger = LoggerUtility.getLogger(this.getClass());
		evidence = null;
	}

	@AfterMethod(alwaysRun = true)
	public void finishEvidence() {
		if (evidence == null) return;
		try {
			evidence.capture("Final browser state");
		} catch (RuntimeException error) {
			ExtentReporterUtility.warning("Final evidence screenshot unavailable: " + error.getMessage());
		} finally {
			evidence.finish();
			evidence = null;
		}
	}

	private void openKYCSuperadminLoginPage() {
		url = JSONUtility.getSuperAdminWebsite().getUrl();
		driver.get(url);
		softAssert.assertTrue(ensureOnPage("/superadmin", "KYC Superadmin"),
				"Corporate Re-KYC login page should be displayed");
	}

	@Test(description = "Verify Demo Capital admin user can be created and deleted by its unique email and mobile", priority = 1)
	public void verifyAddUserFunctionality_DemoCapital() {

		openKYCSuperadminLoginPage();
		evidence = new JourneyEvidence(driver, "verifyAddUserFunctionality_DemoCapital");
		evidence.capture("KYC superadmin login page");

		String email = JSONUtility.getCredentials().getKycAdmin().getEmail();

		String password = JSONUtility.getCredentials().getKycAdmin().getPassword();
		long uniqueValue = System.currentTimeMillis();
		String testUserName = "Ram";
		String testUserMobile = "8" + String.format("%09d", uniqueValue % 1_000_000_000L);
		String testUserEmail = "ramqa" + uniqueValue + "@gmail.com";

		logger.info("Opening Google Sign In");

		loginPage.clickOnButton("googlesignin");

		logger.info("Waiting for email page");

		loginPage.waitForEmailPage();

		logger.info("Entering email");

		loginPage.writeService("email", email);

		logger.info("Clicking email Next");

		loginPage.clickOnButton("nextbtn");

		logger.info("Waiting for password page");

		loginPage.waitForPasswordPage();

		logger.info("Entering password");

		loginPage.writeService("password", password);

		logger.info("Clicking password Next");

		loginPage.clickOnButton("nextbtn");
		dashboardPage.waitForDashboard();
		evidence.capture("Superadmin dashboard after sign in");
		dashboardPage.writeOnSearchBar("DEMO CAPITAL MARKET").clickOnSearchButton_KYCSuperadmin();
		boolean isCompany = dashboardPage.isTextVisible();
		evidence.capture("Demo Capital company result");
		dashboardPage.clickOnButton("visitadmin_button").switchToNewAdminTab();
		evidence.capture("Demo Capital admin dashboard");
		if (isCompany)
			dashboardPage_DemoCapital.clickOnUsersList_DemoCapital();
		else
			throw new RuntimeException("Search functionality is not working--->Required company name returning: "
					+ dashboardPage.isTextVisible());
		evidence.capture("Admin Users page before creation");
		adminUserPage_DemoCapital
		        .openAddUserFormWithoutModules();
		evidence.capture("Add Admin User form");
		adminUserPage_DemoCapital
		        .writeService("name", testUserName).writeService("mobile", testUserMobile)
					.writeService("email_address", testUserEmail).writeService("password", "Saurabh@12345")
				.clickOnSaveButton();
		evidence.capture("Admin user save response");
		adminUserPage_DemoCapital.findCreatedAdminUser(testUserName, testUserEmail, testUserMobile);
		evidence.capture("Created user found by unique mobile");
		adminUserPage_DemoCapital.deleteCreatedAdminUser(testUserName, testUserEmail, testUserMobile);
		evidence.capture("Created user deleted successfully");

		softAssert.assertTrue(adminUserPage_DemoCapital.isHeadingVisible("deleteuser_successmsg"), "Deleted");
		softAssert.assertAll();
	}

}
