package com.ui.cloud.kyc.mandot;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

/** Common controls and safe manual hand-offs for the Mandot KYC workflow. */
public class MandotKycJourneyPage extends BrowserUtility {

    private static final By MOBILE = By.id("mobile_number");
    private static final By DEPENDENCY = By.id("select_dependancy");
    private static final By TERMS = By.id("exampleCheck1");
    private static final String JWT_PATTERN = "(?<![A-Za-z0-9_-])eyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}(?![A-Za-z0-9_-])";
    private final Logger logger = LoggerUtility.getLogger(getClass());

    public MandotKycJourneyPage(WebDriver driver) {
        super(driver);
    }

    public MandotKycJourneyPage startApplication(MandotKycTestData data) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(MOBILE));
        mobile.clear();
        mobile.sendKeys(data.mobileNumber());
        new Select(wait.until(ExpectedConditions.elementToBeClickable(DEPENDENCY))).selectByVisibleText(data.dependency());
        WebElement terms = wait.until(ExpectedConditions.presenceOfElementLocated(TERMS));
        if (!terms.isSelected()) {
            // The UAT landing page visually overlays this native checkbox on some viewports.
            // A DOM click preserves the normal change event without relying on pixel position.
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", terms);
            wait.until(webDriver -> terms.isSelected());
        }
        return this;
    }

    /** Waits for the current module to transition after an operator-owned step. */
    public MandotKycJourneyPage waitForManualAdvance(MandotKycWorkflowStep checkpoint, long timeoutSeconds) {
        assertCheckpointIsFunctionalAndSessionSafe(checkpoint);
        PageState startingState = capturePageState();
        logger.info("Manual checkpoint [{}]: {}. Waiting up to {} seconds.",
                checkpoint.ordinal() + 1, checkpoint.displayName(), timeoutSeconds);
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds)).until(driver ->
                !capturePageState().equals(startingState));
        assertCheckpointIsFunctionalAndSessionSafe(checkpoint);
        return this;
    }

    /**
     * Regression coverage for the session-token VAPT finding. Values are never
     * read or logged: the JavaScript returns only counts and boolean outcomes.
     */
    private void assertCheckpointIsFunctionalAndSessionSafe(MandotKycWorkflowStep checkpoint) {
        JavascriptExecutor javascript = (JavascriptExecutor) getDriver();
        Boolean pageHasVisibleText = (Boolean) javascript.executeScript(
                "return Boolean(document.body && document.body.innerText.trim());");
        Long controls = ((Number) javascript.executeScript(
                "return document.querySelectorAll('input,select,textarea,button,a[href]').length;")).longValue();
        Boolean tokenInDomOrReadableCookie = (Boolean) javascript.executeScript(
                "const jwt = /" + JWT_PATTERN + "/; return jwt.test(document.documentElement.outerHTML) || jwt.test(document.cookie);");
        Long populatedTokenInputs = ((Number) javascript.executeScript(
                "return Array.from(document.querySelectorAll('input[type=hidden]')).filter(input => "
                        + "/(access_token|secret_token)/i.test(input.id + ' ' + input.name) && input.value.trim()).length;")).longValue();
        Long tokenBearingResourceUrls = ((Number) javascript.executeScript(
                "const jwt = /" + JWT_PATTERN + "/; return performance.getEntriesByType('resource')"
                        + ".filter(entry => jwt.test(entry.name)).length;")).longValue();
        Long insecureResourceUrls = ((Number) javascript.executeScript(
                "return performance.getEntriesByType('resource').filter(entry => entry.name.startsWith('http://')"
                        + " && !entry.name.startsWith('http://localhost')).length;")).longValue();
        Long failedSameOriginApiCalls = ((Number) javascript.executeScript(
                "return performance.getEntriesByType('resource').filter(entry => ['fetch','xmlhttprequest'].includes(entry.initiatorType))"
                        + ".filter(entry => new URL(entry.name).origin === location.origin)"
                        + ".filter(entry => Number(entry.responseStatus) >= 400).length;")).longValue();

        if (!Boolean.TRUE.equals(pageHasVisibleText)) {
            throw new AssertionError("KYC checkpoint " + checkpoint.displayName() + " rendered no visible content.");
        }
        if (checkpoint.isApplicantFacing() && controls == 0) {
            throw new AssertionError("KYC checkpoint " + checkpoint.displayName() + " rendered no interactive controls.");
        }
        if (Boolean.TRUE.equals(tokenInDomOrReadableCookie)) {
            throw new AssertionError("VAPT regression: JWT-like value exposed in KYC DOM or script-readable cookie at "
                    + checkpoint.displayName() + ".");
        }
        if (populatedTokenInputs > 0) {
            throw new AssertionError("VAPT regression: populated hidden token input exposed at "
                    + checkpoint.displayName() + ".");
        }
        if (tokenBearingResourceUrls > 0) {
            throw new AssertionError("VAPT regression: JWT-like value exposed in a KYC resource URL at "
                    + checkpoint.displayName() + ".");
        }
        if (insecureResourceUrls > 0) {
            throw new AssertionError("KYC checkpoint " + checkpoint.displayName() + " loaded insecure HTTP resources.");
        }
        if (failedSameOriginApiCalls > 0) {
            throw new AssertionError("KYC checkpoint " + checkpoint.displayName()
                    + " has failed same-origin API responses; inspect the browser network panel.");
        }
    }

    private PageState capturePageState() {
        String url = getDriver().getCurrentUrl();
        JavascriptExecutor javascript = (JavascriptExecutor) getDriver();
        String visibleText = javascript.executeScript("return document.body ? document.body.innerText : ''; ").toString();
        String structure = javascript.executeScript("return Array.from(document.querySelectorAll('h1,h2,h3,h4,h5,h6,form,button')).map(element => element.tagName + ':' + (element.innerText || element.getAttribute('name') || '')).join('|');").toString();
        return new PageState(url, visibleText, structure);
    }

    private static final class PageState {
        private final String url;
        private final String visibleText;
        private final String structure;

        private PageState(String url, String visibleText, String structure) {
            this.url = url;
            this.visibleText = visibleText;
            this.structure = structure;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PageState)) {
                return false;
            }
            PageState that = (PageState) other;
            return url.equals(that.url) && visibleText.equals(that.visibleText) && structure.equals(that.structure);
        }

        @Override
        public int hashCode() {
            int result = url.hashCode();
            result = 31 * result + visibleText.hashCode();
            return 31 * result + structure.hashCode();
        }
    }
}
