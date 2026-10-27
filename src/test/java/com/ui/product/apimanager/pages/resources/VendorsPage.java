package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Vendors. */
public final class VendorsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Vendors']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public VendorsPage(WebDriver driver) {
        super(driver, "/resources/vendors", "Vendors", true, true);
    }
}
