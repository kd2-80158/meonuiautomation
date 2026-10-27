package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Plans. */
public final class PlansPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Plans']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public PlansPage(WebDriver driver) {
        super(driver, "/resources/plans", "Plans", true, true);
    }
}
