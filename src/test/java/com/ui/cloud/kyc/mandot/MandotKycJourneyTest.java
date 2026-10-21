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

import com.ui.base.BaseTest;
import com.ui.utilities.SeveritySoftAssert;

@Listeners({ com.ui.listeners.TestListener.class })
public class MandotKycJourneyTest extends BaseTest {

    private static final String MANDOT_UAT_URL = "https://kycuat.meon.co.in/mandotsecurities/individual_rm_anmol";
    private static final String TAX_COUNTRIES_API = "https://closure.meon.co.in/tax-countries";

    private MandotKycLandingPage landingPage;

    @BeforeMethod(alwaysRun = true)
    public void setUpJourney() {
        super.setupBrowser();
        driver.get(MANDOT_UAT_URL);
        landingPage = new MandotKycLandingPage(driver);
    }

    @Test(description = "tc_mandot_kyc_001 - Verify the Mandot individual KYC entry journey and required-document contract", groups = {
            "kyc", "mandot", "smoke" })
    public void verifyMandotIndividualKycEntryJourney() {
        SeveritySoftAssert assertions = softAssert;
        assertions.assertTrue(landingPage.isLoaded(), "Mandot UAT must show the Signup Now KYC entry page");
        assertions.assertTrue(landingPage.hasApplicationInput(), "KYC entry field must be visible");
        assertions.assertTrue(landingPage.hasEnabledSubmitControl(), "KYC entry submit control must be enabled");

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
}
