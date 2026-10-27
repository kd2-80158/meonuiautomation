package com.ui.cloud.apimanager.pages;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.cloud.apimanager.admin.ApiManagerAuthenticatedTest;
import com.ui.product.apimanager.pages.RuntimeTesterPage_APIManager;

public final class RuntimeTesterPage_APIManagerTest extends ApiManagerAuthenticatedTest {
    @Test(groups = {"apimanager", "read-only", "functional"})
    public void runtimeModesSwitchWithoutApiCall() {
        RuntimeTesterPage_APIManager page = new RuntimeTesterPage_APIManager(driver);
        page.open(baseUrl);
        Assert.assertTrue(page.hasModeControls());
        page.switchModesWithoutCallingApi();
    }
}
