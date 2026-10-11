package com.ui.utilities;

import com.microsoft.playwright.Page;

public abstract class PlaywrightUtility {

    protected Page page;

    protected PlaywrightUtility(Page page) {
        this.page = page;
    }
}
