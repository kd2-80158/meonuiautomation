package com.ui.product.apimanager.pages;

import org.openqa.selenium.WebDriver;

/** Sidebar category: Customers. */
public final class CustomersCategoryPage extends ApiManagerSidebarCategoryPage {
    public CustomersCategoryPage(WebDriver driver) { super(driver); }
    @Override public String label() { return "Customers"; }
    @Override public String[] routes() { return new String[] {"/resources/customers", "/resources/customer-user-assignments", "/resources/customer-plan-assignments", "/resources/customer-product-assignments"}; }
}
