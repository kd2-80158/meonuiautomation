package com.ui.cloud.digilocker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.TreeSet;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ui.product.digilocker.pages.DigilockerWebhookConsentPage;
import com.ui.product.digilocker.pages.DigilockerUatWebhookPage;
import com.ui.product.rekyc.pages.superadmin.LoginPage_REKYCSuperadmin;
import com.ui.utilities.ExtentReporterUtility;
import com.ui.utilities.JSONUtility;

/** Synthetic receiver checks plus an opt-in Aadhaar/consent journey. */
public final class DigilockerWebhookReceiverTest {
    private HttpClient client;
    private URI callback;
    private URI eventsUrl;
    private String adminToken;

    @BeforeClass
    public void configure() {
        String url = System.getenv("DIGILOCKER_WEBHOOK_URL");
        adminToken = System.getenv("DIGILOCKER_WEBHOOK_ADMIN_TOKEN");
        if (url == null || url.isBlank() || adminToken == null || adminToken.isBlank())
            throw new SkipException("Set DIGILOCKER_WEBHOOK_URL and DIGILOCKER_WEBHOOK_ADMIN_TOKEN to run receiver checks");
        callback = URI.create(url);
        Assert.assertEquals(callback.getScheme(), "https", "Never send DigiLocker callbacks over HTTP");
        Assert.assertEquals(callback.getHost(), "api.extora.store", "Use the verified OCI QA receiver");
        eventsUrl = URI.create(callback.getScheme() + "://" + callback.getAuthority() + "/qa/events");
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest post(URI url, String body, String contentType) {
        return HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(15))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
    }

    private JsonObject events() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(eventsUrl).timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + adminToken).GET().build();
        HttpResponse<String> response = send(request);
        Assert.assertEquals(response.statusCode(), 200, "Authenticated metadata readback");
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private JsonObject rawEvents() throws Exception {
        URI rawUrl = URI.create(callback.getScheme() + "://" + callback.getAuthority() + "/qa/raw-events");
        HttpRequest request = HttpRequest.newBuilder(rawUrl).timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + adminToken).GET().build();
        HttpResponse<String> response = send(request);
        Assert.assertEquals(response.statusCode(), 200, "Authenticated short-lived raw callback readback");
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private void attachReceivedCallbackProof(String expectedHash, long notBeforeMs) throws Exception {
        for (JsonElement item : rawEvents().getAsJsonArray("events")) {
            JsonObject entry = item.getAsJsonObject();
            if (entry.get("receivedAtMs").getAsLong() < notBeforeMs) continue;
            JsonObject payload = JsonParser.parseString(entry.get("rawBody").getAsString()).getAsJsonObject();
            if (!expectedHash.equals(canonicalHash(payload))) continue;
            Assert.assertEquals(entry.get("event").getAsString(), "entire_data.ready", "Callback event header");
            Assert.assertEquals(payload.get("code").getAsInt(), 200, "Received callback response code");
            Assert.assertTrue(payload.get("success").getAsBoolean(), "Received callback success flag");
            JsonObject data = payload.getAsJsonObject("data");
            Assert.assertNotNull(data, "Received callback data");
            JsonObject proof = new JsonObject();
            proof.addProperty("event", entry.get("event").getAsString());
            proof.addProperty("receivedAtMs", entry.get("receivedAtMs").getAsLong());
            proof.addProperty("deliveryCount", entry.get("deliveryCount").getAsInt());
            proof.addProperty("code", payload.get("code").getAsInt());
            proof.addProperty("status", payload.get("status").getAsString());
            proof.addProperty("success", payload.get("success").getAsBoolean());
            proof.addProperty("payloadSha256", expectedHash);
            JsonObject documents = new JsonObject();
            for (String field : new String[] { "aadhar_filename", "aadhar_xml", "aadhar_img_filename", "pan_image_path" })
                documents.addProperty(field, data.has(field) && !data.get(field).isJsonNull()
                        && !data.get(field).getAsString().isBlank());
            documents.addProperty("other_documents_files", data.has("other_documents_files")
                    && data.get("other_documents_files").isJsonObject()
                    ? data.getAsJsonObject("other_documents_files").size() : 0);
            proof.add("documentsPresent", documents);
            ExtentReporterUtility.info("Received webhook response (identity fields and URLs redacted): " + proof);
            return;
        }
        Assert.fail("Matching raw callback proof was not available in the 15-minute receiver window");
    }

    private JsonArray webhookRelayRequests(String binId) throws Exception {
        Assert.assertTrue(binId.matches("[0-9a-fA-F-]{36}"), "Webhook Relay bin ID must be a UUID");
        URI bin = URI.create("https://bin.webhookrelay.com/v1/bins/" + binId);
        HttpResponse<String> response = send(HttpRequest.newBuilder(bin)
                .timeout(Duration.ofSeconds(15)).GET().build());
        Assert.assertEquals(response.statusCode(), 200, "Webhook Relay bin readback HTTP status");
        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        Assert.assertTrue(body.has("requests") && body.get("requests").isJsonArray(),
                "Webhook Relay bin readback is missing requests");
        return body.getAsJsonArray("requests");
    }

    private boolean isMatchingRelayPost(JsonObject request, String expectedHash, long notBeforeMs) throws Exception {
        if (!request.has("method") || !"POST".equalsIgnoreCase(request.get("method").getAsString())) return false;
        if (!request.has("receivedAt") || request.get("receivedAt").getAsLong() * 1000 < notBeforeMs) return false;
        if (!request.has("body") || !request.get("body").isJsonPrimitive()) return false;
        JsonObject payload;
        try {
            payload = JsonParser.parseString(request.get("body").getAsString()).getAsJsonObject();
        } catch (RuntimeException ignored) {
            return false;
        }
        if (!expectedHash.equals(canonicalHash(payload))) return false;
        Assert.assertEquals(request.get("responseStatus").getAsInt(), 200, "Webhook Relay delivery response");
        Assert.assertTrue(request.has("ip") && !request.get("ip").getAsString().isBlank(),
                "Webhook Relay did not record the request IP");
        JsonObject headers = request.getAsJsonObject("header");
        Assert.assertNotNull(headers, "Webhook Relay headers are missing");
        JsonObject eventHeader = headers.getAsJsonObject("X-Digilocker-Event");
        Assert.assertNotNull(eventHeader, "DigiLocker event header is missing");
        Assert.assertTrue(eventHeader.getAsJsonArray("values").toString().contains("entire_data.ready"),
                "Unexpected DigiLocker event type");
        Assert.assertTrue(payload.get("success").getAsBoolean(), "Webhook export success flag");
        Assert.assertEquals(payload.get("code").getAsInt(), 200, "Webhook export code");
        Assert.assertTrue(payload.has("data") && payload.get("data").isJsonObject(), "Webhook export data is missing");
        return true;
    }

    private String successPayload(String id, String marker) {
        JsonObject data = new JsonObject();
        data.addProperty("aadhar_no", "xxxxxxxx0000");
        data.addProperty("pan_number", "SYNTHETIC-PAN");
        data.addProperty("aadhar_filename", "https://example.invalid/" + marker + "/AADHAR.pdf");
        data.addProperty("aadhar_xml", "https://example.invalid/" + marker + "/AADHAR.xml");
        data.addProperty("aadhar_img_filename", "https://example.invalid/" + marker + "/Photo.jpg");
        data.addProperty("pan_image_path", "https://example.invalid/" + marker + "/PAN.pdf");
        JsonObject other = new JsonObject();
        other.addProperty("in.gov.transport-DRVLC-TEST", "https://example.invalid/" + marker + "/DRVLC.pdf");
        other.addProperty("in.gov.transport-DRVLC-TEST-xml", "https://example.invalid/" + marker + "/DRVLC.xml");
        data.add("other_documents_files", other);
        JsonObject payload = new JsonObject();
        payload.addProperty("event_id", id);
        payload.addProperty("code", 200);
        payload.add("data", data);
        payload.addProperty("msg", "Data Exported Successfully");
        payload.addProperty("status", "success");
        payload.addProperty("success", true);
        return payload.toString();
    }

    private String dedupeKey(String id) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(id.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte value : digest) hex.append(String.format("%02x", value & 0xff));
        return "id:" + hex.substring(0, 24);
    }

    private HttpResponse<String> postJson(URI url, JsonObject body) throws Exception {
        return send(HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())).build());
    }

    private JsonObject successfulJson(HttpResponse<String> response, String step) {
        Assert.assertEquals(response.statusCode(), 200, step + " HTTP status");
        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        boolean success = body.has("success") && body.get("success").getAsBoolean();
        if (!success && body.has("status") && body.get("status").isJsonPrimitive()
                && body.get("status").getAsJsonPrimitive().isBoolean())
            success = body.get("status").getAsBoolean();
        Assert.assertTrue(success, step + " success flag");
        return body;
    }

    private String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank())
            throw new SkipException("Set " + name + " in the test process environment; never commit this value");
        return value;
    }

    private JsonElement sortedJson(JsonElement value) {
        if (value.isJsonObject()) {
            JsonObject sorted = new JsonObject();
            for (String key : new TreeSet<>(value.getAsJsonObject().keySet()))
                sorted.add(key, sortedJson(value.getAsJsonObject().get(key)));
            return sorted;
        }
        if (value.isJsonArray()) {
            JsonArray sorted = new JsonArray();
            for (JsonElement item : value.getAsJsonArray()) sorted.add(sortedJson(item));
            return sorted;
        }
        return value;
    }

    private String canonicalHash(JsonObject body) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(sortedJson(body).toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte value : digest) hex.append(String.format("%02x", value & 0xff));
        return hex.toString();
    }

    private void loginToUatSuperadmin(FirefoxDriver browser) {
        DigilockerUatWebhookPage page = new DigilockerUatWebhookPage(browser);
        page.openSuperadmin();
        if (page.needsGoogleSignIn()) {
            page.clickGoogleSignIn();
            new WebDriverWait(browser, Duration.ofSeconds(40)).until(d -> {
                for (String handle : d.getWindowHandles()) {
                    d.switchTo().window(handle);
                    if (d.getCurrentUrl().contains("accounts.google.com")) return true;
                }
                return false;
            });
            By workAccount = By.xpath("//*[contains(normalize-space(.),'saurabh.chhimwal@meon.co.in') and (self::a or self::div[@role='link'])]");
            if (!browser.findElements(workAccount).isEmpty()) {
                browser.findElement(workAccount).click();
            } else {
                LoginPage_REKYCSuperadmin google = new LoginPage_REKYCSuperadmin(browser);
                google.waitForEmailPage().writeService("email", JSONUtility.getCredentials().getKycAdmin().getEmail())
                    .clickOnButton("nextbtn").waitForPasswordPage()
                    .writeService("password", JSONUtility.getCredentials().getKycAdmin().getPassword())
                    .clickOnButton("nextbtn");
            }
        }
        new WebDriverWait(browser, Duration.ofSeconds(60)).until(d -> {
            for (String handle : d.getWindowHandles()) {
                d.switchTo().window(handle);
                if (!d.findElements(By.id("search__company")).isEmpty()) return true;
            }
            return false;
        });
        page.searchDemoCapital();
        ExtentReporterUtility.info("DigiLocker UAT superadmin login and Demo Capital search succeeded");
    }

    @Test(description = "DL_WH_API_01 - Valid TLS certificate and HTTPS callback")
    public void callbackUsesValidTls() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(callback).timeout(Duration.ofSeconds(15)).GET().build();
        Assert.assertEquals(send(request).statusCode(), 404, "GET must not deliver a webhook");
    }

    @Test(description = "DL_WH_API_02 - Unknown callback path is rejected")
    public void forgedCallbackPathIsRejected() throws Exception {
        URI forged = URI.create(eventsUrl.toString().replace("/qa/events", "/webhooks/digilocker/invalid"));
        Assert.assertEquals(send(post(forged, successPayload(UUID.randomUUID().toString(), "forged"), "application/json")).statusCode(), 404);
    }

    @Test(description = "DL_WH_API_03 - Metadata readback requires bearer authentication")
    public void metadataEndpointRequiresAdminToken() throws Exception {
        Assert.assertEquals(send(HttpRequest.newBuilder(eventsUrl).GET().build()).statusCode(), 401);
        Assert.assertEquals(send(HttpRequest.newBuilder(eventsUrl).header("Authorization", "Bearer wrong-token").GET().build()).statusCode(), 401);
    }

    @Test(description = "DL_WH_API_04 - Malformed, incomplete and non-JSON deliveries are rejected")
    public void invalidPayloadsAreRejected() throws Exception {
        Assert.assertEquals(send(post(callback, "{bad", "application/json")).statusCode(), 400);
        Assert.assertEquals(send(post(callback, "{\"status\":\"success\"}", "application/json")).statusCode(), 400);
        Assert.assertEquals(send(post(callback, "plain", "text/plain")).statusCode(), 415);
    }

    @Test(description = "DL_WH_API_05 - Duplicate delivery is idempotent and metadata is redacted")
    public void duplicateDeliveryAndMapping() throws Exception {
        String id = "qa-" + UUID.randomUUID();
        String marker = UUID.randomUUID().toString();
        String payload = successPayload(id, marker);
        HttpResponse<String> first = send(post(callback, payload, "application/json"));
        Assert.assertEquals(first.statusCode(), 200);
        Assert.assertFalse(JsonParser.parseString(first.body()).getAsJsonObject().get("duplicate").getAsBoolean());
        for (int i = 0; i < 2; i++) {
            HttpResponse<String> repeat = send(post(callback, payload, "application/json"));
            Assert.assertEquals(repeat.statusCode(), 200);
            Assert.assertTrue(JsonParser.parseString(repeat.body()).getAsJsonObject().get("duplicate").getAsBoolean());
        }
        JsonObject readback = events();
        String text = readback.toString();
        Assert.assertFalse(text.contains("xxxxxxxx0000"));
        Assert.assertFalse(text.contains("SYNTHETIC-PAN"));
        Assert.assertFalse(text.contains(marker));
        JsonArray entries = readback.getAsJsonArray("events");
        int matches = 0;
        for (int i = 0; i < entries.size(); i++) {
            JsonObject entry = entries.get(i).getAsJsonObject();
            if (!dedupeKey(id).equals(entry.get("dedupeKey").getAsString())) continue;
            matches++;
            Assert.assertEquals(entry.get("deliveryCount").getAsInt(), 3,
                    "First delivery plus two retries must remain one event");
            Assert.assertEquals(entry.get("payloadHash").getAsString(),
                    canonicalHash(JsonParser.parseString(payload).getAsJsonObject()), "Export and webhook payload hashes must match");
            Assert.assertTrue(entry.get("exportSuccess").getAsBoolean());
            Assert.assertEquals(entry.get("responseCode").getAsInt(), 200);
            JsonObject docs = entry.getAsJsonObject("documentPresence");
            for (String key : new String[] { "aadhaarPdf", "aadhaarXml", "aadhaarImage", "panPdf", "drivingLicencePdf", "drivingLicenceXml" })
                Assert.assertTrue(docs.get(key).getAsBoolean(), "Missing document flag: " + key);
        }
        Assert.assertEquals(matches, 1, "A repeated event must create exactly one record");
    }

    @Test(description = "DL_WH_51 - Fresh journeys for one identity are distinct; exact redelivery is deduplicated")
    public void freshJourneysAreNotDuplicateDeliveries() throws Exception {
        JsonObject first = JsonParser.parseString(successPayload("synthetic-first", UUID.randomUUID().toString()))
                .getAsJsonObject();
        JsonObject second = JsonParser.parseString(successPayload("synthetic-second", UUID.randomUUID().toString()))
                .getAsJsonObject();
        first.remove("event_id");
        second.remove("event_id");
        String firstBody = first.toString();
        String secondBody = second.toString();
        String firstHash = canonicalHash(first);
        String secondHash = canonicalHash(second);
        Assert.assertNotEquals(firstHash, secondHash, "Fresh journeys must have distinct document URLs/payloads");

        HttpResponse<String> firstResponse = send(post(callback, firstBody, "application/json"));
        HttpResponse<String> secondResponse = send(post(callback, secondBody, "application/json"));
        Assert.assertEquals(firstResponse.statusCode(), 200);
        Assert.assertEquals(secondResponse.statusCode(), 200);
        JsonObject firstReceipt = JsonParser.parseString(firstResponse.body()).getAsJsonObject();
        JsonObject secondReceipt = JsonParser.parseString(secondResponse.body()).getAsJsonObject();
        Assert.assertFalse(firstReceipt.get("duplicate").getAsBoolean(), "First journey is new");
        Assert.assertFalse(secondReceipt.get("duplicate").getAsBoolean(), "Second journey is also new");

        HttpResponse<String> repeatedResponse = send(post(callback, firstBody, "application/json"));
        Assert.assertEquals(repeatedResponse.statusCode(), 200);
        JsonObject repeatedReceipt = JsonParser.parseString(repeatedResponse.body()).getAsJsonObject();
        Assert.assertTrue(repeatedReceipt.get("duplicate").getAsBoolean(), "Exact redelivery is a duplicate");

        int firstMatches = 0;
        int secondMatches = 0;
        for (JsonElement item : events().getAsJsonArray("events")) {
            JsonObject event = item.getAsJsonObject();
            String digest = event.get("payloadHash").getAsString();
            if (firstHash.equals(digest)) {
                firstMatches++;
                Assert.assertEquals(event.get("deliveryCount").getAsInt(), 2);
                Assert.assertEquals(event.get("dedupeKey").getAsString(), "body:" + firstHash);
            }
            if (secondHash.equals(digest)) {
                secondMatches++;
                Assert.assertEquals(event.get("deliveryCount").getAsInt(), 1);
                Assert.assertEquals(event.get("dedupeKey").getAsString(), "body:" + secondHash);
            }
        }
        Assert.assertEquals(firstMatches, 1, "First journey should have one stored event");
        Assert.assertEquals(secondMatches, 1, "Second journey should have one stored event");
        ExtentReporterUtility.info("Two fresh synthetic journeys remained separate; exact redelivery incremented only the first delivery count. Identity and URLs redacted.");
    }

    @Test(description = "DL_WH_API_06 - Real UAT callback arrives after the manual consent journey")
    public void liveCallbackAfterManualJourney() throws Exception {
        if (!"true".equalsIgnoreCase(System.getenv("DIGILOCKER_LIVE_CALLBACK_EXPECTED")))
            throw new SkipException("Opt in only while running a real DigiLocker consent journey; synthetic events do not prove E2E delivery");
        int baseline = events().get("count").getAsInt();
        long deadline = System.nanoTime() + Duration.ofMinutes(10).toNanos();
        while (System.nanoTime() < deadline) {
            JsonObject readback = events();
            if (readback.get("count").getAsInt() > baseline) {
                JsonArray entries = readback.getAsJsonArray("events");
                JsonObject latest = entries.get(entries.size() - 1).getAsJsonObject();
                Assert.assertTrue(latest.get("exportSuccess").getAsBoolean(), "New callback was not a successful document export");
                return;
            }
            Thread.sleep(5000);
        }
        Assert.fail("No new DigiLocker callback arrived within 10 minutes of the manual journey");
    }

    @Test(description = "DL_WH_API_07 - HMAC verification requires a documented DigiLocker signing contract")
    public void signatureContractMustBeKnown() {
        throw new SkipException("UAT settings do not document a signature header or shared secret; do not claim HMAC verification until confirmed");
    }

    @Test(description = "DL_WH_API_08 - IP allowlisting requires documented sender egress addresses")
    public void sourceIpContractMustBeKnown() {
        throw new SkipException("DigiLocker UAT sender IPs are not documented; path-token auth is active but source allowlisting is unverified");
    }

    @Test(description = "DL_WH_API_10 - Webhook Relay bin contains a successful DigiLocker export POST")
    public void webhookRelayBinContainsExport() throws Exception {
        String binId = System.getenv("DIGILOCKER_WEBHOOK_BIN_ID");
        if (binId == null || binId.isBlank())
            throw new SkipException("Set DIGILOCKER_WEBHOOK_BIN_ID to inspect a Webhook Relay capture");
        for (JsonElement item : webhookRelayRequests(binId)) {
            JsonObject request = item.getAsJsonObject();
            if (!request.has("method") || !"POST".equalsIgnoreCase(request.get("method").getAsString())) continue;
            if (!request.has("body") || !request.get("body").isJsonPrimitive()) continue;
            JsonObject payload;
            try {
                payload = JsonParser.parseString(request.get("body").getAsString()).getAsJsonObject();
            } catch (RuntimeException ignored) {
                continue;
            }
            if (!payload.has("success") || !payload.get("success").getAsBoolean()) continue;
            Assert.assertEquals(request.get("responseStatus").getAsInt(), 200);
            Assert.assertEquals(payload.get("code").getAsInt(), 200);
            JsonObject data = payload.getAsJsonObject("data");
            Assert.assertNotNull(data, "Webhook data object is missing");
            Assert.assertTrue(data.has("aadhar_filename") && data.has("aadhar_xml")
                    && data.has("pan_image_path"), "Expected document links are missing");
            JsonObject eventHeader = request.getAsJsonObject("header").getAsJsonObject("X-Digilocker-Event");
            Assert.assertNotNull(eventHeader, "DigiLocker event header is missing");
            Assert.assertTrue(eventHeader.getAsJsonArray("values").toString().contains("entire_data.ready"));
            ExtentReporterUtility.info("Webhook Relay bin contains a successful DigiLocker export POST (identity data redacted)");
            return;
        }
        Assert.fail("No successful DigiLocker export POST was found in the Webhook Relay bin");
    }

    @Test(description = "DL_WH_API_09 - Three APIs, DigiLocker consent and matching webhook", groups = { "digilocker", "e2e" })
    public void completeDigilockerJourneyAndVerifyWebhook() throws Exception {
        if (!"true".equalsIgnoreCase(System.getenv("DIGILOCKER_RUN_FULL_JOURNEY")))
            throw new SkipException("Opt in with DIGILOCKER_RUN_FULL_JOURNEY=true; this sends a real Aadhaar OTP and grants document consent");
        String secret = requiredEnvironment("DIGILOCKER_UAT_SECRET_TOKEN");
        String aadhaar = requiredEnvironment("DIGILOCKER_AADHAAR_NUMBER");
        String pin = requiredEnvironment("DIGILOCKER_PIN");
        String relayBinId = System.getenv("DIGILOCKER_WEBHOOK_BIN_ID");
        long callbackNotBefore = System.currentTimeMillis();
        Assert.assertTrue(aadhaar.matches("[0-9]{12}"), "Aadhaar test value must contain 12 digits");
        Assert.assertTrue(pin.matches("[0-9]{6}"), "DigiLocker PIN must contain six digits");
        URI api = URI.create("https://digilocker-uat.meon.co.in");

        JsonObject tokenRequest = new JsonObject();
        tokenRequest.addProperty("company_name", "democapital");
        tokenRequest.addProperty("secret_token", secret);
        JsonObject issued = successfulJson(postJson(api.resolve("/get_access_token"), tokenRequest), "get_access_token");
        String clientToken = issued.get("client_token").getAsString();
        String state = issued.get("state").getAsString();
        Assert.assertFalse(clientToken.isBlank(), "Client token is missing");
        Assert.assertFalse(state.isBlank(), "State is missing");
        ExtentReporterUtility.info("DigiLocker UAT client token and state received (values redacted)");

        JsonObject linkRequest = new JsonObject();
        linkRequest.addProperty("client_token", clientToken);
        linkRequest.addProperty("redirect_url", "https://digilocker-uat.meon.co.in/dashboard");
        linkRequest.addProperty("company_name", "democapital");
        linkRequest.addProperty("documents", "aadhaar,pan");
        JsonObject link = successfulJson(postJson(api.resolve("/digi_url"), linkRequest), "digi_url");
        URI consentUrl = URI.create(link.get("url").getAsString());
        Assert.assertEquals(consentUrl.getScheme(), "https");
        Assert.assertEquals(consentUrl.getHost(), "digilocker-uat.meon.co.in", "Unexpected consent-link host");
        ExtentReporterUtility.info("DigiLocker consent link generated (URL redacted)");

        FirefoxOptions options = new FirefoxOptions();
        if (Boolean.parseBoolean(System.getProperty("isHeadless", "false"))) options.addArguments("-headless");
        FirefoxDriver browser = new FirefoxDriver(options);
        try {
            browser.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
            loginToUatSuperadmin(browser);
            browser.get(consentUrl.toString());
            DigilockerWebhookConsentPage consent = new DigilockerWebhookConsentPage(browser);
            consent.enterAadhaarAndContinue(aadhaar);
            ExtentReporterUtility.info("Aadhaar submitted; waiting up to two minutes for real OTP (value redacted)");
            consent.waitForOtpAndContinue();
            consent.enterPinAndContinue(pin);
            int selected = consent.selectAllAvailableDocumentsAndAllow();
            Assert.assertTrue(selected >= 0, "DigiLocker consent was not completed");
            ExtentReporterUtility.info(selected == 0
                    ? "DigiLocker returned to UAT without a document-selection screen (existing consent)"
                    : "DigiLocker consent completed for " + selected + " visible documents");
        } finally {
            browser.quit();
        }

        JsonObject exportRequest = new JsonObject();
        exportRequest.addProperty("client_token", clientToken);
        exportRequest.addProperty("state", state);
        exportRequest.addProperty("status", true);
        HttpResponse<String> exportResponse = null;
        long exportDeadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        do {
            exportResponse = postJson(api.resolve("/v2/send_entire_data"), exportRequest);
            if (exportResponse.statusCode() != 204) break;
            ExtentReporterUtility.info("send_entire_data returned 204; waiting for DigiLocker export to become ready");
            Thread.sleep(5000);
        } while (System.nanoTime() < exportDeadline);
        Assert.assertNotNull(exportResponse);
        Assert.assertEquals(exportResponse.statusCode(), 200,
                "send_entire_data did not return an export body after two minutes (last HTTP status) ");
        JsonObject export = successfulJson(exportResponse, "send_entire_data");
        Assert.assertEquals(export.get("code").getAsInt(), 200);
        Assert.assertEquals(export.get("status").getAsString(), "success");
        Assert.assertTrue(export.has("data") && export.get("data").isJsonObject(), "Export data is missing");
        String expectedHash = canonicalHash(export);
        ExtentReporterUtility.info("DigiLocker export succeeded; waiting for matching webhook (response body redacted)");

        long deadline = System.nanoTime() + Duration.ofMinutes(10).toNanos();
        while (System.nanoTime() < deadline) {
            if (relayBinId != null && !relayBinId.isBlank()) {
                for (JsonElement item : webhookRelayRequests(relayBinId)) {
                    if (!isMatchingRelayPost(item.getAsJsonObject(), expectedHash, callbackNotBefore)) continue;
                    ExtentReporterUtility.info("Matching DigiLocker POST received by Webhook Relay bin; payload hash and event header verified");
                    return;
                }
                Thread.sleep(5000);
                continue;
            }
            JsonArray entries = events().getAsJsonArray("events");
            for (JsonElement item : entries) {
                JsonObject event = item.getAsJsonObject();
                if (event.get("lastDeliveryAtMs").getAsLong() < callbackNotBefore) continue;
                if (!expectedHash.equals(event.get("payloadHash").getAsString())) continue;
                Assert.assertTrue(event.get("exportSuccess").getAsBoolean(), "Matching webhook was not a successful export");
                attachReceivedCallbackProof(expectedHash, callbackNotBefore);
                ExtentReporterUtility.info("Matching DigiLocker callback received and authenticated raw response verified");
                return;
            }
            Thread.sleep(5000);
        }
        Assert.fail("Export succeeded but no matching webhook arrived within 10 minutes; inspect the status-only receiver trace");
    }

    @Test(description = "DL_WH_API_11 - Observe a state-matched callback after consent without an explicit export call", groups = { "digilocker", "e2e" })
    public void consentWithoutExplicitExportTriggersWebhook() throws Exception {
        if (!"true".equalsIgnoreCase(System.getenv("DIGILOCKER_RUN_CONSENT_ONLY")))
            throw new SkipException("Opt in with DIGILOCKER_RUN_CONSENT_ONLY=true; this starts a fresh Aadhaar consent journey");
        String secret = requiredEnvironment("DIGILOCKER_UAT_SECRET_TOKEN");
        String aadhaar = requiredEnvironment("DIGILOCKER_AADHAAR_NUMBER");
        String pin = requiredEnvironment("DIGILOCKER_PIN");
        Assert.assertTrue(aadhaar.matches("[0-9]{12}"), "Aadhaar test value must contain 12 digits");
        Assert.assertTrue(pin.matches("[0-9]{6}"), "DigiLocker PIN must contain six digits");

        // Start watching before any journey step. This test deliberately never calls
        // /v2/send_entire_data; unrelated concurrent tenant traffic remains a limitation.
        long notBeforeMs = System.currentTimeMillis();
        int baselineCount = events().get("count").getAsInt();
        URI api = URI.create("https://digilocker-uat.meon.co.in");
        JsonObject tokenRequest = new JsonObject();
        tokenRequest.addProperty("company_name", "democapital");
        tokenRequest.addProperty("secret_token", secret);
        JsonObject issued = successfulJson(postJson(api.resolve("/get_access_token"), tokenRequest), "get_access_token");
        String clientToken = issued.get("client_token").getAsString();
        String state = issued.get("state").getAsString();
        Assert.assertFalse(clientToken.isBlank(), "Client token is missing");
        Assert.assertFalse(state.isBlank(), "State is missing");

        JsonObject linkRequest = new JsonObject();
        linkRequest.addProperty("client_token", clientToken);
        linkRequest.addProperty("redirect_url", "https://digilocker-uat.meon.co.in/dashboard");
        linkRequest.addProperty("company_name", "democapital");
        linkRequest.addProperty("documents", "aadhaar,pan");
        JsonObject link = successfulJson(postJson(api.resolve("/digi_url"), linkRequest), "digi_url");
        URI consentUrl = URI.create(link.get("url").getAsString());
        Assert.assertEquals(consentUrl.getScheme(), "https");
        Assert.assertEquals(consentUrl.getHost(), "digilocker-uat.meon.co.in");

        FirefoxOptions options = new FirefoxOptions();
        if (Boolean.parseBoolean(System.getProperty("isHeadless", "false"))) options.addArguments("-headless");
        FirefoxDriver browser = new FirefoxDriver(options);
        try {
            browser.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
            loginToUatSuperadmin(browser);
            browser.get(consentUrl.toString());
            DigilockerWebhookConsentPage consent = new DigilockerWebhookConsentPage(browser);
            consent.enterAadhaarAndContinue(aadhaar);
            consent.waitForOtpAndContinue();
            consent.enterPinAndContinue(pin);
            int selected = consent.selectAllAvailableDocumentsAndAllow();
            Assert.assertTrue(selected >= 0, "DigiLocker consent was not completed");
            ExtentReporterUtility.info("Fresh consent completed; /v2/send_entire_data was intentionally not called");
        } finally {
            browser.quit();
        }

        long deadline = System.nanoTime() + Duration.ofMinutes(5).toNanos();
        int unrelatedCallbacks = 0;
        while (System.nanoTime() < deadline) {
            JsonObject readback = rawEvents();
            for (JsonElement item : readback.getAsJsonArray("events")) {
                JsonObject event = item.getAsJsonObject();
                if (event.get("receivedAtMs").getAsLong() < notBeforeMs) continue;
                JsonObject payload = JsonParser.parseString(event.get("rawBody").getAsString()).getAsJsonObject();
                JsonObject data = payload.getAsJsonObject("data");
                String aadhaarPdf = data != null && data.has("aadhar_filename")
                        ? data.get("aadhar_filename").getAsString() : "";
                if (!aadhaarPdf.contains("/" + state + "/")) {
                    unrelatedCallbacks++;
                    continue;
                }
                Assert.assertEquals(event.get("event").getAsString(), "entire_data.ready");
                Assert.assertEquals(payload.get("code").getAsInt(), 200);
                Assert.assertTrue(payload.get("success").getAsBoolean());
                ExtentReporterUtility.info("State-matched webhook arrived after consent without an explicit "
                        + "/v2/send_entire_data call in this test; event=entire_data.ready, code=200, "
                        + "receivedAtMs=" + event.get("receivedAtMs").getAsLong() + ". Identity and state values redacted.");
                return;
            }
            Thread.sleep(5000);
        }
        Assert.fail("No state-matched webhook arrived within five minutes after consent-only journey; "
                + "baseline metadata count=" + baselineCount + ", unrelated callback observations=" + unrelatedCallbacks);
    }
}
