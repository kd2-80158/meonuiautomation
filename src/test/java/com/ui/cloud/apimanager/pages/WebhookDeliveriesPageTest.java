package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.WebhookDeliveriesPage;

/** Functional UI tests for Webhook Deliveries; shared assertions are inherited. */
public final class WebhookDeliveriesPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new WebhookDeliveriesPage(driver);
    }
}
