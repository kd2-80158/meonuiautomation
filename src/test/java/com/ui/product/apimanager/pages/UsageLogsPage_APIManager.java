package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class UsageLogsPage_APIManager extends ApiManagerStandalonePage {
    private static final By REQUEST_SEARCH = By.cssSelector("input[placeholder^='Request ID']");
    public UsageLogsPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/usage-logs", "Usage Logs"); }
    public boolean hasFilters() {
        return hasButton("Refresh") && hasButton("All customers") && hasButton("All results")
                && hasButton("Live and sandbox") && !driver.findElements(REQUEST_SEARCH).isEmpty();
    }
}
