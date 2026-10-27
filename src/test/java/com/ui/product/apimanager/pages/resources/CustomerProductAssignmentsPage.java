package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Customer Product Assignments. */
public final class CustomerProductAssignmentsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Customer Product Assignments']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public CustomerProductAssignmentsPage(WebDriver driver) {
        super(driver, "/resources/customer-product-assignments", "Customer Product Assignments", true, true);
    }
}
