package com.ui.utilities;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.ui.models.CredentialsConfig;
import com.ui.pojos.AdminURL;
import com.ui.pojos.Config;
import com.ui.pojos.WebsiteURL;
import com.ui.pojos.WorkflowURL;

public class JSONUtility {

	private static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/config.json";

	private static Config loadConfig() {
		File configFile = new File(CONFIG_PATH);
		if (!configFile.exists()) {
			throw new RuntimeException("Config file not found at path: " + CONFIG_PATH);
		}

		try (Reader reader = new FileReader(configFile)) {
			Gson gson = new Gson();
			Config config = gson.fromJson(reader, Config.class);
			if (config == null) {
				throw new RuntimeException("Parsed Config object is NULL. Check if config.json is empty.");
			}
			return config;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load or parse config.json at: " + CONFIG_PATH, e);
		}
	}

	// ---------- ADMIN ----------
	public static AdminURL getAdmin() {
		Config config = loadConfig();
		AdminURL admin = config.getAdmin();
		if (admin == null) {
			throw new RuntimeException("Admin config missing in config.json");
		}
		return admin;
	}

	// ---------- WEBSITE ----------
	public static WebsiteURL getWebsite(String key) {
		Config config = loadConfig();
		Map websiteMap = config.getWebsite();

		if (websiteMap == null) {
			throw new RuntimeException("'website' section is missing in config.json");
		}

		WebsiteURL website = (WebsiteURL) websiteMap.get(key);
		if (website == null) {
			throw new RuntimeException(
					"Website key '" + key + "' not found in config.json. Available keys: " + websiteMap.keySet());
		}
		return website;
	}

	public static WebsiteURL getMainWebsite() {
		return getWebsite("main");
	}

	public static WebsiteURL getSuperAdminWebsite() {
		return getWebsite("superadmin");
	}

	// ---------- WORKFLOWS ----------
	public static WorkflowURL getWorkflow(String workflowKey) {
		Config config = loadConfig();
		Map workflows = config.getWorkflowURL();

		if (workflows == null) {
			throw new RuntimeException(
					"'workflowURL' map section is missing in config.json or field name mismatched in Config POJO.");
		}

		WorkflowURL workflow = (WorkflowURL) workflows.get(workflowKey);
		if (workflow == null) {
			throw new RuntimeException("Workflow key '" + workflowKey + "' not found in config.json. Available keys: "
					+ workflows.keySet());
		}
		return workflow;
	}

	public static String getWorkflowUrl(String workflowKey) {
		WorkflowURL workflow = getWorkflow(workflowKey);
		if (workflow.getUrl() == null) {
			throw new RuntimeException("The 'url' field is null for workflow key: '" + workflowKey
					+ "'. Check property key naming in config.json.");
		}
		return workflow.getUrl();
	}

	// ---------- Convenience Wrappers ----------
	public static WorkflowURL getWorkflow1() {
		return getWorkflow("workflow1");
	}

	public static WorkflowURL getRekycUrl() {
		return getWorkflow("rekyc");
	}

	public static CredentialsConfig getCredentials() {

		try (InputStream inputStream = JSONUtility.class.getClassLoader()
				.getResourceAsStream("config/credentials.json")) {

			if (inputStream == null) {
				throw new RuntimeException("config/credentials.json not found in classpath");
			}

			InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

			Gson gson = new Gson();

			CredentialsConfig config = gson.fromJson(reader, CredentialsConfig.class);

			if (config == null || config.getKycAdmin() == null) {

				throw new RuntimeException("Invalid credentials.json configuration");
			}

			return config;

		} catch (Exception e) {

			throw new RuntimeException("Unable to load credentials.json", e);
		}
	}
}