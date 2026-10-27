package com.ui.cloud.rekyc.autopush;

import java.util.List;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public final class AutoPushConfigurationTest extends AutoPushUatTestBase {
	private void readConfig() {
		openReadWorkflow();
		page.configuration();
	}

	private void disposableConfig() {
		openDisposableWorkflow();
		page.configuration();
	}

	@Test(description = "TC_AP_008 - Configuration tab navigation", groups = { "autopush", "uat", "read" })
	public void tcAp008_configurationControlsRender() {
		readConfig();
		Assert.assertTrue(page.configurationVisible());
		Assert.assertFalse(page.pushOrder().isEmpty());
	}

	@Test(description = "TC_AP_009 - Enable automatic push switch persists and restores", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp009_enablementPersistsAndRestores() {
		disposableConfig();
		page.entries();
		if (page.queueEligible() != 0)
			throw new SkipException("Eligible entries could be launched");
		page.configuration();
		boolean original = page.automaticPushEnabled();
		try {
			page.setAutomaticPushEnabled(!original);
			page.saveConfiguration();
			disposableConfig();
			Assert.assertEquals(page.automaticPushEnabled(), !original);
		} finally {
			disposableConfig();
			page.setAutomaticPushEnabled(original);
			page.saveConfiguration();
		}
		disposableConfig();
		Assert.assertEquals(page.automaticPushEnabled(), original);
	}

	@Test(description = "TC_AP_010 - Start now mode displays immediate behavior", groups = { "autopush", "uat",
			"read" })
	public void tcAp010_startNowHidesDelay() {
		readConfig();
		try {
			page.setConfigStartMode("Start now");
			Assert.assertTrue(page.delayFieldHidden());
			Assert.assertFalse(page.areWindowFieldsVisible());
		} finally {
			page.revertConfiguration();
		}
	}

	@Test(description = "TC_AP_011 - Start after mode exposes delay", groups = { "autopush", "uat", "read" })
	public void tcAp011_startAfterShowsDelay() {
		readConfig();
		try {
			page.setConfigStartMode("Start after");
			Assert.assertTrue(page.isDelayFieldVisible());
		} finally {
			page.revertConfiguration();
		}
	}

	@Test(description = "TC_AP_012 - Delay rejects blank, zero and negative values", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp012_delayValidation() {
		requireMutationOptIn();
		throw new SkipException("Need confirmed delay bounds before submitting invalid configuration");
	}

	@Test(description = "TC_AP_013 - Daily time window exposes start and end", groups = { "autopush", "uat", "read" })
	public void tcAp013_dailyWindowFieldsRender() {
		readConfig();
		try {
			page.setConfigStartMode("Daily time window");
			Assert.assertTrue(page.areWindowFieldsVisible());
			Assert.assertTrue(page.delayFieldHidden());
		} finally {
			page.revertConfiguration();
		}
	}

	@Test(description = "TC_AP_014 - Daily window validates missing or reversed times", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp014_dailyWindowValidation() {
		requireMutationOptIn();
		throw new SkipException("Need product rule for overnight windows before asserting reversed time is invalid");
	}

	@Test(description = "TC_AP_015 - Auto-select eligible Verify entries switch persists and restores", groups = {
			"autopush", "uat", "mutation" })
	public void tcAp015_autoSelectPersistsAndRestores() {
		disposableConfig();
		page.entries();
		if (page.queueEligible() != 0)
			throw new SkipException("Eligible entries could be launched");
		page.configuration();
		boolean original = page.autoSelectEnabled();
		try {
			page.setAutoSelectEnabled(!original);
			page.saveConfiguration();
			disposableConfig();
			Assert.assertEquals(page.autoSelectEnabled(), !original);
		} finally {
			disposableConfig();
			page.setAutoSelectEnabled(original);
			page.saveConfiguration();
		}
		disposableConfig();
		Assert.assertEquals(page.autoSelectEnabled(), original);
	}

	@Test(description = "TC_AP_016 - Default failure policy options render", groups = { "autopush", "uat", "read" })
	public void tcAp016_failurePoliciesRender() {
		readConfig();
		Assert.assertTrue(page.failureModes().containsAll(
				List.of("Retry failed step, then stop entry", "Stop this entry immediately", "Log error & continue")));
	}

	@Test(description = "TC_AP_017 - Retry policy exposes numeric and jitter fields", groups = { "autopush", "uat",
			"read" })
	public void tcAp017_retryFieldsRender() {
		readConfig();
		try {
			page.setFailureMode("Retry failed step, then stop entry");
			Assert.assertTrue(page.retryFieldsVisible());
			Assert.assertTrue(page.retryJitterVisible());
		} finally {
			page.revertConfiguration();
		}
	}

	@Test(description = "TC_AP_018 - Retry limits validate invalid values", groups = { "autopush", "uat", "mutation" })
	public void tcAp018_retryLimitValidation() {
		requireMutationOptIn();
		throw new SkipException("Need product-defined retry limits before submitting invalid retry policy");
	}

	@Test(description = "TC_AP_019 - Failure policy update persists and restores", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp019_failurePolicyPersistsAndRestores() {
		disposableConfig();
		String original = page.failureMode();
		String alternate = original.equals("Log error & continue") ? "Stop this entry immediately"
				: "Log error & continue";
		try {
			page.setFailureMode(alternate);
			page.saveConfiguration();
			disposableConfig();
			Assert.assertEquals(page.failureMode(), alternate);
		} finally {
			disposableConfig();
			page.setFailureMode(original);
			page.saveConfiguration();
		}
		disposableConfig();
		Assert.assertEquals(page.failureMode(), original);
	}

	@Test(description = "TC_AP_020 - Push order displays configured operations", groups = { "autopush", "uat", "read" })
	public void tcAp020_pushOrderHasUniqueOperations() {
		readConfig();
		List<String> order = page.pushOrder();
		Assert.assertFalse(order.isEmpty());
		Assert.assertEquals(order.stream().distinct().count(), order.size());
	}

	@Test(description = "TC_AP_021 - First up and last down are disabled", groups = { "autopush", "uat", "read" })
	public void tcAp021_sequenceBoundaryButtonsDisabled() {
		readConfig();
		Assert.assertTrue(page.firstUpDisabled());
		Assert.assertTrue(page.lastDownDisabled());
	}

	@Test(description = "TC_AP_022 - Move-down changes order; Revert restores", groups = { "autopush", "uat", "read" })
	public void tcAp022_moveAndRevertSequence() {
		readConfig();
		List<String> original = page.pushOrder();
		if (original.size() < 2)
			throw new SkipException("Need at least two operations");
		try {
			page.moveFirstOperationDown();
			Assert.assertNotEquals(page.pushOrder(), original);
		} finally {
			page.revertConfiguration();
		}
		Assert.assertEquals(page.pushOrder(), original);
	}

	@Test(description = "TC_AP_023 - Keyboard reorders sequence", groups = { "autopush", "uat", "read" })
	public void tcAp023_keyboardReordersSequence() {
		readConfig();
		List<String> original = page.pushOrder();
		if (original.size() < 2)
			throw new SkipException("Need at least two operations");
		try {
			page.moveFirstOperationDownByKeyboard();
			Assert.assertNotEquals(page.pushOrder(), original);
		} finally {
			page.revertConfiguration();
		}
		Assert.assertEquals(page.pushOrder(), original);
	}

	@Test(description = "TC_AP_024 - Operation-level failure overrides are available", groups = { "autopush", "uat",
			"read" })
	public void tcAp024_operationPoliciesRender() {
		readConfig();
		Assert.assertTrue(page.hasOperationFailurePolicy());
		Assert.assertTrue(page.operationPolicyOptions()
				.containsAll(List.of("Use inherited policy", "Retry, then stop", "Stop entry", "Log & continue")));
	}

	@Test(description = "TC_AP_025 - KRA and KRA V3 preserve push-to-SFTP steps", groups = { "autopush", "uat",
			"read" })
	public void tcAp025_kraSftpPoliciesRender() {
		readConfig();
		if (!page.pushOrder().containsAll(List.of("KRA", "KRA_V3")))
			throw new SkipException("KRA/KRA V3 not configured");
		for (String step : List.of("KRA PUSH", "KRA SFTP", "KRA V3 PUSH", "KRA V3 SFTP"))
			Assert.assertTrue(page.hasStepPolicy(step), "Missing " + step);
	}

	@Test(description = "TC_AP_026 - CDSL and NSDL preserve internal step order", groups = { "autopush", "uat",
			"read" })
	public void tcAp026_cdslNsdlStepsRender() {
		readConfig();
		if (!page.pushOrder().containsAll(List.of("CDSL", "NSDL")))
			throw new SkipException("CDSL/NSDL not configured");
		for (String step : List.of("CDSL PUSH", "CDSL STATUS", "CDSL RESPONSE", "NSDL PUSH", "NSDL STATUS"))
			Assert.assertTrue(page.hasStepPolicy(step), "Missing " + step);
	}

	@Test(description = "TC_AP_027 - Save configuration persists sequence change", groups = { "autopush", "uat",
			"mutation" })
	public void tcAp027_savedOrderPersistsAndRestores() {
		disposableConfig();
		List<String> original = page.pushOrder();
		if (original.size() < 2)
			throw new SkipException("Need two disposable operations");
		try {
			page.moveFirstOperationDown();
			List<String> changed = page.pushOrder();
			page.saveConfiguration();
			disposableConfig();
			Assert.assertEquals(page.pushOrder(), changed);
		} finally {
			disposableConfig();
			if (!page.pushOrder().equals(original)) {
				page.moveFirstOperationDown();
				Assert.assertEquals(page.pushOrder(), original, "Could not restore original order");
				page.saveConfiguration();
			}
		}
		disposableConfig();
		Assert.assertEquals(page.pushOrder(), original);
	}

	@Test(description = "TC_AP_028 - Revert discards all unsaved configuration edits", groups = { "autopush", "uat",
			"read" })
	public void tcAp028_revertRestoresMultipleFields() {
		readConfig();
		String mode = page.configStartMode();
		List<String> order = page.pushOrder();
		if (order.size() < 2)
			throw new SkipException("Need two operations");
		page.setConfigStartMode(mode.equals("Start now") ? "Start after" : "Start now");
		page.moveFirstOperationDown();
		page.revertConfiguration();
		Assert.assertEquals(page.configStartMode(), mode);
		Assert.assertEquals(page.pushOrder(), order);
	}

}
