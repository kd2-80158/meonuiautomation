package com.ui.cloud.kyc.mandot;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.utilities.EnvironmentUtility;

/**
 * Loads only isolated UAT fixture values. Secrets and personal data must never
 * be committed; provide them through the execution environment.
 */
public final class MandotKycTestData {

    private final String mobileNumber;
    private final String dependency;
    private final JsonObject fixture;

    public MandotKycTestData(String mobileNumber, String dependency) {
        this(mobileNumber, dependency, null);
    }

    private MandotKycTestData(String mobileNumber, String dependency, JsonObject fixture) {
        this.mobileNumber = mobileNumber;
        this.dependency = dependency;
        this.fixture = fixture;
    }

    public String mobileNumber() {
        return mobileNumber;
    }

    public String dependency() {
        return dependency;
    }

    /**
     * Returns a non-sensitive fixture value for a page object when the runtime
     * fixture has it.  Never log this value: the supplied UAT record contains
     * identity data, OTPs, document links and provider tokens.
     */
    public String fixtureValue(String key) {
        if (fixture == null || !fixture.has(key) || fixture.get(key).isJsonNull()) {
            return "";
        }
        return fixture.get(key).getAsString();
    }

    public static MandotKycTestData fromEnvironment() {
        String fixtureFile = System.getenv().getOrDefault("MANDOT_KYC_FIXTURE_FILE", "").trim();
        if (!fixtureFile.isEmpty()) {
            return fromFixtureFile(Path.of(fixtureFile));
        }
        return new MandotKycTestData(
                EnvironmentUtility.getRequiredVariable("MANDOT_KYC_TEST_MOBILE"),
                System.getenv().getOrDefault("MANDOT_KYC_DEPENDENCY", "Self"));
    }

    private static MandotKycTestData fromFixtureFile(Path fixtureFile) {
        try (Reader reader = Files.newBufferedReader(fixtureFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            if (data == null) {
                throw new IllegalArgumentException("KYC fixture must contain a top-level data object.");
            }
            return new MandotKycTestData(requiredString(data, "mobile_number"),
                    optionalString(data, "select_dependancy", "Self"), data);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load MANDOT_KYC_FIXTURE_FILE. Check the file path and JSON structure.",
                    exception);
        }
    }

    private static String requiredString(JsonObject data, String key) {
        String value = optionalString(data, key, "");
        if (value.isBlank()) {
            throw new IllegalArgumentException("KYC fixture is missing required key: " + key);
        }
        return value;
    }

    private static String optionalString(JsonObject data, String key, String defaultValue) {
        JsonElement value = data.get(key);
        return value == null || value.isJsonNull() ? defaultValue : value.getAsString();
    }

    public static boolean isEnabled() {
        return Boolean.parseBoolean(System.getenv().getOrDefault("MANDOT_KYC_E2E_ENABLED", "false"));
    }

    public static long manualWaitSeconds() {
        String value = System.getenv().getOrDefault("MANDOT_KYC_MANUAL_WAIT_SECONDS", "300");
        try {
            return Math.max(30, Math.min(900, Long.parseLong(value)));
        } catch (NumberFormatException ignored) {
            return 300;
        }
    }
}
