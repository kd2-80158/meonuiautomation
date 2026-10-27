package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Analytics & Logs. */
public final class AnalyticsAndLogsCategoryPage extends ApiManagerSidebarCategoryPage {
    public AnalyticsAndLogsCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Analytics & Logs"; }
    @Override public String[] routes() { return new String[] {"/usage-logs", "/vendor-monitoring", "/resources/alert-rules", "/resources/alert-templates", "/resources/alert-destinations", "/resources/webhooks", "/resources/webhook-deliveries"}; }
}
