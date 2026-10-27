package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Dashboard. */
public final class DashboardCategoryPage extends ApiManagerSidebarCategoryPage {
    public DashboardCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Dashboard"; }
    @Override public String[] routes() { return new String[] {"/dashboard"}; }
}
