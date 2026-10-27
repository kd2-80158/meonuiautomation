package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Vendor WSDL Artifacts. */
public final class VendorWsdlArtifactsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Vendor WSDL Artifacts']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public VendorWsdlArtifactsPage(WebDriver driver) {
        super(driver, "/resources/vendor-wsdl-artifacts", "Vendor WSDL Artifacts", false, true);
    }
}
