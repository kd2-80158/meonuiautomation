package com.ui.kyc.pages.superadmin;

import java.time.Duration;

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
public class AdminUserPage_DemoCapital extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	public AdminUserPage_DemoCapital(WebDriver driver) {
		super(driver);
	}

	private static final By ADDUSER_BUTTON = By.xpath("//div[@class='add-user-container']");
	private static final By NAMEINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL = By.xpath("(//input[@placeholder='Name'])[1]");
	private static final By MOBILEINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL = By.xpath("(//input[@placeholder='Mobile'])[1]");
	private static final By EMAILADDRESSINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL = By
			.xpath("(//input[@placeholder='Email address'])[1]");
	private static final By PASSWORDINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL = By
			.xpath("//input[@id='companyPassword' and @class='overlay-input-fields']");
	private static final By PERMISSIONDROPDOWN_ADMINUSERPAGE_DEMOCAPITAL = By
			.xpath("//select[@class='overlay-input-fields' and @id='select__permission']");
	private static final By OPTION_PERMISSIONDROPDOWN_ADMINUSERPAGE_DEMOCAPITAL = By
			.xpath("(//option[@value='User'])[1]");
	private static final By SAVEBUTTON_ADMINUSERPAGE_DEMOCAPITAL = By.xpath("(//button[@class='save-button'])[1]");
	private static final By CLOSE_ADDUSER_DIALOGBOX = By.cssSelector("#overlay .close-btn");

	private static final By HEADING_ADDUSER_DIALOGBOX_DEMOCAPITAL = By.id("overlay_add_user_text");
	private static final By HEADING_ADMINPAGE_DEMOCAPITAL = By.xpath("//div[text()='Admin Users']");
	private static final By HEADING_DELETE_DIALOGBOX_DEMOCAPITAL = By.xpath("//div[normalize-space()='Deleted']");
	private static final By HEADING_SUCCESSMSGFORUSER_DIALOGBOX_DEMOCAPITAL = By.id("success__msg");

	private static final By SEARCHBAR_ADMINPAGE_DEMOCAPITAL = By.id("userdetailsSearch");
	private static final By ACTION_MENU_ADMINPAGE_DEMOCAPITAL = By
			.cssSelector("#user-table tbody tr .mdi-dots-vertical");
	private static final By REMOVEUSER_OPTION_ADMINPAGE_DEMOCAPITAL = By.xpath("(//div[text()='Remove'])[1]");
	private static final By CONFIRM_BUTTON_DIALOGBOX_ADMINPAGE_DEMOCAPITAL = By
			.xpath("//button[@id='openConfirmationOverlay']");
	private static final By USER_SEARCHBAR = By.id("userdetailsSearch");
	private static final By USER_TABLE = By.id("user-table");
	private static final By ROW_ACTION_MENU = By.cssSelector("td:last-child .mdi-dots-vertical");
	private static final By ROW_REMOVE_OPTION = By.cssSelector("td:last-child .overlay .option[onclick^='removeUser(']");

	private static final By SAVE_OVERLAY = By.id("overlay-save");
	private static final By ADD_USER_OVERLAY = By.id("overlay");
	private static final By LOADING_OVERLAY = By.cssSelector("div.overlay-bg");

	public AdminUserPage_DemoCapital clickOn(String text) {
		By locator;
		switch (text.toLowerCase()) {
		case "action_menu":
			locator = ACTION_MENU_ADMINPAGE_DEMOCAPITAL;
			break;

		case "remove":
			locator = REMOVEUSER_OPTION_ADMINPAGE_DEMOCAPITAL;
			break;

		case "confirm":
			locator = CONFIRM_BUTTON_DIALOGBOX_ADMINPAGE_DEMOCAPITAL;
			break;

		case "add_user":
			locator = ADDUSER_BUTTON;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);

		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		if (text.equalsIgnoreCase("action_menu")) {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
		}
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(locator));
		option.click();
		return this;
	}

	public AdminUserPage_DemoCapital writeService(String text, String input) {
		By locator;
		switch (text.toLowerCase()) {
		case "name":
			locator = NAMEINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL;
			break;

		case "mobile":
			locator = MOBILEINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL;
			break;

		case "email_address":
			locator = EMAILADDRESSINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL;
			break;

		case "password":
			locator = PASSWORDINPUTBOX_ADMINUSERPAGE_DEMOCAPITAL;
			break;

		case "searchbar":
			locator = SEARCHBAR_ADMINPAGE_DEMOCAPITAL;
			break;

		default:
			throw new IllegalArgumentException("Invalid button locator key: " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement sendtext = wait.until(ExpectedConditions.elementToBeClickable(locator));
		sendtext.click();
		sendtext.clear();
		sendtext.sendKeys(input);
		return this;
	}

	public AdminUserPage_DemoCapital clickOnSaveButton() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		WebElement saveButton = wait
				.until(ExpectedConditions.visibilityOfElementLocated(SAVEBUTTON_ADMINUSERPAGE_DEMOCAPITAL));
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

	public AdminUserPage_DemoCapital openAddUserFormWithoutModules() {
		// The application initializes its empty module arrays only when closeOverlay runs.
		// Reopening the form reproduces the valid manual flow for a user with no modules.
		clickOn("add_user");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
		wait.until(ExpectedConditions.elementToBeClickable(CLOSE_ADDUSER_DIALOGBOX)).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(CLOSE_ADDUSER_DIALOGBOX));
		return clickOn("add_user");
	}

	public boolean isHeadingVisible(String text) {
		By locator;

		switch (text.toLowerCase()) {

		case "admin_heading":
			locator = HEADING_ADMINPAGE_DEMOCAPITAL;
			break;

		case "dialogbox_heading":
			locator = HEADING_ADDUSER_DIALOGBOX_DEMOCAPITAL;
			break;

		case "successmsg":
			locator = HEADING_SUCCESSMSGFORUSER_DIALOGBOX_DEMOCAPITAL;
			break;

		case "deleteuser_successmsg":
			locator = HEADING_DELETE_DIALOGBOX_DEMOCAPITAL;
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

	public String getActualText(String text) {
		By locator;
		switch (text.toLowerCase()) {

		case "successheadingdeletion_adduser":
			locator = HEADING_DELETE_DIALOGBOX_DEMOCAPITAL;
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

	public AdminUserPage_DemoCapital clickDropdown(String text) {
		By locator = null;
		switch (text.toLowerCase()) {
		case "permission":
			locator = PERMISSIONDROPDOWN_ADMINUSERPAGE_DEMOCAPITAL;
			break;
		case "option_user":
			locator = OPTION_PERMISSIONDROPDOWN_ADMINUSERPAGE_DEMOCAPITAL;
			break;
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(locator));
		dropdown.click();
		return this;
	}

	public AdminUserPage_DemoCapital writeOnUserSearchBar(String value) {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		wait.until(ExpectedConditions.invisibilityOfElementLocated(SAVE_OVERLAY));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(ADD_USER_OVERLAY));

		WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(USER_SEARCHBAR));

		searchBar.click();
		searchBar.clear();
		searchBar.sendKeys(value);

		return this;
	}

	/** Search by the unique mobile and confirm all identity fields before any delete action. */
	public AdminUserPage_DemoCapital findCreatedAdminUser(String name, String email, String mobile) {
		if (name == null || name.isBlank() || email == null || email.isBlank()
				|| mobile == null || mobile.isBlank()) {
			throw new IllegalArgumentException("Created user name, email and mobile are required for lookup");
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		writeOnUserSearchBar(mobile);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
		wait.until(driver -> findExactAdminUserRow(name, email, mobile) != null);
		return this;
	}

	/** Delete only the exact user previously confirmed in the filtered table. */
	public AdminUserPage_DemoCapital deleteCreatedAdminUser(String name, String email, String mobile) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		wait.until(driver -> findExactAdminUserRow(name, email, mobile) != null);
		clickActionInExactRow(wait, name, email, mobile, ROW_ACTION_MENU);
		clickActionInExactRow(wait, name, email, mobile, ROW_REMOVE_OPTION);
		wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_BUTTON_DIALOGBOX_ADMINPAGE_DEMOCAPITAL)).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(HEADING_DELETE_DIALOGBOX_DEMOCAPITAL));
		wait.until(driver -> findExactAdminUserRow(name, email, mobile) == null);
		return this;
	}

	private void clickActionInExactRow(WebDriverWait wait, String name, String email, String mobile, By action) {
		wait.until(driver -> {
			try {
				WebElement row = findExactAdminUserRow(name, email, mobile);
				if (row == null) return false;
				WebElement button = row.findElement(action);
				if (!button.isDisplayed() || !button.isEnabled()) return false;
				button.click();
				return true;
			} catch (org.openqa.selenium.StaleElementReferenceException
					| org.openqa.selenium.ElementClickInterceptedException
					| org.openqa.selenium.NoSuchElementException ignored) {
				// Filtering repaints the table; reacquire the row and action on the next poll.
				return false;
			}
		});
	}

	private WebElement findExactAdminUserRow(String name, String email, String mobile) {
		WebElement table = getDriver().findElement(USER_TABLE);
		for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
			try {
				java.util.List<WebElement> cells = row.findElements(By.tagName("td"));
				boolean nameMatches = cells.stream().anyMatch(cell -> name.equalsIgnoreCase(cell.getText().trim()));
				boolean emailMatches = cells.stream().anyMatch(cell -> email.equalsIgnoreCase(cell.getText().trim()));
				boolean mobileMatches = cells.stream().anyMatch(cell -> mobile.equals(cell.getText().trim()));
				if (nameMatches && emailMatches && mobileMatches) {
					return row;
				}
			} catch (org.openqa.selenium.StaleElementReferenceException ignored) {
				// The table can repaint after filtering; WebDriverWait will retry.
			}
		}
		return null;
	}

}
