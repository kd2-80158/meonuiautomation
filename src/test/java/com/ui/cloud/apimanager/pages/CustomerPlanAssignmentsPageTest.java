package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.CustomerPlanAssignmentsPage;

/** Functional UI tests for Customer Plan Assignments; shared assertions are inherited. */
public final class CustomerPlanAssignmentsPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new CustomerPlanAssignmentsPage(driver);
    }
}
