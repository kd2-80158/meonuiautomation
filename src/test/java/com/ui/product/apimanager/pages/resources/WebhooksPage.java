package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Webhooks. */
public final class WebhooksPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Webhooks']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public WebhooksPage(WebDriver driver) {
        super(driver, "/resources/webhooks", "Webhooks", true, true);
    }
}
