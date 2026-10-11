package com.ui.pages.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import com.ui.utilities.BrowserUtility;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class AdminSignInPage extends BrowserUtility {
	
	public AdminSignInPage(WebDriver driver) {
		super(driver);
	}

	private static final By WORKFLOW_ICON =  By.xpath("//span[normalize-space()='workflow']");
	private static final By WORKFLOW_IFRAME =  By.id("iframeworkflow");
	private static final By TABLE = By.tagName("table");
	
	public AdminSignInPage goToWorkflow() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement workflowBtn = wait
				.until(ExpectedConditions.elementToBeClickable(WORKFLOW_ICON));
		workflowBtn.click();
		return this;
	}

	private void switchToWorkflowIframe() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(WORKFLOW_IFRAME));
	}

	public AdminSignInPage searchWorkFlow(String workflowName) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(40));
		switchToWorkflowIframe();
		wait.until(ExpectedConditions.presenceOfElementLocated(TABLE));
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
		WebElement input = wait.until(driver -> (WebElement) ((JavascriptExecutor) driver)
				.executeScript("return document.querySelector('input.MuiInputBase-input:not([disabled])');"));
		if (input == null) {
			throw new RuntimeException("Search input not found inside iframe");
		}
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
		input.click();
		input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		input.sendKeys(Keys.DELETE);
		new Actions(getDriver())
		.pause(Duration.ofMillis(300))
		.sendKeys(input, workflowName)
		.pause(Duration.ofMillis(300))
		.sendKeys(Keys.ENTER).perform();
		return this;
	}

	public AdminSignInPage clickTestWorkflow(String workflowName) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		By testWorkflowBtn = By.xpath("//tr[.//td[normalize-space()='" + workflowName + "']]"
				+ "//button[normalize-space()='Test Workflow']");
		WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(testWorkflowBtn));
		btn.click();

		return this;
	}

	public AdminSignInPage doAdminLogin() {
		getDriver().get("https://live.meon.co.in/admin/democapital");
		WebElement userId = new WebDriverWait(getDriver(), Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
		userId.clear();
		userId.sendKeys("saurabh@meon.co.in");
		WebElement password = getDriver().findElement(By.id("password-input"));
		password.clear();
		password.sendKeys(JSONUtility.getAdmin().getAdminurl());
		getDriver().findElement(By.xpath("//button[@type='submit']")).click();
		return this;
	}
	
//	public void clickOnTest
	

}
