package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Assignment Wallet Payments. */
public final class WalletPaymentsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Assignment Wallet Payments']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public WalletPaymentsPage(WebDriver driver) {
        super(driver, "/resources/wallet-payments", "Assignment Wallet Payments", false, false);
    }
}
