package com.ui.kyc.pages.superadmin;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class AdminUsersPage_KYCSuperadmin extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	public AdminUsersPage_KYCSuperadmin(WebDriver driver) {
		super(driver);
	}

	private static final By ADDUSER_BUTTON = By.xpath("//div[@class='add-user-container']");
	private static final By HEADING_ADMINUSERPAGE = By.xpath("//div[@class='company-text']");
	// dialog box locators
	private static final By HEADING_USERDIALOGBOX = By.id("overlay_add_user_text");
	private static final By USERNAME_DROPDOWN = By.id("username_btn");
	private static final By ACCESSGRANTED_DROPDOWN = By.xpath("(//button[@class='btn user-access-dropdown'])[1]");
	private static final By USERACTIVE_DROPDOWN = By.xpath("(//button[@class='btn user-access-dropdown'])[2]");
	private static final By COMPANYSELECTION_DROPDOWN = By.xpath("//div[@id='multi']");
	private static final By SAVE_BUTTON = By.xpath("//button[@class='save-button']");
	private static final By OK_BUTTON = By.xpath("(//button[contains(normalize-space(), 'OK')])[2]");
	private static final By SUCCESSMESSAGE_ADDUSER = By
			.xpath("//h4[contains(normalize-space(), 'Successfully Saved !')]");
	private static final By SEARCHBAR_ADMINUSERPAGE = By.xpath("//input[@id='search__user']");
	private static final By REMOVEUSER_BUTTON = By.xpath("//button[@id='openConfirmationOverlay']");
	private static final By HEADINGFORDELETION_USERDIALOGBOX = By
			.xpath("(//div[@class='confirmation-container'])[1]/div[1]");
	private static final By USERNAME_OPTIONS = By.cssSelector("li");
	private static final By ACCESSGRANTED_OPTION = By
			.xpath("(//a[contains(text(),'User') and @class='dropdown-item'])[1]");
	private static final By USERACTIVE_OPTION = By
			.xpath("(//a[contains(text(),'True') and @class='dropdown-item'])[1]");
	private static final By COMPANYSELECTION_OPTION = By.xpath("//span[contains(text(),'sun') and @class='text']");
    private static final By SUCCESSMSG_DELETION = By.xpath("(//div[contains(normalize-space(),'Deleted')])[8]");
	
    public AdminUsersPage_KYCSuperadmin clickOnButton(String text) {
		if ("user_subhi".equalsIgnoreCase(text)) {
			return selectUsername(System.getProperty("kyc.superadmin.testUsername", "Shubhi"));
		}
		By locator;
		switch (text.toLowerCase()) {
		case "adduser_button":
			locator = ADDUSER_BUTTON;
			break;

		case "save_button":
			locator = SAVE_BUTTON;
			break;

		case "ok_button":
			locator = OK_BUTTON;
			break;

		case "searchbar":
			locator = SEARCHBAR_ADMINUSERPAGE;
			break;

		case "removeuser":
			locator = REMOVEUSER_BUTTON;

		case "accessgrant_subhi":
			locator = ACCESSGRANTED_OPTION;
			break;

		case "useractive":
			locator = USERACTIVE_OPTION;
			break;

		case "companyselection":
			locator = COMPANYSELECTION_OPTION;
			break;

		case "dialogbox_heading":
			locator = HEADING_USERDIALOGBOX;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));
		button.click();
		return this;
	}

	/** Select an exact visible dropdown option, including text nested inside the li. */
	public AdminUsersPage_KYCSuperadmin selectUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("KYC superadmin test username must not be blank");
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		List<WebElement> matches = visibleUsernameOptions(username);
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(USERNAME_DROPDOWN));
		if (matches.isEmpty() && !"true".equalsIgnoreCase(dropdown.getAttribute("aria-expanded"))) {
			dropdown.click();
		}
		try {
			WebElement option = wait.until(driver -> {
				List<WebElement> visible = visibleUsernameOptions(username);
				return visible.isEmpty() ? null : visible.get(0);
			});
			wait.until(ExpectedConditions.elementToBeClickable(option)).click();
		} catch (org.openqa.selenium.TimeoutException e) {
			int visibleOptionCount = getDriver().findElements(USERNAME_OPTIONS).stream()
					.filter(WebElement::isDisplayed).collect(Collectors.toList()).size();
			throw new AssertionError("Username '" + username + "' was not available in the open dropdown ("
					+ visibleOptionCount + " visible options). Check the test fixture or set "
					+ "-Dkyc.superadmin.testUsername to an available user.", e);
		}
		return this;
	}

	private List<WebElement> visibleUsernameOptions(String username) {
		return getDriver().findElements(USERNAME_OPTIONS).stream()
				.filter(option -> {
					try {
						return option.isDisplayed() && username.equals(option.getText().trim());
					} catch (org.openqa.selenium.StaleElementReferenceException ignored) {
						return false;
					}
				})
				.collect(Collectors.toList());
	}

	public AdminUsersPage_KYCSuperadmin writeService(String text, String input) {
		By locator;
		switch (text.toLowerCase()) {
		case "searchbar":
			locator = SEARCHBAR_ADMINUSERPAGE;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement sendtext = wait.until(ExpectedConditions.elementToBeClickable(locator));
		sendtext.clear();
		sendtext.sendKeys(input);
		return this;
	}

	public AdminUsersPage_KYCSuperadmin clickOnSaveButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(SAVE_BUTTON));
		((org.openqa.selenium.JavascriptExecutor) getDriver())
				.executeScript("arguments[0].scrollIntoView({block:'center'});", saveButton);
		wait.until(ExpectedConditions.elementToBeClickable(saveButton));
		try {
			saveButton.click();
		} catch (org.openqa.selenium.ElementClickInterceptedException e) {
			logger.warn("Normal click intercepted for Save button. Trying JavaScript click.");
			((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", saveButton);
		}
		return this;
	}

	public boolean isHeadingVisible(String text) {
		By locator;

		switch (text.toLowerCase()) {

		case "admin_heading":
			locator = HEADING_ADMINUSERPAGE;
			break;

		case "dialogbox_heading":
			locator = HEADING_USERDIALOGBOX;
			break;

		case "successmsg":
			locator = SUCCESSMESSAGE_ADDUSER;
			break;
			
		case "deleteuser_successmsg":
			locator = SUCCESSMSG_DELETION;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		if (element.isDisplayed())
			return true;
		return false;
	}

	public AdminUsersPage_KYCSuperadmin clickRemoveUser(String userCode) {

		By removeUserLocator = By.xpath("/html/body/div/div/div[2]/table/tbody/tr[157]/td[6]/span");
		By removeLocator = By.xpath("/html/body/div/div/div[2]/table/tbody/tr[157]/td[6]/div/div[2]");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(removeUserLocator));
		removeButton.click();
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(removeLocator));
		option.click();
		WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(REMOVEUSER_BUTTON));
		confirmBtn.click();
		return this;
	}

	public String getActualText(String text) {
		By locator;
		switch (text.toLowerCase()) {

		case "successheadingdeletion_adduser":
			locator = HEADINGFORDELETION_USERDIALOGBOX;
			break;

		default:
			throw new IllegalArgumentException("Invalid error message locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		logger.info("The element value is: " + element.getText());
		System.out.println("The element value is: " + element.getText());
		return element.getText();
	}

	public AdminUsersPage_KYCSuperadmin clickDropdown(String text) {
		By locator = null;
		switch (text.toLowerCase()) {
		case "username":
			locator = USERNAME_DROPDOWN;
			break;
		case "accessgranted":
			locator = ACCESSGRANTED_DROPDOWN;
			break;
		case "useractive":
			locator = USERACTIVE_DROPDOWN;
			break;
		case "companyselection":
			locator = COMPANYSELECTION_DROPDOWN;
			break;
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(locator));
		dropdown.click();
		return this;
	}

}
