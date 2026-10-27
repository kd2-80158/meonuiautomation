package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for API Versions. */
public final class ApiVersionsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search API Versions']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public ApiVersionsPage(WebDriver driver) {
        super(driver, "/resources/api-versions", "API Versions", false, true);
    }
}
