package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.BulkOperationsPage_APIManager;

public final class BulkOperationsPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void batchControlsRenderWithoutCreatingBatch() {
        BulkOperationsPage_APIManager page = new BulkOperationsPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasBatchControls());
    }
}
