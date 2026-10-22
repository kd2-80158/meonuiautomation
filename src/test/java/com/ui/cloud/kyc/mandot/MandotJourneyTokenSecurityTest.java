package com.ui.cloud.kyc.mandot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ui.base.BaseTest;
import com.ui.utilities.SeveritySoftAssert;

/**
 * Opt-in DAST regression for TDL-002. It deliberately records only booleans;
 * a bearer token must never be written to logs, reports, screenshots or test output.
 */
@Listeners({ com.ui.listeners.TestListener.class })
public class MandotJourneyTokenSecurityTest extends BaseTest {

    private static final Pattern JWT_LIKE_VALUE = Pattern.compile(
            "(?<![A-Za-z0-9_-])eyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}(?![A-Za-z0-9_-])");
    private static final int STATIC_PAGE_COUNT = 23;

    private final String baseUrl = System.getenv().getOrDefault("MANDOT_SECURITY_BASE_URL",
            "https://kycuat.meon.co.in/mandotsecurities/individual_rm_anmol").trim();
    private final String journeyCookieName = System.getenv().getOrDefault("MANDOT_JOURNEY_COOKIE_NAME", "journey_token");

    @BeforeMethod(alwaysRun = true)
    public void setUpSecurityBrowser() {
        super.setupBrowser();
    }

    @Test(description = "sec_tdl_002 - Unauthenticated KYC markup and DOM must not disclose a live JWT", groups = {
            "kyc", "mandot", "security" })
    public void verifyUnauthenticatedJourneyTokenIsHttpOnlyCookieOnly() throws Exception {
        requireSecurityRunEnabled();
        HttpResponse<String> response = unauthenticatedGet(baseUrl);

        SeveritySoftAssert assertions = softAssert;
        assertions.assertEquals(response.statusCode(), 200, "Unauthenticated KYC landing page must be available");
        assertions.assertFalse(containsJwtLikeValue(response.body()),
                "Unauthenticated server-rendered HTML must not contain a JWT-like bearer value");

        String cookie = response.headers().allValues("set-cookie").stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(journeyCookieName.toLowerCase(Locale.ROOT) + "="))
                .findFirst().orElse("");
        assertions.assertFalse(cookie.isBlank(), "The journey session cookie must be issued without exposing its value in markup");
        assertions.assertTrue(hasAttribute(cookie, "httponly"), "Journey session cookie must be HttpOnly");
        assertions.assertTrue(hasAttribute(cookie, "secure"), "Journey session cookie must be Secure");
        assertions.assertTrue(hasAttribute(cookie, "samesite=strict"), "Journey session cookie must use SameSite=Strict");

        driver.get(baseUrl);
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
        Boolean tokenInDom = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const jwt = /(?<![A-Za-z0-9_-])eyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}(?![A-Za-z0-9_-])/;"
                        + "return jwt.test(document.documentElement.outerHTML) || jwt.test(document.cookie);");
        Long populatedTokenInputs = ((Number) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('input[type=hidden]')).filter(input => "
                        + "/(access_token|secret_token)/i.test(input.id + ' ' + input.name) && input.value.trim()).length;")).longValue();
        assertions.assertFalse(Boolean.TRUE.equals(tokenInDom),
                "Unauthenticated browser DOM and script-readable cookies must not expose a JWT-like value");
        assertions.assertEquals(populatedTokenInputs.longValue(), 0L,
                "Unauthenticated DOM must not contain populated access/secret token inputs");
        assertSafeRenderedResources(assertions, "KYC landing page");
        assertions.assertAll();
    }

    @Test(description = "sec_tdl_002_static - Every static KYC design page renders without a JWT in source or DOM", groups = {
            "kyc", "mandot", "security", "static-design" })
    public void verifyAllStaticJourneyPagesRenderWithoutTokenDisclosure() throws Exception {
        if (!Boolean.parseBoolean(System.getenv().getOrDefault("MANDOT_STATIC_PAGE_REGRESSION_ENABLED", "false"))) {
            throw new SkipException("Set MANDOT_STATIC_PAGE_REGRESSION_ENABLED=true to inspect all 23 static pages.");
        }
        requireNonProductionBaseUrl();

        SeveritySoftAssert assertions = softAssert;
        for (int page = 1; page <= STATIC_PAGE_COUNT; page++) {
            String staticUrl = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "get_static_design=" + page;
            HttpResponse<String> response = unauthenticatedGet(staticUrl);
            assertions.assertEquals(response.statusCode(), 200, "Static page " + page + " must be available");
            assertions.assertFalse(containsJwtLikeValue(response.body()),
                    "Static page " + page + " HTML must not contain a JWT-like bearer value");

            driver.get(staticUrl);
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver ->
                    "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
            String visibleText = (String) ((JavascriptExecutor) driver).executeScript(
                    "return document.body ? document.body.innerText.trim() : ''; ");
            Long interactiveControlCount = (Long) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelectorAll('input, select, button, textarea, a[href]').length;");
            Boolean tokenInDom = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "const jwt = /(?<![A-Za-z0-9_-])eyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}(?![A-Za-z0-9_-])/;"
                            + "return jwt.test(document.documentElement.outerHTML) || jwt.test(document.cookie);");
            Long populatedTokenInputs = ((Number) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('input[type=hidden]')).filter(input => "
                            + "/(access_token|secret_token)/i.test(input.id + ' ' + input.name) && input.value.trim()).length;")).longValue();
            assertions.assertFalse(visibleText.isBlank(), "Static page " + page + " must render visible UI content");
            assertions.assertTrue(interactiveControlCount > 0,
                    "Static page " + page + " must render at least one functional UI control");
            assertions.assertFalse(Boolean.TRUE.equals(tokenInDom),
                    "Static page " + page + " must not expose a JWT-like value in the DOM or script-readable cookies");
            assertions.assertEquals(populatedTokenInputs.longValue(), 0L,
                    "Static page " + page + " must not contain populated access/secret token inputs");
            assertSafeRenderedResources(assertions, "Static page " + page);
        }
        assertions.assertAll();
    }

    private HttpResponse<String> unauthenticatedGet(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private boolean containsJwtLikeValue(String content) {
        return JWT_LIKE_VALUE.matcher(content).find();
    }

    private boolean hasAttribute(String cookie, String attribute) {
        return List.of(cookie.toLowerCase(Locale.ROOT).split(";")).stream()
                .map(String::trim).anyMatch(attribute::equals);
    }

    @SuppressWarnings("unchecked")
    private void assertSafeRenderedResources(SeveritySoftAssert assertions, String pageName) {
        List<String> resourceUrls = (List<String>) ((JavascriptExecutor) driver).executeScript(
                "return performance.getEntriesByType('resource').map(entry => entry.name);");
        assertions.assertFalse(resourceUrls.stream().anyMatch(url -> JWT_LIKE_VALUE.matcher(url).find()),
                pageName + " must not send a JWT-like value in a loaded resource URL");
        assertions.assertFalse(resourceUrls.stream().anyMatch(url ->
                url.startsWith("http:") && !url.startsWith("http://localhost")),
                pageName + " must not load insecure HTTP API or static resources");
    }

    private void requireSecurityRunEnabled() {
        if (!Boolean.parseBoolean(System.getenv().getOrDefault("MANDOT_SECURITY_REGRESSION_ENABLED", "false"))) {
            throw new SkipException("Set MANDOT_SECURITY_REGRESSION_ENABLED=true to run the TDL-002 security regression.");
        }
        requireNonProductionBaseUrl();
    }

    private void requireNonProductionBaseUrl() {
        if (baseUrl.isBlank()) {
            throw new SkipException("Set MANDOT_SECURITY_BASE_URL to an approved non-production KYC environment.");
        }
        String host = URI.create(baseUrl).getHost();
        if (host == null || host.equalsIgnoreCase("live.meon.co.in")) {
            throw new IllegalArgumentException("MANDOT_SECURITY_BASE_URL must be an approved non-production KYC environment.");
        }
    }
}
