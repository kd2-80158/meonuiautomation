package com.ui.cloud.apimanager.admin;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.ui.product.apimanager.pages.ApiManagerSidebarCategoryPage;

/** One concrete test class per sidebar category supplies its page object. */
public abstract class ApiManagerSidebarCategoryContractTest extends ApiManagerAuthenticatedTest {
    protected abstract ApiManagerSidebarCategoryPage createCategoryPage();

    @Test(groups = {"apimanager", "read-only", "navigation"})
    public void categoryContainsConfiguredDestinations() {
        ApiManagerSidebarCategoryPage page = createCategoryPage();
        page.openSidebar(baseUrl);
        page.expand();
        for (String route : page.routes()) {
            Assert.assertTrue(page.hasRoute(route), page.label() + " is missing " + route);
        }
    }
}
