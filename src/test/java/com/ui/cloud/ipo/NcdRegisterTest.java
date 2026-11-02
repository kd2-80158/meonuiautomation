package com.ui.cloud.ipo;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ui.base.BaseTest;
import com.ui.product.ipo.pages.NcdLandingPage;
import com.ui.product.ipo.pages.NcdRegisterPage;

public final class NcdRegisterTest extends BaseTest {
    private NcdRegisterPage page;

    @BeforeMethod(alwaysRun = true)
    public void openPage() { page = new NcdLandingPage(driver).open().openRegister(); }

    @Test(description = "NCD_AUTH_13 - Registration details fields render", groups = {"ipo", "ncd", "smoke"})
    public void detailsFieldsRender() {
        Assert.assertTrue(page.hasAllDetailsFields());
    }

    @Test(description = "NCD_AUTH_14 - Registration progress and links render", groups = {"ipo", "ncd"})
    public void progressAndLinksRender() {
        Assert.assertTrue(page.hasThreeSteps());
        Assert.assertTrue(page.hasTermsAndNavigationLinks());
    }

    @Test(description = "NCD_AUTH_17 - Invalid email fails browser syntax validation", groups = {"ipo", "ncd", "validation"})
    public void invalidEmailFormatRejected() {
        Assert.assertEquals(page.emailType(), "email");
        Assert.assertTrue(page.invalidEmailFailsBrowserValidation());
    }

    @Test(description = "NCD_AUTH_18/19 - Mobile and PAN length caps", groups = {"ipo", "ncd", "validation"})
    public void mobileAndPanLengthCaps() {
        Assert.assertEquals(page.mobileMaxLength(), "10");
        Assert.assertEquals(page.panMaxLength(), "10");
    }

    @Test(description = "NCD_AUTH_20 - Depository selection is exclusive", groups = {"ipo", "ncd", "validation"})
    public void depositoryRadioExclusive() {
        Assert.assertTrue(page.radioOptionsAreExclusive());
    }

    @Test(description = "NCD_REG_01-13 - Approved registration through verification and demat", groups = {"ipo", "ncd", "manual-fixture"})
    public void approvedRegistrationJourney() {
        throw new SkipException("Requires disposable identity, OTP access and action-time Terms consent; no account creation is performed by default");
    }
}
