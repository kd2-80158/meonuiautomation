package com.ui.product.apimanager.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Navigation and waits shared by API Manager's non-resource page objects. */
public abstract class ApiManagerStandalonePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected ApiManagerStandalonePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    protected void open(String baseUrl, String path, String heading) {
        driver.get(baseUrl + path);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//*[self::h1 or self::h2 or self::h3][normalize-space()='" + heading + "']")));
    }

    protected boolean hasButton(String name) {
        return !driver.findElements(By.xpath("//button[normalize-space()='" + name + "']")).isEmpty();
    }

    protected void clickButton(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//button[normalize-space()='" + name + "']"))).click();
    }
}
