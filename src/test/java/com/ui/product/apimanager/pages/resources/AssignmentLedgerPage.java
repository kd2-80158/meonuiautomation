package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Assignment Wallet Ledger. */
public final class AssignmentLedgerPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Assignment Wallet Ledger']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public AssignmentLedgerPage(WebDriver driver) {
        super(driver, "/resources/assignment-wallet-ledger", "Assignment Wallet Ledger", false, false);
    }
}
