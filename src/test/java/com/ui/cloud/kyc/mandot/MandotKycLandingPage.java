package com.ui.cloud.kyc.mandot;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ui.utilities.BrowserUtility;

/** Page contract for the public Mandot individual KYC entry screen. */
public class MandotKycLandingPage extends BrowserUtility {

    private static final By SIGNUP_HEADING = By.xpath("//h3[normalize-space()='Signup Now']");
    private static final By APPLICATION_INPUT = By.cssSelector("input#dummyfield");
    private static final By SUBMIT = By.id("SaveData");
    private static final By REQUIRED_DOCUMENTS = By.cssSelector("ol li");

    public MandotKycLandingPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_HEADING)).isDisplayed();
    }

    public boolean hasApplicationInput() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(APPLICATION_INPUT)).isDisplayed();
    }

    public boolean hasEnabledSubmitControl() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(SUBMIT)).isEnabled();
    }

    public List<String> requiredDocuments() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(REQUIRED_DOCUMENTS)).stream()
                .map(WebElement::getText)
                .toList();
    }
}
