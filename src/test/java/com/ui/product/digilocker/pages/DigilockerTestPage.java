package com.ui.product.digilocker.pages;

import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Tenant DigiLocker Test launch controls. */
public final class DigilockerTestPage {
    private static final By LINK = By.cssSelector("a[href='/api-playground']");
    private static final By GENERATE = By.xpath("//button[normalize-space()='Generate Test URL']");
    private static final By DIALOG = By.cssSelector("[role='dialog']");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DigilockerTestPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    public void openFromSidebar() {
        wait.until(ExpectedConditions.elementToBeClickable(LINK)).click();
        wait.until(ExpectedConditions.urlContains("/api-playground"));
        wait.until(ExpectedConditions.elementToBeClickable(GENERATE));
    }

    public String generateAndRedirect() {
        wait.until(ExpectedConditions.elementToBeClickable(GENERATE)).click();
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(DIALOG));
        String url = dialog.getText().lines().map(String::trim)
                .filter(line -> line.startsWith("https://digilocker.meon.co.in/redirect/digilocker/"))
                .findFirst().orElseThrow(() -> new AssertionError("Generated DigiLocker URL is missing"));
        URI uri = URI.create(url);
        if (!"digilocker.meon.co.in".equals(uri.getHost()))
            throw new AssertionError("Unexpected DigiLocker redirect host");
        dialog.findElement(By.xpath(".//button[normalize-space()='Redirect']")).click();
        return uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
    }
}
