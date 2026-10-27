package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Users. */
public final class UsersPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Users']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public UsersPage(WebDriver driver) {
        super(driver, "/resources/users", "Users", true, true);
    }
}
