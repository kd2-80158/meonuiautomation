package com.ui.cloud.apimanager.pages;

import com.ui.cloud.apimanager.admin.ApiManagerSidebarCategoryContractTest;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;
import com.ui.product.apimanager.pages.SecurityCategoryPage;

public final class SecurityCategoryTest extends ApiManagerSidebarCategoryContractTest {
    @Override protected ApiManagerSidebarCategoryPage createCategoryPage() {
        return new SecurityCategoryPage(driver);
    }
}
