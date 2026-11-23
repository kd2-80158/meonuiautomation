package com.ui.product.rekyc.pages.mandot;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.ChromeDevToolsInspector;

/**
 * Read-only Page Object for the Mandot Modification journey.  It intentionally
 * never clicks save, edit, upload, submit, or any account-changing control.
 */
public final class MandotModificationLivePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By CLIENT_CODE = By.id("clientid");
    private static final By SEND_OTP = By.id("sendotp");
    private static final By OTP = By.id("otpsign");
    private static final By VERIFY = By.id("client_signup");
    // Live Mandot uses the heading id; the UAT card id is not present here.
    private static final By ACCOUNT_CARD = By.cssSelector("#account_details_heading");
    private static final By ACCOUNT_TOGGLE = By.cssSelector("button.account-details-toggle");
    private static final By NOTIFICATIONS = By.cssSelector("#dropdownMenuButton");
    private static final By SIDE_NAV_TOGGLE = By.cssSelector("#iconNavbarSidenav");

    public MandotModificationLivePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public void login(String clientCode, String otp) {
        input(CLIENT_CODE, clientCode);
        click(SEND_OTP);
        input(OTP, otp);
        click(VERIFY);
        wait.until(d -> d.getCurrentUrl().contains("/v1/user/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_CARD));
    }

    public void openStep(String page) {
        String url = driver.getCurrentUrl();
        int marker = url.indexOf("/v1/user/");
        if (marker < 0) {
            throw new AssertionError("Mandot workflow user page is not open: " + url);
        }
        String target = url.substring(0, marker) + "/v1/user/" + page + "/page";
        driver.get(target);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_CARD));
    }

    public WebElement accountDetails() { return visible(ACCOUNT_CARD, By.xpath("//h2[normalize-space()='Account Details']")); }
    public WebElement accountToggle() { return visible(ACCOUNT_TOGGLE, By.xpath("//button[contains(@aria-label, 'account details') or contains(., 'Account Details')]")); }
    public WebElement notifications() {
        List<WebElement> visibleNotifications = driver.findElements(NOTIFICATIONS).stream()
                .filter(WebElement::isDisplayed).collect(java.util.stream.Collectors.toList());
        if (!visibleNotifications.isEmpty()) return visibleNotifications.get(0);
        // With DevTools docked the top navigation becomes compact. Open the
        // responsive menu before inspecting the same notification control.
        WebElement menu = visible(By.cssSelector(".top-nav-menu-toggle"),
                By.cssSelector("[aria-label*='accessibility and account menu']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        return visible(NOTIFICATIONS, By.xpath("//*[@aria-label='View notifications']"));
    }
    public WebElement sideNavigationToggle() {
        return visible(SIDE_NAV_TOGGLE,
                By.cssSelector("[aria-label='Toggle sidebar'], .top-nav-menu-toggle"));
    }
    public WebElement annualIncome() { return visible(By.id("addannual_income"), By.xpath("//*[contains(normalize-space(), 'Annual Income as on date')]/ancestor::div[1]")); }
    public WebElement ckycFetch() { return visible(By.xpath("//*[normalize-space()='CKYC Fetch']"), By.xpath("//*[contains(normalize-space(), 'Fetch CKYC')]/ancestor::div[1]")); }
    public WebElement brokerageDetails() { return visible(By.id("segment_div"), By.xpath("//*[normalize-space()='Brokerage Details']")); }
    public WebElement documentUpload() { return visible(By.id("upload_document"), By.xpath("//h2[normalize-space()='Upload Document']")); }
    public WebElement bankDetails() { return visible(By.id("bank-details"), By.xpath("//h2[normalize-space()='Bank Details']")); }
    public WebElement bankAddAccount() { return visible(By.xpath("//button[contains(normalize-space(), 'Add Account')]"), By.xpath("//*[contains(normalize-space(), 'Addition of Bank Account')]/ancestor::div[1]")); }
    public WebElement nomineeDetails() { return visible(By.id("nominee_div"), By.xpath("//h2[normalize-space()='Nominee Details']")); }
    public WebElement nomineeSelection() { return visible(By.xpath("//*[contains(normalize-space(), 'Do you wish to make changes in Nomination')]/ancestor::div[1]"), By.xpath("//*[normalize-space()='NO CHANGE']")); }

    /** Adds a temporary inspection overlay, mirroring a DevTools element highlight. */
    public void highlight(WebElement element, String label) {
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('[data-mandot-inspection]').forEach(e=>e.remove());"
                + "const r=arguments[0].getBoundingClientRect(), tag=document.createElement('div');"
                + "tag.dataset.mandotInspection='true';tag.style.cssText='position:fixed;z-index:2147483647;pointer-events:none;"
                + "left:'+r.left+'px;top:'+r.top+'px;width:'+r.width+'px;height:'+r.height+'px;"
                + "border:3px solid #ff7a00;box-sizing:border-box;border-radius:5px;box-shadow:0 0 0 4px rgba(255,122,0,.20)';"
                + "document.body.appendChild(tag);const tip=document.createElement('div');tip.dataset.mandotInspection='true';"
                + "tip.textContent=arguments[1];tip.style.cssText='position:fixed;z-index:2147483647;pointer-events:none;"
                + "left:'+Math.max(8,r.left)+'px;top:'+Math.max(8,r.top-34)+'px;background:#3d126f;color:#fff;"
                + "padding:7px 10px;border-radius:6px;font:700 13px Arial;box-shadow:0 3px 12px rgba(0,0,0,.24)';"
                + "document.body.appendChild(tip);", element, label);
    }

    public void clearHighlight() {
        ((JavascriptExecutor) driver).executeScript("document.querySelectorAll('[data-mandot-inspection]').forEach(e=>e.remove());");
    }

    /** Selects the precise node in Chrome DevTools when DevTools is open. */
    public void inspectInChromeDevTools(WebElement element) {
        if (!ChromeDevToolsInspector.selectInElementsPanel(driver, element)) {
            ((JavascriptExecutor) driver).executeScript(
                    "try { if (typeof inspect === 'function') inspect(arguments[0]); } catch (ignored) {}", element);
        }
    }

    public String domSnippet(WebElement element) {
        String html = element.getAttribute("outerHTML").replaceAll("\\s+", " ").trim();
        return html.length() > 900 ? html.substring(0, 900) + "…" : html;
    }

    private WebElement visible(By primary, By fallback) {
        return wait.until(d -> firstVisible(d.findElements(primary), d.findElements(fallback)));
    }

    private WebElement firstVisible(List<WebElement> primary, List<WebElement> fallback) {
        return primary.stream().filter(WebElement::isDisplayed).findFirst()
                .or(() -> fallback.stream().filter(WebElement::isDisplayed).findFirst()).orElse(null);
    }

    private void input(By locator, String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.clear();
        input.sendKeys(text);
    }

    private void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        element.click();
    }
}
