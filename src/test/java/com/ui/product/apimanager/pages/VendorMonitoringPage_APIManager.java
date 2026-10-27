package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class VendorMonitoringPage_APIManager extends ApiManagerStandalonePage {
    private static final By REQUEST_SEARCH = By.cssSelector("input[placeholder='Search Request ID...']");
    public VendorMonitoringPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/vendor-monitoring", "Vendor Health & Monitoring"); }
    public boolean hasMonitoringControls() {
        return hasButton("Last 1 Hour") && hasButton("Last 24 Hours") && hasButton("Last 7 Days")
                && hasButton("All Calls") && hasButton("⚠️ Issues Only")
                && !driver.findElements(REQUEST_SEARCH).isEmpty();
    }
    public void changeTimeRange() {
        for (String range : new String[] {"Last 1 Hour", "Last 24 Hours", "Last 7 Days"}) clickButton(range);
    }
    public void toggleIssueFilter() {
        clickButton("⚠️ Issues Only");
        clickButton("All Calls");
    }
}
