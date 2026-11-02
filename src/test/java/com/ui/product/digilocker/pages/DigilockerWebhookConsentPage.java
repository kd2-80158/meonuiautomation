package com.ui.product.digilocker.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page object for the DigiLocker consent screens opened by UAT /digi_url. */
public final class DigilockerWebhookConsentPage {
    private static final By AADHAAR_FIRST = By.id("aadhaar_1");
    private static final By AADHAAR_SECOND = By.id("aadhaar_2");
    private static final By AADHAAR_THIRD = By.id("aadhaar_3");
    private static final By NEXT = By.xpath("//button[normalize-space(.)='Next']");
    private static final By OTP = By.id("a_otp_1");
    private static final By OTP_SUBMIT = By.id("otp_button");
    private static final By PIN_FIRST = By.id("v_pin_1");
    private static final By PIN_SUBMIT = By.id("verify_pin_button");
    private static final By CONSENT_DOCUMENTS = By.cssSelector("input[type='checkbox'][name='consent']");
    private static final By ALLOW = By.cssSelector("input[value='Allow']");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DigilockerWebhookConsentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    public void enterAadhaarAndContinue(String aadhaar) {
        if (aadhaar == null || !aadhaar.matches("[0-9]{12}"))
            throw new IllegalArgumentException("A 12-digit Aadhaar value is required; it will not be logged");
        wait.until(ExpectedConditions.visibilityOfElementLocated(AADHAAR_FIRST)).sendKeys(aadhaar.substring(0, 4));
        driver.findElement(AADHAAR_SECOND).sendKeys(aadhaar.substring(4, 8));
        driver.findElement(AADHAAR_THIRD).sendKeys(aadhaar.substring(8, 12));
        wait.until(ExpectedConditions.elementToBeClickable(NEXT)).click();
        wait.until(d -> !d.findElements(OTP).isEmpty() || !d.findElements(PIN_FIRST).isEmpty());
    }

    /** Waits for a real OTP or a user-entered OTP; never guesses or bypasses it. */
    public void waitForOtpAndContinue() {
        new WebDriverWait(driver, Duration.ofMinutes(2)).until(d -> {
            if (!d.findElements(PIN_FIRST).isEmpty()) return true;
            List<WebElement> fields = d.findElements(OTP);
            return !fields.isEmpty() && String.valueOf(fields.get(0).getAttribute("value")).matches("[0-9]{6}");
        });
        if (!driver.findElements(PIN_FIRST).isEmpty()) return;
        wait.until(ExpectedConditions.elementToBeClickable(OTP_SUBMIT)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(PIN_FIRST));
    }

    public void enterPinAndContinue(String pin) {
        if (pin == null || !pin.matches("[0-9]{6}"))
            throw new IllegalArgumentException("A six-digit DigiLocker PIN is required; it will not be logged");
        wait.until(ExpectedConditions.visibilityOfElementLocated(PIN_FIRST));
        for (int i = 0; i < 6; i++) driver.findElement(By.id("v_pin_" + (i + 1))).sendKeys(pin.substring(i, i + 1));
        wait.until(ExpectedConditions.elementToBeClickable(PIN_SUBMIT)).click();
        new WebDriverWait(driver, Duration.ofSeconds(90)).until(d ->
                !d.findElements(CONSENT_DOCUMENTS).isEmpty() || returnedToRequester(d.getCurrentUrl()));
    }

    public int selectAllAvailableDocumentsAndAllow() {
        // DigiLocker may redirect immediately when consent was already granted for this account.
        if (returnedToRequester(driver.getCurrentUrl())) return 0;
        wait.until(ExpectedConditions.presenceOfElementLocated(CONSENT_DOCUMENTS));
        List<WebElement> documents = driver.findElements(CONSENT_DOCUMENTS);
        int selected = 0;
        for (WebElement document : documents) {
            if (!document.isDisplayed() || !document.isEnabled()) continue;
            if (!document.isSelected()) document.click();
            selected++;
        }
        if (selected == 0) throw new IllegalStateException("No consent documents were available");
        wait.until(ExpectedConditions.elementToBeClickable(ALLOW)).click();
        new WebDriverWait(driver, Duration.ofSeconds(90))
                .until(d -> !String.valueOf(d.getCurrentUrl()).contains("accounts.digitallocker.gov.in"));
        return selected;
    }

    private boolean returnedToRequester(String url) {
        return url != null && (url.startsWith("https://digilocker-uat.meon.co.in/")
                || url.startsWith("https://digilocker.meon.co.in/"))
                && !url.contains("/redirect/digilocker/");
    }
}
