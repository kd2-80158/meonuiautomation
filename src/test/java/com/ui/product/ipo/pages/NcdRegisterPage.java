package com.ui.product.ipo.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class NcdRegisterPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public NcdRegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public NcdRegisterPage awaitLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("register-title")));
        wait.until(d -> d.getCurrentUrl().contains("/v1/marfatia/register"));
        return this;
    }

    public boolean hasAllDetailsFields() {
        for (String id : new String[] {"name", "email", "number", "pan"}) {
            if (driver.findElements(By.id(id)).isEmpty()) return false;
        }
        return driver.findElements(By.cssSelector("input[name='cdslornsdl'][type='radio']")).size() == 2;
    }

    public boolean hasThreeSteps() {
        String html = driver.getPageSource();
        return html.contains("Your details") && html.contains("Verification") && html.contains("Demat account");
    }

    public String mobileMaxLength() {
        return driver.findElement(By.id("number")).getAttribute("maxlength");
    }

    public String panMaxLength() {
        return driver.findElement(By.id("pan")).getAttribute("maxlength");
    }

    public String emailType() {
        return driver.findElement(By.id("email")).getAttribute("type");
    }

    public boolean invalidEmailFailsBrowserValidation() {
        org.openqa.selenium.WebElement email = driver.findElement(By.id("email"));
        email.clear();
        email.sendKeys("not-an-email");
        return !(Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity()", email);
    }

    public boolean radioOptionsAreExclusive() {
        java.util.List<org.openqa.selenium.WebElement> options = driver.findElements(
                By.cssSelector("input[name='cdslornsdl'][type='radio']"));
        options.get(0).click();
        boolean firstOnly = options.get(0).isSelected() && !options.get(1).isSelected();
        options.get(1).click();
        return firstOnly && !options.get(0).isSelected() && options.get(1).isSelected();
    }

    public boolean hasTermsAndNavigationLinks() {
        return !driver.findElements(By.linkText("Terms and Conditions")).isEmpty()
                && !driver.findElements(By.linkText("Already registered? Sign in")).isEmpty()
                && !driver.findElements(By.linkText("Browse NCDs")).isEmpty();
    }
}
