package com.ui.cloud.developer.sso;

import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.ui.base.BaseTest;
import com.ui.product.developer.pages.DeveloperPortalPage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JourneyEvidence;
import com.ui.utilities.JSONUtility;

/** Critical, read-only SSO checks from each supported developer product. */
@Listeners(com.ui.listeners.TestListener.class)
public final class DeveloperProductSsoTest extends BaseTest {
    private static final List<String> INCLUDED = Arrays.asList(
            "Penny Drop", "eSign", "OCR", "PAN API", "Aadhaar Fetch",
            "Driving License Fetch", "Educational Documents Fetch", "CKYC Integration",
            "Meon Account Aggregator.", "Reverse Penny Drop", "GST Verification",
            "Vehicle RC Fetch", "PFX Sign", "International API Suite", "Location Fetch",
            "UAN Card Fetch", "Income Certificate Fetch", "Insurance Fetch", "Passport Verification");
    private static final Set<String> EXCLUDED = Set.of("Meon Face Verification", "KYC", "IPO", "Rekyc");
    private DeveloperPortalPage portal;
    private JourneyEvidence evidence;
    private String portalWindow;

    @BeforeClass(alwaysRun = true)
    public void openPortal() {
        portal = new DeveloperPortalPage(driver);
        portal.open();
        portalWindow = driver.getWindowHandle();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.findElements(By.cssSelector("a[href^='/new/products/']")).isEmpty());
        portal.selectProduct("Penny Drop");
        Set<String> before = new HashSet<>(driver.getWindowHandles());
        portal.clickMyCredentials();
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                portal.googleLoginVisible() || d.getWindowHandles().size() > before.size());
        if (portal.googleLoginVisible()) {
            portal.clickGoogleLogin();
            String googleWindow = new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
                for (String handle : d.getWindowHandles()) {
                    if (!before.contains(handle)) return handle;
                }
                return null;
            });
            driver.switchTo().window(googleWindow);
            LoginPage_REKYCSuperadmin google = new LoginPage_REKYCSuperadmin(driver);
            google.waitForEmailPage()
                    .writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
                    .clickOnButton("nextbtn")
                    .waitForPasswordPage()
                    .writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
                    .clickOnButton("nextbtn");
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
                d.switchTo().window(portalWindow);
                return !portal.googleLoginVisible();
            });
        }
        for (String handle : new HashSet<>(driver.getWindowHandles())) {
            if (!handle.equals(portalWindow)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(portalWindow);
        portal.open();
    }

    @Test(description = "TC_SSO_01 - Catalog scope and exclusions through Passport Verification",
            groups = {"critical", "sso", "readonly"})
    public void catalogHasAllScopedProducts() {
        evidence = new JourneyEvidence(driver, "developer_sso_catalog");
        List<String> actual = portal.sidebarProducts();
        evidence.capture("Developer portal product catalog");
        int passport = actual.indexOf("Passport Verification");
        Assert.assertTrue(passport >= 0, "Passport Verification missing from sidebar");
        List<String> scoped = new ArrayList<>(actual.subList(0, passport + 1));
        scoped.removeIf(EXCLUDED::contains);
        Assert.assertEquals(scoped, INCLUDED, "Product scope/order changed; review SSO coverage");
    }

    @DataProvider(name = "ssoProducts")
    public Object[][] ssoProducts() {
        return INCLUDED.stream().map(name -> new Object[]{name}).toArray(Object[][]::new);
    }

    @Test(dataProvider = "ssoProducts", description = "TC_SSO_02 - My Credentials opens authenticated admin",
            groups = {"critical", "sso", "readonly"})
    public void myCredentialsOpensAdminWithoutLogin(String product) {
        portal.open();
        evidence = new JourneyEvidence(driver, "developer_sso_" + product);
        Assert.assertTrue(portal.sidebarProducts().contains(product), "Product missing from sidebar: " + product);
        portal.selectProduct(product);
        evidence.capture(product + " documentation page");
        Assert.assertTrue(portal.credentialsVisible(), "My Credentials missing for " + product);
        Set<String> before = new HashSet<>(driver.getWindowHandles());
        portal.clickMyCredentials();
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            for (String handle : d.getWindowHandles()) {
                if (!before.contains(handle)) {
                    d.switchTo().window(handle);
                    return true;
                }
            }
            return !d.getCurrentUrl().contains("developer.meon.co.in/new/products/");
        });
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            URI uri = URI.create(d.getCurrentUrl());
            String path = uri.getPath().toLowerCase(Locale.ROOT);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            return (host.equals("meon.co.in") || host.endsWith(".meon.co.in"))
                    && !host.equals("developer.meon.co.in")
                    && (path.contains("admin") || path.contains("dashboard") || path.contains("credential"));
        });
        URI destination = URI.create(driver.getCurrentUrl());
        String safeDestination = destination.getScheme() + "://" + destination.getHost() + destination.getPath();
        Assert.assertTrue(driver.findElements(By.cssSelector("input[type='password']")).stream()
                .noneMatch(WebElement::isDisplayed), "Login form shown after SSO: " + product);
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                !d.findElement(By.tagName("body")).getText().isBlank());
        evidence.capture(product + " admin landing");
        ExtentReporterUtility.info(product + " SSO destination: " + safeDestination);
    }

    @AfterMethod(alwaysRun = true)
    public void finishEvidence(Method method) {
        try {
            if (evidence != null) {
                try { evidence.capture("Final state - " + method.getName()); }
                catch (RuntimeException error) { ExtentReporterUtility.warning("Final screenshot unavailable: " + error.getMessage()); }
                evidence.finish();
            }
        } finally {
            evidence = null;
            if (driver != null && portalWindow != null) {
                for (String handle : new HashSet<>(driver.getWindowHandles())) {
                    if (!handle.equals(portalWindow)) {
                        driver.switchTo().window(handle);
                        driver.close();
                    }
                }
                driver.switchTo().window(portalWindow);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) { driver.quit(); driver = null; }
    }
}
