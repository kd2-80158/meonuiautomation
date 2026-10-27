package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class AuditLogsPage_APIManager extends ApiManagerStandalonePage {
    private static final By DESCRIPTION_SEARCH = By.cssSelector("input[placeholder^='Description']");
    public AuditLogsPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/audit-logs", "Activity Logs"); }
    public boolean hasFilters() {
        return hasButton("Refresh") && hasButton("All categories") && hasButton("All outcomes")
                && hasButton("All customers") && !driver.findElements(DESCRIPTION_SEARCH).isEmpty();
    }
}
