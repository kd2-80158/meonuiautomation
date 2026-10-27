package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.UsageLogsPage_APIManager;

public final class UsageLogsPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void usageFiltersRender() {
        UsageLogsPage_APIManager page = new UsageLogsPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasFilters());
    }
}
