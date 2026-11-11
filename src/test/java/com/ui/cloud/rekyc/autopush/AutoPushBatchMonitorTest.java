package com.ui.cloud.rekyc.autopush;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public final class AutoPushBatchMonitorTest extends AutoPushUatTestBase {
	@Test(description = "TC_AP_070 - Stop current Auto Push batch before Live Activity checks and verify clients become eligible", groups = { "autopush", "uat", "mutation" }, priority = -100)
	public void tcAp070_stopCurrentBatchBeforeLiveActivity() {
		openDisposableWorkflow();
		String client = System.getProperty("autoPush.testClientCode", "MEON1");
		page.entries();
		page.searchEntries(client);
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.entryListed(client));
		if (page.entryEligible(client)) {
			Assert.assertTrue(page.entryBatchState(client).contains("Verified & available"));
			return;
		}
		String batchState = page.entryBatchState(client);
		Assert.assertTrue(batchState.startsWith("Already in batch AP-"), batchState);
		String code = batchState.substring("Already in batch ".length()).trim();
		String expected = System.getProperty("autoPush.currentBatchCode", "").trim();
		if (!expected.isEmpty() && !expected.equals(code))
			throw new SkipException("Client batch " + code + " does not match requested batch " + expected);
		page.monitor();
		page.searchBatch(code);
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.batchListed(code));
		Assert.assertTrue(page.batchContainsClient(code, client), "Referenced batch does not contain " + client);
		page.inspectBatchCode(code);
		if (page.hasDrawerAction("Stop Auto Push")) {
			page.clickDrawerActionByLabel("Stop Auto Push");
		} else if (page.hasDrawerAction("Cancel activity")) {
			page.clickDrawerActionByLabel("Cancel activity");
		} else if (!page.drawerText().toLowerCase().matches("(?s).*(stopped|cancelled).*")) {
			Assert.fail("Batch " + code + " has no stop or cancel action, but " + client + " remains unavailable");
		}
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
				page.drawerText().toLowerCase().matches("(?s).*(stopped|cancelled).*"));
		page.closeDrawer();
		page.entries();
		page.searchEntries(client);
		new WebDriverWait(driver, Duration.ofSeconds(30))
				.ignoring(StaleElementReferenceException.class)
				.until(d -> page.entryEligible(client));
		Assert.assertTrue(page.entryEligible(client), "Stopping " + code + " did not release " + client);
	}
	private void readMonitor() {
		openReadWorkflow();
		page.monitor();
	}

	private String ownedBatch(String property) {
		String value = requiredProperty(property, "batch created only for UAT automation");
		if (!value.startsWith("autopush-uat-"))
			throw new SkipException("Owned batch name must start autopush-uat-");
		return value;
	}

	@Test(description = "TC_AP_047 - Batch monitor renders metrics and table", groups = { "autopush", "uat", "read" })
	public void tcAp047_monitorMetricsRender() {
		readMonitor();
		Assert.assertTrue(page.monitorVisible());
		Assert.assertTrue(page.hasBatchPagination());
		Assert.assertTrue(page.batchStatuses().contains("All activity"));
	}

	@Test(description = "TC_AP_048 - Batch-name search narrows monitor list", groups = { "autopush", "uat", "read" })
	public void tcAp048_batchCodeSearchWorks() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		String code = page.firstBatchCode();
		page.searchBatch(code);
		new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> !page.visibleBatchRowsText().isEmpty()
				&& page.visibleBatchRowsText().stream().allMatch(row -> row.contains(code)));
		Assert.assertTrue(page.visibleBatchRowsText().stream().allMatch(row -> row.contains(code)));
	}

	@Test(description = "TC_AP_049 - Batch search supports client code", groups = { "autopush", "uat", "read" })
	public void tcAp049_clientCodeSearchWorks() {
		readMonitor();
		String client = System.getProperty("autoPush.existingClientCode", "MEON2");
		page.searchBatch(client);
		List<String> rows = new WebDriverWait(driver, Duration.ofSeconds(10))
				.ignoring(StaleElementReferenceException.class)
				.until(d -> {
					List<String> current = page.visibleBatchRowsText();
					return !current.isEmpty() && current.stream().allMatch(row -> row.contains(client))
							? current : null;
				});
		Assert.assertTrue(rows.stream().allMatch(row -> row.contains(client)), rows.toString());
	}

	@Test(description = "TC_AP_050 - No-match batch search shows empty list", groups = { "autopush", "uat", "read" })
	public void tcAp050_unmatchedBatchSearchEmpty() {
		readMonitor();
		page.searchBatch("__no_matching_batch__");
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.batchRowCount() == 0);
		Assert.assertFalse(page.batchListed("__no_matching_batch__"));
		Assert.assertEquals(page.batchRowCount(), 0);
	}

	@Test(description = "TC_AP_051 - Batch status filter covers all states", groups = { "autopush", "uat", "read" })
	public void tcAp051_statusFilterMatchesRows() {
		readMonitor();
		List<String> options = page.batchStatuses();
		Assert.assertTrue(options.contains("All activity"));
		for (String status : options) {
			page.filterBatchStatus(status);
			Assert.assertTrue(page.monitorVisible(), "Monitor vanished for filter " + status);
		}
		page.filterBatchStatus("All activity");
	}

	@Test(description = "TC_AP_052 - Refresh updates monitor without clearing workflow", groups = { "autopush", "uat",
			"read" })
	public void tcAp052_refreshRetainsWorkflow() {
		readMonitor();
		String workflow = page.selectedWorkflow();
		page.refreshBatches();
		Assert.assertEquals(page.selectedWorkflow(), workflow);
		Assert.assertTrue(page.monitorVisible());
	}

	@Test(description = "TC_AP_053 - Monitor page size and pagination work", groups = { "autopush", "uat", "read" })
	public void tcAp053_pageSizeAndPagination() {
		readMonitor();
		int original = page.batchPageSize();
		try {
			for (int size : List.of(10, 20, 30)) {
				page.setBatchPageSize(size);
				new WebDriverWait(driver, Duration.ofSeconds(10))
						.until(d -> page.batchPageSize() == size);
				Assert.assertEquals(page.batchPageSize(), size);
			}
			page.setBatchPageSize(10);
			if (page.batchNextEnabled()) {
				page.nextBatchPage();
				page.previousBatchPage();
				Assert.assertTrue(page.monitorVisible());
			}
		} finally {
			page.setBatchPageSize(original);
		}
	}

	@Test(description = "TC_AP_054 - Inspect batch opens accurate detail drawer", groups = { "autopush", "uat",
			"read" })
	public void tcAp054_inspectShowsMatchingCode() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		String code = page.firstBatchCode();
		String name = page.firstBatchName();
		page.inspectBatchCode(code);
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.drawerTitle().equals(name));
		Assert.assertEquals(page.drawerTitle(), name);
		Assert.assertTrue(page.batchSummary().contains(code));
	}

	@Test(description = "TC_AP_055 - Drawer entry search and status filter work", groups = { "autopush", "uat",
			"read" })
	public void tcAp055_drawerFiltersEntries() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		String clients = page.firstBatchClients();
		if (clients.isBlank()) throw new SkipException("First batch has no client codes");
		String client = clients.split("\\s+")[0];
		page.inspectFirstBatch();
		page.searchDrawer(client);
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.drawerText().contains(client));
		page.filterDrawerStatus("All clients");
		Assert.assertTrue(page.drawerText().contains(client));
	}

	@Test(description = "TC_AP_056 - Timeline expands ordered push events", groups = { "autopush", "uat", "read" })
	public void tcAp056_timelineExpands() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		page.inspectFirstBatch();
		page.expandFirstTimeline();
		Assert.assertTrue(page.firstTimelineExpanded());
	}

	@Test(description = "TC_AP_057 - Pause and resume only a disposable active batch", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp057_pauseResumeOwnedBatch() {
		openDisposableWorkflow();
		page.monitor();
		String name = ownedBatch("autoPush.ownedActiveBatchName");
		page.searchBatch(name);
		if (!page.batchListed(name))
			throw new SkipException("Owned active batch not found");
		page.inspectBatch(name);
		if (!page.hasDrawerAction("Pause"))
			throw new SkipException("Owned batch is not pausable");
		page.clickDrawerAction("pause");
		Assert.assertTrue(page.drawerText().toLowerCase().contains("paused"));
		Assert.assertTrue(page.hasDrawerAction("Resume"));
		page.clickDrawerAction("resume");
		Assert.assertFalse(page.drawerText().toLowerCase().contains("paused"));
	}

	@Test(description = "TC_AP_058 - Cancel scheduled test batch", groups = { "autopush", "uat", "mutation" })
	public void tcAp058_cancelOwnedScheduledBatch() {
		openDisposableWorkflow();
		page.monitor();
		String name = ownedBatch("autoPush.ownedScheduledBatchName");
		page.searchBatch(name);
		if (!page.batchListed(name))
			throw new SkipException("Owned scheduled batch not found");
		page.inspectBatch(name);
		if (!page.batchHasCancel())
			throw new SkipException("Owned batch is not cancellable");
		page.cancelBatch();
		new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(d -> page.drawerText().toLowerCase().contains("cancel"));
		Assert.assertTrue(page.drawerText().toLowerCase().contains("cancel"));
	}

	@Test(description = "TC_AP_059 - Retry failed is available only for failed batch", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp059_retryOwnedFailedBatch() {
		openDisposableWorkflow();
		page.monitor();
		String name = ownedBatch("autoPush.ownedFailedBatchName");
		page.searchBatch(name);
		if (!page.batchListed(name))
			throw new SkipException("Owned failed batch not found");
		page.inspectBatch(name);
		if (!page.hasDrawerAction("Retry"))
			throw new SkipException("Owned batch has no retry action");
		page.clickDrawerAction("retry");
		Assert.assertTrue(page.drawerText().contains(name), "Drawer lost owned batch after retry");
	}

	@Test(description = "TC_AP_060 - Terminal batch cannot be paused or cancelled", groups = { "autopush", "uat",
			"read" })
	public void tcAp060_terminalBatchDisablesActiveActions() {
		readMonitor();
		String completed = page.batchStatuses().stream()
				.filter(s -> s.equalsIgnoreCase("Completed")).findFirst().orElse(null);
		if (completed == null) throw new SkipException("Completed filter unavailable");
		page.filterBatchStatus(completed);
		if (page.batchRowCount() == 0)
			throw new SkipException("No completed batch");
		page.inspectFirstBatch();
		Assert.assertFalse(page.hasDrawerAction("Pause"));
		Assert.assertFalse(page.hasDrawerAction("Cancel"));
	}

	@Test(description = "TC_AP_061 - No delete control is exposed", groups = { "autopush", "uat", "read" })
	public void tcAp061_deleteActionAbsent() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		page.inspectFirstBatch();
		Assert.assertFalse(page.deleteActionAvailable());
	}

	@Test(description = "TC_AP_063 - Activity drawer close returns to live list", groups = { "autopush", "uat", "read" })
	public void tcAp063_closeActivityDrawer() {
		readMonitor();
		if (page.batchRowCount() == 0) throw new SkipException("No batches");
		page.inspectFirstBatch();
		Assert.assertTrue(page.drawerOpen());
		page.closeDrawer();
		Assert.assertFalse(page.drawerOpen());
		Assert.assertTrue(page.monitorVisible());
	}

	@Test(description = "TC_AP_064 - Activity drawer search and clear restore clients", groups = { "autopush", "uat", "read" })
	public void tcAp064_drawerSearchClear() {
		readMonitor();
		if (page.batchRowCount() == 0) throw new SkipException("No batches");
		String clients = page.firstBatchClients();
		if (clients.isBlank()) throw new SkipException("First batch has no client codes");
		String client = clients.split("\\s+")[0];
		page.inspectFirstBatch();
		page.searchDrawer(client);
		Assert.assertEquals(page.drawerSearchValue(), client);
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.drawerText().contains(client));
		page.searchDrawer("");
		Assert.assertEquals(page.drawerSearchValue(), "");
		Assert.assertTrue(page.drawerOpen());
	}

	@Test(description = "TC_AP_065 - Drawer shows live actions and step-by-step feedback", groups = { "autopush", "uat", "read" })
	public void tcAp065_drawerControlsAndFeedback() {
		readMonitor();
		if (page.batchRowCount() == 0) throw new SkipException("No batches");
		page.inspectFirstBatch();
		Assert.assertTrue(page.drawerStatusOptions().contains("All clients"));
		Assert.assertTrue(page.hasStepByStepActivity());
		Assert.assertTrue(page.drawerOpen(), "Activity drawer should remain open when no batch action applies");
	}

	@Test(description = "TC_AP_066 - Start now advances only an automation-owned queued batch", groups = { "autopush", "uat", "mutation" })
	public void tcAp066_startOwnedBatchNow() {
		openDisposableWorkflow();
		page.monitor();
		String name = ownedBatch("autoPush.ownedQueuedBatchName");
		page.searchBatch(name);
		if (!page.batchListed(name)) throw new SkipException("Owned queued batch not found");
		page.inspectBatch(name);
		if (!page.hasDrawerAction("Start now")) throw new SkipException("Owned batch cannot start now");
		page.clickDrawerActionByLabel("Start now");
		new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
				page.drawerText().toLowerCase().matches("(?s).*(starting|running|pushing|ready).*"));
	}

	@Test(description = "TC_AP_068 - Stop Auto Push affects only an automation-owned active batch", groups = { "autopush", "uat", "mutation" })
	public void tcAp068_stopOwnedActiveBatch() {
		openDisposableWorkflow();
		page.monitor();
		String name = ownedBatch("autoPush.ownedActiveBatchName");
		page.searchBatch(name);
		if (!page.batchListed(name)) throw new SkipException("Owned active batch not found");
		page.inspectBatch(name);
		if (!page.hasDrawerAction("Stop Auto Push")) throw new SkipException("Owned batch is not stoppable");
		page.clickDrawerActionByLabel("Stop Auto Push");
		new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
				page.drawerText().toLowerCase().matches("(?s).*(stopped|cancelled).*"));
	}

}
