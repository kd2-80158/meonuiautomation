package com.ui.product.rekyc.pages.admin.meontechnologies;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object for the read-only Workflow V2 Modification editor. */
public final class ModificationWorkflowV2Page {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ModificationWorkflowV2Page(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public void openPageConfiguration(String pageName) {
        driver.findElements(By.cssSelector("button[aria-label='fit view']")).stream().findFirst()
                .ifPresent(button -> jsClick(button));
        WebElement node = driver.findElements(By.cssSelector(".react-flow__node")).stream()
                .filter(candidate -> normalize(candidate.getText()).contains(normalize(pageName)))
                .findFirst().orElseThrow(() -> new AssertionError("Workflow page node not found: " + pageName));
        for (int attempt = 0; attempt < 3; attempt++) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})", node);
            try { new Actions(driver).moveToElement(node).click().perform(); }
            catch (RuntimeException ignored) { jsClick(node); }
            try {
                new WebDriverWait(driver, Duration.ofSeconds(8)).until(
                        d -> d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
                return;
            } catch (org.openqa.selenium.TimeoutException ignored) { }
        }
        throw new AssertionError("Workflow page configuration did not open: " + pageName);
    }

    public void selectModule(String moduleName) {
        WebElement tab = driver.findElements(By.tagName("button")).stream()
                .filter(button -> normalize(button.getText()).equals(normalize(moduleName)))
                .filter(WebElement::isDisplayed).findFirst()
                .orElseThrow(() -> new AssertionError("Workflow module tab was not found: " + moduleName));
        try { tab.click(); } catch (RuntimeException ignored) { jsClick(tab); }
        wait.until(d -> d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
    }

    public void closePageConfiguration() {
        List<WebElement> close = driver.findElements(By.cssSelector("button[aria-label='Close']"));
        if (close.isEmpty()) {
            close = driver.findElements(By.tagName("button")).stream()
                    .filter(button -> normalize(button.getText()).equals("close")).toList();
        }
        if (!close.isEmpty()) jsClick(close.get(0)); else new Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
        wait.until(d -> !d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
    }

    private void jsClick(WebElement element) { ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element); }
    private static String normalize(String value) { return value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9]", ""); }
}
