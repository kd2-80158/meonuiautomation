package com.ui.product.developer.pages;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Developer-portal navigation and the My Credentials SSO entry point. */
public final class DeveloperPortalPage {
    public static final String URL = "https://developer.meon.co.in/new/";
    private static final By PRODUCTS = By.cssSelector("a[href^='/new/products/']");
    private static final By CREDENTIALS = By.xpath("//button[normalize-space()='My Credentials']");
    private static final By GOOGLE_LOGIN = By.cssSelector("button.googleSignINbtn");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DeveloperPortalPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    public void open() { driver.get(URL); }

    public List<String> sidebarProducts() {
        List<String> names = new ArrayList<>();
        for (WebElement link : wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PRODUCTS))) {
            String href = link.getAttribute("href");
            String slug = href.substring(href.lastIndexOf('/') + 1);
            names.add(URLDecoder.decode(slug, StandardCharsets.UTF_8));
        }
        return names;
    }

    public void selectProduct(String product) {
        By link = By.xpath("//a[@href='/new/products/" + product + "']");
        wait.until(ExpectedConditions.elementToBeClickable(link)).click();
        wait.until(d -> d.getCurrentUrl().contains("/new/products/"));
    }

    public boolean credentialsVisible() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> d.findElements(CREDENTIALS).stream().anyMatch(WebElement::isDisplayed));
        } catch (org.openqa.selenium.TimeoutException missing) {
            return false;
        }
    }

    public void clickMyCredentials() {
        wait.until(ExpectedConditions.elementToBeClickable(CREDENTIALS)).click();
    }

    public boolean googleLoginVisible() {
        try {
            return driver.findElements(GOOGLE_LOGIN).stream().anyMatch(WebElement::isDisplayed);
        } catch (StaleElementReferenceException rerendering) {
            return false;
        }
    }

    public void clickGoogleLogin() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_LOGIN));
        try {
            button.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException rerendered) {
            button = wait.until(ExpectedConditions.elementToBeClickable(GOOGLE_LOGIN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", button);
        }
    }
}
