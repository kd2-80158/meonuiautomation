package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Sandbox Operation Scenarios. */
public final class SandboxScenariosPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Sandbox Operation Scenarios']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public SandboxScenariosPage(WebDriver driver) {
        super(driver, "/resources/sandbox-operation-scenarios", "Sandbox Operation Scenarios", true, true);
    }
}
