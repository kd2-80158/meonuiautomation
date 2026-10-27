package com.ui.cloud.rekyc.autopush;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public final class AutoPushBatchMonitorTest extends AutoPushUatTestBase {
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
		Assert.assertTrue(page.batchStatuses().contains("All statuses"));
	}

	@Test(description = "TC_AP_048 - Batch-name search narrows monitor list", groups = { "autopush", "uat", "read" })
	public void tcAp048_batchCodeSearchWorks() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		String code = page.firstBatchCode();
		page.searchBatch(code);
		new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> page.batchListed(code));
		Assert.assertTrue(page.visibleBatchText().contains(code));
	}

	@Test(description = "TC_AP_049 - Batch search supports client code", groups = { "autopush", "uat", "read" })
	public void tcAp049_clientCodeSearchWorks() {
		readMonitor();
		String client = System.getProperty("autoPush.existingClientCode", "MEON2");
		page.searchBatch(client);
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches for configured client");
		Assert.assertTrue(page.visibleBatchText().contains(client));
	}

	@Test(description = "TC_AP_050 - No-match batch search shows empty list", groups = { "autopush", "uat", "read" })
	public void tcAp050_unmatchedBatchSearchEmpty() {
		readMonitor();
		page.searchBatch("__no_matching_batch__");
		Assert.assertFalse(page.batchListed("__no_matching_batch__"));
		Assert.assertEquals(page.batchRowCount(), 0);
	}

	@Test(description = "TC_AP_051 - Batch status filter covers all states", groups = { "autopush", "uat", "read" })
	public void tcAp051_statusFilterMatchesRows() {
		readMonitor();
		java.util.Map<String, String> classes = java.util.Map.of("WAITING_TO_START", "queued", "RUNNING", "running",
				"PAUSED", "paused", "COMPLETED", "completed", "COMPLETED_WITH_ERRORS", "completed_with_errors",
				"CANCELLED", "cancelled");
		for (java.util.Map.Entry<String, String> item : classes.entrySet()) {
			page.filterBatchStatus(item.getKey());
			for (String css : page.visibleBatchStatusClasses())
				Assert.assertTrue(css.contains(item.getValue()), "Mismatched status for " + item.getKey() + ": " + css);
		}
		page.filterBatchStatus("All statuses");
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
		page.inspectFirstBatch();
		Assert.assertEquals(page.drawerTitle(), name);
		Assert.assertTrue(page.batchSummary().contains(code));
	}

	@Test(description = "TC_AP_055 - Drawer entry search and status filter work", groups = { "autopush", "uat",
			"read" })
	public void tcAp055_drawerFiltersEntries() {
		readMonitor();
		if (page.batchRowCount() == 0)
			throw new SkipException("No batches");
		page.inspectFirstBatch();
		String client = System.getProperty("autoPush.existingClientCode", "MEON2");
		page.searchDrawer(client);
		Assert.assertTrue(page.drawerText().contains(client));
		page.filterDrawerStatus("All entry statuses");
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
		page.filterBatchStatus("COMPLETED");
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

}
