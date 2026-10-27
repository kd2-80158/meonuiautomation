package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class DashboardPage_APIManager extends ApiManagerStandalonePage {
    private static final By METRICS = By.xpath("//main//h3");

    public DashboardPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/dashboard", "Platform Command Center"); }
    public boolean hasMetric(String name) {
        return driver.findElements(By.xpath("//main//h3[normalize-space()='" + name + "']")).size() > 0;
    }
    public boolean hasRecentLogs() {
        return !driver.findElements(By.xpath("//main//h3[normalize-space()='Recent Platform Logs']")).isEmpty()
                && !driver.findElements(By.cssSelector("main table")).isEmpty();
    }
}
