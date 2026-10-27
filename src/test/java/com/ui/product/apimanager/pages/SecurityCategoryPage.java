package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Security. */
public final class SecurityCategoryPage extends ApiManagerSidebarCategoryPage {
    public SecurityCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Security"; }
    @Override public String[] routes() { return new String[] {"/audit-logs"}; }
}
