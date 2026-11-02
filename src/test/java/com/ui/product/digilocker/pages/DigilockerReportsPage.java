package com.ui.product.digilocker.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Read-only transaction report controls and records. */
public final class DigilockerReportsPage {
    private static final By REPORT_LINK = By.cssSelector("a[href='/reports']");
    private static final By HEADING = By.xpath("//h1[normalize-space()='Retrieval reports']");
    private static final By ROWS = By.cssSelector("table tbody tr");
    private static final By REFRESH = By.xpath("//button[contains(normalize-space(.),'Refresh reports') or @aria-label='Refresh reports']");
    private static final By NEXT = By.xpath("//button[contains(@aria-label,'next page')]");
    private static final By PREVIOUS = By.xpath("//button[contains(@aria-label,'previous page')]");
    private static final By SEARCH = By.cssSelector("input[placeholder='Search by ID, name, PAN, Aadhaar or state']");
    private static final By APPLY = By.xpath("//button[normalize-space()='Apply filters']");
    private static final By RESET = By.xpath("//button[normalize-space()='Reset']");
    private static final By STATUS = By.id("mui-component-select-status");
    private static final By DOCUMENT_DIALOG = By.cssSelector("[role='dialog']");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DigilockerReportsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void openFromSidebar() {
        wait.until(ExpectedConditions.elementToBeClickable(REPORT_LINK)).click();
        wait.until(ExpectedConditions.urlContains("/reports"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING));
    }

    public void openFresh() {
        driver.get("https://digilocker.meon.co.in/reports");
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING));
        wait.until(d -> !d.findElements(ROWS).isEmpty());
    }

    public void refresh() {
        wait.until(ExpectedConditions.elementToBeClickable(REFRESH)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING));
    }

    public void searchFor(String transactionId) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(SEARCH));
        field.clear();
        field.sendKeys(transactionId);
        wait.until(ExpectedConditions.elementToBeClickable(APPLY)).click();
        wait.until(d -> visibleEntries().stream().anyMatch(row -> row.transactionId.equals(transactionId)));
    }

    public void filterStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(STATUS)).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@role='option' and normalize-space()='" + status + "']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(APPLY)).click();
        wait.until(d -> !visibleEntries().isEmpty());
    }

    public void resetFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(RESET)).click();
        wait.until(d -> d.findElement(SEARCH).getAttribute("value").isEmpty());
    }

    public String nextPage() {
        String before = visibleEntries().get(0).transactionId;
        wait.until(ExpectedConditions.elementToBeClickable(NEXT)).click();
        wait.until(d -> !visibleEntries().isEmpty() && !visibleEntries().get(0).transactionId.equals(before));
        return visibleEntries().get(0).transactionId;
    }

    public void previousPage() {
        String before = visibleEntries().get(0).transactionId;
        wait.until(ExpectedConditions.elementToBeClickable(PREVIOUS)).click();
        wait.until(d -> !visibleEntries().isEmpty() && !visibleEntries().get(0).transactionId.equals(before));
    }

    public String openFirstCompletedDocuments() {
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table//tbody/tr[.//button[normalize-space()='View documents']][1]")));
        String id = row.findElements(By.tagName("td")).get(1).getText().trim();
        row.findElement(By.xpath(".//button[normalize-space()='View documents']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        return id;
    }

    public List<String> availableDocumentTypes() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        List<String> types = new ArrayList<>();
        for (WebElement button : dialog.findElements(By.tagName("button"))) {
            String label = button.getText().trim();
            if (label.startsWith("Aadhaar")) types.add("Aadhaar");
            else if (label.startsWith("PAN")) types.add("PAN");
        }
        return types;
    }

    public void selectDocumentType(String type) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        dialog.findElement(By.xpath(".//button[starts-with(normalize-space(.),'" + type + "')]")).click();
        wait.until(d -> dialog.getText().contains("Document information"));
    }

    public boolean documentDialogShowsFiles(String transactionId, String type) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        if (!dialog.getText().contains(transactionId) || !dialog.getText().contains(type)
                || !dialog.getText().contains("Document information")) return false;
        List<WebElement> links = dialog.findElements(By.xpath(".//a[starts-with(normalize-space(.),'Open ')]"));
        if (links.isEmpty()) return false;
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            if (href == null || !href.contains("/" + transactionId + "/")) return false;
        }
        return true;
    }

    public List<String> availableFileActions() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        List<String> actions = new ArrayList<>();
        for (WebElement link : dialog.findElements(By.xpath(".//a[starts-with(normalize-space(.),'Open ')]"))) {
            actions.add(link.getText().trim());
        }
        return actions;
    }

    public int clickFileAndVerifyResponse(String action, String transactionId) throws IOException {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        String parent = driver.getWindowHandle();
        Set<String> before = new HashSet<>(driver.getWindowHandles());
        WebElement link = dialog.findElement(By.xpath(".//a[normalize-space()='" + action + "']"));
        String href = link.getAttribute("href");
        if (href == null || !href.contains("/" + transactionId + "/"))
            throw new IllegalStateException("File link does not belong to selected transaction");
        URI uri = URI.create(href);
        if (!"https".equals(uri.getScheme()) || !"digilocker.meon.co.in".equals(uri.getHost()))
            throw new IllegalStateException("Fetched-document link is outside DigiLocker origin");
        link.click();
        HttpURLConnection request = (HttpURLConnection) new URL(href).openConnection();
        request.setInstanceFollowRedirects(false);
        request.setRequestMethod("HEAD");
        request.setConnectTimeout(10000);
        request.setReadTimeout(10000);
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie : driver.manage().getCookies()) {
            if (cookies.length() > 0) cookies.append("; ");
            cookies.append(cookie.getName()).append('=').append(cookie.getValue());
        }
        if (cookies.length() > 0) request.setRequestProperty("Cookie", cookies.toString());
        int status = request.getResponseCode();
        request.disconnect();
        for (String handle : driver.getWindowHandles()) {
            if (!before.contains(handle)) {
                try { driver.switchTo().window(handle); driver.close(); }
                catch (org.openqa.selenium.WebDriverException transientViewer) { /* PDF viewer may close itself. */ }
            }
        }
        driver.switchTo().window(parent);
        wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        return status;
    }

    public void closeDocuments() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DOCUMENT_DIALOG));
        dialog.findElement(By.xpath(".//button[1]")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(DOCUMENT_DIALOG));
    }

    public List<ReportEntry> visibleEntries() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING));
        return wait.until(d -> {
            try {
                List<ReportEntry> records = new ArrayList<>();
                for (WebElement row : d.findElements(ROWS)) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.size() < 6) continue;
                    records.add(new ReportEntry(cells.get(1).getText().trim(), cells.get(2).getText().trim(),
                            cells.get(3).getText().trim(), cells.get(4).getText().trim(), cells.get(5).getText().trim()));
                }
                return records.isEmpty() ? null : records;
            } catch (StaleElementReferenceException rerendering) {
                return null;
            }
        });
    }

    public List<ReportEntry> findOnRecentPages(String transactionId, int maxPages) {
        List<ReportEntry> found = new ArrayList<>();
        for (int page = 0; page < maxPages; page++) {
            for (ReportEntry row : visibleEntries()) {
                if (row.transactionId.equalsIgnoreCase(transactionId)) found.add(row);
            }
            List<WebElement> next = driver.findElements(NEXT);
            if (next.isEmpty() || !next.get(0).isEnabled()) break;
            String first = visibleEntries().isEmpty() ? "" : visibleEntries().get(0).transactionId;
            next.get(0).click();
            wait.until(d -> {
                List<WebElement> rows = d.findElements(ROWS);
                return rows.isEmpty() || !rows.get(0).getText().contains(first);
            });
        }
        return found;
    }

    public static final class ReportEntry {
        public final String transactionId;
        public final String requestedAt;
        public final String document;
        public final String result;
        public final String status;

        public ReportEntry(String transactionId, String requestedAt, String document, String result, String status) {
            this.transactionId = transactionId;
            this.requestedAt = requestedAt;
            this.document = document;
            this.result = result;
            this.status = status;
        }
    }
}
