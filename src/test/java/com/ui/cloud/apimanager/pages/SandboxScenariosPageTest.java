package com.ui.cloud.apimanager.pages;

import org.openqa.selenium.WebDriver;
import com.ui.cloud.apimanager.admin.ApiManagerResourceContractTest;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;
import com.ui.product.apimanager.pages.resources.SandboxScenariosPage;

/** Functional UI tests for Sandbox Operation Scenarios; shared assertions are inherited. */
public final class SandboxScenariosPageTest extends ApiManagerResourceContractTest {
    @Override
    protected ApiManagerResourcePage createPage(WebDriver driver) {
        return new SandboxScenariosPage(driver);
    }
}
