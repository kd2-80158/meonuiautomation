package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Routing Rules. */
public final class RoutingRulesPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Routing Rules']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public RoutingRulesPage(WebDriver driver) {
        super(driver, "/resources/routing-rules", "Routing Rules", true, true);
    }
}
