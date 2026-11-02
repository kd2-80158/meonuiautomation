package com.ui.cloud.ipo;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ui.base.BaseTest;
import com.ui.product.ipo.pages.NcdLandingPage;
import com.ui.product.ipo.pages.NcdLoginPage;

public final class NcdLoginTest extends BaseTest {
    private NcdLoginPage page;

    @BeforeMethod(alwaysRun = true)
    public void openPage() { page = new NcdLandingPage(driver).open().openLogin(); }

    @Test(description = "NCD_AUTH_04 - Login progress and guidance", groups = {"ipo", "ncd"})
    public void progressRenders() {
        Assert.assertTrue(page.hasThreeSteps());
    }

    @Test(description = "NCD_AUTH_05 - Empty client code rejected", groups = {"ipo", "ncd", "validation"})
    public void emptyClientCodeRejected() {
        page.enterClientCode("");
        page.continueToVerification();
        Assert.assertTrue(page.hasClientCodeError());
        Assert.assertTrue(page.staysOnFirstStep());
    }

    @Test(description = "NCD_AUTH_06 - One-character client code rejected", groups = {"ipo", "ncd", "validation"})
    public void shortClientCodeRejected() {
        page.enterClientCode("A");
        page.continueToVerification();
        Assert.assertTrue(page.hasClientCodeError());
        Assert.assertTrue(page.staysOnFirstStep());
    }

    @Test(description = "NCD_AUTH_07 - Client-code input caps at 50 characters", groups = {"ipo", "ncd", "validation"})
    public void clientCodeMaxLength() {
        Assert.assertEquals(page.clientCodeMaxLength(), "50");
        page.enterClientCode("X".repeat(51));
        Assert.assertEquals(page.clientCodeValue().length(), 50);
    }

    @Test(description = "NCD_AUTH_08 - Whitespace-only client code rejected", groups = {"ipo", "ncd", "validation"})
    public void whitespaceClientCodeRejected() {
        page.enterClientCode("   ");
        page.continueToVerification();
        Assert.assertTrue(page.hasClientCodeError());
        Assert.assertTrue(page.staysOnFirstStep());
    }

    @Test(description = "NCD_AUTH_10/24-32 - Approved login through OTP and demat", groups = {"ipo", "ncd", "manual-fixture"})
    public void approvedOtpAndDematJourney() {
        throw new SkipException("Requires disposable client code, OTP access and observed step-2/3 locators; no production account is supplied");
    }
}
