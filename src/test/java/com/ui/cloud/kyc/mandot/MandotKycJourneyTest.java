package com.ui.cloud.kyc.mandot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.SkipException;

import com.ui.base.BaseTest;
import com.ui.utilities.SeveritySoftAssert;

@Listeners({ com.ui.listeners.TestListener.class })
public class MandotKycJourneyTest extends BaseTest {

    private static final String MANDOT_UAT_URL = "https://kycuat.meon.co.in/mandotsecurities/individual_rm_anmol";
    private static final String TAX_COUNTRIES_API = "https://closure.meon.co.in/tax-countries";

    private MandotKycLandingPage landingPage;
    private MandotKycJourneyPage journeyPage;

    @BeforeMethod(alwaysRun = true)
    public void setUpJourney() {
        super.setupBrowser();
        driver.get(MANDOT_UAT_URL);
        landingPage = new MandotKycLandingPage(driver);
        journeyPage = new MandotKycJourneyPage(driver);
    }

    @Test(description = "tc_mandot_kyc_001 - Verify the Mandot individual KYC entry journey and required-document contract", groups = {
            "kyc", "mandot", "smoke" })
    public void verifyMandotIndividualKycEntryJourney() {
        SeveritySoftAssert assertions = softAssert;
        assertions.assertTrue(landingPage.isLoaded(), "Mandot UAT must show the Signup Now KYC entry page");
        assertions.assertTrue(landingPage.hasMobileEntryControls(),
                "Mobile, dependency, CAPTCHA and terms controls must be visible");
        assertions.assertFalse(landingPage.hasEnabledSubmitControl(),
                "SEND OTP must remain disabled until the required entry controls are completed");

        List<String> documents = landingPage.requiredDocuments();
        assertions.assertTrue(documents.stream().anyMatch(text -> text.contains("PAN")), "PAN must be listed as required");
        assertions.assertTrue(documents.stream().anyMatch(text -> text.contains("Bank Proof")), "Bank proof must be listed as required");
        assertions.assertTrue(documents.stream().anyMatch(text -> text.contains("Signature")), "Signature must be listed as required");
        assertions.assertTrue(documents.stream().anyMatch(text -> text.contains("Aadhaar")), "Aadhaar must be listed as required");
        assertions.assertTrue(documents.stream().anyMatch(text -> text.contains("Nominee")), "Nominee proof must be listed as required");
        assertions.assertAll();
    }

    @Test(description = "tc_mandot_kyc_002 - Verify the tax-country network dependency used by the KYC form", groups = {
            "kyc", "mandot", "api" })
    public void verifyTaxCountriesNetworkContract() throws Exception {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(TAX_COUNTRIES_API))
                .timeout(Duration.ofSeconds(30)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        softAssert.assertEquals(response.statusCode(), 200, "Tax-country API must be available to the KYC form");
        softAssert.assertTrue(response.headers().firstValue("content-type").orElse("").contains("application/json"),
                "Tax-country API must return JSON");
        softAssert.assertTrue(response.body().contains("\"data\""), "Tax-country response must provide data");
        softAssert.assertAll();
    }

    @Test(description = "tc_mandot_kyc_003 - Complete Individual RM Anmol journey with explicit operator checkpoints", groups = {
            "kyc", "mandot", "e2e" })
    public void completeIndividualRmAnmolJourney() {
        if (!MandotKycTestData.isEnabled()) {
            throw new SkipException("Set MANDOT_KYC_E2E_ENABLED=true and provide an isolated UAT fixture to run this journey.");
        }

        MandotKycTestData data = MandotKycTestData.fromEnvironment();
        long manualWaitSeconds = MandotKycTestData.manualWaitSeconds();
        journeyPage.startApplication(data);
        for (MandotKycWorkflowStep step : MandotKycWorkflowStep.orderedSteps()) {
            journeyPage.waitForManualAdvance(step, manualWaitSeconds);
        }

        softAssert.assertTrue(driver.getCurrentUrl().contains("mandotsecurities"),
                "The completed journey must remain within the Mandot UAT application");
        softAssert.assertAll();
    }
}
