package com.ui.pojos;

/** Values for the DigiLocker APAAR UAT journey in config/config.json. */
public class DigilockerApaarConfig {
    private boolean liveTestEnabled;

    public boolean isLiveTestEnabled() { return liveTestEnabled; }
    private String superadminUrl;
    private String googleEmail;
    private String googlePassword;
    private String companySearch;
    private String issuerSearch;
    private String apaarId;
    private String rollNumber;
    private String passingYear;
    private String level;
    private String aadhaarNumber;
    private String securityPin;
    private String otpEndpoint;
    private String otpReadToken;
    private String playgroundUrl;

    public String getSuperadminUrl() { return superadminUrl; }

    public String getGoogleEmail() { return googleEmail; }

    public String getGooglePassword() { return googlePassword; }

    public String getCompanySearch() { return companySearch; }

    public String getIssuerSearch() { return issuerSearch; }

    public String getApaarId() { return apaarId; }

    public String getRollNumber() { return rollNumber; }

    public String getPassingYear() { return passingYear; }

    public String getLevel() { return level; }

    public String getAadhaarNumber() { return aadhaarNumber; }

    public String getSecurityPin() { return securityPin; }

    public String getOtpEndpoint() { return otpEndpoint; }

    public String getOtpReadToken() { return otpReadToken; }

    public String getPlaygroundUrl() { return playgroundUrl; }
}
