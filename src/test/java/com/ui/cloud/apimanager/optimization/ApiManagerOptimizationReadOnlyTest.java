package com.ui.cloud.apimanager.optimization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.UsageLogsPage_APIManager;
import com.ui.product.apimanager.pages.VendorMonitoringPage_APIManager;
import com.ui.product.apimanager.pages.resources.AlertRulesPage;
import com.ui.product.apimanager.pages.resources.AssignmentLedgerPage;
import com.ui.product.apimanager.pages.resources.ApiOperationsPage;
import com.ui.product.apimanager.pages.resources.VendorsPage;
import com.ui.utilities.ExtentReporterUtility;


public final class ApiManagerOptimizationReadOnlyTest extends ApiManagerAuthenticatedTest {

    @Test(description = "OPT-01: Billing ledger remains readable after refund lookup optimization",
            groups = {"apimanager", "optimization", "read-only"})
    public void refundLedgerReadContractRemainsAvailable() {
        AssignmentLedgerPage ledger = new AssignmentLedgerPage(driver);
        ledger.open(baseUrl);
        Assert.assertEquals(ledger.heading(), ledger.expectedHeading());
        Assert.assertTrue(ledger.hasSearch(), "Ledger search is missing");
        Assert.assertTrue(ledger.hasButton("Refresh"), "Ledger refresh is missing");
        captureEvidence("OPT-01", ledger.expectedHeading(), "Ledger heading, search, and refresh controls verified");
    }

    @Test(description = "OPT-02: API operations remain readable after rate-card bulk-upsert optimization",
            groups = {"apimanager", "optimization", "read-only"})
    public void operationCatalogReadContractRemainsAvailable() {
        ApiOperationsPage operations = new ApiOperationsPage(driver);
        operations.open(baseUrl);
        Assert.assertEquals(operations.heading(), operations.expectedHeading());
        Assert.assertTrue(operations.hasSearch(), "Operation search is missing");
        captureEvidence("OPT-02", operations.expectedHeading(), "Operation heading and search control verified");
    }

    @Test(description = "OPT-03: Usage-log view remains available after invoice-generation optimization",
            groups = {"apimanager", "optimization", "read-only"})
    public void usageLogsReadContractRemainsAvailable() {
        UsageLogsPage_APIManager usage = new UsageLogsPage_APIManager(driver);
        usage.open(baseUrl);
        Assert.assertTrue(usage.hasFilters(), "Usage-log filters are missing");
        captureEvidence("OPT-03", "Usage Logs", "Usage-log filters verified");
    }

    @Test(description = "OPT-04: Vendor list and monitoring remain available after health-check optimization",
            groups = {"apimanager", "optimization", "read-only"})
    public void vendorHealthReadContractRemainsAvailable() {
        VendorsPage vendors = new VendorsPage(driver);
        vendors.open(baseUrl);
        Assert.assertEquals(vendors.heading(), vendors.expectedHeading());
        captureEvidence("OPT-04", vendors.expectedHeading(), "Vendor list heading verified");
        VendorMonitoringPage_APIManager monitoring = new VendorMonitoringPage_APIManager(driver);
        monitoring.open(baseUrl);
        Assert.assertTrue(monitoring.hasMonitoringControls(), "Vendor monitoring controls are missing");
        captureEvidence("OPT-04", "Vendor Health & Monitoring", "Vendor monitoring controls verified");
    }

    @Test(description = "OPT-05: Alert rules remain readable after observability maintenance optimization",
            groups = {"apimanager", "optimization", "read-only"})
    public void alertRulesReadContractRemainsAvailable() {
        AlertRulesPage rules = new AlertRulesPage(driver);
        rules.open(baseUrl);
        Assert.assertEquals(rules.heading(), rules.expectedHeading());
        Assert.assertTrue(rules.hasSearch(), "Alert-rule search is missing");
        captureEvidence("OPT-05", rules.expectedHeading(), "Alert-rule heading and search control verified");
    }

    @AfterMethod(alwaysRun = true)
    public void captureFailureHeading(ITestResult result) {
        if (result.getStatus() != ITestResult.FAILURE || driver == null) return;
        String method = result.getMethod().getMethodName();
        String caseId;
        String[] allowedHeadings;
        switch (method) {
            case "refundLedgerReadContractRemainsAvailable":
                caseId = "OPT-01"; allowedHeadings = new String[] {"Assignment Wallet Ledger"}; break;
            case "operationCatalogReadContractRemainsAvailable":
                caseId = "OPT-02"; allowedHeadings = new String[] {"API Operations"}; break;
            case "usageLogsReadContractRemainsAvailable":
                caseId = "OPT-03"; allowedHeadings = new String[] {"Usage Logs"}; break;
            case "vendorHealthReadContractRemainsAvailable":
                caseId = "OPT-04"; allowedHeadings = new String[] {"Vendors", "Vendor Health & Monitoring"}; break;
            case "alertRulesReadContractRemainsAvailable":
                caseId = "OPT-05"; allowedHeadings = new String[] {"Vendor Alert Rules"}; break;
            default:
                return;
        }
        try {
            for (String heading : allowedHeadings) {
                if (driver.findElements(By.xpath("//main//*[self::h1 or self::h2 or self::h3]"
                        + "[normalize-space()='" + heading + "']")).size() > 0) {
                    captureEvidence(caseId, heading, "Failure-state heading captured; see assertion failure for details");
                    return;
                }
            }
            ExtentReporterUtility.warning(caseId + " | Failure occurred before a safe heading was visible");
        } catch (RuntimeException unavailableBrowser) {
            ExtentReporterUtility.warning(caseId + " | Failure evidence unavailable after browser error");
        }
    }

    /** Attach only a known, public heading. Never capture page body, rows, keys, or request URLs. */
    private void captureEvidence(String caseId, String expectedHeading, String assertionNote) {
        ExtentReporterUtility.info(caseId + " | " + assertionNote);
        ExtentTest report = ExtentReporterUtility.getTest();
        if (report == null) return;

        try {
            WebElement heading = driver.findElements(By.cssSelector("main h1, main h2, main h3")).stream()
                    .filter(element -> expectedHeading.equals(element.getText().trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected evidence heading not visible"));
            Path directory = Paths.get(System.getProperty("user.dir"), "logs", "evidence",
                    "api-manager-optimization");
            Files.createDirectories(directory);
            Path screenshot = directory.resolve(caseId + "_" + UUID.randomUUID() + ".png");
            Files.write(screenshot, heading.getScreenshotAs(OutputType.BYTES));
            report.addScreenCaptureFromPath(screenshot.toAbsolutePath().toString(),
                    caseId + " — " + expectedHeading);
        } catch (IOException | RuntimeException captureFailure) {
            report.warning(caseId + " heading evidence unavailable; assertion result remains unchanged");
        }
    }
}
