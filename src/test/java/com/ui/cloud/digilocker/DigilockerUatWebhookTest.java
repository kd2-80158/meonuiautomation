package com.ui.cloud.digilocker;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.ui.base.BaseTest;
import com.ui.product.digilocker.pages.DigilockerUatWebhookPage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;

/** Webhook configuration tests for the Demo Capital DigiLocker UAT tenant. */
@Listeners(com.ui.listeners.TestListener.class)
public final class DigilockerUatWebhookTest extends BaseTest {
    private DigilockerUatWebhookPage page;
    private JourneyEvidence evidence;

    @BeforeClass(alwaysRun = true)
    public void openTenant() {
        page = new DigilockerUatWebhookPage(driver);
        page.openSuperadmin();
        if (page.needsGoogleSignIn()) {
            Set<String> before = new HashSet<>(driver.getWindowHandles());
            page.clickGoogleSignIn();
            new WebDriverWait(driver, Duration.ofSeconds(40)).until(d -> {
                for (String handle : d.getWindowHandles()) {
                    d.switchTo().window(handle);
                    if (d.getCurrentUrl().contains("accounts.google.com")) return true;
                }
                return false;
            });
            By workAccount = By.xpath("//*[contains(normalize-space(.),'saurabh.chhimwal@meon.co.in') and (self::a or self::div[@role='link'])]");
            if (!driver.findElements(workAccount).isEmpty()) {
                driver.findElement(workAccount).click();
            } else {
                LoginPage_REKYCSuperadmin google = new LoginPage_REKYCSuperadmin(driver);
                google.waitForEmailPage().writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
                    .clickOnButton("nextbtn").waitForPasswordPage()
                    .writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
                    .clickOnButton("nextbtn");
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
            for (String handle : d.getWindowHandles()) {
                d.switchTo().window(handle);
                if (!d.findElements(By.id("search__company")).isEmpty()) return true;
            }
            return false;
        });
        page.searchDemoCapital();
        Set<String> beforeVisit = new HashSet<>(driver.getWindowHandles());
        page.visitAdmin();
        new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
            for (String handle : d.getWindowHandles()) {
                if (!beforeVisit.contains(handle)) d.switchTo().window(handle);
                if (d.getCurrentUrl().endsWith("/dashboard") && d.findElement(By.tagName("body")).getText().contains("DEMO CAPITAL MARKET")) return true;
            }
            return false;
        });
        evidence = new JourneyEvidence(driver, "digilocker_uat_webhook_tenant_navigation");
        evidence.capture("Demo Capital dashboard opened");
        page.openFromSidebar();
        page.awaitReady();
        evidence.capture("Webhook Settings opened");
    }

    @Test(description = "DL_WH_01 - Webhook Settings opens for Demo Capital", priority = 1)
    public void opensForCorrectTenant() {
        Assert.assertEquals(driver.getCurrentUrl(), DigilockerUatWebhookPage.WEBHOOK);
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains("DEMO CAPITAL MARKET"));
        evidence.capture("Correct tenant and webhook page");
    }

    @Test(description = "DL_WH_02 - URL, delivery switch and Save are visible", priority = 2)
    public void configurationControlsPresent() {
        Assert.assertTrue(driver.findElement(By.cssSelector("input[type='url']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.cssSelector("input[type='checkbox']")).isEnabled());
        Assert.assertTrue(driver.findElement(By.xpath("//button[normalize-space(.)='Save settings']")).isEnabled());
        evidence.capture("Webhook configuration controls");
    }

    @Test(description = "DL_WH_03 - Invalid webhook URL is blocked by browser validation", priority = 3)
    public void invalidUrlValidation() {
        String original = page.urlValue();
        page.setUrl("not-a-url");
        Assert.assertFalse(page.validationMessage().isEmpty(), "URL field should reject an invalid URL");
        evidence.capture("Invalid URL validation");
        page.setUrl(original);
    }

    @Test(description = "DL_WH_04 - Unsaved switch and URL changes do not persist", priority = 4)
    public void unsavedChangesDoNotPersist() {
        String originalUrl = page.urlValue();
        boolean originalEnabled = page.enabled();
        page.setUrl("https://example.invalid/digilocker-test");
        page.setEnabled(!originalEnabled);
        evidence.capture("Unsaved configuration changes");
        driver.navigate().refresh();
        page.awaitReady();
        Assert.assertEquals(page.urlValue(), originalUrl);
        Assert.assertEquals(page.enabled(), originalEnabled);
        evidence.capture("Original saved configuration restored");
    }

    @Test(description = "DL_WH_05 - Disabled empty webhook settings save successfully", priority = 5)
    public void disabledEmptyConfigurationCanSave() {
        if (page.enabled() || !page.urlValue().isEmpty())
            throw new SkipException("Disabled-empty save applies only before a callback is configured");
        page.save();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.successShown());
        evidence.capture("Disabled empty configuration saved");
    }

    @Test(description = "DL_WH_06 - Webhook guidance states payload and error behavior", priority = 6)
    public void webhookContractVisible() {
        String text = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(text.contains("v2/send_entire_data"));
        Assert.assertTrue(text.contains("HTTP POST"));
        Assert.assertTrue(text.contains("Webhook errors do not change the DigiLocker API response"));
        evidence.capture("Webhook delivery contract visible");
    }

    @Test(description = "DL_WH_07 - Save the hosted QA callback and verify persistence", priority = 7)
    public void configureHostedQaCallback() {
        if (!Boolean.parseBoolean(System.getProperty("digilocker.webhook.configure", "false")))
            throw new SkipException("Opt in with -Ddigilocker.webhook.configure=true to change Demo Capital UAT settings");
        String callback = System.getenv("DIGILOCKER_WEBHOOK_URL");
        boolean approvedTemporaryBin = Boolean.parseBoolean(
                System.getProperty("digilocker.webhook.allowTemporaryRelayBin", "false"))
                && "https://bin.webhookrelay.com/v1/webhooks/1b212d7b-43ef-4baf-8ece-388311009337".equals(callback);
        if (callback == null || (!callback.startsWith("https://api.extora.store/webhooks/digilocker/")
                && !approvedTemporaryBin))
            throw new SkipException("Use the verified OCI HTTPS callback, or explicitly opt in to the approved temporary Relay bin");
        page.setUrl(callback);
        page.setEnabled(true);
        page.save();
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> page.successShown());
        driver.navigate().refresh();
        page.awaitReady();
        Assert.assertEquals(page.urlValue(), callback, "Saved callback URL must persist");
        Assert.assertTrue(page.enabled(), "Webhook delivery must remain enabled");
    }

    @AfterMethod(alwaysRun = true)
    public void restorePage() {
        if (driver != null && driver.getCurrentUrl().contains("/webhook-settings")) {
            driver.navigate().refresh();
            page.awaitReady();
        }
    }

    @AfterClass(alwaysRun = true)
    public void finishEvidence() {
        if (evidence != null) evidence.finish();
    }
}
