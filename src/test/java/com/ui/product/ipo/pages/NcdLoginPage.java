package com.ui.product.ipo.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class NcdLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By clientCode = By.cssSelector("input[name='clientCode']");
    private final By continueButton = By.xpath("//button[normalize-space()='Continue to verification']");

    public NcdLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public NcdLoginPage awaitLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(clientCode));
        wait.until(d -> d.getCurrentUrl().contains("/v1/marfatia/login"));
        return this;
    }

    public boolean hasThreeSteps() {
        String html = driver.getPageSource();
        return html.contains("Client code") && html.contains("Verification") && html.contains("Demat account");
    }

    public void enterClientCode(String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(clientCode));
        input.clear();
        input.sendKeys(value);
    }

    public String clientCodeValue() {
        return driver.findElement(clientCode).getAttribute("value");
    }

    public String clientCodeMaxLength() {
        return driver.findElement(clientCode).getAttribute("maxlength");
    }

    public void continueToVerification() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public boolean hasClientCodeError() {
        return wait.until(d -> d.getPageSource().contains("Enter a valid trading or client code (2–50 characters)."));
    }

    public boolean staysOnFirstStep() {
        return driver.getCurrentUrl().endsWith("/v1/marfatia/login")
                && !driver.findElements(clientCode).isEmpty();
    }

    public NcdRegisterPage openRegister() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(normalize-space(),'Register')]") )).click();
        return new NcdRegisterPage(driver).awaitLoaded();
    }
}
