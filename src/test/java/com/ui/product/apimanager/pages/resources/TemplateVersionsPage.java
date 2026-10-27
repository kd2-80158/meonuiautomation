package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Transformer Template Versions. */
public final class TemplateVersionsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Transformer Template Versions']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public TemplateVersionsPage(WebDriver driver) {
        super(driver, "/resources/transformer-template-versions", "Transformer Template Versions", true, true);
    }
}
