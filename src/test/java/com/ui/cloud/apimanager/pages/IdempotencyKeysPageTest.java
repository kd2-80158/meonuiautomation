package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.IdempotencyKeysPage;

/** Functional UI tests for Idempotency Keys; shared assertions are inherited. */
public final class IdempotencyKeysPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new IdempotencyKeysPage(driver);
    }
}
