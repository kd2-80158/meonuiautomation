package com.ui.product.apimanager.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class ApiCatalogPage_APIManager extends ApiManagerStandalonePage {
    private static final By SEARCH = By.cssSelector("main input[placeholder='Search by name...']");
    private static final By EDIT = By.cssSelector("main table tbody button[title='Edit in catalog']");
    private static final By DIALOG = By.cssSelector("[role='dialog']");

    public ApiCatalogPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/apis", "API Management"); }
    public boolean hasCatalogControls() {
        return !driver.findElements(SEARCH).isEmpty() && hasButton("Create New API")
                && hasButton("All APIs") && hasButton("Active") && hasButton("Inactive") && hasButton("Drafts");
    }
    public void cycleFilters() {
        for (String filter : new String[] {"Active", "Inactive", "Drafts", "All APIs"}) clickButton(filter);
    }
    public boolean createFormOpensAndCancels() {
        clickButton("Create New API");
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DIALOG));
        boolean correctHeading = dialog.getText().contains("Create New API");
        dialog.findElement(By.xpath(".//button[normalize-space()='Cancel']")).click();
        wait.until(ExpectedConditions.invisibilityOf(dialog));
        return correctHeading;
    }
    public boolean editFormOpensAndCancels() {
        if (driver.findElements(EDIT).isEmpty()) return false;
        driver.findElements(EDIT).get(0).click();
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DIALOG));
        boolean correctHeading = dialog.getText().contains("Edit API");
        dialog.findElement(By.xpath(".//button[normalize-space()='Cancel']")).click();
        wait.until(ExpectedConditions.invisibilityOf(dialog));
        return correctHeading;
    }
}
