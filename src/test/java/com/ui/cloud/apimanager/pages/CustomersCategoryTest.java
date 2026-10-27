package com.ui.cloud.apimanager.pages;

import com.ui.cloud.apimanager.admin.ApiManagerSidebarCategoryContractTest;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;
import com.ui.product.apimanager.pages.CustomersCategoryPage;

public final class CustomersCategoryTest extends ApiManagerSidebarCategoryContractTest {
    @Override protected ApiManagerSidebarCategoryPage createCategoryPage() {
        return new CustomersCategoryPage(driver);
    }
}
