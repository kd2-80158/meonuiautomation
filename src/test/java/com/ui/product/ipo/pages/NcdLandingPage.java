package com.ui.product.ipo.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class NcdLandingPage {
    public static final String URL = "https://ipo.meon.co.in/v1/apply_ncd/marfatia";
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By login = By.xpath("//button[normalize-space()='Login']");
    private final By register = By.xpath("//button[normalize-space()='Register']");
    private final By heading = By.xpath("//h1[normalize-space()='Current NCD issues']");

    public NcdLandingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public NcdLandingPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        return this;
    }

    public boolean hasCompanyIdentity() {
        return driver.getPageSource().contains("MARFATIA STOCK BROKING PVT LTD")
                && !driver.findElements(By.cssSelector("img[alt*='MARFATIA']")).isEmpty();
    }

    public boolean hasResolvedIssueState() {
        return wait.until(d -> d.getPageSource().contains("No live NCD opportunities")
                || !d.findElements(By.xpath("//h1[normalize-space()='Current NCD issues']/following::*[contains(text(),'Apply') or contains(text(),'View Details')]")).isEmpty());
    }

    public boolean hasEmptyIssueState() {
        return driver.getPageSource().contains("No live NCD opportunities are currently available for this company.");
    }

    public NcdLoginPage openLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(login)).click();
        return new NcdLoginPage(driver).awaitLoaded();
    }

    public NcdRegisterPage openRegister() {
        wait.until(ExpectedConditions.elementToBeClickable(register)).click();
        return new NcdRegisterPage(driver).awaitLoaded();
    }
}
