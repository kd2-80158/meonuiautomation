package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Vendor Endpoints. */
public final class VendorEndpointsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Vendor Endpoints']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public VendorEndpointsPage(WebDriver driver) {
        super(driver, "/resources/vendor-endpoints", "Vendor Endpoints", true, true);
    }
}
