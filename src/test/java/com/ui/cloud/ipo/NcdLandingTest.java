package com.ui.cloud.ipo;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ui.base.BaseTest;
import com.ui.product.ipo.pages.NcdLandingPage;

public final class NcdLandingTest extends BaseTest {
    private NcdLandingPage page;

    @BeforeMethod(alwaysRun = true)
    public void openPage() { page = new NcdLandingPage(driver).open(); }

    @Test(description = "NCD_AUTH_01 - Company identity renders", groups = {"ipo", "ncd", "smoke"})
    public void companyIdentityRenders() {
        Assert.assertTrue(page.hasCompanyIdentity(), "Marfatia identity or logo is missing");
    }

    @Test(description = "NCD_AUTH_02/NCD_DASH_02 - Login navigation", groups = {"ipo", "ncd", "smoke"})
    public void loginNavigation() {
        Assert.assertTrue(page.openLogin().hasThreeSteps(), "Login progress is incomplete");
    }

    @Test(description = "NCD_AUTH_03/NCD_DASH_02 - Register navigation", groups = {"ipo", "ncd", "smoke"})
    public void registerNavigation() {
        Assert.assertTrue(page.openRegister().hasAllDetailsFields(), "Registration fields are incomplete");
    }

    @Test(description = "NCD_DASH_01 - Live issues or explicit empty state", groups = {"ipo", "ncd", "smoke"})
    public void issueFeedResolves() {
        Assert.assertTrue(page.hasResolvedIssueState(), "Issue feed remained in loading state");
    }
}
