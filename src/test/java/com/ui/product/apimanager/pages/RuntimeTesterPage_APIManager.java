package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Only changes the UI mode; never presses Call Public API. */
public final class RuntimeTesterPage_APIManager extends ApiManagerStandalonePage {
    public RuntimeTesterPage_APIManager(WebDriver driver) { super(driver); }
    public void open(String baseUrl) { open(baseUrl, "/runtime-tester", "Runtime Tester"); }
    public boolean hasModeControls() { return hasButton("Live") && hasButton("Sandbox"); }
    public void switchModesWithoutCallingApi() {
        clickButton("Sandbox");
        clickButton("Live");
    }
}
