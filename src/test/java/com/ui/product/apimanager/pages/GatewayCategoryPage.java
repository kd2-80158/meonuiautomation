package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Gateway. */
public final class GatewayCategoryPage extends ApiManagerSidebarCategoryPage {
    public GatewayCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Gateway"; }
    @Override public String[] routes() { return new String[] {"/resources/routing-rules", "/resources/operation-pipeline-steps", "/resources/sandbox-operation-scenarios", "/resources/transformer-templates", "/resources/transformer-template-versions", "/resources/transformer-publish-runs", "/resources/api-keys", "/resources/idempotency-keys"}; }
}
