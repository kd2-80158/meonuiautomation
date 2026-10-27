package com.ui.cloud.apimanager.admin;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Shared assertions inherited by the dedicated test class for each resource page. */
public abstract class ApiManagerResourceContractTest extends ApiManagerAuthenticatedTest {
    protected abstract ApiManagerResourcePage createPage(WebDriver driver);

    private ApiManagerResourcePage openPage() {
        ApiManagerResourcePage page = createPage(driver);
        page.open(baseUrl);
        return page;
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void pageRendersAndNavigates() {
        ApiManagerResourcePage page = openPage();
        Assert.assertEquals(page.heading(), page.expectedHeading());
        Assert.assertTrue(page.navigationLinkExists(), "Sidebar link missing: " + page.path());
        Assert.assertTrue(page.hasSearch(), "Search missing: " + page.expectedHeading());
        Assert.assertTrue(page.hasButton("Refresh"), "Refresh missing: " + page.expectedHeading());
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void searchAndRefresh() {
        ApiManagerResourcePage page = openPage();
        page.searchFor("__ui_test_no_match_8d769__");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                    d.findElement(By.cssSelector("main")).getText().contains("0 records"));
        } catch (TimeoutException error) {
            Assert.fail("Search did not filter the no-match query on " + page.path(), error);
        }
        page.clickButton("Refresh");
        Assert.assertEquals(page.heading(), page.expectedHeading());
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void statusFilters() {
        ApiManagerResourcePage page = openPage();
        if (!page.supportsStatusFilters()) throw new SkipException("No status filter on this page");
        for (String filter : new String[] {"Active", "Inactive", "All"}) {
            Assert.assertTrue(page.hasButton(filter), "Missing filter: " + filter);
            page.clickButton(filter);
            if (!filter.equals("All")) {
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                            page.visibleActiveRowsMatch(filter.equals("Active")));
                } catch (TimeoutException error) {
                    Assert.fail(filter + " filter displayed mismatched active rows on " + page.path(), error);
                }
            }
        }
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void recordDetailsOpenAndClose() {
        ApiManagerResourcePage page = openPage();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.hasViewAction());
        } catch (TimeoutException noRecord) {
            throw new SkipException("No viewable record on this page");
        }
        Assert.assertTrue(page.detailCanOpenAndClose());
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void addFormOpensAndCancels() {
        ApiManagerResourcePage page = openPage();
        if (!page.supportsAddForm()) throw new SkipException("Read-only page");
        Assert.assertTrue(page.addFormCanOpenAndCancel());
    }

    @Test(groups = {"apimanager", "functional", "read-only"})
    public void paginationAdvancesAndReturns() {
        ApiManagerResourcePage page = openPage();
        if (driver.findElements(By.xpath("//main//button[normalize-space()='Next' and not(@disabled)]")).isEmpty()) {
            throw new SkipException("One page of records");
        }
        Assert.assertTrue(page.paginationCanAdvanceAndReturn());
    }
}
