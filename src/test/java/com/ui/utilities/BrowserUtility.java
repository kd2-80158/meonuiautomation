package com.ui.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

@Listeners({com.ui.listeners.TestListener.class})
public abstract class BrowserUtility{

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	protected WebDriver getDriver() {
	    WebDriver wd =driver.get();
	    if (wd == null) {
	        throw new IllegalStateException("WebDriver is NULL. BrowserUtility was not initialized correctly.");
	    }
	    return wd;
	}

	public BrowserUtility(WebDriver driver) {
		super();
		BrowserUtility.driver.set(driver);
	}
	
	//for api manager
	public static void doLogout(WebDriver driver)
	{
		By USER_AVATAR_LOCATOR = By.xpath("//button[contains(text(),'Test User')]");
		By LOGOUT_BUTTON_LOCATOR = By.xpath("//div[contains(text(),' Logout')]");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		WebElement avatar  = wait.until(ExpectedConditions.visibilityOfElementLocated(USER_AVATAR_LOCATOR));
		avatar.click();
		WebElement logoutBtn  = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_BUTTON_LOCATOR));
	    logoutBtn.click();
	}


	public static String takeScreenShot(String name) {
		TakesScreenshot screenshot = (TakesScreenshot) driver.get();
		File screenshotData = screenshot.getScreenshotAs(OutputType.FILE);
		// timestamp--to differentiate btw screenshot
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStamp = format.format(date);
		// removing absolute path and using relative path
		String path = "./screenshots/" + name + " - " + timeStamp + ".png";
		File screenshotFile = new File(path);
		try {
			FileUtils.copyFile(screenshotData, screenshotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return path;
	}
	
	public String takeElementScreenshot(WebElement element, String name) {
        File screenshotData = element.getScreenshotAs(OutputType.FILE);
        
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(date);
        
        String path = "./screenshots/" + name + "_Element_" + timeStamp + ".png";
        File screenshotFile = new File(path);
        try {
            FileUtils.copyFile(screenshotData, screenshotFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    /**
     * Captures a full-page screenshot as a Base64 string (useful for ExtentReports).
     */
    public String takeBase64Screenshot() {
        TakesScreenshot screenshot = (TakesScreenshot) getDriver();
        return screenshot.getScreenshotAs(OutputType.BASE64);
    }

	public void enterText(By locator, String textToEnter) {
	    WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
	    WebElement element = wait.until(
	            ExpectedConditions.presenceOfElementLocated(locator));
	    String type = element.getAttribute("type");
	    if ("file".equalsIgnoreCase(type)) {
	        element.sendKeys(textToEnter);   // works even if hidden
	    } else {
	        element.clear();
	        element.sendKeys(textToEnter);
	    }
	}
	
	public void waitForOTPToBeEntered(By locator) {
	    new WebDriverWait(getDriver(), Duration.ofMinutes(2)) // enough time for human
	        .until(driver -> {
	            WebElement el = driver.findElement(locator);
	            String value = el.getAttribute("value");
	            return value != null && !value.trim().isEmpty();
	        });
	}
	
	//to click on any option
	public BrowserUtility clickOnOption(By locator, int time)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(time));
		WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		option.click();
		return this;
	}
	
	public BrowserUtility scrollIntoView(By locator) {
	    WebElement el = new WebDriverWait(getDriver(), Duration.ofSeconds(10))
	            .until(ExpectedConditions.visibilityOfElementLocated(locator));

	    ((JavascriptExecutor) getDriver())
	            .executeScript("arguments[0].scrollIntoView({block:'center'});", el);

	    return this;
	}

	
	public BrowserUtility jsClick(By locator) {
	    WebElement el = new WebDriverWait(getDriver(), Duration.ofSeconds(10))
	            .until(ExpectedConditions.visibilityOfElementLocated(locator));

	    ((JavascriptExecutor) getDriver())
	            .executeScript("arguments[0].click();", el);

	    return this;
	}

	
	public BrowserUtility clickDropdown(By locator) {
	    WebElement dropdown = new WebDriverWait(getDriver(), Duration.ofSeconds(10))
	            .until(ExpectedConditions.elementToBeClickable(locator));
	    dropdown.click();
	    return this;
	}
	
	public BrowserUtility waitForProductsDropdown(By locator)
	{
		Actions actions = new Actions(getDriver());
		WebElement e = new WebDriverWait(getDriver(), Duration.ofSeconds(10))
				           .until(ExpectedConditions.elementToBeClickable(locator));
		actions.moveToElement(e).build().perform();;
		return this;
	}
	
//	public BrowserUtility waitForProductsDropdown() {
//	    new WebDriverWait(getDriver(), Duration.ofSeconds(10))
//	            .until(ExpectedConditions.visibilityOfElementLocated(
//	                    By.cssSelector("div.dropdown-menu-product")
//	            ));
//	    return this;
//	}


}
