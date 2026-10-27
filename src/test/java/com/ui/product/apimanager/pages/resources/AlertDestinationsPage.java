package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Alert Destinations. */
public final class AlertDestinationsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Alert Destinations']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public AlertDestinationsPage(WebDriver driver) {
        super(driver, "/resources/alert-destinations", "Alert Destinations", true, true);
    }
}
