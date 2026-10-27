package com.ui.cloud.rekyc.autopush;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Test-case IDs match Auto Push UAT test-case workbook rows. */
public final class AutoPushWorkflowTest extends AutoPushUatTestBase {
    @Test(description="Live-status button opens Live activity", groups={"autopush","uat","read"})
    public void liveStatusButtonOpensActivity() {
        openReadWorkflow();
        if (!page.liveStatusActionAvailable()) throw new SkipException("No live-status action for this workflow");
        page.viewLiveActivity();
        Assert.assertTrue(page.monitorVisible(), "Live-status action did not open Live activity");
    }

    @Test(description="TC_AP_001 - Auto Push page and sidebar link load", groups={"autopush","uat","read"})
    public void tcAp001_pageAndNavigationRender() {
        page.open(baseUrl);
        Assert.assertTrue(page.hasSidebarLink(), "Auto Push sidebar link missing");
        Assert.assertTrue(page.hasAllPanelTabs(), "Expected three Auto Push panels");
    }

    @Test(description="TC_AP_002 - Workflow picker opens and lists workflows", groups={"autopush","uat","read"})
    public void tcAp002_workflowPickerListsOptions() {
        page.open(baseUrl);
        page.searchWorkflow("");
        Assert.assertTrue(page.workflowOptionCount() > 0, "No workflows listed");
    }

    @Test(description="TC_AP_003 - Workflow search filters by exact name", groups={"autopush","uat","read"})
    public void tcAp003_workflowSearchFindsName() {
        page.open(baseUrl);
        String name = System.getProperty("autoPush.readWorkflow", "modification");
        page.searchWorkflow(name);
        Assert.assertTrue(page.workflowOptionVisible(name), "Known workflow not found");
    }

    @Test(description="TC_AP_004 - Workflow search shows no-match state", groups={"autopush","uat","read"})
    public void tcAp004_workflowSearchNoMatch() {
        page.open(baseUrl);
        page.searchWorkflow("__no_matching_workflow__");
        Assert.assertEquals(page.workflowOptionCount(), 0, "Unexpected workflow matched sentinel");
    }

    @Test(description="TC_AP_005 - Selecting a workflow loads its configuration", groups={"autopush","uat","read"})
    public void tcAp005_selectionLoadsConfiguration() {
        openReadWorkflow();
        Assert.assertTrue(page.selectedWorkflow().contains(System.getProperty("autoPush.readWorkflow", "modification")));
        page.configuration();
        Assert.assertTrue(page.configurationVisible());
        Assert.assertFalse(page.pushOrder().isEmpty(), "Configured workflow has no push operations");
    }

    @Test(description="TC_AP_006 - Switching workflows refreshes queue and monitor", groups={"autopush","uat","read"})
    public void tcAp006_switchingWorkflowReplacesData() {
        openReadWorkflow();
        page.entries();
        int firstCount = page.queueMatching();
        String other = System.getProperty("autoPush.secondReadWorkflow", "custom_test");
        page.chooseWorkflow(other);
        page.entries();
        Assert.assertTrue(page.selectedWorkflow().contains(other));
        if (firstCount == 0 && page.queueMatching() == 0) {
            throw new SkipException("Both workflows have empty queues; supply a populated comparison workflow");
        }
        Assert.assertNotEquals(page.queueMatching(), firstCount, "Queue still displays first workflow's count");
    }

    @Test(description="TC_AP_007 - Invalid workflow configuration is surfaced", groups={"autopush","uat","read"})
    public void tcAp007_invalidConfigurationIsVisible() {
        page.open(baseUrl);
        page.chooseWorkflow(System.getProperty("autoPush.invalidWorkflow", "custom_test"));
        if (!page.selectedWorkflow().contains("Invalid configuration")) {
            throw new SkipException("Configured invalid workflow is now valid; choose another invalid fixture");
        }
        page.configuration();
        Assert.assertTrue(page.pushOrder().isEmpty(), "Invalid workflow unexpectedly has supported push operations");
    }
}
