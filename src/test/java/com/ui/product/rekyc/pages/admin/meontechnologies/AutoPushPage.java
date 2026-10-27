package com.ui.product.rekyc.pages.admin.meontechnologies;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for the three Auto Push panels. All selectors are scoped to the
 * UAT page's stable IDs; test assertions and test data stay in the test class.
 */
public final class AutoPushPage {
	public static final String PATH = "/v1/company/auto_push";
	private final WebDriver driver;
	private final WebDriverWait wait;

	public AutoPushPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	}

	private WebElement visible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private void click(By locator) {
		try {
		wait.until(driver -> {
			try {
				WebElement element = ExpectedConditions.elementToBeClickable(locator).apply(driver);
				if (element == null) return false;
				((JavascriptExecutor) driver).executeScript(
						"var e=arguments[0],p=e.parentElement;while(p&&p!==document.body){"
						+ "if(p.scrollWidth>p.clientWidth+4){p.scrollLeft+="
						+ "e.getBoundingClientRect().left-p.getBoundingClientRect().left-p.clientWidth/2;break;}"
						+ "p=p.parentElement;}e.scrollIntoView({block:'center',inline:'center'});", element);
				element.click();
				return true;
			} catch (StaleElementReferenceException | ElementClickInterceptedException notReady) {
				return false;
			}
		});
		} catch (TimeoutException blocked) {
			if (driver.getCurrentUrl().matches(".*/v1/company/+admin(?:[/?#].*)?")) {
				throw new IllegalStateException("ReKYC admin authentication is required: Auto Push redirected to "
						+ driver.getCurrentUrl() + ". Sign in within the WebDriver session before running Auto Push tests.", blocked);
			}
			String obstruction = String.valueOf(((JavascriptExecutor) driver).executeScript(
					"var e=document.querySelector('#apWorkflowTrigger'); if(!e)return 'trigger absent';"
					+ "var r=e.getBoundingClientRect(), p=document.elementFromPoint(r.left+r.width/2,r.top+r.height/2);"
					+ "return p ? p.tagName.toLowerCase()+'#'+(p.id||'')+'.'+(p.className||'') : 'none';"));
			throw new IllegalStateException("Auto Push control " + locator + " is blocked at "
					+ driver.getCurrentUrl() + "; element over workflow selector: " + obstruction, blocked);
		}
	}

	public void open(String baseUrl) {
		driver.get(baseUrl + PATH);
		visible(By.id("apWorkflowTrigger"));
	}

	public void chooseWorkflow(String workflowName) {
		String current = visible(By.id("apWorkflowSelectedName")).getText().trim();
		if (current.equalsIgnoreCase(workflowName)) return;
		click(By.id("apWorkflowTrigger"));
		WebElement search = visible(By.id("apWorkflowSearch"));
		search.clear();
		search.sendKeys(workflowName);
		By option = By.xpath("//*[@id='apWorkflowOptions']//*[normalize-space()='" + workflowName + "']");
		click(option);
		wait.until(d -> d.findElement(By.id("apWorkflowTrigger")).getText().contains(workflowName));
	}

	public String selectedWorkflow() {
		return visible(By.id("apWorkflowTrigger")).getText();
	}

	public String selectedWorkflowId() {
		java.util.regex.Matcher uuid = java.util.regex.Pattern
				.compile("[0-9a-fA-F]{8}-(?:[0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}")
				.matcher(selectedWorkflow());
		if (!uuid.find()) throw new IllegalStateException("Selected workflow has no visible identifier");
		return uuid.group();
	}

	public void searchWorkflow(String text) {
		click(By.id("apWorkflowTrigger"));
		WebElement input = visible(By.id("apWorkflowSearch"));
		input.clear();
		input.sendKeys(text);
	}

	public boolean workflowOptionVisible(String name) {
		return !driver.findElements(By.xpath("//*[@id='apWorkflowOptions']//*[normalize-space()='" + name + "']")).isEmpty();
	}

	public int workflowOptionCount() {
		return driver.findElements(By.cssSelector("#apWorkflowOptions > *")).size();
	}

	public boolean hasSidebarLink() {
		return !driver.findElements(By.cssSelector("a[href='" + PATH + "']")).isEmpty();
	}

	public boolean hasAllPanelTabs() {
		return driver.findElements(By.cssSelector(".ap-tab[data-panel]")).size() == 3;
	}

	public void configuration() {
		click(By.cssSelector(".ap-tab[data-panel='configuration']"));
	}

	public void entries() {
		click(By.cssSelector(".ap-tab[data-panel='entries']"));
	}

	public void monitor() {
		click(By.cssSelector(".ap-tab[data-panel='batches']"));
	}

	public boolean liveStatusActionAvailable() {
		return !driver.findElements(By.id("apLiveStatusAction")).isEmpty()
				&& driver.findElement(By.id("apLiveStatusAction")).isDisplayed();
	}

	public void viewLiveActivity() {
		click(By.id("apLiveStatusAction"));
	}

	public boolean configurationVisible() {
		return visible(By.id("apPanelConfiguration")).isDisplayed();
	}

	public boolean entriesVisible() {
		return visible(By.id("apPanelEntries")).isDisplayed();
	}

	public boolean monitorVisible() {
		return visible(By.id("apPanelBatches")).isDisplayed();
	}

	public String configStartMode() {
		return new Select(visible(By.id("apConfigStartMode"))).getFirstSelectedOption().getText().trim();
	}

	public List<String> configStartModes() {
		return new Select(visible(By.id("apConfigStartMode"))).getOptions().stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public boolean automaticPushEnabled() { return visible(By.id("apEnabled")).isSelected(); }
	public void setAutomaticPushEnabled(boolean enabled) {
		WebElement box = visible(By.id("apEnabled"));
		if (box.isSelected() != enabled) box.click();
	}
	public boolean autoSelectEnabled() { return visible(By.id("apAutoSelect")).isSelected(); }
	public void setAutoSelectEnabled(boolean enabled) {
		WebElement box = visible(By.id("apAutoSelect"));
		if (box.isSelected() != enabled) box.click();
	}

	public boolean delayFieldHidden() {
		return driver.findElements(By.id("apConfigDelaySeconds")).isEmpty()
				|| !driver.findElement(By.id("apConfigDelaySeconds")).isDisplayed();
	}

	public void setConfigDelaySeconds(String seconds) {
		WebElement input = visible(By.id("apConfigDelaySeconds"));
		input.clear();
		input.sendKeys(seconds);
	}

	public void setConfigWindow(String start, String end) {
		WebElement from = visible(By.id("apWindowStart"));
		WebElement to = visible(By.id("apWindowEnd"));
		from.clear(); from.sendKeys(start);
		to.clear(); to.sendKeys(end);
	}

	public void setConfigStartMode(String label) {
		new Select(visible(By.id("apConfigStartMode"))).selectByVisibleText(label);
	}

	public boolean isDelayFieldVisible() {
		return !driver.findElements(By.id("apConfigDelaySeconds")).isEmpty()
				&& driver.findElement(By.id("apConfigDelaySeconds")).isDisplayed();
	}

	public boolean areWindowFieldsVisible() {
		return !driver.findElements(By.id("apWindowStart")).isEmpty()
				&& driver.findElement(By.id("apWindowStart")).isDisplayed()
				&& driver.findElement(By.id("apWindowEnd")).isDisplayed();
	}
	public String configDelaySeconds() { return driver.findElement(By.id("apConfigDelaySeconds")).getAttribute("value"); }
	public String configWindowStart() { return driver.findElement(By.id("apWindowStart")).getAttribute("value"); }
	public String configWindowEnd() { return driver.findElement(By.id("apWindowEnd")).getAttribute("value"); }

	public String failureMode() {
		return new Select(visible(By.id("apFailureMode"))).getFirstSelectedOption().getText().trim();
	}

	public List<String> failureModes() {
		return new Select(visible(By.id("apFailureMode"))).getOptions().stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public void setRetryValues(String attempts, String baseDelay, String maxDelay) {
		setField(By.id("apFailureMaxAttempts"), attempts);
		setField(By.id("apFailureBaseDelay"), baseDelay);
		setField(By.id("apFailureMaxDelay"), maxDelay);
	}

	public boolean retryJitterVisible() { return visible(By.id("apFailureJitter")).isDisplayed(); }

	private void setField(By locator, String value) {
		WebElement input = visible(locator);
		input.clear();
		input.sendKeys(value);
	}

	public void setFailureMode(String label) {
		new Select(visible(By.id("apFailureMode"))).selectByVisibleText(label);
	}

	public boolean retryFieldsVisible() {
		return !driver.findElements(By.id("apFailureMaxAttempts")).isEmpty()
				&& driver.findElement(By.id("apFailureMaxAttempts")).isDisplayed();
	}

	public List<String> pushOrder() {
		return driver.findElements(By.cssSelector("#apSequence [data-operation]")).stream()
				.map(element -> element.getAttribute("data-operation"))
				.filter(value -> value != null && !value.isBlank()).collect(Collectors.toList());
	}

	public void moveFirstOperationDown() {
		click(By.cssSelector("#apSequence [data-operation]:first-child button[title='Move down']"));
	}

	public void moveSecondOperationUp() {
		click(By.cssSelector("#apSequence [data-operation]:nth-child(2) button[title='Move up']"));
	}

	public void moveFirstOperationDownByKeyboard() {
		WebElement grip = visible(By.cssSelector("#apSequence [data-operation]:first-child .ap-drag-handle"));
		grip.click();
		grip.sendKeys(Keys.ARROW_DOWN);
	}

	public boolean firstUpDisabled() {
		return !driver.findElement(By.cssSelector("#apSequence [data-operation]:first-child button[title='Move up']")).isEnabled();
	}

	public boolean lastDownDisabled() {
		return !driver.findElement(By.cssSelector("#apSequence [data-operation]:last-child button[title='Move down']")).isEnabled();
	}

	public List<String> operationPolicyOptions() {
		return new Select(visible(By.cssSelector("#apSequence select[aria-label^='Failure action for ']")))
				.getOptions().stream().map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public boolean hasStepPolicy(String label) {
		return !driver.findElements(By.cssSelector("#apSequence select[aria-label='Failure action for " + label + "']")).isEmpty();
	}

	public String sequenceText() { return visible(By.id("apSequence")).getText(); }

	public boolean hasUnsavedChanges() {
		return !driver.findElements(By.xpath("//*[contains(normalize-space(.), 'Unsaved changes')]")).isEmpty();
	}

	public boolean hasOperationFailurePolicy() {
		return !driver.findElements(By.cssSelector("#apSequence select[aria-label^='Failure action for ']")).isEmpty();
	}

	public boolean hasEntryDateFilters() {
		return driver.findElement(By.id("apFromDate")).isDisplayed()
				&& driver.findElement(By.id("apToDate")).isDisplayed();
	}

	public int queueMatching() { return Integer.parseInt(visible(By.id("apQueueMatching")).getText().trim()); }
	public int queueEligible() { return Integer.parseInt(visible(By.id("apQueueEligible")).getText().trim()); }
	public int queueSelected() { return Integer.parseInt(visible(By.id("apQueueSelected")).getText().trim()); }
	public int eligibleVisibleRowCount() {
		return (int) driver.findElements(By.cssSelector("#apEntriesBody input.ap-entry-check")).stream()
				.filter(WebElement::isEnabled).count();
	}
	public String entryTableText() { return visible(By.id("apEntriesBody")).getText(); }
	public String entrySearchValue() { return visible(By.id("apEntrySearch")).getAttribute("value"); }
	public void resetEntryFilters() { click(By.id("apClearFilters")); }
	public void setEntryDates(String from, String to) {
		setField(By.id("apFromDate"), from);
		setField(By.id("apToDate"), to);
		click(By.id("apApplyFilters"));
	}
	public void selectCurrentEntryPage() { click(By.id("apSelectPage")); }
	public boolean entryNextEnabled() { return driver.findElement(By.id("apEntryNext")).isEnabled(); }
	public void nextEntryPage() { click(By.id("apEntryNext")); }
	public void previousEntryPage() { click(By.id("apEntryPrev")); }
	public String entryPageIndicator() { return visible(By.id("apPanelEntries")).getText(); }

	public int entryPageSize() {
		return Integer
				.parseInt(new Select(visible(By.id("apEntryPageSize"))).getFirstSelectedOption().getText().trim());
	}

	public void setEntryPageSize(int size) {
		new Select(visible(By.id("apEntryPageSize"))).selectByVisibleText(Integer.toString(size));
	}

	public void revertConfiguration() {
		click(By.id("apRevertConfig"));
	}

	public void saveConfiguration() {
		click(By.id("apSaveConfig"));
	}

	public void searchEntries(String clientCode) {
		WebElement search = visible(By.id("apEntrySearch"));
		search.clear();
		search.sendKeys(clientCode);
		click(By.id("apApplyFilters"));
	}

	public boolean entryListed(String clientCode) {
		return !driver
				.findElements(By.xpath("//*[@id='apPanelEntries']//tr[.//td[normalize-space()='" + clientCode + "']]"))
				.isEmpty();
	}

	public boolean entryEligible(String clientCode) {
		By checkbox = By.xpath("//*[@id='apPanelEntries']//tr[.//td[normalize-space()='" + clientCode
				+ "']]//input[@type='checkbox']");
		return !driver.findElements(checkbox).isEmpty() && driver.findElement(checkbox).isEnabled();
	}

	private WebElement entryRow(String clientCode) {
		return visible(By.xpath("//*[@id='apEntriesBody']//tr[.//td[normalize-space()='" + clientCode + "']]"));
	}

	public String entryBatchState(String clientCode) {
		return entryRow(clientCode).findElement(By.cssSelector("td:last-child")).getText().trim();
	}

	public List<String> entryPushSummary(String clientCode) {
		return entryRow(clientCode).findElements(By.cssSelector(".ap-mini-tag")).stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public boolean entryVerified(String clientCode) {
		return !entryRow(clientCode).findElements(By.cssSelector(".ap-verification-state.verified")).isEmpty();
	}

	public void selectEntry(String clientCode) {
		click(By.xpath("//*[@id='apPanelEntries']//tr[.//td[normalize-space()='" + clientCode
				+ "']]//input[@type='checkbox']"));
	}

	public void setBatchName(String name) {
		WebElement input = visible(By.id("apBatchName"));
		input.clear();
		input.sendKeys(name);
	}

	public String batchNameValue() { return visible(By.id("apBatchName")).getAttribute("value"); }

	public void setBatchStartMode(String label) {
		new Select(visible(By.id("apStartMode"))).selectByVisibleText(label);
	}

	public List<String> batchStartModes() {
		return new Select(visible(By.id("apStartMode"))).getOptions().stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public boolean batchDelayVisible() {
		return !driver.findElements(By.id("apBatchDelaySeconds")).isEmpty()
				&& driver.findElement(By.id("apBatchDelaySeconds")).isDisplayed();
	}

	public boolean batchWindowVisible() {
		return !driver.findElements(By.id("apBatchWindowStart")).isEmpty()
				&& driver.findElement(By.id("apBatchWindowStart")).isDisplayed()
				&& driver.findElement(By.id("apBatchWindowEnd")).isDisplayed();
	}

	public List<String> batchPushOrder() {
		return driver.findElements(By.cssSelector("#apBatchSequence .ap-route-copy strong")).stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public void moveFirstBatchOperationLater() {
		click(By.cssSelector("#apBatchSequence button[data-batch-move='down'][data-index='0']"));
	}

	public void setBatchDelaySeconds(int seconds) {
		WebElement input = visible(By.id("apBatchDelaySeconds"));
		input.clear();
		input.sendKeys(Integer.toString(seconds));
	}

	public void createBatch() {
		click(By.id("apCreateBatch"));
	}

	public void searchBatch(String name) {
		WebElement search = visible(By.id("apBatchSearch"));
		search.clear();
		search.sendKeys(name);
	}

	public void filterBatchStatus(String status) {
		new Select(visible(By.id("apBatchStatus"))).selectByVisibleText(status);
	}

	public List<String> batchStatuses() {
		return new Select(visible(By.id("apBatchStatus"))).getOptions().stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public List<String> visibleBatchRowStatuses() {
		return driver.findElements(By.cssSelector("#apBatchesBody tr .ap-status-badge")).stream()
				.map(WebElement::getText).map(String::trim).collect(Collectors.toList());
	}

	public List<String> visibleBatchStatusClasses() {
		return driver.findElements(By.cssSelector("#apBatchesBody tr .ap-status-badge")).stream()
				.map(element -> element.getAttribute("class")).collect(Collectors.toList());
	}
	public String visibleBatchText() { return visible(By.id("apBatchesBody")).getText(); }

	public int batchRowCount() { return driver.findElements(By.cssSelector("#apBatchesBody tr")).size(); }
	public String firstBatchCode() {
		return visible(By.cssSelector("#apBatchesBody tr .ap-batch-code")).getText().trim();
	}
	public String firstBatchStatus() {
		return visible(By.cssSelector("#apBatchesBody tr .ap-status-badge")).getText().trim();
	}
	public String firstBatchClients() {
		return visible(By.cssSelector("#apBatchesBody tr .ap-clientcodes")).getText().trim();
	}
	public boolean batchContainsClient(String batchCode, String clientCode) {
		for (WebElement row : driver.findElements(By.cssSelector("#apBatchesBody tr"))) {
			if (row.getText().contains(batchCode) && row.getText().contains(clientCode)) return true;
		}
		return false;
	}
	public void inspectBatchCode(String batchCode) {
		if (liveStatusActionAvailable() && driver.findElement(By.id("apLiveStatusMeta")).getText().contains(batchCode)) {
			viewLiveActivity();
		} else {
			click(By.xpath("//*[@id='apBatchesBody']//tr[.//*[contains(@class,'ap-batch-code') "
					+ "and normalize-space()='" + batchCode + "']]//button[contains(@class,'ap-open-batch')]"));
		}
		wait.until(d -> "false".equals(d.findElement(By.id("apBatchDrawer")).getAttribute("aria-hidden")));
		wait.until(d -> d.findElement(By.id("apDrawerSummary")).getText().contains(batchCode));
	}
	public String firstBatchName() {
		return visible(By.cssSelector("#apBatchesBody tr .ap-batch-name")).getText().trim();
	}
	public void inspectFirstBatch() {
		click(By.cssSelector("#apBatchesBody tr:first-child button.ap-open-batch"));
		visible(By.cssSelector("#apBatchDrawer[aria-hidden='false']"));
	}
	public String drawerTitle() { return visible(By.id("apDrawerTitle")).getText().trim(); }
	public String drawerText() { return visible(By.id("apBatchDrawer")).getText(); }
	public void searchDrawer(String text) { setField(By.id("apDrawerSearch"), text); }
	public void filterDrawerStatus(String status) {
		new Select(visible(By.id("apDrawerStatus"))).selectByVisibleText(status);
	}
	public void expandFirstTimeline() {
		click(By.cssSelector("#apBatchDrawer button.ap-timeline-toggle[aria-expanded='false']"));
	}
	public boolean hasStepByStepActivity() {
		return !driver.findElements(By.cssSelector("#apBatchDrawer button.ap-timeline-toggle")).isEmpty();
	}
	public String drawerClientStatus(String clientCode) {
		for (WebElement card : driver.findElements(By.cssSelector("#apBatchDrawer .ap-drawer-entry, "
				+ "#apBatchDrawer .ap-entry-card, #apBatchDrawer [data-client-code]"))) {
			if (card.getText().contains(clientCode)) return card.getText();
		}
		return driver.findElement(By.id("apBatchDrawer")).getText();
	}
	public boolean retryFailedClientsAvailable() {
		return !driver.findElements(By.cssSelector("#apDrawerActions button[data-action='retry-failed']")).isEmpty();
	}
	public void retryFailedClients() {
		click(By.cssSelector("#apDrawerActions button[data-action='retry-failed']"));
	}
	public boolean hasTimeline() {
		return !driver.findElements(By.xpath("//*[@id='apBatchDrawer']//*[contains(., 'Timeline')]")).isEmpty();
	}
	public boolean firstTimelineExpanded() {
		return "true".equals(visible(By.cssSelector("#apBatchDrawer .ap-timeline-toggle")).getAttribute("aria-expanded"));
	}
	public boolean hasDrawerAction(String name) {
		return !driver.findElements(By.xpath("//*[@id='apDrawerActions']//button[contains(., '" + name + "')]")).isEmpty();
	}
	public void clickDrawerAction(String action) {
		click(By.cssSelector("#apDrawerActions button[data-action='" + action + "']"));
	}
	public boolean deleteActionAvailable() {
		return !driver.findElements(By.xpath("//*[@id='apBatchDrawer']//button[contains(., 'Delete')]")).isEmpty();
	}
	public boolean batchNextEnabled() { return driver.findElement(By.id("apBatchNext")).isEnabled(); }
	public void nextBatchPage() { click(By.id("apBatchNext")); }
	public void previousBatchPage() { click(By.id("apBatchPrev")); }

	public int batchPageSize() {
		return Integer
				.parseInt(new Select(visible(By.id("apBatchPageSize"))).getFirstSelectedOption().getText().trim());
	}

	public void setBatchPageSize(int size) {
		new Select(visible(By.id("apBatchPageSize"))).selectByVisibleText(Integer.toString(size));
	}

	public boolean hasBatchPagination() {
		return driver.findElement(By.id("apBatchPrev")).isDisplayed()
				&& driver.findElement(By.id("apBatchNext")).isDisplayed();
	}

	public boolean batchListed(String name) {
		return !driver.findElements(By.xpath("//*[@id='apBatchesBody']//tr[contains(., '" + name + "')]")).isEmpty();
	}

	public void inspectBatch(String name) {
		click(By.xpath("//*[@id='apBatchesBody']//tr[contains(., '" + name + "')]//button"));
		visible(By.id("apBatchDrawer"));
	}

	public String batchSummary() {
		return visible(By.id("apDrawerSummary")).getText();
	}

	public boolean batchHasCancel() {
		return !driver.findElements(By.xpath("//*[@id='apBatchDrawer']//button[contains(., 'Cancel')]")).isEmpty();
	}

	public void cancelBatch() {
		click(By.xpath("//*[@id='apBatchDrawer']//button[contains(., 'Cancel')]"));
	}

	public void refreshBatches() {
		click(By.id("apRefreshBatches"));
	}

	public void closeBatch() {
		click(By.id("apCloseDrawer"));
	}
}
