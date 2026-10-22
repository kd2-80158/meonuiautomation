package com.ui.product.facefinder.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class ThresholdPage_FaceFinderCompany extends BrowserUtility {

	public ThresholdPage_FaceFinderCompany(WebDriver driver) {
		super(driver);
	}

	// INCREASE BUTTON
	private static final By PLUSBUTTON_TOOFARDISTANCE_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[1]");
	private static final By PLUSBUTTON_TOOCLOSEDISTANCE_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[2]");
	private static final By PLUSBUTTON_HEADROTATION_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[3]");
	private static final By PLUSBUTTON_EYEOPENNESS_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[4]");
	private static final By PLUSBUTTON_FACECENTER_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[5]");
	private static final By PLUSBUTTON_MOUTHOPENNESS_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[6]");
	private static final By PLUSBUTTON_LOWLIGHTBRIGHTNESS_THRESHOLDPAGE = By.xpath("(//button[@title='Step up'])[7]");

	// DECREASE BUTTON
	private static final By MINUSBUTTON_TOOFARDISTANCE_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[1]");
	private static final By MINUSBUTTON_TOOCLOSEDISTANCE_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[2]");
	private static final By MINUSBUTTON_HEADROTATION_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[3]");
	private static final By MINUSBUTTON_EYEOPENNESS_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[4]");
	private static final By MINUSBUTTON_FACECENTER_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[5]");
	private static final By MINUSBUTTON_MOUTHOPENNESS_THRESHOLDPAGE = By.xpath("(//button[@title='Step down'])[6]");
	private static final By MINUSBUTTON_LOWLIGHTBRIGHTNESS_THRESHOLDPAGE = By
			.xpath("(//button[@title='Step down'])[7]");

	private static final By SAVEBUTTON_THRESHOLDPAGE = By.xpath("//button[text()='Save Thresholds']");
	private static final By CANCELBUTTON_THRESHOLDPAGE = By.xpath("//button[text()='Cancel']");

	public int increaseButtonCount() {
		return getDriver().findElements(By.cssSelector("button[title='Step up']")).size();
	}

	public int decreaseButtonCount() {
		return getDriver().findElements(By.cssSelector("button[title='Step down']")).size();
	}

	public boolean hasSaveButton() {
		return !getDriver().findElements(SAVEBUTTON_THRESHOLDPAGE).isEmpty();
	}

	public void cancel() {
		new org.openqa.selenium.support.ui.WebDriverWait(getDriver(), java.time.Duration.ofSeconds(20)).until(
				org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(CANCELBUTTON_THRESHOLDPAGE))
				.click();
	}
}
