package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.DashboardPage_APIManager;

public final class DashboardPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "smoke"})
    public void dashboardMetricsAndRecentLogsRender() {
        DashboardPage_APIManager page = new DashboardPage_APIManager(driver);
        page.open(baseUrl);
        for (String metric : new String[] {"Total API Hits", "Success Rate", "Net Credits", "Avg Latency"}) {
            Assert.assertTrue(page.hasMetric(metric), "Missing metric: " + metric);
        }
        Assert.assertTrue(page.hasRecentLogs());
    }
}
