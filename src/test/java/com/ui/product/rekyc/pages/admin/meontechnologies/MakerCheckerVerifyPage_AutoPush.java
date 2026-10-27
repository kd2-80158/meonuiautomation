package com.ui.product.rekyc.pages.admin.meontechnologies;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Auto Push's Verify-entry prerequisite, scoped to a named client row. */
public final class MakerCheckerVerifyPage_AutoPush {
	private final WebDriver driver;
	private final WebDriverWait wait;

	public MakerCheckerVerifyPage_AutoPush(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	}

	public void openModificationVerify() {
		new MakerCheckerPage_MeonTechnologies(driver).openMakerCheckerVerify().selectWorkflow("modification");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbody_maker_checker")));
	}

	private WebElement row(String clientCode) {
		By row = By.xpath("//*[@id='tbody_maker_checker']//tr[td[normalize-space()='" + clientCode + "']]");
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(row));
		} catch (TimeoutException missing) {
			String rows = driver.findElements(By.cssSelector("#tbody_maker_checker tr td:nth-child(2)")).stream()
					.limit(5).map(WebElement::getText)
					.collect(Collectors.joining(" | "));
			throw new IllegalStateException("Maker & Checker client " + clientCode
					+ " is not visible in the selected workflow. URL=" + driver.getCurrentUrl()
					+ ", visible client codes=" + rows, missing);
		}
	}

	public boolean isVerified(String clientCode) {
		WebElement clientRow = row(clientCode);
		List<WebElement> verified = clientRow.findElements(By.cssSelector(".maker-nonverify-btn"));
		if (!verified.isEmpty()) scrollHorizontallyTo(verified.get(0));
		return !verified.isEmpty();
	}

	public void markVerifiedIfNeeded(String clientCode) {
		if (isVerified(clientCode)) return;
		WebElement action = row(clientCode).findElement(By.cssSelector(".maker-verification-action"));
		if (!action.getText().toLowerCase().contains("mark verified")) {
			throw new IllegalStateException("Verify action is unavailable for " + clientCode);
		}
		scrollHorizontallyTo(action);
		wait.until(ExpectedConditions.elementToBeClickable(action)).click();
		try {
			new WebDriverWait(driver, Duration.ofSeconds(2))
					.until(ExpectedConditions.alertIsPresent()).accept();
		} catch (org.openqa.selenium.TimeoutException noConfirmation) {
			// This workflow may update verification without a browser alert.
		}
		wait.ignoring(StaleElementReferenceException.class).until(driver -> isVerified(clientCode));
	}

	public List<String> availablePushButtons(String clientCode) {
		List<WebElement> buttons = row(clientCode).findElements(By.cssSelector("td button"));
		if (!buttons.isEmpty()) scrollHorizontallyTo(buttons.get(buttons.size() - 1));
		return buttons.stream()
				.map(WebElement::getText).map(String::trim).filter(text -> !text.isEmpty())
				.collect(Collectors.toList());
	}

	private void scrollHorizontallyTo(WebElement element) {
		((JavascriptExecutor) driver).executeScript(
				"var e=arguments[0], p=e.parentElement;"
				+ "while(p && p!==document.body){if(p.scrollWidth>p.clientWidth+4){"
				+ "p.scrollLeft += e.getBoundingClientRect().left-p.getBoundingClientRect().left-p.clientWidth/2;break;}p=p.parentElement;}"
				+ "e.scrollIntoView({block:'center',inline:'center'});", element);
	}
}
