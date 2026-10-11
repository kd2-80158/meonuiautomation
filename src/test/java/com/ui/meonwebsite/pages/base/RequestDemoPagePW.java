package com.ui.meonwebsite.pages.base;

import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Page;
import com.ui.utilities.LoggerUtility;
import com.ui.utilities.PlaywrightUtility;

public class RequestDemoPagePW extends PlaywrightUtility {

	private final Logger logger = LoggerUtility.getLogger(this.getClass());
	private RequestDemoPagePW requestDemoPagePW;
	
	public RequestDemoPagePW(Page page) {
		super(page);
	}
	
//	public boolean isHeadingVisible() {
//		productsDropdownContainer.waitFor();
//		String headingText = kycSolutionHeading.innerText();
//		logger.info("Dropdown heading text: {}", headingText);
//		return kycSolutionHeading.isVisible();
//	}

}
