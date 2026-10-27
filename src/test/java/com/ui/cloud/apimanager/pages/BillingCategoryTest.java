package com.ui.cloud.apimanager.pages;

import com.ui.cloud.apimanager.admin.ApiManagerSidebarCategoryContractTest;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;
import com.ui.product.apimanager.pages.BillingCategoryPage;

public final class BillingCategoryTest extends ApiManagerSidebarCategoryContractTest {
    @Override protected ApiManagerSidebarCategoryPage createCategoryPage() {
        return new BillingCategoryPage(driver);
    }
}
