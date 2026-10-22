package com.ui.cloud.facefinder.configurations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.facefinder.pages.CredentialsPage_SideNavigationBar_FaceFinderCompany;
import com.ui.product.facefinder.pages.EditCredentialsPage_FaceFinderCompany;
import com.ui.product.facefinder.pages.ThresholdPage_FaceFinderCompany;
import com.ui.product.facefinder.pages.LoginPage_FaceFinderSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

/** Read-only checks for the authenticated FaceFinder credentials workspace.
 *  No credential is created, rotated, updated or deleted.
 *  Authentication must be explicitly established before these tests can run.
 */
@Listeners({ com.ui.listeners.TestListener.class })
public class FaceFinderConfigurationsTest extends BaseTest {

    private static final String BASE = "https://face-finder.meon.co.in";
    private static final By HEADING = By.xpath("//h1[normalize-space()='Credentials']");
    private static final By CANCEL = By.xpath("//button[normalize-space()='Cancel']");
    private WebDriverWait wait;
    private Logger logger;
    private CredentialsPage_SideNavigationBar_FaceFinderCompany credentials;
    private EditCredentialsPage_FaceFinderCompany edit;
    private ThresholdPage_FaceFinderCompany thresholds;
    private LoginPage_FaceFinderSuperadmin loginPage;

    @BeforeClass(alwaysRun = true)
    public void initialize() {
        super.setupBrowser();
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        logger = LoggerUtility.getLogger(getClass());
        credentials = new CredentialsPage_SideNavigationBar_FaceFinderCompany(driver);
        edit = new EditCredentialsPage_FaceFinderCompany(driver);
        thresholds = new ThresholdPage_FaceFinderCompany(driver);
        loginPage = new LoginPage_FaceFinderSuperadmin(driver);
        authenticateIfNecessary();
    }

    private void authenticateIfNecessary() {
        driver.get(BASE + "/dashboard/credentials");
        if (isCredentialsPage()) {
            logger.info("Already authenticated to FaceFinder");
            return;
        }

        driver.get(BASE + "/superadmin-login");
        if (!loginPage.hasGoogleButton()) {
            throw new AssertionError("FaceFinder login page has no Continue with Google button. URL: "
                    + driver.getCurrentUrl());
        }

        String mode = System.getProperty("facefinder.loginMode", "auto").trim().toLowerCase();
        if ("session".equals(mode)) {
            throw new AssertionError("No authenticated session in this Selenium browser. "
                    + "ChromeDriver creates a separate profile by default. Use manual or auto login mode.");
        }

        try {
            loginPage.clickGoogleWhenEnabled(Duration.ofSeconds(45));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError("Google Sign-In button remained disabled or unclickable for 45 seconds "
                    + "on " + driver.getCurrentUrl()
                    + ". Verify application auth initialization, API errors, or Google OAuth configuration. "
                    + "Do not force-click a disabled control.", e);
        }

        logger.info("Clicked FaceFinder Google Sign-In; loginMode={}", mode);
        if ("auto".equals(mode)) {
            try {
                // Google may redirect in the current window or open a new window.
                new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
                    for (String handle : d.getWindowHandles()) {
                        d.switchTo().window(handle);
                        if (d.getCurrentUrl().contains("accounts.google.com")
                                || d.getCurrentUrl().startsWith(BASE + "/dashboard")) return true;
                    }
                    return false;
                });
                if (driver.getCurrentUrl().contains("accounts.google.com")) {
                    performGoogleLogin();
                }
            } catch (org.openqa.selenium.TimeoutException e) {
                throw new AssertionError("Google OAuth did not redirect or open a login window. "
                        + "Check browser popups, console and failed network requests.", e);
            }
        } else if (!"manual".equals(mode)) {
            throw new IllegalArgumentException("Unknown facefinder.loginMode: " + mode);
        }

        // In manual mode finish the legitimate OAuth/consent/MFA flow in Selenium's browser.
        // In auto mode the same wait handles Google's optional MFA and account chooser.
        waitForFaceFinderDashboard();
        driver.get(BASE + "/dashboard/credentials");
        if (!isCredentialsPage()) {
            throw new AssertionError("Google login returned but credentials workspace is inaccessible. "
                    + "Current URL: " + driver.getCurrentUrl()
                    + ". Verify authorized company/admin role and workspace access.");
        }
    }

    private void performGoogleLogin() {
        // Intended for authorized interactive test accounts only; Google may require MFA.
        String email = JSONUtility.getCredentials().getKycAdmin().getEmail();
        String password = JSONUtility.getCredentials().getKycAdmin().getPassword();
        By emailField = By.id("company-identity");
        By passwordField = By.id("company-password");
        if (!driver.findElements(emailField).isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(email);
            wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierNext"))).click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("passwordNext"))).click();
    }

    private void waitForFaceFinderDashboard() {
        new WebDriverWait(driver, Duration.ofSeconds(180)).until(d -> {
            for (String handle : d.getWindowHandles()) {
                d.switchTo().window(handle);
                if (d.getCurrentUrl().startsWith(BASE + "/dashboard")) return true;
            }
            return false;
        });
    }

    private boolean isCredentialsPage() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(HEADING));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) { return false; }
    }

    @BeforeMethod(alwaysRun = true)
    public void resetToCredentials() {
        driver.get(BASE + "/dashboard/credentials");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError("Credentials workspace inaccessible after authentication. "
                    + "Current URL: " + driver.getCurrentUrl()
                    + ". Do not treat login/setup failures as feature-test failures.", e);
        }
    }

    private void showNodes() { credentials.clickAction("nodes"); }

    @Test(priority=1, description="Credentials workspace heading and Add button")
    public void verifyWorkspace() {
        Assert.assertTrue(credentials.isHeadingVisible(), "Credentials heading missing");
        Assert.assertTrue(credentials.hasAction("add"), "Add Credentials missing");
    }

    @Test(priority=2, description="Node, table and refresh actions exist")
    public void verifyToolbar() {
        for (String action : new String[]{"nodes", "table", "refresh"}) {
            Assert.assertTrue(credentials.hasAction(action), "Missing toolbar action: " + action);
        }
    }

    @Test(priority=3, description="Switch between table and node display")
    public void verifyViewSwitch() {
        credentials.clickAction("table");
        Assert.assertTrue(credentials.hasAction("nodes"));
        showNodes();
        Assert.assertTrue(credentials.hasAction("edit"), "Credential node not visible");
    }

    @Test(priority=4, description="Refresh credentials and retain page")
    public void verifyRefresh() {
        credentials.clickAction("refresh");
        Assert.assertTrue(credentials.isHeadingVisible());
    }

    @Test(priority=5, description="Node actions for edit, threshold and copy")
    public void verifyNodeActions() {
        showNodes();
        for (String action : new String[]{"edit", "threshold", "copy_id", "copy_secret"}) {
            Assert.assertTrue(credentials.hasAction(action), "Missing node action: " + action);
        }
        // Intentionally do not copy or log client secrets.
    }

    @Test(priority=6, description="Edit Credentials form structure")
    public void verifyEditForm() {
        showNodes(); credentials.clickAction("edit");
        try {
            Assert.assertTrue(edit.hasUpdateButton(), "Update Credentials missing");
            for (String id : new String[]{"capture_version", "redirect_url", "webhook_url",
                    "speech_text_match_perc", "face_match_percentage", "video_time", "background_color"}) {
                Assert.assertTrue(edit.hasField(id), "Missing edit field: " + id);
            }
        } finally { safeCancel(); }
    }

    @Test(priority=7, description="Check verification toggle controls")
    public void verifyToggleControls() {
        showNodes(); credentials.clickAction("edit");
        try {
            for (String name : new String[]{"is_active", "auto_capture", "video_with_auto_capture",
                    "capture_video", "check_location", "match_faces", "read_script", "spoofing_detection",
                    "enable_webhook", "send_mail_notification", "glasses_detection", "face_center_check",
                    "eyes_open_strict_check", "nakedness_check", "multi_face_strict_check",
                    "motion_blur_detection", "face_mask_detection", "full_view", "remove_background",
                    "open_mouth_check", "gender_detection", "age_detection"}) {
                Assert.assertTrue(edit.hasToggle(name), "Missing verification toggle: " + name);
            }
        } finally { safeCancel(); }
    }

    @Test(priority=8, description="Redirect URL configured with HTTPS")
    public void verifyRedirectUrl() {
        showNodes(); credentials.clickAction("edit");
        try {
            String url = edit.getFieldValue("redirect_url");
            Assert.assertNotNull(url, "Redirect URL value absent");
            if (!url.trim().isEmpty()) Assert.assertTrue(url.startsWith("https://"),
                    "Redirect URL should use HTTPS");
        } finally { safeCancel(); }
    }

    @Test(priority=9, description="Threshold setup controls")
    public void verifyThresholdControls() {
        showNodes(); credentials.clickAction("threshold");
        try {
            Assert.assertTrue(thresholds.hasSaveButton(), "Save Thresholds missing");
            Assert.assertEquals(thresholds.increaseButtonCount(), 7, "Threshold step-up count");
            Assert.assertEquals(thresholds.decreaseButtonCount(), 7, "Threshold step-down count");
        } finally { safeCancel(); }
    }

    @Test(priority=10, description="Add Credentials form opens without saving")
    public void verifyAddCredentialsDialog() {
        credentials.clickAction("add");
        try {
            Assert.assertTrue(!driver.findElements(CANCEL).isEmpty(), "Cancel not found in add form");
            Assert.assertTrue(!driver.findElements(By.cssSelector("input, select")).isEmpty(),
                    "Add form has no input fields");
        } finally { safeCancel(); }
    }

    @Test(priority=11, description="Cancel edit does not change redirect URL")
    public void verifyCancelPersistsOriginal() {
        showNodes(); credentials.clickAction("edit");
        String original;
        try { original = edit.getFieldValue("redirect_url"); }
        finally { safeCancel(); }
        credentials.clickAction("edit");
        try { Assert.assertEquals(edit.getFieldValue("redirect_url"), original); }
        finally { safeCancel(); }
    }

    @Test(priority=12, description="Credentials page refresh survives browser reload")
    public void verifyBrowserReload() {
        driver.navigate().refresh();
        Assert.assertTrue(isCredentialsPage(), "Credentials page did not survive reload");
    }

    private void safeCancel() {
        if (!driver.findElements(CANCEL).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(CANCEL)).click();
        }
    }

    @AfterClass(alwaysRun=true)
    public void closeBrowser() {
        if (logger != null) logger.info("FaceFinder configuration test suite completed");
        if (driver != null) { driver.quit(); driver = null; }
    }
}
