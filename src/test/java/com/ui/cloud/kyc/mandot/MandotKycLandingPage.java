package com.ui.cloud.kyc.mandot;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

/** Page contract for the public Mandot individual KYC entry screen. */
public class MandotKycLandingPage extends BrowserUtility {

    private static final By SIGNUP_HEADING = By.xpath("//h3[normalize-space()='Signup Now']");
    private static final By MOBILE_NUMBER = By.id("mobile_number");
    private static final By DEPENDENCY = By.id("select_dependancy");
    private static final By CAPTCHA = By.id("captcha_input");
    private static final By TERMS = By.id("exampleCheck1");
    private static final By SUBMIT = By.id("SaveData");
    private static final By REQUIRED_DOCUMENTS = By.cssSelector("ol li");

    public MandotKycLandingPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_HEADING)).isDisplayed();
    }

    public boolean hasMobileEntryControls() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(MOBILE_NUMBER)).isDisplayed()
                && getDriver().findElement(DEPENDENCY).isDisplayed()
                && getDriver().findElement(CAPTCHA).isDisplayed()
                && getDriver().findElement(TERMS).isDisplayed();
    }

    public boolean hasEnabledSubmitControl() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(SUBMIT)).isEnabled();
    }

    public List<String> requiredDocuments() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(REQUIRED_DOCUMENTS)).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
