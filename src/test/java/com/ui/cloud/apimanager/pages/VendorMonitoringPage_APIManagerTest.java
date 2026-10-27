package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.VendorMonitoringPage_APIManager;

public final class VendorMonitoringPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void monitoringRangesAndIssueFilterWork() {
        VendorMonitoringPage_APIManager page = new VendorMonitoringPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasMonitoringControls());
        page.changeTimeRange();
        page.toggleIssueFilter();
    }
}
