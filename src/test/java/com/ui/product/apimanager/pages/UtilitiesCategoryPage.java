package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Utilities. */
public final class UtilitiesCategoryPage extends ApiManagerSidebarCategoryPage {
    public UtilitiesCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Utilities"; }
    @Override public String[] routes() { return new String[] {"/runtime-tester", "/bulk-operations"}; }
}
