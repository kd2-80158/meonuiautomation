package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Customer Plan Assignments. */
public final class CustomerPlanAssignmentsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Customer Plan Assignments']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public CustomerPlanAssignmentsPage(WebDriver driver) {
        super(driver, "/resources/customer-plan-assignments", "Customer Plan Assignments", false, true);
    }
}
