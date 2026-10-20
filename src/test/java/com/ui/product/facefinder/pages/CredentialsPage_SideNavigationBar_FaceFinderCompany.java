package com.ui.product.facefinder.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({com.ui.listeners.TestListener.class})
public class CredentialsPage_SideNavigationBar_FaceFinderCompany extends BrowserUtility{

	public CredentialsPage_SideNavigationBar_FaceFinderCompany(WebDriver driver) {
		super(driver);
	}
	
    private static final By HEADING_CREDENTIALSPAGE = By.xpath("//h1[text()='Credentials']");
    private static final By ADDCREDENTIALS_CREDENTIALSPAGE = By.xpath("//button[text()='Add Credentials']");
    private static final By THRESHOLD_CREDENTIALSPAGE = By.xpath("//button[@title='Setup threshold']");
    private static final By EDITCREDENTIALS_CREDENTIALSPAGE = By.xpath("//button[@title='Edit credentials']");
    private static final By HEADING_NODE1_CREDENTIALSPAGE = By.xpath("//p[text()='Credential Node #']");
    
}
