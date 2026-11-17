package com.ui.cloud.facefinder.ipv;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.base.BaseTest;
import com.ui.product.facefinder.pages.DashboardPage_FaceFinderCompany;
import com.ui.product.facefinder.pages.LoginPage_FaceFinderSuperadmin;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.JSONUtility;
import com.ui.utilities.LoggerUtility;

/** Read-only Face Finder IPV coordinate audit using the same Google SSO flow as the UAT UI suite. */
@Listeners({ com.ui.listeners.TestListener.class })
public class IpvCoordinateAuditTest extends BaseTest {

    private static final By GOOGLE_CONTINUE = By.xpath("//button[contains(normalize-space(.),'Continue with Google')]");
    private static final By COMPANY_SEARCH = By.cssSelector("input[placeholder='Search...']");
    private static final By IPV_HEADING = By.xpath("//h1[normalize-space()='IPV Logs']");
    private static final By TABLE_ROWS = By.cssSelector("table tbody tr");
    private static final By NEXT_PAGE = By.xpath("//button[normalize-space()='Next']");
    private static final By ITEMS_PER_PAGE = By.cssSelector(
            "[aria-label*='Items per page'], [title*='Items per page'], [role='combobox'], [aria-haspopup='listbox']");
    private static final Pattern DISPLAY_LAT = Pattern.compile("\\bLat\\s+(-?\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DISPLAY_LNG = Pattern.compile("\\bLong\\s+(-?\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);

    private final String baseUrl = System.getProperty("ipv.baseUrl", "https://face-finder.meon.co.in").replaceAll("/+$", "");
    private final String logsPath = System.getProperty("ipv.logsPath", "/backend/get_ipv_logs");
    private final String status = System.getProperty("ipv.status", "captured");
    private final String rowsPath = System.getProperty("ipv.rowsPath", "data.data");
    private final String totalPath = System.getProperty("ipv.totalPath", "data.total_items");
    private final String pagesPath = System.getProperty("ipv.totalPagesPath", "data.total_pages");
    private final String currentPagePath = System.getProperty("ipv.currentPagePath", "data.current_page");
    private final String targetCompany = System.getProperty("ipv.company", "globe capital market ltd").trim();
    private final String companySearchTerm = System.getProperty("ipv.companySearch", "globe capital").trim();
    private final int searchAttempts = Math.max(1, Integer.getInteger("ipv.searchAttempts", 3));
    private final int requestedPageSize = Math.max(10, Integer.getInteger("ipv.pageSize", 100));
    private final String latitudeFields = System.getProperty("ipv.latitudeFields",
            "latitude,lat,location.latitude,coordinates.latitude,geo.latitude");
    private final String longitudeFields = System.getProperty("ipv.longitudeFields",
            "longitude,lng,long,location.longitude,coordinates.longitude,geo.longitude");
    private final String idFields = System.getProperty("ipv.idFields", "transaction_id,transactionId,transaction.id,id");
    private final int maxPages = Math.max(1, Integer.getInteger("ipv.maxPages", 10000));
    private final int actionDelaySeconds = Math.max(5, Integer.getInteger("ipv.actionDelaySeconds", 5));
    private final boolean allowNullIsland = Boolean.getBoolean("ipv.allowNullIsland");

    private Logger logger;
    private WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void initializeAudit() {
        // CDP response inspection below is Chrome-only; the audit needs these logs
        // to compare the authenticated API payload with the rendered rows.
        System.setProperty("browser", "chrome");
        super.setupBrowser();
        logger = LoggerUtility.getLogger(getClass());
        wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        navigateAndWait(baseUrl + "/superadmin-login");
        authenticateWithGoogleIfNeeded();
        selectTargetCompany();
        enterCompanyAdmin();
        openIpvLogs();
    }

    @Test(description = "Audit each configured company's IPV logs for backend and rendered coordinate issues", groups = {
            "facefinder", "ipv", "api", "ui", "readonly" })
    public void auditAllIpvLogCoordinates() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard/ipv-logs"),
                "Expected IPV Logs after Visit Admin SSO, actual URL: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.findElement(IPV_HEADING).isDisplayed(), "IPV Logs heading is not visible.");

        List<CoordinateFinding> findings = new ArrayList<>();
        Set<Integer> apiPagesSeen = new HashSet<>();
        Set<String> transactionIdsSeen = new HashSet<>();
        int expectedPages = -1;
        long expectedTotal = -1;
        int visiblePage = 1;

        enableNetworkCapture();
        // Refresh first (to capture the first list response), then change page size;
        // refreshing after this selection would reset the UI back to its default 10.
        clearNetworkLog();
        choosePageSize(requestedPageSize);

        while (visiblePage <= maxPages) {
            Map<String, RenderedCoordinates> visibleRows = readVisibleRows(findings);
            List<String> visibleIds = new ArrayList<>(visibleRows.keySet());
            List<JsonObject> apiRows = captureIpvApiResponse(apiPagesSeen);
            if (apiRows.isEmpty() && !visibleIds.isEmpty()) {
                Assert.fail("Could not capture the get_ipv_logs API response for visible page " + visiblePage
                        + ". Check that the IPV table loaded its JSON response in this Chrome session.");
            }
            if (!apiRows.isEmpty()) {
                JsonObject response = latestIpvPayload;
                expectedPages = (int) numberAt(response, pagesPath, expectedPages);
                expectedTotal = (long) numberAt(response, totalPath, expectedTotal);
                if (visiblePage == 1) logger.info("IPV API reports {} records across {} pages for company '{}'",
                        expectedTotal, expectedPages, targetCompany);
                long apiPage = (long) numberAt(response, currentPagePath, visiblePage);
                compareBackendAndRenderedRows(apiRows, visibleRows, (int) apiPage, findings, transactionIdsSeen);
                logger.info("IPV audit page {} scanned ({} visible rows)", apiPage, visibleIds.size());
            }

            if (expectedPages > 0 && apiPagesSeen.size() >= expectedPages) break;
            if (visibleIds.isEmpty()) break;
            if (visiblePage >= maxPages) break;
            if (!goToNextPage(visiblePage)) break;
            visiblePage++;
        }

        boolean complete = expectedPages > 0 && apiPagesSeen.size() >= expectedPages
                && (expectedTotal < 0 || transactionIdsSeen.size() >= expectedTotal);
        Assert.assertTrue(complete, "IPV audit incomplete: captured " + apiPagesSeen.size() + " of "
                + expectedPages + " pages and " + transactionIdsSeen.size() + " of " + expectedTotal + " records.");

        logger.info("IPV coordinate audit complete: company={}, pages={}, records={}, findings={}",
                targetCompany, apiPagesSeen.size(), transactionIdsSeen.size(), findings.size());
        if (!findings.isEmpty()) {
            StringBuilder detail = new StringBuilder("IPV coordinate issues found:");
            for (CoordinateFinding finding : findings) detail.append(System.lineSeparator()).append(" - ").append(finding);
            Assert.fail(detail.toString());
        }
    }

    private JsonObject latestIpvPayload;

    private void authenticateWithGoogleIfNeeded() {
        if (driver.getCurrentUrl().contains("/superadmin/company")) return;
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_CONTINUE));
        continueButton.click();
        pauseAfterAction();

        Set<String> handlesBefore = new HashSet<>(driver.getWindowHandles());
        String googleHandle = new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
            for (String handle : safeWindowHandles(d)) {
                try {
                    d.switchTo().window(handle);
                    if (d.getCurrentUrl().contains("accounts.google.com")) return handle;
                } catch (org.openqa.selenium.WebDriverException ignored) {
                    // OAuth may replace or close a transient window between polling ticks.
                }
            }
            return null;
        });
        driver.switchTo().window(googleHandle);
        LoginPage_REKYCSuperadmin googleLogin = new LoginPage_REKYCSuperadmin(driver);
        String email = JSONUtility.getCredentials().getKycAdmin().getEmail();
        String password = JSONUtility.getCredentials().getKycAdmin().getPassword();

        if (!driver.findElements(By.id("identifierId")).isEmpty()) {
            googleLogin.waitForEmailPage().writeService("email", email);
            pauseAfterAction();
            googleLogin.clickOnButton("nextbtn");
            pauseAfterAction();
        }
        if (!driver.findElements(By.cssSelector("input[type='password']")).isEmpty()) {
            googleLogin.waitForPasswordPage().writeService("password", password);
            pauseAfterAction();
            googleLogin.clickOnButton("nextbtn");
            pauseAfterAction();
        }

        String directoryHandle = new WebDriverWait(driver, Duration.ofSeconds(180)).until(d -> {
            for (String handle : safeWindowHandles(d)) {
                try {
                    d.switchTo().window(handle);
                    if (d.getCurrentUrl().startsWith(baseUrl + "/superadmin/company")
                            || !d.findElements(COMPANY_SEARCH).isEmpty()) return handle;
                } catch (org.openqa.selenium.WebDriverException ignored) {
                    // Ignore a stale OAuth popup and continue checking the surviving app tab.
                }
            }
            return null;
        });
        driver.switchTo().window(directoryHandle);
        Assert.assertNotNull(directoryHandle, "Google sign-in did not return to Face Finder company directory.");
        pauseAfterAction();
        if (handlesBefore.isEmpty()) logger.debug("No pre-existing window handles found before Google sign-in.");
    }

    private Set<String> safeWindowHandles(org.openqa.selenium.WebDriver webDriver) {
        try { return webDriver.getWindowHandles(); }
        catch (org.openqa.selenium.WebDriverException ignored) { return java.util.Collections.emptySet(); }
    }

    private void selectTargetCompany() {
        String normalizedTarget = targetCompany.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        By companyResult = By.xpath("//*[self::a or self::button or self::div or self::span or self::p or self::h3 or self::h4]"
                + "[contains(normalize-space(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')),"
                + xpathLiteral(normalizedTarget) + ")]");
        for (int attempt = 1; attempt <= searchAttempts; attempt++) {
            navigateAndWait(baseUrl + "/superadmin/company");
            WebElement search = wait.until(ExpectedConditions.elementToBeClickable(COMPANY_SEARCH));
            search.clear();
            pauseAfterAction();
            search.sendKeys(companySearchTerm);
            pauseAfterAction();
            WebElement result;
            try {
                result = new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> d.findElements(companyResult).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(e -> e.getText().toLowerCase(Locale.ROOT).contains(normalizedTarget))
                        .min(java.util.Comparator.comparingInt(e -> e.getText().length()))
                        .orElse(null));
            } catch (org.openqa.selenium.TimeoutException e) {
                logger.warn("Company '{}' was not in search results (attempt {}/{})", targetCompany, attempt, searchAttempts);
                continue;
            }
            logger.info("Selecting Face Finder company '{}'", result.getText().trim());
            result.click();
            pauseAfterAction();
            if (driver.getCurrentUrl().contains("/superadmin/company/")
                    && driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT).contains(normalizedTarget)
                    && !driver.findElements(By.xpath("//a[normalize-space()='Visit Admin']")).isEmpty()) return;
            logger.warn("Company detail did not open for '{}' (attempt {}/{})", targetCompany, attempt, searchAttempts);
        }
        Assert.fail("Company '" + targetCompany + "' was not selectable after " + searchAttempts
                + " search attempts. Verify the signed-in superadmin has access to this exact company.");
    }

    private void enterCompanyAdmin() {
        Set<String> before = new HashSet<>(driver.getWindowHandles());
        WebElement visitAdmin = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Visit Admin']")));
        visitAdmin.click();
        pauseAfterAction();
        String adminHandle = new WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
            for (String handle : safeWindowHandles(d)) {
                try {
                    d.switchTo().window(handle);
                    if (d.getCurrentUrl().startsWith(baseUrl + "/dashboard")) return handle;
                } catch (org.openqa.selenium.WebDriverException ignored) {
                    // SSO can close the source tab or leave a transient handle behind.
                }
            }
            return null;
        });
        driver.switchTo().window(adminHandle);
        if (before.size() < driver.getWindowHandles().size()) pauseAfterAction();
    }

    private void openIpvLogs() {
        if (!driver.getCurrentUrl().contains("/dashboard/ipv-logs")) {
            new DashboardPage_FaceFinderCompany(driver).clickOn("ipv");
            pauseAfterAction();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(IPV_HEADING));
        wait.until(d -> !d.findElements(TABLE_ROWS).isEmpty());
        selectStatusFilter();
    }

    private void selectStatusFilter() {
        if ("captured".equalsIgnoreCase(status)) return;
        By statusButton = By.xpath("//button[normalize-space(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))="
                + xpathLiteral(status.toLowerCase(Locale.ROOT)) + "]");
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(statusButton));
        button.click();
        pauseAfterAction();
        wait.until(d -> !d.findElements(TABLE_ROWS).isEmpty());
    }

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        String[] parts = value.split("'", -1);
        StringBuilder literal = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) literal.append(",\"'\",");
            literal.append("'").append(parts[i]).append("'");
        }
        return literal.append(')').toString();
    }

    private void choosePageSize(int pageSize) {
        Object controls = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('select,button,[role=combobox],[aria-haspopup]'))"
                + ".map(e => [e.tagName, e.getAttribute('role'), e.getAttribute('aria-label'), e.getAttribute('title'), (e.innerText||'').trim()].join('|'))"
                + ".filter(s => /page|^(10|25|50|100)$/i.test(s)).join(' || ')");
        logger.info("Pagination controls: {}", controls);
        for (WebElement selectElement : driver.findElements(By.cssSelector("select"))) {
            try {
                Select select = new Select(selectElement);
                if (select.getOptions().stream().anyMatch(option -> option.getText().trim().equals(String.valueOf(pageSize)))) {
                    select.selectByVisibleText(String.valueOf(pageSize));
                    pauseAfterAction();
                    if (waitForPageSize(pageSize)) return;
                }
            } catch (org.openqa.selenium.WebDriverException ignored) {
                // Try the accessible custom dropdown below.
            }
        }
        List<WebElement> dropdowns = driver.findElements(ITEMS_PER_PAGE);
        for (WebElement dropdown : dropdowns) {
            String label = (dropdown.getAttribute("aria-label") + " " + dropdown.getAttribute("title") + " " + dropdown.getText()).trim();
            if (!label.matches("(?is).*\\b(10|25|50|100)\\b.*")) continue;
            try {
                dropdown.click();
            } catch (org.openqa.selenium.WebDriverException ignored) {
                continue;
            }
            pauseAfterAction();
            By option = By.xpath("//*[@role='option' or self::option][normalize-space(.)='" + pageSize + "']");
            List<WebElement> options = driver.findElements(option);
            if (!options.isEmpty()) {
                options.get(0).click();
                pauseAfterAction();
                if (waitForPageSize(pageSize)) return;
            }
        }
        logger.warn("Could not select page size {}; continuing with the UI default.", pageSize);
    }

    private boolean waitForPageSize(int requestedSize) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> d.findElements(TABLE_ROWS).size() >= requestedSize);
            logger.info("IPV table page size set to {} rows", requestedSize);
            return true;
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return false;
        }
    }

    private Map<String, RenderedCoordinates> readVisibleRows(List<CoordinateFinding> findings) {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        Map<String, RenderedCoordinates> visible = new java.util.LinkedHashMap<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.size() < 3) continue;
            String transactionId = cells.get(0).getText().trim();
            String coordinates = cells.get(2).getText().trim();
            Matcher lat = DISPLAY_LAT.matcher(coordinates);
            Matcher lng = DISPLAY_LNG.matcher(coordinates);
            String latitude = lat.find() ? lat.group(1) : null;
            String longitude = lng.find() ? lng.group(1) : null;
            visible.put(transactionId, new RenderedCoordinates(latitude, longitude));
            addCoordinateFindings(findings, transactionId, -1, latitude, longitude, "frontend");
        }
        return visible;
    }

    private List<JsonObject> captureIpvApiResponse(Set<Integer> pagesSeen) {
        if (!(driver instanceof ChromeDriver)) {
            Assert.fail("The IPV audit requires ChromeDriver performance logs to inspect backend responses.");
        }
        List<JsonObject> matching = new ArrayList<>();
        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
            try {
                JsonObject envelope = JsonParser.parseString(entry.getMessage()).getAsJsonObject();
                JsonObject message = envelope.getAsJsonObject("message");
                if (!"Network.responseReceived".equals(message.get("method").getAsString())) continue;
                JsonObject params = message.getAsJsonObject("params");
                JsonObject response = params.getAsJsonObject("response");
                String url = response.get("url").getAsString();
                if (!url.contains(logsPath)) continue;
                String mime = response.has("mimeType") ? response.get("mimeType").getAsString() : "";
                if (!mime.toLowerCase(Locale.ROOT).contains("json") || response.get("status").getAsInt() >= 400) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> bodyResult = ((ChromeDriver) driver).executeCdpCommand("Network.getResponseBody",
                        java.util.Collections.singletonMap("requestId", params.get("requestId").getAsString()));
                String body = String.valueOf(bodyResult.get("body"));
                if (Boolean.TRUE.equals(bodyResult.get("base64Encoded")))
                    body = new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8);
                JsonObject payload = JsonParser.parseString(body).getAsJsonObject();
                JsonArray rows = jsonPath(payload, rowsPath).getAsJsonArray();
                long apiPage = (long) numberAt(payload, currentPagePath, -1);
                if (apiPage < 1 || !pagesSeen.add((int) apiPage)) continue;
                latestIpvPayload = payload;
                matching.addAll(toObjects(rows));
            } catch (RuntimeException ignored) {
                // Ignore unrelated or already-collected DevTools events; never log response bodies or auth headers.
            }
        }
        return matching;
    }

    private List<JsonObject> toObjects(JsonArray rows) {
        List<JsonObject> result = new ArrayList<>();
        for (JsonElement row : rows) if (row.isJsonObject()) result.add(row.getAsJsonObject());
        return result;
    }

    private void compareBackendAndRenderedRows(List<JsonObject> apiRows, Map<String, RenderedCoordinates> visibleRows, int page,
            List<CoordinateFinding> findings, Set<String> transactionIdsSeen) {
        Set<String> visibleIds = visibleRows.keySet();
        for (JsonObject row : apiRows) {
            String id = stringValue(firstValue(row, idFields), "<missing transaction id>");
            transactionIdsSeen.add(id);
            String latitude = stringValue(firstValue(row, latitudeFields), null);
            String longitude = stringValue(firstValue(row, longitudeFields), null);
            addCoordinateFindings(findings, id, page, latitude, longitude, "backend");
            RenderedCoordinates rendered = visibleRows.get(id);
            if (rendered == null) {
                findings.add(new CoordinateFinding(id, page, latitude, longitude, "frontend_row_missing"));
            } else {
                if (latitude != null && rendered.latitude != null && !sameNumber(latitude, rendered.latitude))
                    findings.add(new CoordinateFinding(id, page, latitude, longitude, "frontend_latitude_mismatch"));
                if (longitude != null && rendered.longitude != null && !sameNumber(longitude, rendered.longitude))
                    findings.add(new CoordinateFinding(id, page, latitude, longitude, "frontend_longitude_mismatch"));
            }
        }
        for (String id : visibleIds) {
            if (apiRows.stream().noneMatch(row -> id.equals(stringValue(firstValue(row, idFields), ""))))
                findings.add(new CoordinateFinding(id, page, null, null, "backend_row_missing"));
        }
    }

    private void addCoordinateFindings(List<CoordinateFinding> findings, String id, int page, String latitude,
            String longitude, String layer) {
        String latIssue = coordinateIssue(latitude, -90, 90);
        String lngIssue = coordinateIssue(longitude, -180, 180);
        if (latIssue != null) findings.add(new CoordinateFinding(id, page, latitude, longitude, layer + "_latitude_" + latIssue));
        if (lngIssue != null) findings.add(new CoordinateFinding(id, page, latitude, longitude, layer + "_longitude_" + lngIssue));
    }

    private String coordinateIssue(String raw, double min, double max) {
        if (raw == null || raw.trim().isEmpty()) return "missing";
        try {
            double value = Double.parseDouble(raw.trim());
            if (!Double.isFinite(value)) return "not_finite";
            if (value < min || value > max) return "out_of_range";
            if (!allowNullIsland && value == 0d) return "suspicious_zero";
            return null;
        } catch (NumberFormatException e) { return "not_numeric"; }
    }

    private static boolean sameNumber(String left, String right) {
        try { return Double.compare(Double.parseDouble(left), Double.parseDouble(right)) == 0; }
        catch (NumberFormatException e) { return false; }
    }

    private boolean goToNextPage(int currentPage) {
        for (int attempt = 0; attempt < 3; attempt++) {
            clearNetworkLog();
            String firstIdBefore = firstVisibleTransactionId();
            List<WebElement> buttons = driver.findElements(NEXT_PAGE);
            if (buttons.isEmpty() || !buttons.get(0).isEnabled() || "true".equals(buttons.get(0).getAttribute("disabled"))) return false;
            buttons.get(0).click();
            pauseAfterAction();
            boolean advanced = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(d -> !firstIdBefore.equals(firstVisibleTransactionId()));
            if (advanced) return true;
        }
        Assert.fail("Pagination did not advance after three attempts from visible page " + currentPage);
        return false;
    }

    private String firstVisibleTransactionId() {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        if (rows.isEmpty()) return "";
        List<WebElement> cells = rows.get(0).findElements(By.cssSelector("td"));
        return cells.isEmpty() ? "" : cells.get(0).getText().trim();
    }

    private void enableNetworkCapture() {
        ((ChromeDriver) driver).executeCdpCommand("Network.enable", java.util.Collections.emptyMap());
        clearNetworkLog();
        // Trigger a fresh list request on the current page so its response body is captured.
        driver.navigate().refresh();
        pauseAfterAction();
        wait.until(ExpectedConditions.visibilityOfElementLocated(IPV_HEADING));
        wait.until(d -> !d.findElements(TABLE_ROWS).isEmpty());
    }

    private void clearNetworkLog() {
        driver.manage().logs().get(LogType.PERFORMANCE);
    }

    private void navigateAndWait(String url) {
        driver.get(url);
        pauseAfterAction();
    }

    private void pauseAfterAction() {
        try { Thread.sleep(actionDelaySeconds * 1000L); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException("Interrupted during requested UI settle wait.", e); }
    }

    private JsonElement firstValue(JsonObject row, String candidates) {
        for (String candidate : candidates.split(",")) {
            JsonElement value = jsonPath(row, candidate.trim());
            if (value != null && !value.isJsonNull() && !stringValue(value, "").trim().isEmpty()) return value;
        }
        return null;
    }

    private static JsonElement jsonPath(JsonElement root, String path) {
        JsonElement current = root;
        for (String key : path.split("\\.")) {
            if (current == null || current.isJsonNull() || !current.isJsonObject()) return null;
            current = current.getAsJsonObject().get(key);
        }
        return current;
    }

    private static double numberAt(JsonElement root, String path, double fallback) {
        try { return jsonPath(root, path).getAsDouble(); }
        catch (RuntimeException e) { return fallback; }
    }

    private static String stringValue(JsonElement value, String fallback) {
        try { return value == null || value.isJsonNull() ? fallback : value.getAsString(); }
        catch (RuntimeException e) { return fallback; }
    }

    private static String coordinateCapture(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static final class CoordinateFinding {
        private final String transactionId;
        private final int page;
        private final String latitude;
        private final String longitude;
        private final String issue;
        CoordinateFinding(String transactionId, int page, String latitude, String longitude, String issue) {
            this.transactionId = transactionId;
            this.page = page;
            this.latitude = latitude;
            this.longitude = longitude;
            this.issue = issue;
        }
        @Override public String toString() {
            return "transaction=" + transactionId + ", page=" + (page < 0 ? "ui" : page) + ", latitude="
                    + latitude + ", longitude=" + longitude + ", issue=" + issue;
        }
    }

    private static final class RenderedCoordinates {
        private final String latitude;
        private final String longitude;
        RenderedCoordinates(String latitude, String longitude) { this.latitude = latitude; this.longitude = longitude; }
    }

    @AfterClass(alwaysRun = true)
    public void closeAuditBrowser() {
        if (driver != null) driver.quit();
    }
}
