package com.ui.cloud.apimanager.admin;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.ui.base.BaseTest;
import com.ui.product.apimanager.pages.LoginPage_APIManager;

/** Authenticated fixture; page tests never store credentials in source or reports. */
public abstract class ApiManagerAuthenticatedTest extends BaseTest {
    protected final String baseUrl = System.getProperty("apiManager.baseUrl", "https://api.meon.co.in");

    @BeforeClass(alwaysRun = true)
    public void signInToApiManager() {
        String email = System.getProperty("apiManager.email", "test@meon.co.in");
        String password = System.getProperty("apiManager.password", "test@1234");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new SkipException("Set API_MANAGER_EMAIL and API_MANAGER_PASSWORD");
        }
        setupBrowser();
        driver.get(baseUrl + "/login");
        if (!driver.getCurrentUrl().contains("/dashboard")) {
            new LoginPage_APIManager(driver).writeEmail(email).writePassword(password).clickOnSignInButton();
        }
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.urlContains("/dashboard"));
    }

    @AfterClass(alwaysRun = true)
    public void closeApiManagerBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
