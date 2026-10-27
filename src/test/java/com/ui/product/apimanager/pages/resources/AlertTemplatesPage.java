package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Alert Templates. */
public final class AlertTemplatesPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Alert Templates']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public AlertTemplatesPage(WebDriver driver) {
        super(driver, "/resources/alert-templates", "Alert Templates", false, true);
    }
}
