package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Idempotency Keys. */
public final class IdempotencyKeysPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Idempotency Keys']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public IdempotencyKeysPage(WebDriver driver) {
        super(driver, "/resources/idempotency-keys", "Idempotency Keys", false, false);
    }
}
