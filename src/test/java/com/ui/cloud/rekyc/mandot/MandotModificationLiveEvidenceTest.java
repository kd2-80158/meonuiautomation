package com.ui.cloud.rekyc.mandot;

import java.time.Duration;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.mandot.MandotModificationLivePage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.JourneyEvidence;
import com.ui.utilities.ChromeDevToolsInspector;

/**
 * Evidence-only regression of the live Mandot Modification journey. Every test
 * has a single UAT screenshot counterpart. The test is read-only: it opens
 * existing pages and temporarily highlights exactly one DOM finding.
 */
@Listeners({ com.ui.listeners.TestListener.class })
public final class MandotModificationLiveEvidenceTest extends BaseTest {
    private static final String SUPERADMIN = "https://rekyc.meon.co.in/v1/company/superadmin";
    private static final String TENANT_HOST = "https://rekyc.mandotsecurities.co.in";
    private static final Duration WAIT = Duration.ofSeconds(45);
    private static final String CLIENT_CODE = System.getProperty("mandot.clientCode", "MEON1");
    private static final String STATIC_OTP = System.getProperty("mandot.staticOtp", "123456");

    private WebDriverWait wait;
    private MandotModificationLivePage journey;
    private JourneyEvidence evidence;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setupBrowser() {
        if (System.getProperty("browser") == null || System.getProperty("browser").isBlank()) System.setProperty("browser", "chrome");
        super.setupBrowser();
    }

    @BeforeClass(alwaysRun = true)
    public void establishMandotModificationSession() {
        // TestNG does not guarantee sibling @BeforeClass declaration order across
        // inherited classes. Make the live-session initializer safe in local and
        // cloud executions instead of relying on that order.
        if (driver == null) setupBrowser();
        wait = new WebDriverWait(driver, WAIT);
        loginToMandotWorkflow();
        journey = new MandotModificationLivePage(driver, WAIT);
        journey.login(CLIENT_CODE, STATIC_OTP);
    }

    @BeforeMethod(alwaysRun = true)
    public void startEvidence(java.lang.reflect.Method method) {
        evidence = new JourneyEvidence(driver, method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void finishEvidence() {
        if (journey != null) journey.clearHighlight();
        if (evidence != null) evidence.finish();
    }

    @Test(description = "MANDOT-EXPLORE - Traverse every target page with Chrome Elements panel open", priority = 0)
    public void exploreAllTargetPagesWithDevToolsOpen() {
        for (String page : List.of("Others", "Personal", "Segment", "Document", "Bank", "Nominee")) {
            journey.openStep(page);
            WebElement accountDetails = journey.accountDetails();
            Assert.assertTrue(accountDetails.isDisplayed(), "Account Details is missing on " + page);
            journey.highlight(accountDetails, page + " / Account Details");
            journey.inspectInChromeDevTools(accountDetails);
            evidence.capture("MANDOT explore " + page + " [DevTools-open responsive layout]");
            if (Boolean.parseBoolean(System.getProperty("meon.open.devtools", "false"))) {
                evidence.captureChromeDevTools("MANDOT explore " + page
                        + " [Chrome Elements selected: Account Details]");
            }
            journey.clearHighlight();
        }
    }

    @Test(description = "MANDOT-001 - Others: Account Details toggle element evidence", priority = 1)
    public void evidenceOthersAccountDetailsToggle() { capture("Others", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-002 - Others: Annual Income element evidence", priority = 2)
    public void evidenceOthersAnnualIncome() { capture("Others", "Annual Income as on date", () -> journey.annualIncome()); }
    @Test(description = "MANDOT-003 - Others: notification trigger element evidence", priority = 3)
    public void evidenceOthersNotifications() { capture("Others", "Notification trigger", () -> journey.notifications()); }
    @Test(description = "MANDOT-004 - Personal: Account Details toggle element evidence", priority = 4)
    public void evidencePersonalAccountDetailsToggle() { capture("Personal", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-005 - Personal: CKYC Fetch element evidence", priority = 5)
    public void evidencePersonalCkycFetch() { capture("Personal", "CKYC Fetch", () -> journey.ckycFetch()); }
    @Test(description = "MANDOT-006 - Segment: Account Details toggle element evidence", priority = 6)
    public void evidenceSegmentAccountDetailsToggle() { capture("Segment", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-007 - Segment: Brokerage Details element evidence", priority = 7)
    public void evidenceSegmentBrokerageDetails() { capture("Segment", "Brokerage Details", () -> journey.brokerageDetails()); }
    @Test(description = "MANDOT-008 - Segment: notification trigger element evidence", priority = 8)
    public void evidenceSegmentNotifications() { capture("Segment", "Notification trigger", () -> journey.notifications()); }
    @Test(description = "MANDOT-009 - Document: Account Details toggle element evidence", priority = 9)
    public void evidenceDocumentAccountDetailsToggle() { capture("Document", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-010 - Document: Upload Document element evidence", priority = 10)
    public void evidenceDocumentUploadControls() { capture("Document", "Upload Document", () -> journey.documentUpload()); }
    @Test(description = "MANDOT-011 - Bank: Account Details toggle element evidence", priority = 11)
    public void evidenceBankAccountDetailsToggle() { capture("Bank", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-012 - Bank: Add Account element evidence", priority = 12)
    public void evidenceBankAddAccount() { capture("Bank", "Add Account", () -> journey.bankAddAccount()); }
    @Test(description = "MANDOT-013 - Nominee: sidebar trigger element evidence", priority = 13)
    public void evidenceNomineeSideNavigationToggle() { capture("Nominee", "Toggle sidebar", () -> journey.sideNavigationToggle()); }
    @Test(description = "MANDOT-014 - Nominee: Account Details toggle element evidence", priority = 14)
    public void evidenceNomineeAccountDetailsToggle() { capture("Nominee", "Account Details toggle", () -> journey.accountToggle()); }
    @Test(description = "MANDOT-015 - Nominee: nomination option element evidence", priority = 15)
    public void evidenceNomineeSelection() { capture("Nominee", "Nomination choice", () -> journey.nomineeSelection()); }

    @Test(description = "MANDOT-016 - Read live Text size colors and verify them in WebAIM", priority = 16)
    public void evidenceLiveTextSizeColorContrastInWebAim() {
        driver.get(TENANT_HOST + "/v1/company/mandot/modification/login");
        WebElement label = wait.until(d -> d.findElements(By.cssSelector(".accessibility-toolbar__label")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().trim().equalsIgnoreCase("Text size"))
                .findFirst().orElse(null));
        @SuppressWarnings("unchecked")
        Map<String, Object> colors = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(
                "const hex=c=>{const n=c.match(/\\d+(?:\\.\\d+)?/g)||[];return '#'+n.slice(0,3).map(v=>Math.round(+v).toString(16).padStart(2,'0')).join('');};"
                + "let bg=arguments[0], style=getComputedStyle(bg);while(bg&&(!style.backgroundColor||style.backgroundColor==='transparent'||style.backgroundColor.endsWith(', 0)'))){bg=bg.parentElement;style=bg?getComputedStyle(bg):style;}"
                + "return {foreground:hex(getComputedStyle(arguments[0]).color),background:hex(style.backgroundColor)};", label);
        String foreground = String.valueOf(colors.get("foreground"));
        String background = String.valueOf(colors.get("background"));
        Assert.assertTrue(foreground.matches("#[0-9a-fA-F]{6}"), "Live foreground hex was not resolved");
        Assert.assertTrue(background.matches("#[0-9a-fA-F]{6}"), "Live background hex was not resolved");
        journey.highlight(label, "Text size / live colors " + foreground + " on " + background);
        journey.inspectInChromeDevTools(label);
        evidence.capture("Mandot login - Text size label [live computed colors]");
        if (Boolean.parseBoolean(System.getProperty("meon.open.devtools", "false"))) {
            evidence.captureChromeDevTools("Mandot login Text size [Chrome Elements selected]");
        }
        driver.get("https://webaim.org/resources/contrastchecker/?fcolor=" + foreground.substring(1)
                + "&bcolor=" + background.substring(1));
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contrastForm")));
        String result = driver.findElement(By.id("resultsContainer")).getText();
        System.out.println("WebAIM live contrast " + foreground + " on " + background + ": " + result.replaceAll("\\s+", " "));
        Assert.assertTrue(result.contains("Contrast Ratio"), "WebAIM did not render a contrast ratio");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", form);
        ChromeDevToolsInspector.selectInElementsPanel(driver, form);
        evidence.capture("WebAIM - live " + foreground + " foreground / " + background + " background");
        if (Boolean.parseBoolean(System.getProperty("meon.open.devtools", "false"))) {
            evidence.captureChromeDevTools("WebAIM contrast result [Chrome DevTools Elements inspection]");
        }
    }

    private void capture(String page, String finding, Supplier<WebElement> findingSupplier) {
        journey.openStep(page);
        Assert.assertTrue(journey.accountDetails().isDisplayed(), "Account Details is missing on " + page);
        WebElement element = findingSupplier.get();
        Assert.assertTrue(element.isDisplayed(), finding + " is not visible on " + page);
        journey.highlight(element, finding);
        journey.inspectInChromeDevTools(element);
        evidence.capture("MANDOT " + page + " - " + finding + " [inspection highlight]");
        if (Boolean.parseBoolean(System.getProperty("meon.open.devtools", "false"))) {
            evidence.captureChromeDevTools("MANDOT " + page + " - " + finding
                    + " [Chrome DevTools Elements inspection]");
        }
        System.out.println("MANDOT inspection evidence: " + finding + System.lineSeparator() + journey.domSnippet(element));
    }

    private void loginToMandotWorkflow() {
        driver.get(SUPERADMIN);
        if (!driver.getCurrentUrl().contains("/v1/company/companies")) {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.google-btn, #googleSignInButton"))).click();
            LoginPage_REKYCSuperadmin google = new LoginPage_REKYCSuperadmin(driver);
            google.waitForEmailPage().writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
                    .clickOnButton("nextbtn").waitForPasswordPage()
                    .writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword()).clickOnButton("nextbtn");
            wait.until(ExpectedConditions.urlContains("/v1/company/companies"));
        }
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.id("search__company")));
        search.clear();
        search.sendKeys("mandot", Keys.ENTER);
        WebElement mandot = findMandotRow(Duration.ofSeconds(5));
        if (mandot == null) {
            search = wait.until(ExpectedConditions.elementToBeClickable(By.id("search__company")));
            search.clear();
            search.sendKeys("mandot", Keys.ENTER);
            mandot = findMandotRow(Duration.ofSeconds(12));
        }
        if (mandot == null) {
            // The company directory's responsive layout sometimes retains the
            // unfiltered first page while DevTools is docked. The search has
            // still been performed through the UI; continue with the same
            // authenticated Mandot admin route so page exploration can proceed.
            driver.get(TENANT_HOST + "/v1/company/mandot/dashboard");
            driver.switchTo().defaultContent();
            wait.until(d -> d.getCurrentUrl().startsWith(TENANT_HOST));
            openWorkflowStudio();
            return;
        }
        Set<String> windows = new HashSet<>(driver.getWindowHandles());
        WebElement visit = mandot.findElements(By.cssSelector("button.visit-admin, button")).stream()
                .filter(button -> normalise(button.getText()).equals("visitadmin"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Mandot row has no Visit Admin button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", visit);
        wait.until(d -> d.getWindowHandles().size() > windows.size() || d.getCurrentUrl().contains("/v1/company/mandot"));
        for (String handle : driver.getWindowHandles()) {
            if (!windows.contains(handle)) { driver.switchTo().window(handle); break; }
        }
        openWorkflowStudio();
    }

    private WebElement findMandotRow(Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(d -> d
                    .findElements(By.cssSelector("tr")).stream()
                    .filter(row -> {
                        try { return row.isDisplayed() && normalise(row.getText()).contains("mandot"); }
                        catch (StaleElementReferenceException ignored) { return false; }
                    }).findFirst().orElse(null));
        } catch (TimeoutException notFound) {
            return null;
        }
    }

    private void openWorkflowStudio() {
        // Docked DevTools intentionally narrows the page and can hide this
        // desktop sidebar link. Prefer the visible UI path, but retain the
        // identical authenticated route as a responsive-layout fallback.
        List<WebElement> workflowLinks = driver.findElements(By.cssSelector("a[href='/v1/company/workflow_v2']"));
        WebElement workflowLink = workflowLinks.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
        if (workflowLink != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", workflowLink);
        } else {
            try {
                URI current = URI.create(driver.getCurrentUrl());
                driver.get(current.getScheme() + "://" + current.getAuthority() + "/v1/company/workflow_v2");
            } catch (IllegalArgumentException invalidUrl) {
                throw new AssertionError("Cannot resolve authenticated Workflow V2 route", invalidUrl);
            }
        }
        WebElement frame = wait.until(d -> d.findElements(By.tagName("iframe")).stream()
                .filter(f -> { String src = f.getAttribute("src"); return src != null && src.contains("workflow"); }).findFirst().orElse(null));
        driver.switchTo().frame(frame);
        wait.until(d -> d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT).contains("workflow studio"));

        // The studio can restore the last-opened graph. Only use the list
        // control when it is present; on an already-listed state it is absent.
        buttonsWithText("all workflows").stream().findFirst().ifPresent(button -> {
            try { button.click(); }
            catch (RuntimeException ignored) { ((JavascriptExecutor) driver).executeScript("arguments[0].click()", button); }
        });
        wait.until(d -> d.findElements(By.cssSelector("tr")).stream().anyMatch(this::isModificationRow));
        WebElement modification = driver.findElements(By.cssSelector("tr")).stream().filter(this::isModificationRow)
                .findFirst().orElseThrow(() -> new AssertionError("Modification workflow row was not found"));
        WebElement test = modification.findElements(By.cssSelector("button, a")).stream()
                .filter(element -> normalise(element.getText()).contains("test")
                        || normalise(element.getAttribute("aria-label")).contains("test"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Modification workflow has no Test button"));
        Set<String> beforeTest = new HashSet<>(driver.getWindowHandles());
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", test);
        wait.until(d -> d.getWindowHandles().size() > beforeTest.size() || d.getCurrentUrl().contains("mandotsecurities.co.in"));
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().startsWith(TENANT_HOST)) break;
        }
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.urlContains("mandotsecurities.co.in"));
    }

    private List<WebElement> buttonsWithText(String text) {
        return driver.findElements(By.cssSelector("button, a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> normalise(element.getText()).equals(normalise(text))).toList();
    }

    private boolean isModificationRow(WebElement row) {
        return row.findElements(By.cssSelector("td")).stream()
                .anyMatch(cell -> normalise(cell.getText()).equals("modification"));
    }

    private static String normalise(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
