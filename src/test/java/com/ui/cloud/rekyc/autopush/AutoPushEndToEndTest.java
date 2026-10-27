package com.ui.cloud.rekyc.autopush;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.ui.product.rekyc.pages.admin.meontechnologies.MakerCheckerVerifyPage_AutoPush;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JourneyEvidence;

public final class AutoPushEndToEndTest extends AutoPushUatTestBase {
	private static final Map<String, String> MAKER_PUSH_LABELS = Map.ofEntries(Map.entry("KRA", "Complete Kra"),
			Map.entry("KRA SFTP", "Kra SFTP Upload"), Map.entry("KRA V3", "Complete Kra V3"),
			Map.entry("BSE", "Complete BSE"), Map.entry("NSE", "Complete NSE"), Map.entry("MCX", "Push MCX"),
			Map.entry("BSE STAR MF", "BSE Start MF"), Map.entry("BACKOFFICE", "Complete Backoffice"),
			Map.entry("CKYC", "CKYC Push"), Map.entry("CDSL", "Cdsl Push"), Map.entry("NSDL", "Nsdl Push"));

	@Test(description = "Auto Push E2E - Start now", groups = { "autopush", "uat", "e2e", "mutation" })
	public void startNowJourney() {
		runTimingJourney("Start now", "now");
	}

	@Test(description = "Auto Push E2E - Start after delay", groups = { "autopush", "uat", "e2e", "mutation" })
	public void startAfterJourney() {
		runTimingJourney("Start after", "delay");
	}

	@Test(description = "Auto Push E2E - Daily time window", groups = { "autopush", "uat", "e2e", "mutation" })
	public void dailyWindowJourney() {
		runTimingJourney("Daily time window", "window");
	}

	@Test(description = "Verified MEON client is protected from duplicate batching and has traceable activity", groups = {
			"autopush", "uat", "e2e", "read" })
	public void alreadyBatchedClientCanBeTraced() {
		String client = clientCode();
		MakerCheckerVerifyPage_AutoPush maker = openMakerVerify();
		Assert.assertTrue(maker.isVerified(client), client + " must be Verified before Auto Push");
		List<String> available = maker.availablePushButtons(client);
		openReadWorkflow();
		page.entries();
		page.searchEntries(client);
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> page.entryListed(client));
		Assert.assertTrue(page.entryVerified(client));
		assertPushSummaryMatchesMaker(client, available);
		Assert.assertFalse(page.entryEligible(client), "Already-batched client must not be selectable again");
		String batchState = page.entryBatchState(client);
		Assert.assertTrue(batchState.startsWith("Already in batch AP-"), batchState);
		String code = batchState.substring("Already in batch ".length()).trim();
		page.monitor();
		page.searchBatch(code);
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> page.batchListed(code));
		Assert.assertTrue(page.batchContainsClient(code, client), "Referenced activity must contain " + client);
		page.inspectBatchCode(code);
		Assert.assertTrue(page.batchSummary().contains(code));
		if (page.hasStepByStepActivity())
			page.expandFirstTimeline();
		ExtentReporterUtility.info("Existing activity " + code + ": " + page.drawerClientStatus(client));
	}

	@Test(description = "Retry failed MEON client only in a named UAT activity", groups = { "autopush", "uat", "e2e",
			"mutation" })
	public void retryFailedOwnedActivity() {
		requireMutationOptIn();
		String client = clientCode();
		String code = requiredProperty("autoPush.retryBatchCode", "failed UAT activity code to retry");
		openDisposableWorkflow();
		page.monitor();
		page.searchBatch(code);
		Assert.assertTrue(page.batchContainsClient(code, client),
				"Retry target must contain the configured test client");
		page.inspectBatchCode(code);
		Assert.assertTrue(page.retryFailedClientsAvailable(), "Retry is only valid for failed clients");
		if (page.hasStepByStepActivity())
			page.expandFirstTimeline();
		String priorDetails = page.drawerClientStatus(client).toLowerCase(Locale.ROOT);
		if ((priorDetails.contains("timed out") || priorDetails.contains("outcome is unknown"))
				&& !Boolean.parseBoolean(System.getProperty("autoPush.retryConfirmed", "false"))) {
			throw new SkipException("Provider outcome is uncertain; confirm it before retrying with "
					+ "-DautoPush.retryConfirmed=true");
		}
		JourneyEvidence evidence = new JourneyEvidence(driver, "autoPush_retry_" + client);
		try {
			evidence.capture("Failed client before retry");
			page.retryFailedClients();
			new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> page.drawerText().contains(code));
			ExtentReporterUtility.info("Retry requested for failed client " + client + " in " + code);
			page.closeBatch();
			monitorActivity(code, client, evidence);
		} finally {
			evidence.finish();
		}
	}

	private void runTimingJourney(String mode, String slug) {
		requireMutationOptIn();
		Assert.assertEquals(testWorkflow().toLowerCase(Locale.ROOT), "modification",
				"E2E fixture must use the Modification workflow");
		String client = clientCode();
		MakerCheckerVerifyPage_AutoPush maker = openMakerVerify();
		maker.markVerifiedIfNeeded(client);
		Assert.assertTrue(maker.isVerified(client));
		List<String> available = maker.availablePushButtons(client);
		openDisposableWorkflow();
		page.entries();
		page.searchEntries(client);
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> page.entryListed(client));
		Assert.assertTrue(page.entryVerified(client));
		assertPushSummaryMatchesMaker(client, available);
		if (!page.entryEligible(client)) {
			throw new SkipException(client + " is " + page.entryBatchState(client)
					+ "; use a fresh verified UAT entry for a new " + mode + " journey");
		}
		page.monitor();
		String priorCode = page.batchRowCount() == 0 ? "" : page.firstBatchCode();
		page.configuration();
		boolean originalEnabled = page.automaticPushEnabled();
		boolean originalAutoSelect = page.autoSelectEnabled();
		String originalMode = page.configStartMode();
		String originalDelay = page.configDelaySeconds();
		String originalStart = page.configWindowStart();
		String originalEnd = page.configWindowEnd();
		List<String> originalOrder = page.pushOrder();
		JourneyEvidence evidence = new JourneyEvidence(driver, "autoPush_" + slug + "_" + client);
		try {
			evidence.capture("Setup before " + mode);
			page.setAutomaticPushEnabled(true);
			page.setAutoSelectEnabled(false);
			page.setConfigStartMode(mode);
			if ("Start after".equals(mode)) {
				Assert.assertTrue(page.isDelayFieldVisible());
				page.setConfigDelaySeconds("30");
			} else
				Assert.assertTrue(page.delayFieldHidden());
			if ("Daily time window".equals(mode)) {
				Assert.assertTrue(page.areWindowFieldsVisible());
				page.setConfigWindow("00:00", "23:59");
			} else
				Assert.assertFalse(page.areWindowFieldsVisible());
			Assert.assertFalse(page.autoSelectEnabled(), "Newly verified auto-add must be disabled");
			if (originalOrder.size() > 1) {
				page.moveFirstOperationDown();
				Assert.assertNotEquals(page.pushOrder(), originalOrder);
			}
			List<String> configuredOrder = page.pushOrder();
			page.saveConfiguration();
			openDisposableWorkflow();
			page.configuration();
			Assert.assertTrue(page.automaticPushEnabled());
			Assert.assertFalse(page.autoSelectEnabled());
			Assert.assertEquals(page.configStartMode(), mode);
			Assert.assertEquals(page.pushOrder(), configuredOrder, "Saved push order did not persist");
			evidence.capture("Saved " + mode + " configuration");
			page.monitor();
			String code = new WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
				page.refreshBatches();
				String latest = page.batchRowCount() == 0 ? "" : page.firstBatchCode();
				return !latest.isEmpty() && !latest.equals(priorCode) && page.firstBatchClients().contains(client)
						? latest
						: null;
			});
			page.entries();
			page.searchEntries(client);
			new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> page.entryBatchState(client).contains(code));
			Assert.assertFalse(page.entryEligible(client), "Launched client must not be batchable twice");
			page.monitor();
			monitorActivity(code, client, evidence);
		} finally {
			try {
				openDisposableWorkflow();
				page.configuration();
				page.setAutomaticPushEnabled(originalEnabled);
				page.setAutoSelectEnabled(originalAutoSelect);
				page.setConfigStartMode(originalMode);
				if ("Start after".equals(originalMode) && !originalDelay.isBlank())
					page.setConfigDelaySeconds(originalDelay);
				if ("Daily time window".equals(originalMode))
					page.setConfigWindow(originalStart, originalEnd);
				if (!page.pushOrder().equals(originalOrder) && originalOrder.size() > 1)
					page.moveSecondOperationUp();
				Assert.assertEquals(page.pushOrder(), originalOrder, "Original push order must be restored");
				page.saveConfiguration();
				evidence.capture("Original configuration restored");
			} finally {
				evidence.finish();
			}
		}
	}

	private void monitorActivity(String code, String client, JourneyEvidence evidence) {
		Instant deadline = Instant.now().plus(Duration.ofMinutes(10));
		String previous = "";
		String status = "";
		while (Instant.now().isBefore(deadline)) {
			page.refreshBatches();
			page.searchBatch(code);
			Assert.assertTrue(page.batchContainsClient(code, client));
			status = page.firstBatchStatus();
			if (!status.equals(previous)) {
				evidence.capture("Activity " + code + " - " + status);
				ExtentReporterUtility.info(code + " state: " + status);
				previous = status;
			}
			if (List.of("Completed", "Needs attention", "Cancelled").contains(status))
				break;
			try {
				Thread.sleep(10_000);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Activity monitoring interrupted", interrupted);
			}
		}
		page.inspectBatchCode(code);
		if (page.hasStepByStepActivity())
			page.expandFirstTimeline();
		String details = page.drawerClientStatus(client);
		ExtentReporterUtility.info("Activity " + code + " technical details: " + details);
		evidence.capture("Step-by-step client activity");
		Assert.assertFalse(details.isBlank(), "Client technical details should be visible");
		Assert.assertTrue(List.of("Completed", "Needs attention", "Cancelled").contains(status),
				"Activity did not reach a terminal state in 10 minutes: " + status);
		Assert.assertEquals(status, "Completed", "Push ended without full success. Details: " + details);
	}

	private MakerCheckerVerifyPage_AutoPush openMakerVerify() {
		page.open(baseUrl);
		page.chooseWorkflow("modification");
		String workflowId = page.selectedWorkflowId();
		driver.get(baseUrl + "/v1/company/" + workflowId + "/maker_checker");
		MakerCheckerVerifyPage_AutoPush maker = new MakerCheckerVerifyPage_AutoPush(driver);
		maker.openModificationVerify();
		return maker;
	}

	private String clientCode() {
		String client = System.getProperty("autoPush.testClientCode", "MEON2").trim();
		if (!List.of("MEON1", "MEON2").contains(client))
			throw new SkipException("Only MEON1 or MEON2 is allowed for this UAT E2E suite");
		return client;
	}

	private void assertPushSummaryMatchesMaker(String client, List<String> available) {
		List<String> summary = page.entryPushSummary(client);
		Assert.assertFalse(summary.isEmpty(), "Auto Push queue has no push summary for " + client);
		for (String push : summary) {
			String makerLabel = MAKER_PUSH_LABELS.get(push.toUpperCase(Locale.ROOT));
			Assert.assertNotNull(makerLabel, "Unknown push mapping: " + push);
			Assert.assertTrue(available.stream().anyMatch(button -> button.equalsIgnoreCase(makerLabel)),
					"Queue push " + push + " is missing in Maker & Checker");
		}
	}
}
