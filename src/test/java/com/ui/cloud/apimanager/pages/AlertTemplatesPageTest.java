package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.AlertTemplatesPage;

/** Functional UI tests for Alert Templates; shared assertions are inherited. */
public final class AlertTemplatesPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new AlertTemplatesPage(driver);
    }
}
