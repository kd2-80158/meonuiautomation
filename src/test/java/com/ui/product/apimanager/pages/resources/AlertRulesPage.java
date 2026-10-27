package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Vendor Alert Rules. */
public final class AlertRulesPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Vendor Alert Rules']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public AlertRulesPage(WebDriver driver) {
        super(driver, "/resources/alert-rules", "Vendor Alert Rules", true, true);
    }
}
