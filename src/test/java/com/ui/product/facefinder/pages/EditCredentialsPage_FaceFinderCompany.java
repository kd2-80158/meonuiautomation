package com.ui.product.facefinder.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class EditCredentialsPage_FaceFinderCompany extends BrowserUtility{

	public EditCredentialsPage_FaceFinderCompany(WebDriver driver) {
		super(driver);
	}
	
	//VERIFICATION CONTROLS
	private static final By ACTIVE_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='is_active']");
	private static final By AUTOCAPTURE_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='auto_capture']");
	private static final By VIDEOWITHAUTOCAPTURE_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='video_with_auto_capture']");
	private static final By CAPTUREVIDEO_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='capture_video']");
	private static final By CHECKLOCATION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='check_location']");
	private static final By MATCHFACES_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='match_faces']");
	private static final By READSCRIPT_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='read_script']");
	private static final By SPOOFINGDETECTION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='spoofing_detection']");
	private static final By ENABLEWEBHOOK_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='enable_webhook']");
	private static final By MAILNOTIFICATION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='send_mail_notification']");
	private static final By GLASSDETECTION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='glasses_detection']");
	private static final By FACECENTER_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='face_center_check']");
	private static final By EYEOPENSTRICT_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='eyes_open_strict_check']");
	private static final By NAKEDNESSCHECK_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='nakedness_check']");
	private static final By MULTIFACE_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='multi_face_strict_check']");
	private static final By MOTIONBLUR_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='motion_blur_detection']");
	private static final By FACEMASK_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='face_mask_detection']");
	private static final By CENTERZOOM_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='full_view']");
	private static final By REMOVEBACKGROUND_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='remove_background']");
	private static final By OPENMOUTH_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='open_mouth_check']");
	private static final By GENDERDETECTION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='gender_detection']");
	private static final By AGEDETECTION_TOGGLE_EDITCREDENTIALPAGE = By.xpath("//input[@name='age_detection']");
	
	//ROUTING AND THRESHOLDS 
	private static final By CAPTUREEXPERIENCE_OPTION_EDITCREDENTIALPAGE = By.xpath("//select[@id='capture_version']");
	private static final By REDIRECTURL_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='redirect_url']");
	private static final By WEBHOOKURL_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='webhook_url']");
	private static final By SPEEDRECOGNITIONPERCENTAGE_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='speech_text_match_perc']");
	private static final By FACEMATCHPERCENTAGE_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='face_match_percentage']");
	private static final By VIDEOTIME_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='video_time']");
	private static final By BACKGROUNDCOLOR_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//input[@id='background_color']");
	
	private static final By UPDATEBUTTON_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//button[text()='Update Credentials']");
	private static final By CANCELBUTTON_INPUTBOX_EDITCREDENTIALPAGE = By.xpath("//button[text()='Cancel']");
	

}
