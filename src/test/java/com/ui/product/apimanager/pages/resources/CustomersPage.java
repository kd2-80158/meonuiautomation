package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Customers. */
public final class CustomersPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Customers']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public CustomersPage(WebDriver driver) {
        super(driver, "/resources/customers", "Customers", true, true);
    }
}
