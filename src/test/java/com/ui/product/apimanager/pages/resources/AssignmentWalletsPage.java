package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Assignment Wallets. */
public final class AssignmentWalletsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Assignment Wallets']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public AssignmentWalletsPage(WebDriver driver) {
        super(driver, "/resources/assignment-wallet-accounts", "Assignment Wallets", false, false);
    }
}
