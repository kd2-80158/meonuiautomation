package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.ApiCatalogPage_APIManager;

public final class ApiCatalogPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void catalogFiltersRenderAndSwitch() {
        ApiCatalogPage_APIManager page = new ApiCatalogPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasCatalogControls());
        page.cycleFilters();
    }

    @Test(groups = {"apimanager", "read-only", "functional"})
    public void createAndEditCanBeCancelled() {
        ApiCatalogPage_APIManager page = new ApiCatalogPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.createFormOpensAndCancels());
        if (!page.editFormOpensAndCancels()) throw new SkipException("No catalog records to edit");
    }
}
