package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.VendorWsdlArtifactsPage;

/** Functional UI tests for Vendor WSDL Artifacts; shared assertions are inherited. */
public final class VendorWsdlArtifactsPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new VendorWsdlArtifactsPage(driver);
    }
}
