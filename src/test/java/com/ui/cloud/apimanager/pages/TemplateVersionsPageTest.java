package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.TemplateVersionsPage;

/** Functional UI tests for Transformer Template Versions; shared assertions are inherited. */
public final class TemplateVersionsPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new TemplateVersionsPage(driver);
    }
}
