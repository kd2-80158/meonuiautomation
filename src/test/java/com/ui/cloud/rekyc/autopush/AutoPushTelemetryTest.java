package com.ui.cloud.rekyc.autopush;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.utilities.ExtentReporterUtility;

/** Chrome network/console evidence for all three Auto Push panels. */
public final class AutoPushTelemetryTest extends AutoPushUatTestBase {
	@Test(description = "Setup, Verified clients and Live activity have healthy backend and console signals", groups = {
			"autopush", "uat", "network", "read" })
	public void allPanelsHaveHealthyNetworkAndConsole() throws IOException {
		if (!(driver instanceof ChromeDriver))
			throw new SkipException("Run with -Dbrowser=chrome for DevTools network logs");
		List<String> evidence = new ArrayList<>();
		for (String panel : List.of("configuration", "entries", "batches")) {
			driver.manage().logs().get(LogType.PERFORMANCE);
			driver.manage().logs().get(LogType.BROWSER);
			openReadWorkflow();
			switchPanel(panel);
			Assert.assertTrue(panelIsVisible(panel), panel + " did not render");
			if ("entries".equals(panel))
				Assert.assertTrue(page.queueMatching() >= page.queueEligible(), "Queue totals are inconsistent");
			List<String> requests = new ArrayList<>();
			List<String> failures = new ArrayList<>();
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
					String url = response.get("url").getAsString();
					if (!url.startsWith(baseUrl))
						continue;
					int status = response.get("status").getAsInt();
					String safePath = url.substring(baseUrl.length()).split("[?#]", 2)[0];
					requests.add(status + " " + safePath);
					if (status >= 400)
						failures.add(status + " " + safePath);
				} catch (RuntimeException malformed) {
					// Ignore unrelated browser events.
				}
			}
			List<String> consoleErrors = new ArrayList<>();
			for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
				if (entry.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue())
					consoleErrors.add(entry.getMessage().replaceAll("https?://[^\\s]+", "[URL redacted]"));
			}
			evidence.add(panel + " | backend calls=" + requests.size() + " | HTTP errors=" + failures
					+ " | console errors=" + consoleErrors);
			Assert.assertFalse(requests.isEmpty(), panel + " produced no observable backend response");
			Assert.assertTrue(failures.isEmpty(), panel + " backend errors: " + failures);
			Assert.assertTrue(consoleErrors.isEmpty(), panel + " console errors: " + consoleErrors);
		}
		Path directory = Paths.get(System.getProperty("user.dir"), "logs", "evidence");
		Files.createDirectories(directory);
		String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		Path file = directory.resolve("autopush-telemetry-" + stamp + ".txt");
		Files.writeString(file, String.join(System.lineSeparator(), evidence));
		ExtentReporterUtility.info("Auto Push network/console evidence: " + file.toAbsolutePath());
		for (String line : evidence)
			ExtentReporterUtility.info(line);
	}

	private void switchPanel(String panel) {
		switch (panel) {
		case "configuration":
			page.configuration();
			break;
		case "entries":
			page.entries();
			break;
		case "batches":
			page.monitor();
			break;
		default:
			throw new IllegalArgumentException(panel);
		}
	}

	private boolean panelIsVisible(String panel) {
		switch (panel) {
		case "configuration":
			return page.configurationVisible();
		case "entries":
			return page.entriesVisible();
		case "batches":
			return page.monitorVisible();
		default:
			return false;
		}
	}
}
