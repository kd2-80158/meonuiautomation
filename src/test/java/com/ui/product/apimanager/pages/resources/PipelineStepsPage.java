package com.ui.product.apimanager.pages.resources;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ui.product.apimanager.pages.ApiManagerResourcePage;

/** Page object for Operation Pipeline Steps. */
public final class PipelineStepsPage extends ApiManagerResourcePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search Operation Pipeline Steps']");

    @Override
    protected By searchLocator() { return SEARCH; }

    public PipelineStepsPage(WebDriver driver) {
        super(driver, "/resources/operation-pipeline-steps", "Operation Pipeline Steps", true, true);
    }
}
