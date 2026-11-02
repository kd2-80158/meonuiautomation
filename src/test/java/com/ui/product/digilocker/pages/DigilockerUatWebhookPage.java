package com.ui.product.digilocker.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Demo Capital DigiLocker UAT webhook settings; no document payloads are sent here. */
public final class DigilockerUatWebhookPage {
    public static final String SUPERADMIN = "https://digilocker-uat.meon.co.in/digilocker/superadmin";
    public static final String WEBHOOK = "https://digilocker-uat.meon.co.in/webhook-settings";
    private static final By GOOGLE = By.xpath("//*[contains(normalize-space(.),'Continue with Google') and (self::button or self::a or @onclick)][last()]");
    private static final By SEARCH = By.id("search__company");
    private static final By TENANT = By.xpath("//table[@id='company-table']//tr[.//td[contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'DEMO CAPITAL MARKET')]]");
    private static final By WEBHOOK_LINK = By.cssSelector("a[href='/webhook-settings']");
    private static final By URL = By.cssSelector("input[type='url']");
    private static final By ENABLED = By.cssSelector("input[type='checkbox']");
    private static final By SAVE = By.xpath("//button[normalize-space(.)='Save settings']");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DigilockerUatWebhookPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    public void openSuperadmin() { driver.get(SUPERADMIN); }
    public boolean needsGoogleSignIn() { return !driver.findElements(GOOGLE).isEmpty(); }
    public void clickGoogleSignIn() { wait.until(ExpectedConditions.elementToBeClickable(GOOGLE)).click(); }
    public void searchDemoCapital() {
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(SEARCH));
        search.clear();
        search.sendKeys("demo capital");
        wait.until(ExpectedConditions.visibilityOfElementLocated(TENANT));
    }
    public void visitAdmin() { wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(TENANT).findElement(By.xpath(".//button[contains(normalize-space(.),'Visit Admin')]")))).click(); }
    public void openFromSidebar() { wait.until(ExpectedConditions.elementToBeClickable(WEBHOOK_LINK)).click(); wait.until(ExpectedConditions.urlContains("/webhook-settings")); }
    public void awaitReady() { wait.until(ExpectedConditions.visibilityOfElementLocated(URL)); wait.until(ExpectedConditions.visibilityOfElementLocated(SAVE)); }
    public String urlValue() { return driver.findElement(URL).getAttribute("value"); }
    public boolean enabled() { return driver.findElement(ENABLED).isSelected(); }
    public void setUrl(String value) { WebElement field = wait.until(ExpectedConditions.elementToBeClickable(URL)); field.clear(); field.sendKeys(value); }
    public void setEnabled(boolean enabled) { if (enabled() != enabled) driver.findElement(ENABLED).findElement(By.xpath(".." )).click(); }
    public void save() { wait.until(ExpectedConditions.elementToBeClickable(SAVE)).click(); }
    public String validationMessage() { return driver.findElement(URL).getAttribute("validationMessage"); }
    public boolean successShown() { return driver.findElement(By.tagName("body")).getText().contains("Webhook settings updated successfully"); }
}
