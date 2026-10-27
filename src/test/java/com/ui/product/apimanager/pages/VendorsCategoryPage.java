package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Vendors. */
public final class VendorsCategoryPage extends ApiManagerSidebarCategoryPage {
    public VendorsCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Vendors"; }
    @Override public String[] routes() { return new String[] {"/resources/vendors", "/resources/vendor-endpoints", "/resources/vendor-wsdl-artifacts"}; }
}
