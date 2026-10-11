package com.ui.models;

public class CredentialsConfig {

    private KycAdmin kycAdmin;

    public KycAdmin getKycAdmin() {
        return kycAdmin;
    }

    public static class KycAdmin {

        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}