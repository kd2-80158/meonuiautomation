package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for API Operations. */
public final class ApiOperationsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search API Operations']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public ApiOperationsPage(WebDriver driver) {
        super(driver, "/resources/api-operations", "API Operations", true, true);
    }
}
