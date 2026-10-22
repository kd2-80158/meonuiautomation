package com.ui.cloud.kyc.userlist;

import org.apache.logging.log4j.Logger;
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
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class KycUserListTest extends BaseTest {

	LoginPage_KYCSuperadmin loginPage;
	DashboardPage_KYCSuperadmin dashboardPage;
	DashboardPage_DemoCapital dashboardPage_DemoCapital;
	AdminUsersPage_KYCSuperadmin adminUserPage;
	AdminUserPage_DemoCapital adminUserPage_DemoCapital;
	Logger logger;
	String url;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setupBrowser();
		loginPage = new LoginPage_KYCSuperadmin(driver);
		dashboardPage = new DashboardPage_KYCSuperadmin(driver);
		adminUserPage = new AdminUsersPage_KYCSuperadmin(driver);
		dashboardPage_DemoCapital = new DashboardPage_DemoCapital(driver);
		adminUserPage_DemoCapital = new AdminUserPage_DemoCapital(driver);
		logger = LoggerUtility.getLogger(this.getClass());
	}

	private void openKYCSuperadminLoginPage() {
		url = JSONUtility.getSuperAdminWebsite().getUrl();
		driver.get(url);
		softAssert.assertTrue(ensureOnPage("/superadmin", "KYC Superadmin"),
				"Corporate Re-KYC login page should be displayed");
	}

	@Test(description = "Verify add user functionality in KYC superadmin", priority = 1)
	public void verifyAddUserFunctionality_DemoCapital() {

		openKYCSuperadminLoginPage();

		String email = JSONUtility.getCredentials().getKycAdmin().getEmail();

		String password = JSONUtility.getCredentials().getKycAdmin().getPassword();
		long uniqueValue = System.currentTimeMillis();
		String testUserMobile = "8" + String.format("%09d", uniqueValue % 1_000_000_000L);
		String testUserEmail = "saurabhtest+" + uniqueValue + "@gmail.com";

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
		dashboardPage.writeOnSearchBar("DEMO CAPITAL MARKET").clickOnSearchButton_KYCSuperadmin();
		boolean isCompany = dashboardPage.isTextVisible();
		dashboardPage.clickOnButton("visitadmin_button").switchToNewAdminTab();
		if (isCompany)
			dashboardPage_DemoCapital.clickOnUsersList_DemoCapital();
		else
			throw new RuntimeException("Search functionality is not working--->Required company name returning: "
					+ dashboardPage.isTextVisible());
		adminUserPage_DemoCapital
		        .openAddUserFormWithoutModules()
		        .writeService("name", "Saurabh").writeService("mobile", testUserMobile)
					.writeService("email_address", testUserEmail).writeService("password", "Saurabh@12345")
				.clickOnSaveButton()
					.writeOnUserSearchBar(testUserMobile)
					.clickOn("action_menu")
				.clickOn("remove")
				.clickOn("confirm");

		softAssert.assertTrue(adminUserPage_DemoCapital.isHeadingVisible("deleteuser_successmsg"), "Deleted");
		softAssert.assertAll();
	}

}
