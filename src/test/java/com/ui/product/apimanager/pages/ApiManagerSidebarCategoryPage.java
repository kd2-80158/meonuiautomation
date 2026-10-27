package com.ui.product.apimanager.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Read-only page object for a top-level sidebar section and its routes. */
public abstract class ApiManagerSidebarCategoryPage {
    protected final WebDriver driver;
    private final WebDriverWait wait;

    protected ApiManagerSidebarCategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public abstract String label();
    public abstract String[] routes();

    public void openSidebar(String baseUrl) {
        driver.get(baseUrl + "/dashboard");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href='/dashboard']")));
    }

    public void expand() {
        By control = By.xpath("//button[@aria-label='Expand " + label() + "']");
        if (!driver.findElements(control).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(control)).click();
        }
    }

    public boolean hasRoute(String route) {
        return !driver.findElements(By.cssSelector("a[href='" + route + "']")).isEmpty();
    }
}
