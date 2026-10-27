package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Customer User Assignments. */
public final class CustomerUserAssignmentsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Customer User Assignments']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public CustomerUserAssignmentsPage(WebDriver driver) {
        super(driver, "/resources/customer-user-assignments", "Customer User Assignments", false, true);
    }
}
