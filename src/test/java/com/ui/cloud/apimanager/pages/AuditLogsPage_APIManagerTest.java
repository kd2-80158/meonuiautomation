package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.AuditLogsPage_APIManager;

public final class AuditLogsPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void activityFiltersRender() {
        AuditLogsPage_APIManager page = new AuditLogsPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasFilters());
    }
}
