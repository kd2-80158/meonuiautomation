package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.WalletPaymentsPage;

/** Functional UI tests for Assignment Wallet Payments; shared assertions are inherited. */
public final class WalletPaymentsPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new WalletPaymentsPage(driver);
    }
}
