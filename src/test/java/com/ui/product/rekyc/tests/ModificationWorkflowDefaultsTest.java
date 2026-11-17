package com.ui.product.rekyc.tests;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.base.BaseTest;
import com.ui.product.rekyc.pages.superadmin.DashboardPage_REKYCSuperadmin;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.product.rekyc.pages.admin.meontechnologies.ModificationWorkflowV2Page;
import com.ui.utilities.JSONUtility;

/**
 * Read-only UI/API regression for mandatory defaults in the Workflow V2
 * Modification workflow. It does not save any workflow or module settings. Run
 * with -Dbrowser=chrome so Selenium can capture Network response bodies.
 */
@Listeners({ com.ui.listeners.TestListener.class })
public final class ModificationWorkflowDefaultsTest extends BaseTest {

	private static final String BASE_URL = System.getProperty("workflowV2.baseUrl", "https://rekycuat.meon.co.in");
	private static final String WORKFLOW_NAME = "modification";
	private static final Duration WAIT = Duration.ofSeconds(45);
	private static final By WORKFLOW_V2_LINK = By.cssSelector("a[href='/v1/company/workflow_v2']");
	private static final By IFRAME = By.cssSelector("iframe");
	private WebDriverWait wait;
	private final List<JsonElement> apiPayloads = new ArrayList<>();
	private final Map<String, List<UiControl>> uiControlsByModule = new LinkedHashMap<>();
	private final List<String> failures = new ArrayList<>();
	private int pagesOpened;
	private int modulesOpened;
	private int mandatoryControlsChecked;
	private int requiredRadioGroupsChecked;
	private Path evidenceDir;
	private ModificationWorkflowV2Page modificationWorkflowPage;

	private static final List<PageModules> MODIFICATION_MODULES = List.of(
			new PageModules("Login With Clientcode", List.of("clientcode_with_otp", "design")),
			new PageModules("Personal",
					List.of("mobile", "mobile_dependency", "email", "email_dependency", "address", "re_activation",
							"Father Name", "Mother Name", "Date of Birth", "Gender", "Marital Status", "Client Name",
							"Ckyc Number")),
			new PageModules("Bank", List.of("bank_addition", "bank_modification", "bank_default")),
			new PageModules("Segment", List.of("segment_addition", "Brokerage", "Risk Disclosure FNO")),
			new PageModules("Nominee", List.of("nominee_addition")),
			new PageModules("Others",
					List.of("annual_income", "networth", "networth_date", "ddpi", "Occupation", "dpid",
							"mobile trading", "IBT", "Liquidation Policy", "Signature Consent")),
			new PageModules("Document", List.of("document", "IPV", "pdf_generation", "Digilocker")),
			new PageModules("eSign", List.of("esign")), new PageModules("Admin_Config", List.of("Checker & Maker")));

	@Override
	@BeforeClass(alwaysRun = true)
	public void setupBrowser() {
		if (System.getProperty("browser") == null || System.getProperty("browser").isBlank()) {
			System.setProperty("browser", "chrome");
		}
		super.setupBrowser();
	}

	@BeforeClass(alwaysRun = true, dependsOnMethods = "setupBrowser")
	public void openModificationWorkflow() {
		if (!(driver instanceof ChromeDriver)) {
			throw new IllegalStateException(
					"This test requires Chrome for API response-body inspection. " + "Set -Dbrowser=chrome.");
		}

		wait = new WebDriverWait(driver, WAIT);
		modificationWorkflowPage = new ModificationWorkflowV2Page(driver, WAIT);
		evidenceDir = Paths.get(System.getProperty("user.dir"), "logs", "workflow-v2-evidence", String.valueOf(System.currentTimeMillis()));
		loginToCompanyAdmin();
		captureEvidence("01-company-admin");
		openWorkflowV2FromSidebar();
		switchToWorkflowStudioFrame();
		captureEvidence("02-workflow-studio");
		clearNetworkLog();
		openModificationForEdit();
		captureEvidence("03-modification-editor");
	}

	private void loginToCompanyAdmin() {
		driver.get(BASE_URL + "/v1/company/superadmin");
		if (!driver.getCurrentUrl().contains("/v1/company/companies")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.google-btn"))).click();
			LoginPage_REKYCSuperadmin login = new LoginPage_REKYCSuperadmin(driver);
			login.waitForEmailPage().writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
					.clickOnButton("nextbtn").waitForPasswordPage()
					.writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
					.clickOnButton("nextbtn");
			wait.until(ExpectedConditions.urlContains("/v1/company/companies"));
		}

		DashboardPage_REKYCSuperadmin dashboard = new DashboardPage_REKYCSuperadmin(driver);
		dashboard.clickOn("search").writeService("search", System.getProperty("workflowV2.companySearch", "meon"));
		String superadminHandle = driver.getWindowHandle();
		Set<String> priorHandles = new HashSet<>(driver.getWindowHandles());
		dashboard.clickOn("visitadmin");
		String adminHandle = wait.until(d -> {
			for (String handle : d.getWindowHandles()) {
				if (handle.equals(superadminHandle) || priorHandles.contains(handle))
					continue;
				d.switchTo().window(handle);
				String url = d.getCurrentUrl();
				if (url.startsWith(BASE_URL + "/v1/company/") && !url.contains("/companies")
						&& !url.contains("/superadmin")
						&& !d.findElements(By.cssSelector("a[href='#general-pages']")).isEmpty())
					return handle;
			}
			return null;
		});
		driver.switchTo().window(adminHandle);
	}

	private void openWorkflowV2FromSidebar() {
		wait.until(ExpectedConditions.elementToBeClickable(WORKFLOW_V2_LINK)).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(IFRAME));
	}

	private void switchToWorkflowStudioFrame() {
		wait.until(d -> !d.findElements(IFRAME).isEmpty());
		List<WebElement> frames = driver.findElements(IFRAME);
		WebElement studio = frames.stream().filter(frame -> {
			String src = frame.getAttribute("src");
			return src != null && src.contains("workflow-uat.meon.co.in/rekyc_v2");
		}).findFirst().orElse(frames.get(0));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})", studio);
		driver.switchTo().frame(studio);
		wait.until(
				d -> d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT).contains("workflow studio"));
	}

	private void openModificationForEdit() {
		// The app may restore its last workflow. Return to the list first so this
		// check always exercises the same list → exact workflow → Edit route.
		List<WebElement> allWorkflows = buttonsWithExactText("All workflows");
		if (!allWorkflows.isEmpty())
			allWorkflows.get(0).click();
		wait.until(d -> d.findElements(By.cssSelector("tr")).stream().anyMatch(this::isModificationRow));
		WebElement row = driver.findElements(By.cssSelector("tr")).stream().filter(this::isModificationRow).findFirst()
				.orElseThrow(() -> new AssertionError("Exact Modification workflow row was not found"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", row);
		WebElement edit = row.findElements(By.cssSelector("button")).stream().filter(button -> {
			String aria = button.getAttribute("aria-label");
			return (aria != null && aria.toLowerCase(Locale.ROOT).contains("edit modification"))
					|| normalize(button.getText()).equals("edit");
		}).findFirst().orElseThrow(() -> new AssertionError("Edit action missing on exact Modification row"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})", edit);
		try { edit.click(); } catch (org.openqa.selenium.ElementClickInterceptedException e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", edit);
		}
		wait.until(d -> d.findElements(By.cssSelector(".react-flow__node")).size() >= MODIFICATION_MODULES.size());
		Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains("Modification"),
				"Modification workflow editor did not open");
	}

	private boolean isModificationRow(WebElement row) {
		return row.findElements(By.cssSelector("td")).stream()
				.anyMatch(cell -> normalize(cell.getText()).equals(WORKFLOW_NAME));
	}

	@Test(priority = 1, description = "WFV2-001 to WFV2-005 - Access, tenant, Workflow V2 navigation, Modification editor and graph", groups = { "rekyc", "workflowv2", "uat", "read" })
	public void verifyWorkflowAccessAndGraph() {
		if (!(driver instanceof ChromeDriver))
			throw new SkipException("Chrome DevTools network logs are required for API assertions");
		Assert.assertTrue(driver.findElements(By.cssSelector(".react-flow__node")).size() >= MODIFICATION_MODULES.size(),
				"Modification graph does not expose every configured page");
	}

	@DataProvider(name = "modificationModules")
	public Object[][] modificationModules() {
		List<Object[]> cases = new ArrayList<>();
		int caseNumber = 6;
		for (PageModules page : MODIFICATION_MODULES)
			for (String module : page.modules)
				cases.add(new Object[] { String.format("WFV2-%03d", caseNumber++), page.pageName, module });
		return cases.toArray(new Object[0][]);
	}

	@Test(priority = 2, dataProvider = "modificationModules", description = "WFV2-006 to WFV2-043 - module default-state validation", groups = { "rekyc", "workflowv2", "uat", "read", "network" })
	public void verifyModuleDefaultState(String caseId, String pageName, String moduleName) {
		int failuresBefore = failures.size();
		modificationWorkflowPage.openPageConfiguration(pageName);
		pagesOpened++;
		captureEvidence("page-" + pageName);
		modificationWorkflowPage.selectModule(moduleName);
		modulesOpened++;
		captureApiPayloads();
		List<UiControl> controls = readControls();
		uiControlsByModule.put(moduleKey(pageName, moduleName), controls);
		captureEvidence("module-" + pageName + "-" + moduleName);
		checkUiDefaults(pageName, moduleName, controls);
		modificationWorkflowPage.closePageConfiguration();
		Assert.assertEquals(failures.size(), failuresBefore, caseId + " has a UI default-state mismatch");
 		captureApiPayloads();
	}

	@Test(priority = 3, description = "WFV2-044 - Workflow configuration JSON response captured", groups = { "rekyc", "workflowv2", "uat", "network" })
	public void verifyWorkflowConfigurationResponse() {
		captureApiPayloads();
		Assert.assertEquals(uiControlsByModule.size(), MODIFICATION_MODULES.stream().mapToInt(p -> p.modules.size()).sum(),
					"Not all modules were visited");
		Assert.assertTrue(!apiPayloads.isEmpty(), "No JSON API response body was captured from the workflow service");
	}

	@Test(priority = 4, description = "WFV2-045 - UI/API default parity for safely mapped controls", groups = { "rekyc", "workflowv2", "uat", "network" })
	public void verifyUiApiParity() {
		int failuresBefore = failures.size();
		compareDefaultsToApi();
		if (mandatoryControlsChecked == 0)
			System.out.println("Workflow V2 note: no native Mandatory/Is mandatory control was exposed by the rendered markup.");
		Assert.assertEquals(failures.size(), failuresBefore, "UI/API default parity mismatch detected");
	}

	@Test(priority = 5, description = "WFV2-046 - Read-only safety: workflow is never saved", groups = { "rekyc", "workflowv2", "uat", "read" })
	public void verifyNoWorkflowSaveAction() {
		Assert.assertTrue(!driver.findElement(By.tagName("body")).getText().contains("Saved successfully"),
				"A read-only scan must not cause a workflow save confirmation");
		Assert.assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
	}

	private void openPageConfiguration(String pageName) {
		// React Flow keeps nodes outside the visible canvas in the DOM.  Centre the
		// graph before locating/clicking a node so the final node is not "clicked"
		// behind the canvas viewport.
		List<WebElement> fitView = driver.findElements(By.cssSelector("button[aria-label='fit view']"));
		if (!fitView.isEmpty()) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", fitView.get(0));
		}
		List<WebElement> nodes = driver.findElements(By.cssSelector(".react-flow__node"));
		WebElement node = nodes.stream().filter(candidate -> {
			String text = normalize(candidate.getText());
			return text.contains(normalize(pageName));
		}).findFirst().orElseThrow(() -> new AssertionError("Workflow page node not found: " + pageName));
		for (int attempt = 0; attempt < 3; attempt++) {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})", node);
			try {
				new org.openqa.selenium.interactions.Actions(driver).moveToElement(node).click().perform();
			} catch (RuntimeException ignored) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", node);
			}
			try {
				new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(8)).until(
						d -> d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
				return;
			} catch (org.openqa.selenium.TimeoutException ignored) {
				// A React Flow node occasionally receives the first click while it is
				// still panning.  Retrying is safe because this test never saves.
			}
		}
		throw new AssertionError("Workflow page configuration did not open: " + pageName);
	}

	private void selectModule(String pageName, String moduleName) {
		List<WebElement> tabs = buttonsWithExactText(moduleName);
		if (tabs.isEmpty()) {
			failures.add(pageName + " / " + moduleName + ": module tab was not found");
			throw new AssertionError(pageName + " / " + moduleName + ": module tab was not found");
		}
		// Some single-module panels have a collapsible header with the same text;
		// a tab is preferred by visible horizontal-tab semantics and exact label.
		WebElement tab = tabs.stream().filter(WebElement::isDisplayed).findFirst().orElse(tabs.get(0));
		try {
			tab.click();
		} catch (RuntimeException intercept) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", tab);
		}
		wait.until(d -> d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
	}

	private void closePageConfiguration() {
		List<WebElement> close = buttonsWithExactText("Close");
		if (close.isEmpty()) close = driver.findElements(By.cssSelector("button[aria-label='Close']"));
		if (!close.isEmpty()) {
			try { close.get(0).click(); } catch (org.openqa.selenium.ElementClickInterceptedException e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", close.get(0));
			}
		}
		else new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
		wait.until(d -> !d.findElement(By.tagName("body")).getText().contains("PAGE CONFIGURATION"));
	}

	private List<UiControl> readControls() {
		String script = "const visible=e=>{if(!e)return false;const s=getComputedStyle(e),r=e.getBoundingClientRect();"
				+ "return s.visibility!=='hidden'&&s.display!=='none'&&(r.width>0||r.height>0)};"
				+ "const labelOf=e=>{const labels=e.labels?Array.from(e.labels).map(x=>x.innerText||x.textContent||''):[];"
				+ "const g=e.closest('fieldset,[role=group]');const legend=g?(g.querySelector('legend,[aria-label]')?.innerText||g.getAttribute('aria-label')||''):'';"
				+ "const direct=e.getAttribute('aria-label')||e.getAttribute('title')||'';const w=e.closest('label')||e.parentElement;"
				+ "return {label:(labels.join(' ')||direct||(w?.innerText||w?.textContent||'')).trim(),groupLabel:(legend||'').trim(),groupText:(g?.innerText||'').trim()}};"
				+ "const all=Array.from(document.querySelectorAll('input[type=checkbox],input[type=radio],[role=checkbox],[role=radio]'));"
				+ "return all.map(e=>{const i=labelOf(e),c=e.labels?.[0]||e.closest('label')||e.parentElement;"
					+ "/* Styled workflow controls often hide the native input while exposing its label. Keep it for default-state validation. */"
					+ "const t=e.type||e.getAttribute('role'),g=e.closest('fieldset,[role=group]');"
				+ "return {type:t,id:e.id||e.getAttribute('data-testid')||'',name:e.name||'',value:e.value||e.getAttribute('value')||'',"
				+ "checked:e.checked===undefined?e.getAttribute('aria-checked')==='true':!!e.checked,"
				+ "required:!!e.required||e.getAttribute('aria-required')==='true'||g?.getAttribute('aria-required')==='true'||g?.hasAttribute('required'),"
				+ "label:i.label,groupLabel:i.groupLabel,groupText:i.groupText}}).filter(Boolean)";
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rows = (List<Map<String, Object>>) ((JavascriptExecutor) driver)
				.executeScript(script);
		List<UiControl> controls = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			String type = text(row.get("type"));
			String label = text(row.get("label"));
			String groupLabel = text(row.get("groupLabel"));
			String groupText = text(row.get("groupText"));
			String id = text(row.get("id"));
			String name = text(row.get("name"));
			String value = text(row.get("value"));
			boolean checked = Boolean.TRUE.equals(row.get("checked"));
			boolean required = Boolean.TRUE.equals(row.get("required"));
			controls.add(new UiControl(type, label, groupLabel, groupText, id, name, value, checked, required));
		}
		return controls;
	}

	private void checkUiDefaults(String pageName, String moduleName, List<UiControl> controls) {
		List<UiControl> mandatoryCheckboxes = controls.stream()
				.filter(control -> control.isCheckbox() && containsMandatory(control.label))
				.collect(Collectors.toList());
		for (UiControl control : mandatoryCheckboxes) {
			boolean expected = !normalize(control.label).contains("nonmandatory");
			mandatoryControlsChecked++;
			if (control.checked != expected)
				failures.add(pageName + " / " + moduleName + " / checkbox '" + control.label + "' expected checked="
						+ expected + " but was " + control.checked);
		}

		Map<String, List<UiControl>> radioGroups = controls.stream().filter(UiControl::isRadio)
				.collect(Collectors.groupingBy(UiControl::radioGroupKey, LinkedHashMap::new, Collectors.toList()));
		for (List<UiControl> group : radioGroups.values()) {
			UiControl first = group.get(0);
			String groupName = first.groupLabel.isBlank() ? first.groupText : first.groupLabel;
			boolean mandatory = group.stream().anyMatch(control -> control.required) || containsMandatory(groupName)
					|| containsRequired(groupName);
			if (!mandatory)
				continue;
			requiredRadioGroupsChecked++;
			long selected = group.stream().filter(control -> control.checked).count();
			if (selected != 1)
				failures.add(pageName + " / " + moduleName + " / radio group '" + groupName
						+ "' expected exactly one selected option but found " + selected);
		}
	}

	private void compareDefaultsToApi() {
		// The UI is revisited after rendering to keep API comparison limited to
		// business-config controls. Secret integration values are never logged.
		for (PageModules page : MODIFICATION_MODULES) {
			for (String module : page.modules) {
					List<UiControl> controls = uiControlsByModule.get(moduleKey(page.pageName, module));
					if (controls == null) {
						failures.add(page.pageName + " / " + module + ": UI controls were not captured");
						continue;
					}
				for (UiControl control : controls) {
					if (control.isCheckbox() && containsMandatory(control.label)) {
						boolean expected = !normalize(control.label).contains("nonmandatory");
						List<String> candidates = control.apiKeys();
						ApiMatch match = findApiValue(page.pageName, module, candidates);
						if (match == null) {
							failures.add(page.pageName + " / " + module + " / API field not mapped for '"
									+ control.label + "'");
						} else if (booleanValue(match.value) == null || booleanValue(match.value) != expected) {
							failures.add(page.pageName + " / " + module + " / API default disagrees for '"
									+ control.label + "' (response path " + match.path + ")");
						}
						} else if (control.isRadio()) {
							// The visual radio wrapper may not expose any machine-readable
							// option identity. Do not guess a backend field from an empty key.
							if (control.label.isBlank() || control.value.isBlank())
								continue;
						ApiMatch match = findApiValue(page.pageName, module, control.apiKeys());
						if (match == null) {
							failures.add(page.pageName + " / " + module + " / API field not mapped for radio option '"
									+ control.label + "'");
						} else if (booleanValue(match.value) == null || booleanValue(match.value) != control.checked) {
							failures.add(page.pageName + " / " + module + " / API radio default disagrees for '"
									+ control.label + "' (response path " + match.path + ")");
						}
					}
				}
			}
			closePageConfiguration();
		}
	}

	private String moduleKey(String pageName, String moduleName) {
		return normalize(pageName) + "::" + normalize(moduleName);
	}

	private boolean isRequiredRadio(UiControl control) {
		return control.required || containsMandatory(control.groupLabel) || containsMandatory(control.groupText)
				|| containsRequired(control.groupLabel) || containsRequired(control.groupText);
	}

	private ApiMatch findApiValue(String pageName, String moduleName, List<String> candidates) {
		List<ApiMatch> matches = new ArrayList<>();
		for (JsonElement payload : apiPayloads)
			flatten(payload, "$", pageName, moduleName, candidates, matches);
		if (matches.isEmpty())
			return null;
		matches.sort((left, right) -> Integer.compare(right.contextScore, left.contextScore));
		int best = matches.get(0).contextScore;
		List<ApiMatch> bestMatches = matches.stream().filter(match -> match.contextScore == best)
				.collect(Collectors.toList());
		if (bestMatches.size() != 1)
			return null;
		return bestMatches.get(0);
	}

	private void flatten(JsonElement element, String path, String pageName, String moduleName, List<String> candidates,
			List<ApiMatch> matches) {
		if (element == null || element.isJsonNull())
			return;
		if (element.isJsonObject()) {
			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				String nextPath = path + "." + entry.getKey();
				if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString())
					nextPath += "=" + entry.getValue().getAsString();
				if (candidateMatches(entry.getKey(), candidates) && entry.getValue().isJsonPrimitive()) {
					String normalizedPath = normalize(nextPath);
					int score = (normalizedPath.contains(normalize(moduleName)) ? 2 : 0)
							+ (normalizedPath.contains(normalize(pageName)) ? 1 : 0);
					matches.add(new ApiMatch(nextPath, entry.getValue(), score));
				}
				flatten(entry.getValue(), nextPath, pageName, moduleName, candidates, matches);
			}
		} else if (element.isJsonArray()) {
			for (int i = 0; i < element.getAsJsonArray().size(); i++)
				flatten(element.getAsJsonArray().get(i), path + "[" + i + "]", pageName, moduleName, candidates,
						matches);
		}
	}

	private boolean candidateMatches(String key, List<String> candidates) {
		String normalizedKey = normalize(key);
		return candidates.stream().filter(candidate -> !candidate.isBlank())
				.map(ModificationWorkflowDefaultsTest::normalize)
				.anyMatch(candidate -> normalizedKey.equals(candidate) || normalizedKey.endsWith(candidate));
	}

	private void clearNetworkLog() {
		((ChromeDriver) driver).executeCdpCommand("Network.enable", Collections.emptyMap());
		driver.manage().logs().get(LogType.PERFORMANCE);
	}

	private void captureEvidence(String label) {
		try {
			Files.createDirectories(evidenceDir);
			String filename = label.replaceAll("[^A-Za-z0-9_-]+", "_") + ".png";
			Files.write(evidenceDir.resolve(filename), ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
			System.out.println("Workflow V2 evidence: " + evidenceDir.resolve(filename));
		} catch (IOException | RuntimeException e) {
			throw new AssertionError("Unable to save workflow evidence for " + label, e);
		}
	}

	private void captureApiPayloads() {
		for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
			try {
				JsonObject event = JsonParser.parseString(entry.getMessage()).getAsJsonObject()
						.getAsJsonObject("message");
				if (!"Network.responseReceived".equals(event.get("method").getAsString()))
					continue;
				JsonObject params = event.getAsJsonObject("params");
				String type = params.get("type").getAsString();
				if (!"XHR".equals(type) && !"Fetch".equals(type))
					continue;
				JsonObject response = params.getAsJsonObject("response");
				URI uri = URI.create(response.get("url").getAsString());
				if (uri.getHost() == null || !uri.getHost().endsWith("meon.co.in"))
					continue;
				String mimeType = response.has("mimeType") ? response.get("mimeType").getAsString() : "";
				if (!mimeType.toLowerCase(Locale.ROOT).contains("json") || response.get("status").getAsInt() >= 400)
					continue;
				String requestId = params.get("requestId").getAsString();
				@SuppressWarnings("unchecked")
				Map<String, Object> bodyResult = ((ChromeDriver) driver).executeCdpCommand("Network.getResponseBody",
						Collections.singletonMap("requestId", requestId));
				String body = String.valueOf(bodyResult.get("body"));
				if (Boolean.TRUE.equals(bodyResult.get("base64Encoded")))
					body = new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8);
				JsonElement parsed = JsonParser.parseString(body);
				if (apiPayloads.stream().noneMatch(existing -> existing.equals(parsed)))
					apiPayloads.add(parsed);
			} catch (RuntimeException unavailableOrUnrelated) {
				// Do not log response bodies, credentials, integration settings, or raw URLs.
			}
		}
	}

	private List<WebElement> buttonsWithExactText(String text) {
		return driver.findElements(By.xpath("//button[normalize-space(.)=" + xpathLiteral(text) + "]"));
	}

	private static Boolean booleanValue(JsonElement value) {
		if (value == null || value.isJsonNull() || !value.isJsonPrimitive())
			return null;
		if (value.getAsJsonPrimitive().isBoolean())
			return value.getAsBoolean();
		if (value.getAsJsonPrimitive().isNumber()) {
			double number = value.getAsDouble();
			return number == 0 ? Boolean.FALSE : number == 1 ? Boolean.TRUE : null;
		}
		if (value.getAsJsonPrimitive().isString()) {
			String text = value.getAsString().trim().toLowerCase(Locale.ROOT);
			if (List.of("true", "1", "yes", "on", "checked").contains(text))
				return Boolean.TRUE;
			if (List.of("false", "0", "no", "off", "unchecked").contains(text))
				return Boolean.FALSE;
		}
		return null;
	}

	private static boolean containsMandatory(String text) {
		String label = normalize(text);
		return label.contains("mandatory") || label.contains("mendatory");
	}

	private static boolean containsRequired(String text) {
		return normalize(text).contains("required");
	}

	private static String normalize(String text) {
		return text == null ? ""
				: text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "").replace("mendatory", "mandatory");
	}

	private static String text(Object value) {
		return value == null ? "" : String.valueOf(value).trim();
	}

	private static String xpathLiteral(String value) {
		if (!value.contains("'"))
			return "'" + value + "'";
		if (!value.contains("\""))
			return "\"" + value + "\"";
		String[] parts = value.split("'", -1);
		List<String> quoted = new ArrayList<>();
		for (int i = 0; i < parts.length; i++) {
			if (i > 0)
				quoted.add("\"'\"");
			quoted.add("'" + parts[i] + "'");
		}
		return "concat(" + String.join(",", quoted) + ")";
	}

	@AfterClass(alwaysRun = true)
	public void closeWorkflowDefaultsBrowser() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	private static final class PageModules {
		final String pageName;
		final List<String> modules;

		PageModules(String pageName, List<String> modules) {
			this.pageName = pageName;
			this.modules = modules;
		}
	}

	private static final class UiControl {
		final String type, label, groupLabel, groupText, id, name, value;
		final boolean checked, required;

		UiControl(String type, String label, String groupLabel, String groupText, String id, String name, String value,
				boolean checked, boolean required) {
			this.type = type;
			this.label = label;
			this.groupLabel = groupLabel;
			this.groupText = groupText;
			this.id = id;
			this.name = name;
			this.value = value;
			this.checked = checked;
			this.required = required;
		}

		boolean isCheckbox() {
			return "checkbox".equalsIgnoreCase(type);
		}

		boolean isRadio() {
			return "radio".equalsIgnoreCase(type);
		}

		String radioGroupKey() {
			return !name.isBlank() ? name : !groupLabel.isBlank() ? groupLabel : groupText;
		}

		List<String> apiKeys() {
			List<String> keys = new ArrayList<>();
			if (!id.isBlank())
				keys.add(id);
			if (!name.isBlank())
				keys.add(name);
			if (!label.isBlank())
				keys.add(label);
			if (!groupLabel.isBlank())
				keys.add(groupLabel);
			return keys;
		}
	}

	private static final class ApiMatch {
		final String path;
		final JsonElement value;
		final int contextScore;

		ApiMatch(String path, JsonElement value, int contextScore) {
			this.path = path;
			this.value = value;
			this.contextScore = contextScore;
		}
	}
}
