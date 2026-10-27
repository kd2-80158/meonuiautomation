package com.ui.cloud.rekyc.autopush;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.admin.meontechnologies.AutoPushPage;
import com.ui.product.rekyc.pages.superadmin.DashboardPage_REKYCSuperadmin;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.JSONUtility;

/** Shared browser and explicit test-workflow guard for Auto Push UAT tests. */
public abstract class AutoPushUatTestBase extends BaseTest {
    protected AutoPushPage page;
    protected final String baseUrl = System.getProperty("autoPush.baseUrl", "https://rekycuat.meon.co.in");

    @BeforeClass(alwaysRun = true)
    public void openAutoPush() {
        setupBrowser();
        driver.get(baseUrl + "/v1/company/superadmin");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        if (!driver.getCurrentUrl().contains("/v1/company/companies")) {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.google-btn"))).click();
            LoginPage_REKYCSuperadmin login = new LoginPage_REKYCSuperadmin(driver);
            login.waitForEmailPage()
                    .writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
                    .clickOnButton("nextbtn")
                    .waitForPasswordPage()
                    .writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
                    .clickOnButton("nextbtn");
            wait.until(ExpectedConditions.urlContains("/v1/company/companies"));
        }
        DashboardPage_REKYCSuperadmin dashboard = new DashboardPage_REKYCSuperadmin(driver);
        dashboard.clickOn("search").writeService("search", "meon");
        String superadminHandle = driver.getWindowHandle();
        Set<String> existingHandles = new HashSet<>(driver.getWindowHandles());
        dashboard.clickOn("visitadmin");
        String adminHandle = wait.until(d -> {
            for (String handle : d.getWindowHandles()) {
                if (!handle.equals(superadminHandle) && existingHandles.contains(handle)) continue;
                d.switchTo().window(handle);
                String url = d.getCurrentUrl();
                if (url.startsWith(baseUrl + "/v1/company/")
                        && !url.contains("/companies") && !url.contains("/superadmin")
                        && !d.findElements(By.cssSelector("a[href='#general-pages']")).isEmpty()) {
                    return handle;
                }
            }
            return null;
        });
        driver.switchTo().window(adminHandle);
        driver.get(baseUrl + AutoPushPage.PATH);
        try {
            wait.until(d -> d.getCurrentUrl().equals(baseUrl + AutoPushPage.PATH)
                    && d.findElements(By.id("apWorkflowTrigger")).stream().anyMatch(WebElement::isDisplayed));
        } catch (org.openqa.selenium.TimeoutException noSession) {
            throw new SkipException("ReKYC Google sign-in did not establish a tenant Admin session for Auto Push");
        }
        page = new AutoPushPage(driver);
    }

    protected String testWorkflow() {
        String workflow = System.getProperty("autoPush.testWorkflow", "").trim();
        if (workflow.isEmpty()) throw new SkipException("Set -DautoPush.testWorkflow to a disposable UAT workflow");
        return workflow;
    }

    protected void selectReadWorkflow() {
        page.chooseWorkflow(System.getProperty("autoPush.readWorkflow", "modification"));
    }

    protected void openReadWorkflow() {
        page.open(baseUrl);
        selectReadWorkflow();
    }

    protected void openDisposableWorkflow() {
        requireMutationOptIn();
        page.open(baseUrl);
        page.chooseWorkflow(testWorkflow());
        if (page.selectedWorkflow().contains("Invalid configuration")) {
            throw new SkipException("Disposable workflow has no valid Maker/Checker Verify configuration");
        }
    }

    protected String requiredProperty(String name, String explanation) {
        String value = System.getProperty(name, "").trim();
        if (value.isEmpty()) throw new SkipException("Set -D" + name + ": " + explanation);
        return value;
    }

    protected void requireMutationOptIn() {
        if (!Boolean.parseBoolean(System.getProperty("autoPush.mutate", "false"))) {
            throw new SkipException("Mutating UAT test requires -DautoPush.mutate=true");
        }
        testWorkflow();
    }

    @AfterClass(alwaysRun = true)
    public void closeAutoPushBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
