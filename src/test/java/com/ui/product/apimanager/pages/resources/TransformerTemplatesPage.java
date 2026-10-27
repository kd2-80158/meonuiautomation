package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Transformer Templates. */
public final class TransformerTemplatesPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Transformer Templates']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public TransformerTemplatesPage(WebDriver driver) {
        super(driver, "/resources/transformer-templates", "Transformer Templates", true, true);
    }
}
