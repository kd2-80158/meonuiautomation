package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: APIs. */
public final class ApisCategoryPage extends ApiManagerSidebarCategoryPage {
    public ApisCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "APIs"; }
    @Override public String[] routes() { return new String[] {"/apis", "/resources/api-versions", "/resources/api-operations"}; }
}
