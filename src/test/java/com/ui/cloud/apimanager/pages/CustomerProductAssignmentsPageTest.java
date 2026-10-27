package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.CustomerProductAssignmentsPage;

/** Functional UI tests for Customer Product Assignments; shared assertions are inherited. */
public final class CustomerProductAssignmentsPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new CustomerProductAssignmentsPage(driver);
    }
}
