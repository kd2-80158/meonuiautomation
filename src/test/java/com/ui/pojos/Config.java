package com.ui.pojos;

import java.util.Map;

public class Config {

    private AdminURL admin;
    // Specify generics so Gson knows what to instantiate inside the Map
    private Map<String, WorkflowURL> workflowURL;
    private Map<String, WebsiteURL> website;

    public AdminURL getAdmin() {
        return admin;
    }

    public void setAdmin(AdminURL admin) {
        this.admin = admin;
    }

    public Map<String, WorkflowURL> getWorkflowURL() {
        return workflowURL;
    }

    public void setWorkflowURL(Map<String, WorkflowURL> workflowURL) {
        this.workflowURL = workflowURL;
    }

    public Map<String, WebsiteURL> getWebsite() {
        return website;
    }

    public void setWebsite(Map<String, WebsiteURL> website) {
        this.website = website;
    }
}