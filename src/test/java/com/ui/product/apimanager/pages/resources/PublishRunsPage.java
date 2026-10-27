package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Transformer Publish Runs. */
public final class PublishRunsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Transformer Publish Runs']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public PublishRunsPage(WebDriver driver) {
        super(driver, "/resources/transformer-publish-runs", "Transformer Publish Runs", false, false);
    }
}
