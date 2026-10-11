package com.ui.cloud.kyc.userlist;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.kyc.pages.superadmin.AdminUsersPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.DashboardPage_KYCSuperadmin;
import com.ui.kyc.pages.superadmin.LoginPage_KYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class KycUserListTest extends BaseTest {

	LoginPage_KYCSuperadmin loginPage;
	DashboardPage_KYCSuperadmin dashboardPage;
	AdminUsersPage_KYCSuperadmin adminUserPage;
	Logger logger;
	String url;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setupBrowser();
		loginPage = new LoginPage_KYCSuperadmin(driver);
		dashboardPage = new DashboardPage_KYCSuperadmin(driver);
		adminUserPage = new AdminUsersPage_KYCSuperadmin(driver);
		logger = LoggerUtility.getLogger(this.getClass());
	}
	
	private void openKYCSuperadminLoginPage() {

		url = JSONUtility.getSuperAdminWebsite().getUrl();
		driver.get(url);
		softAssert.assertTrue(ensureOnPage("/superadmin", "KYC Superadmin"),
				"Corporate Re-KYC login page should be displayed");
	}

	@Test(description="Verify add user functionality in KYC superadmin",priority=1)
	public void verifyAddUserFunctionality_KYCSuperadmin()
	{
			openKYCSuperadminLoginPage();
		loginPage.clickOnButton("googlesignin")
		         .writeService("email", "saurabh.chhimwal@meon.co.in")
		         .clickOnButton("nextbtn")
		         .writeService("password", "Saurabhmeon@27")
		         .clickOnButton("nextbtn");
		dashboardPage.clickOnUsers_KYCSuperadmin();
		adminUserPage.clickOnButton("adduser_button").clickDropdown("username")
		             .clickOnButton("user_subhi")
		             .clickDropdown("accessgranted").clickOnButton("accessgrant_subhi")
		             .clickDropdown("useractive").clickOnButton("useractive")
		             .clickDropdown("companyselection").clickOnButton("companyselection")
		             .clickOnButton("dialogbox_heading")
		             .clickOnSaveButton()
		             .clickOnButton("ok_button")
		             .clickOnButton("searchbar")
		             .writeService("searchbar", "shubhi")
		             .clickRemoveUser("M314");
		softAssert.assertTrue(adminUserPage.isHeadingVisible("deleteuser_successmsg"), "Deleted");
		softAssert.assertAll();	
	}

}
