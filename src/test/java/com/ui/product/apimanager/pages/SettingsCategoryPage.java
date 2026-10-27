package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Settings. */
public final class SettingsCategoryPage extends ApiManagerSidebarCategoryPage {
    public SettingsCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Settings"; }
    @Override public String[] routes() { return new String[] {"/resources/users", "/resources/plans"}; }
}
