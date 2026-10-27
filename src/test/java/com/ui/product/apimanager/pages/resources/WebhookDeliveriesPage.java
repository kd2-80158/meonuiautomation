package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Webhook Deliveries. */
public final class WebhookDeliveriesPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Webhook Deliveries']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public WebhookDeliveriesPage(WebDriver driver) {
        super(driver, "/resources/webhook-deliveries", "Webhook Deliveries", false, false);
    }
}
