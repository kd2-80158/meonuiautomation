package com.ui.cloud.digilocker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ui.pojos.DigilockerApaarConfig;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Live UAT journey. Run only with the explicitly enabled test account. */
@Listeners(com.ui.listeners.TestListener.class)
public class DigiLockerApaarPlaywrightTest {
    private static final Logger log = LoggerUtility.getLogger(DigiLockerApaarPlaywrightTest.class);
    private String phase = "configuration";

    @Test(description = "DigiLocker APAAR ID retrieval through UAT", groups = {"digilocker", "e2e"})
    public void verifyApaarRetrieval() throws Exception {
        DigilockerApaarConfig config = JSONUtility.getDigilockerApaar();
        boolean liveEnabled = Boolean.parseBoolean(System.getProperty(
                "digilocker.apaar.live", String.valueOf(config.isLiveTestEnabled())));
        if (!liveEnabled) {
            throw new SkipException("Set digilockerApaar.liveTestEnabled in config/config.json to run the live APAAR journey.");
        }
        String aadhaar = required(config.getAadhaarNumber(), "aadhaarNumber").replace(" ", "");
        String pin = required(config.getSecurityPin(), "securityPin");
        String readToken = required(config.getOtpReadToken(), "otpReadToken");
        String endpoint = required(config.getOtpEndpoint(), "otpEndpoint");
        String apaar = required(config.getApaarId(), "apaarId");
        String roll = required(config.getRollNumber(), "rollNumber");
        String year = required(config.getPassingYear(), "passingYear");
        String level = required(config.getLevel(), "level");
        String email = required(config.getGoogleEmail(), "googleEmail");
        String password = required(config.getGooglePassword(), "googlePassword");
        String superadminUrl = required(config.getSuperadminUrl(), "superadminUrl");
        String playgroundUrl = required(config.getPlaygroundUrl(), "playgroundUrl");
        String companySearch = required(config.getCompanySearch(), "companySearch");
        String issuerSearch = required(config.getIssuerSearch(), "issuerSearch");
        Assert.assertTrue(aadhaar.matches("[0-9]{12}"), "Aadhaar must contain 12 digits");
        Assert.assertTrue(pin.matches("[0-9]{6}"), "Security PIN must contain 6 digits");
        boolean headless = Boolean.parseBoolean(System.getProperty("isHeadless", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            try {
                BrowserContext context = browser.newContext();
                context.setDefaultTimeout(30_000);
                Page page = context.newPage();
                step("opening superadmin and Google sign-in");
                page.navigate(superadminUrl);
                page.locator("//div[contains(text(),'Continue with Google')]").click();
                page.locator("input[aria-label='Email or phone']").fill(email);
                page.locator("(//button[contains(@class,'VfPpkd-LgbsSe')])[2]").click();
                page.locator("input[name='Passwd']").fill(password);
                page.locator("(//button[contains(@class,'VfPpkd-LgbsSe')])[2]").click();
                page.locator("#search__company").fill(companySearch);
                step("opening company admin");
                Page sso = context.waitForPage(() -> page.locator("button.visit-admin").first().click());
                sso.waitForLoadState();
                sso.locator("//div[text()='DigiLocker Test']").click();
                assertThat(sso).hasURL(playgroundUrl);
                step("selecting APAAR document");
                sso.locator("//span[text()='Aadhaar']").click();
                sso.locator("input[placeholder*='Search issuer']").fill(issuerSearch);
                sso.locator("button").filter(new Locator.FilterOptions().setHasText("APAAR ID")).click();
                sso.locator("input[placeholder*='16270XX']").fill(apaar);
                sso.locator("input[placeholder*='Roll Number']").fill(roll);
                sso.locator("input[placeholder*='2025']").fill(year);
                sso.locator("input[placeholder*='level1']").fill(level);
                sso.locator("(//button[contains(@tabindex,'0')])[13]").click();
                sso.locator("//button[contains(@class,'css-wzi0gt')]").click();
                Locator generated = sso.locator("//h6[contains(text(),'DigiLocker URL generated')]");
                assertThat(generated).isVisible();
                captureHeading(generated, "url-generated");
                step("opening DigiLocker and requesting OTP");
                Page digi = context.waitForPage(() -> sso.locator("//button[contains(@class,'css-d7dzuv')]").click());
                digi.waitForLoadState();
                digi.locator("#aadhaar_1").fill(aadhaar.substring(0, 4));
                digi.locator("#aadhaar_2").fill(aadhaar.substring(4, 8));
                digi.locator("#aadhaar_3").fill(aadhaar.substring(8, 12));
                long requestedAt = System.currentTimeMillis();
                digi.locator("//button[text()='Next']").click();
                assertThat(digi.locator("#a_otp_1")).isVisible();
                step("waiting for fresh relay OTP");
                String otp = awaitOtp(endpoint, readToken, requestedAt);
                step("submitting OTP and PIN");
                digi.locator("#a_otp_1").fill(otp);
                digi.locator("#otp_button").click();
                assertThat(digi.locator("#v_pin_1")).isVisible();
                for (int i = 0; i < 6; i++) digi.locator("#v_pin_" + (i + 1)).fill(pin.substring(i, i + 1));
                digi.locator("#verify_pin_button").click();
                step("granting APAAR document consent");
                Locator document = digi.locator("input[value*='in.gov.abc-ABCID-']");
                assertThat(document).isVisible();
                if (!document.isChecked()) document.check();
                digi.locator("input[value='Allow']").click();
                Locator configured = digi.locator("//h1[text()='DigiLocker Successfully Configured']");
                assertThat(configured).isVisible(new com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions().setTimeout(60_000));
                captureHeading(configured, "digilocker-configured");
                step("checking reports");
                sso.bringToFront();
                sso.locator("(//button[contains(@class,'MuiButton-disableElevation css-907gl7')])[3]").click();
                sso.locator("//div[text()='Reports']").click();
                Locator heading = sso.locator("//h1[text()='Retrieval reports']");
                assertThat(heading).isVisible();
                captureHeading(heading, "retrieval-reports");
                assertThat(sso.locator("(//span[text()='Complete'])[1]")).isVisible();
                ExtentReporterUtility.info("A Complete status is visible; it was not correlated to the current transaction.");
                step("completed");
            } finally { browser.close(); }
        } catch (Exception | AssertionError failure) {
            String detail = safeFailure(failure);
            log.error("DigiLocker APAAR flow failed at phase={}, errorType={}, detail={}", phase, failure.getClass().getSimpleName(), detail);
            ExtentReporterUtility.fail("Failed at phase: " + phase + "; " + failure.getClass().getSimpleName() + ": " + detail);
            // Avoid publishing page contents or credential-bearing URLs in logs.
            throw new AssertionError("DigiLocker APAAR flow failed at " + phase + " (" + failure.getClass().getSimpleName() + "): " + detail);
        }
    }

    private void step(String message) {
        phase = message;
        log.info("[DigiLocker APAAR] {}", message);
        ExtentReporterUtility.info("[DigiLocker APAAR] " + message);
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing digilockerApaar config value: " + name);
        return value.trim();
    }

    private static String safeFailure(Throwable failure) {
        String message = failure.getMessage();
        if (message == null || message.isBlank()) return "No further error detail available";
        String firstLine = message.split("\\R", 2)[0]
                .replaceAll("https?://[^\\s]+", "[URL omitted]")
                .replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+", "[email omitted]")
                .replaceAll("[0-9]{4,}", "[number omitted]")
                .replaceAll("[A-Za-z0-9_-]{24,}", "[value omitted]");
        return firstLine.length() > 300 ? firstLine.substring(0, 300) : firstLine;
    }

    private static String awaitOtp(String endpoint, String token, long after) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        while (System.nanoTime() < deadline) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint + (endpoint.contains("?") ? "&" : "?") + "after=" + after))
                    .header("Authorization", "Bearer " + token).timeout(Duration.ofSeconds(5)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 401 || response.statusCode() == 403)
                throw new IllegalStateException("OTP relay read credential was rejected");
            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (json.has("success") && json.get("success").getAsBoolean() && json.has("otp") && json.has("timestamp")) {
                    String otp = json.get("otp").getAsString();
                    if (otp.matches("[0-9]{6}") && json.get("timestamp").getAsLong() >= after) {
                        ExtentReporterUtility.info("Fresh OTP received from relay (value omitted)");
                        return otp;
                    }
                }
            } else if (response.statusCode() != 404) {
                log.warn("OTP relay returned HTTP {}", response.statusCode());
            }
            Thread.sleep(5000);
        }
        throw new IllegalStateException("No fresh OTP arrived within two minutes");
    }

    private static void captureHeading(Locator heading, String label) {
        try {
            Path directory = Paths.get("logs", "evidence", "digilocker-apaar");
            Files.createDirectories(directory);
            Path image = directory.resolve(label + "-" + UUID.randomUUID() + ".png");
            heading.screenshot(new Locator.ScreenshotOptions().setPath(image));
            ExtentReporterUtility.addImageToReport(label, image.toString());
        } catch (Exception e) {
            ExtentReporterUtility.warning("Checkpoint image unavailable: " + label);
        }
    }
}
