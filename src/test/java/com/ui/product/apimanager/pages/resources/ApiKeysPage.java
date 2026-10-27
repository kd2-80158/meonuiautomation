package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for API Keys. */
public final class ApiKeysPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search API Keys']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public ApiKeysPage(WebDriver driver) {
        super(driver, "/resources/api-keys", "API Keys", true, true);
    }
}
