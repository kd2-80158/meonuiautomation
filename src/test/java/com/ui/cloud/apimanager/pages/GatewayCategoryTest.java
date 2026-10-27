package com.ui.cloud.apimanager.pages;

import com.ui.cloud.apimanager.admin.ApiManagerSidebarCategoryContractTest;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;
import com.ui.product.apimanager.pages.GatewayCategoryPage;

public final class GatewayCategoryTest extends ApiManagerSidebarCategoryContractTest {
    @Override protected ApiManagerSidebarCategoryPage createCategoryPage() {
        return new GatewayCategoryPage(driver);
    }
}
