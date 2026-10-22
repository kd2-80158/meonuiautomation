# Mandot individual RM Anmol — UAT E2E plan

The workflow configured in UAT contains 48 sequential modules. The automated suite is split into three layers:

1. **Entry and validation:** mobile, dependency, CAPTCHA, terms acceptance, mobile OTP, email and email OTP.
2. **KYC capture:** segment, details PAN, KRA fetch/details, DigiLocker, PAN validation, personal details, nominee, bank, reverse penny-drop, document uploads, live image, income proof and account aggregation.
3. **Completion and downstream verification:** PDF/eSign, CDSL, KRA bridge/new/KRA, back-office, NSE/BSE/MSC/NSE Invest, CDSL upload, NSDL/NSDL UAT, BSE Star MF, MCX and CKYC push.

The full execution may only use an isolated UAT test identity and documented UAT provider simulators. It must not send real OTPs, create customer applications, submit to production exchanges, or retain personal KYC data in source control.

## Data contract

Populate the secure runtime values described in `mandot-kyc-e2e.properties.example`. Provide a test OTP/eSign bypass or a test inbox/OTP retrieval mechanism before enabling the full suite.

## Assertions per stage

- UI: visible step title, required fields, validation error, successful transition and no browser-console errors.
- API: status code, expected response schema, correlation/request ID, and no fallback upstream where applicable.
- Maker-checker: target test identity is visible, stage state advances, and enabled downstream actions return success or the configured UAT simulation result.
- Cleanup: mark the test record for deletion/archive and verify it no longer appears in the active maker-checker list.
