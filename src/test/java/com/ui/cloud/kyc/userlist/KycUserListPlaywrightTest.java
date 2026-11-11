package com.ui.cloud.kyc.userlist;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.ui.base.BaseTestPW;
import com.ui.utilities.JSONUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class KycUserListPlaywrightTest extends BaseTestPW {
	private static final String COMPANY = "DEMO CAPITAL MARKET";
	private Path evidenceDirectory;

	@Test(description = "Playwright: create, find and delete the exact Demo Capital admin user")
	public void verifyAddUserFunctionality_DemoCapital() throws Exception {
		String testUserPassword = "Qa!" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		evidenceDirectory = Path.of("logs", "evidence", "kyc_add_user_pw_" + Instant.now().toEpochMilli());
		Files.createDirectories(evidenceDirectory);
		long unique = System.currentTimeMillis();
		String name = "Ram";
		String mobile = "8" + String.format("%09d", unique % 1_000_000_000L);
		String email = "ramqa" + unique + "@gmail.com";
		try {
			page.navigate(JSONUtility.getSuperAdminWebsite().getUrl());
			capture(page, "01-login");
			page.locator("#googleSignInButton").click();
			Page google = waitForPageWith("accounts.google.com", 30_000);
			google.locator("#identifierId").fill(JSONUtility.getCredentials().getKycAdmin().getEmail());
			google.locator("#identifierNext").click();
			google.locator("input[type=password]").fill(JSONUtility.getCredentials().getKycAdmin().getPassword());
			google.locator("#passwordNext").click();
			page.locator("#search__company").waitFor();
			capture(page, "02-dashboard");
			Locator companySearch = page.locator("#search__company");
			companySearch.fill(COMPANY);
			Locator searchButton = page.locator("#search-superadmin-company-btn");
			if (searchButton.count() > 0 && searchButton.isVisible()) searchButton.click();
			else companySearch.press("Enter");
			Locator companyRow = page.locator("tr").filter(new Locator.FilterOptions().setHasText(COMPANY));
			companyRow.waitFor(new Locator.WaitForOptions().setTimeout(30_000));
			Assert.assertTrue(companyRow.locator("td").allTextContents().stream()
					.anyMatch(value -> COMPANY.equalsIgnoreCase(value.trim())), "No exact Demo Capital row");
			capture(page, "03-company-result");
			companyRow.locator("button.visit-admin").click();
			Page admin = waitForPageWith("/admin/", 30_000);
			admin.locator("a, button").filter(new Locator.FilterOptions().setHasText("User List"))
					.first().click();
			admin.locator("#userdetailsSearch").waitFor();
			capture(admin, "04-user-list");
			admin.locator(".add-user-container").click();
			admin.locator("#overlay .close-btn").click();
			admin.locator("#overlay").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
			admin.locator(".add-user-container").click();
			admin.locator("#overlay input[placeholder='Name']").fill(name);
			admin.locator("#overlay input[placeholder='Mobile']").fill(mobile);
			admin.locator("#overlay input[placeholder='Email address']").fill(email);
			admin.locator("#companyPassword").fill(testUserPassword);
			capture(admin, "05-filled-form");
			admin.locator("#overlay button.save-button").click();
			admin.locator("#overlay-save").waitFor(new Locator.WaitForOptions()
					.setState(WaitForSelectorState.HIDDEN).setTimeout(30_000));
			capture(admin, "06-save-response");
			Locator row = exactUserRow(admin, name, email, mobile);
			capture(admin, "07-created-user");
			row.locator("td:last-child .mdi-dots-vertical").click();
			row.locator("td:last-child .overlay .option[onclick^='removeUser(']").click();
			admin.locator("#openConfirmationOverlay").click();
			admin.locator("text=Deleted").waitFor();
			row.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
			capture(admin, "08-deleted-user");
		} catch (RuntimeException | Error failure) {
			for (Page candidate : page.context().pages()) capture(candidate, "failure-" + candidate.hashCode());
			throw failure;
		}
	}

	private Locator exactUserRow(Page admin, String name, String email, String mobile) {
		admin.locator("#userdetailsSearch").fill(mobile);
		Locator row = admin.locator("#user-table tbody tr")
				.filter(new Locator.FilterOptions().setHasText(mobile));
		row.waitFor(new Locator.WaitForOptions().setTimeout(30_000));
		List<String> cells = row.locator("td").allTextContents().stream().map(String::trim).toList();
		Assert.assertTrue(cells.stream().anyMatch(name::equalsIgnoreCase), "Name missing: " + cells);
		Assert.assertTrue(cells.stream().anyMatch(email::equalsIgnoreCase), "Email missing: " + cells);
		Assert.assertTrue(cells.contains(mobile), "Mobile missing: " + cells);
		return row;
	}

	private Page waitForPageWith(String fragment, long timeoutMs) {
		long deadline = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < deadline) {
			for (Page candidate : page.context().pages())
				if (!candidate.isClosed() && candidate.url().contains(fragment)) return candidate;
			page.waitForTimeout(250);
		}
		throw new AssertionError("No page reached " + fragment + "; open URLs="
				+ page.context().pages().stream().filter(candidate -> !candidate.isClosed()).map(Page::url).toList());
	}

	private void capture(Page target, String label) {
		if (target == null || target.isClosed()) return;
		try { target.screenshot(new Page.ScreenshotOptions().setPath(evidenceDirectory.resolve(label + ".png"))); }
		catch (RuntimeException ignored) { /* Preserve the original test failure. */ }
	}
}
