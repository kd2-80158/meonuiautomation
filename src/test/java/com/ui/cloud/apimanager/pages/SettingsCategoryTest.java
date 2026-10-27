package com.ui.cloud.apimanager.pages;

import com.ui.cloud.apimanager.admin.ApiManagerSidebarCategoryContractTest;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;
import com.ui.product.apimanager.pages.SettingsCategoryPage;

public final class SettingsCategoryTest extends ApiManagerSidebarCategoryContractTest {
    @Override protected ApiManagerSidebarCategoryPage createCategoryPage() {
        return new SettingsCategoryPage(driver);
    }
}
