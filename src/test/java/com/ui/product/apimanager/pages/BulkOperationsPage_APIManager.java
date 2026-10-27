package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Inspects batch controls without uploading a file or creating a batch. */
public final class BulkOperationsPage_APIManager extends ApiManagerStandalonePage {
    private static final By UPLOAD = By.cssSelector("input[type='file']");
    public BulkOperationsPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/bulk-operations", "Bulk API Operations"); }
    public boolean hasBatchControls() {
        return hasButton("Create Batch") && hasButton("Export Results")
                && !driver.findElements(UPLOAD).isEmpty();
    }
}
