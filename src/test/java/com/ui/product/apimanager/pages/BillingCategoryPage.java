package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Billing. */
public final class BillingCategoryPage extends ApiManagerSidebarCategoryPage {
    public BillingCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Billing"; }
    @Override public String[] routes() { return new String[] {"/resources/assignment-wallet-accounts", "/resources/assignment-wallet-ledger", "/resources/wallet-payments"}; }
}
