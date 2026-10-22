package com.ui.cloud.kyc.mandot;

import java.util.List;

/** Ordered server-side workflow configured for Mandot Individual RM Anmol. */
public enum MandotKycWorkflowStep {
	MOBILE("Mobile / CAPTCHA and SEND OTP"), MOBILE_OTP("Mobile OTP"), EMAIL("Email"), EMAIL_OTP("Email OTP"),
	SEGMENTS("Segments"), DETAILS_PAN("Details PAN"), KRA_FETCH("KRA Fetch"), KRA_DETAILS("KRA Details"),
	DIGILOCKER("DigiLocker"), PAN("PAN validation"), PERSONAL_DETAILS("Personal Details"), NOMINEE("Nominee"),
	SECONDARY_MOBILE_OTP("Secondary Mobile OTP"), BANK("Bank"), RPD("RPD"), REVERSE_PENNY_DROP("Reverse Penny Drop"),
	BANK_UPLOAD("Bank Upload"), CUSTOM_PAGE("Custom Page"), ACCOUNT_AGGREGATOR("Account Aggregator"),
	INCOME_PROOF("Income Proof Upload"), PAN_UPLOAD("PAN Upload"),
	SIGNATURE_UPLOAD("Signature Upload or Digital Signature"), LIVE_IMAGE("Live Image"),
	PDF_ESIGN("PDF / eSign preparation"), PDF_ESIGN_CONFIRMATION("PDF / eSign confirmation"),
	PDF_GENERATION("PDF generation"), PDF_CONFIRMATION("PDF confirmation"), ESIGN("eSign"),
	ESIGN_CONFIRMATION("eSign confirmation"), CDSL("CDSL"), KRA_BRIDGE("KRA Bridge"), BACKOFFICE("Backoffice"),
	NSE("NSE"), BSE("BSE"), MSC_PUSH("MSC Push"), MSC_STATUS("MSC Status"), NSE_INVEST("NSE Invest"),
	CDSL_UPLOAD("CDSL Upload"), NSDL("NSDL"), BSE_STAR_MF("BSE Star MF"), KRA_NEW("KRA New"), MCX("MCX"),
	CKYC_PUSH("CKYC Push"), PAN_RECHECK("PAN recheck"), NSE_INVEST_RECHECK("NSE Invest recheck"),
	KRA_RECHECK("KRA recheck"), NSDL_UAT("NSDL UAT"), NSE_UAT("NSE UAT");

	private final String displayName;

	MandotKycWorkflowStep(String displayName) {
		this.displayName = displayName;
	}

	public String displayName() {
		return displayName;
	}

	/**
	 * The workflow through live-image capture is rendered for the applicant;
	 * later steps are back-office or provider processing states and need not
	 * expose browser controls.
	 */
	public boolean isApplicantFacing() {
		return ordinal() <= LIVE_IMAGE.ordinal();
	}

	public static List<MandotKycWorkflowStep> orderedSteps() {
		return List.of(values());
	}
}
