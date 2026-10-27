package com.ui.product.apimanager.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Shared, non-mutating controls used by the API Manager resource pages. */
public class ApiManagerResourcePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String path;
    private final String expectedHeading;
    private final boolean statusFilters;
    private final boolean addForm;

    protected ApiManagerResourcePage(WebDriver driver, String path, String expectedHeading,
            boolean statusFilters, boolean addForm) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.path = path;
        this.expectedHeading = expectedHeading;
        this.statusFilters = statusFilters;
        this.addForm = addForm;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + path);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//*[self::h1 or self::h2 or self::h3][normalize-space()='" + expectedHeading + "']")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//button[normalize-space()='Refresh']")));
        } catch (TimeoutException error) {
            String actualHeading = driver.findElements(By.cssSelector("h1,h2,h3")).stream()
                    .findFirst().map(WebElement::getText).orElse("<none>");
            throw new AssertionError("Page did not render: " + path + ", URL=" + driver.getCurrentUrl()
                    + ", heading=" + actualHeading + ", title=" + driver.getTitle(), error);
        }
    }

    public String path() { return path; }
    public String expectedHeading() { return expectedHeading; }
    public boolean supportsStatusFilters() { return statusFilters; }
    public boolean supportsAddForm() { return addForm; }

    public boolean navigationLinkExists() {
        return navigationLinkExists(path);
    }

    public String heading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1,h2,h3"))).getText().trim();
    }

    public boolean navigationLinkExists(String path) {
        return !driver.findElements(By.cssSelector("nav a[href='" + path + "']")).isEmpty();
    }

    public boolean hasSearch() {
        return !driver.findElements(searchLocator()).isEmpty();
    }

    protected By searchLocator() {
        return By.cssSelector("main input[placeholder^='Search ']");
    }

    public void searchFor(String value) {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
                searchLocator()));
        search.clear();
        search.sendKeys(value);
    }

    public boolean hasVisibleRecordContaining(String text) {
        return driver.findElements(By.xpath("//main//table//tbody//tr[contains(.,'" + text + "')]")).size() > 0;
    }

    public int visibleRecordCount() {
        return driver.findElements(By.cssSelector("main table tbody tr")).size();
    }

    public boolean hasViewAction() {
        return !driver.findElements(By.cssSelector("main table tbody button[title='View']")).isEmpty();
    }

    public boolean hasButton(String name) {
        return !driver.findElements(By.xpath("//main//button[normalize-space()='" + name + "']")).isEmpty();
    }

    public void clickButton(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//main//button[normalize-space()='" + name + "']"))).click();
    }

    public boolean detailCanOpenAndClose() {
        List<WebElement> view = driver.findElements(By.cssSelector("main table tbody button[title='View']"));
        if (view.isEmpty()) return false;
        view.get(0).click();
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@role='dialog'][.//h2[normalize-space()='Record Details']]")));
        boolean hasFields = !dialog.findElements(By.cssSelector("table tbody tr")).isEmpty();
        dialog.sendKeys(org.openqa.selenium.Keys.ESCAPE);
        wait.until(ExpectedConditions.invisibilityOf(dialog));
        return hasFields;
    }

    public boolean addFormCanOpenAndCancel() {
        if (!hasButton("Add")) return false;
        clickButton("Add");
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@role='dialog'][.//h2[starts-with(normalize-space(),'Create ')]]")));
        boolean hasSave = !dialog.findElements(By.xpath(".//button[normalize-space()='Save']")).isEmpty();
        dialog.findElement(By.xpath(".//button[normalize-space()='Cancel']")).click();
        wait.until(ExpectedConditions.invisibilityOf(dialog));
        return hasSave;
    }

    public boolean paginationCanAdvanceAndReturn() {
        List<WebElement> nextButtons = driver.findElements(By.xpath("//main//button[normalize-space()='Next']"));
        if (nextButtons.isEmpty() || !nextButtons.get(0).isEnabled()) return false;
        nextButtons.get(0).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("main"), "Page 2 of"));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//main//button[normalize-space()='Previous']"))).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("main"), "Page 1 of"));
        return true;
    }

    /** Returns true when visible rows match the selected active state. Empty results are valid. */
    public boolean visibleActiveRowsMatch(boolean expectedActive) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "const headers=[...document.querySelectorAll('main table thead th')].map(x=>x.textContent.trim());"
                + "const index=headers.indexOf('is_active'); if(index<0) return true;"
                + "return [...document.querySelectorAll('main table tbody tr')].every(row=>{"
                + "const cells=row.querySelectorAll('td'); if(cells.length<=index) return true;"
                + "const value=cells[index].textContent.trim().toLowerCase();"
                + "return value==='no records found'||value===(arguments[0]?'true':'false');});",
                expectedActive));
    }
}
