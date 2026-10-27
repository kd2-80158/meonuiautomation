package com.ui.cloud.rekyc.autopush;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Entries and batch cases TC_AP_029 through TC_AP_046. */
public final class AutoPushEntriesBatchTest extends AutoPushUatTestBase {
    private void readEntries() { openReadWorkflow(); page.entries(); }
    private String newBatchName() { return "autopush-uat-" + UUID.randomUUID().toString().substring(0,8); }
    private String prepareDisposableEntry() { openDisposableWorkflow(); page.entries(); String client=requiredProperty("autoPush.testClientCode","eligible disposable Verify entry"); page.searchEntries(client); if(!page.entryEligible(client)) throw new SkipException("Disposable entry is not eligible or is already batched"); page.selectEntry(client); return client; }
    private void createScheduled(String name,String client) { page.setBatchName(name); page.setBatchStartMode("Start after a delay"); page.setBatchDelaySeconds(3600); page.createBatch(); }
    private void cancelOwnedBatch(String name) { page.monitor(); page.searchBatch(name); if(page.batchListed(name)) { page.inspectBatch(name); if(page.batchHasCancel()) page.cancelBatch(); } }

    @Test(description="TC_AP_029 - Entries tab renders queue and launch form", groups={"autopush","uat","read"})
    public void tcAp029_queueAndLaunchFormRender() {
        readEntries(); Assert.assertTrue(page.entriesVisible()); Assert.assertTrue(page.hasEntryDateFilters()); Assert.assertTrue(page.queueMatching()>=page.queueEligible());
    }

    @Test(description="TC_AP_030 - Client-code search filters Verify queue", groups={"autopush","uat","read"})
    public void tcAp030_clientSearchFiltersQueue() {
        readEntries(); String client=System.getProperty("autoPush.existingClientCode","MEON2"); page.searchEntries(client); if(!page.entryListed(client)) throw new SkipException("Configured client not in read workflow queue"); Assert.assertTrue(page.entryTableText().contains(client)); Assert.assertTrue(page.queueMatching()>0);
    }

    @Test(description="TC_AP_031 - Unmatched client search shows empty state", groups={"autopush","uat","read"})
    public void tcAp031_unmatchedSearchShowsEmptyQueue() {
        readEntries(); page.searchEntries("__no_matching_client__"); Assert.assertEquals(page.queueMatching(),0); Assert.assertTrue(page.entryTableText().contains("No matching Verify entries"));
    }

    @Test(description="TC_AP_032 - Updated-from and updated-to filter inclusively", groups={"autopush","uat","read"})
    public void tcAp032_dateBoundsIncludeEntry() {
        readEntries(); String client=requiredProperty("autoPush.existingClientCode","known queue client"); String date=requiredProperty("autoPush.existingEntryDate","entry update date yyyy-MM-dd"); page.setEntryDates(date,date); if(!page.entryListed(client)) Assert.fail("Entry missing on inclusive update-date boundary");
    }

    @Test(description="TC_AP_033 - Invalid date range is rejected or empty", groups={"autopush","uat","read"})
    public void tcAp033_reversedDateRangeIsNotAccepted() {
        throw new SkipException("Expected behavior for reversed date range needs product confirmation before assertion");
    }

    @Test(description="TC_AP_034 - Reset filters restores unfiltered queue", groups={"autopush","uat","read"})
    public void tcAp034_resetRestoresQueue() {
        readEntries(); int before=page.queueMatching(); page.searchEntries("__no_matching_client__"); Assert.assertEquals(page.queueMatching(),0); page.resetEntryFilters(); Assert.assertEquals(page.entrySearchValue(),""); Assert.assertEquals(page.queueMatching(),before);
    }

    @Test(description="TC_AP_035 - Queue summary reconciles selection", groups={"autopush","uat","read"})
    public void tcAp035_selectedCountTracksEntry() {
        readEntries(); String client=requiredProperty("autoPush.testClientCode","eligible disposable Verify entry"); page.searchEntries(client); if(!page.entryEligible(client)) throw new SkipException("Entry is not eligible"); int before=page.queueSelected(); page.selectEntry(client); Assert.assertEquals(page.queueSelected(),before+1); page.selectEntry(client); Assert.assertEquals(page.queueSelected(),before);
    }

    @Test(description="TC_AP_036 - Ineligible or already-batched row cannot be selected", groups={"autopush","uat","read"})
    public void tcAp036_batchedEntryCheckboxDisabled() {
        readEntries(); String client=System.getProperty("autoPush.alreadyBatchedClientCode","MEON2"); page.searchEntries(client); if(!page.entryListed(client)) throw new SkipException("No already-batched fixture"); Assert.assertFalse(page.entryEligible(client)); Assert.assertTrue(page.entryTableText().contains("Already in batch"));
    }

    @Test(description="TC_AP_037 - Select current page selects only eligible rows", groups={"autopush","uat","read"})
    public void tcAp037_selectPageExcludesIneligible() {
        readEntries(); int eligible=page.eligibleVisibleRowCount(); if(eligible==0) throw new SkipException("No eligible visible entries"); page.selectCurrentEntryPage(); Assert.assertEquals(page.queueSelected(),eligible); page.selectCurrentEntryPage(); Assert.assertEquals(page.queueSelected(),0);
    }

    @Test(description="TC_AP_038 - Queue page size options work", groups={"autopush","uat","read"})
    public void tcAp038_entryPageSizesWork() {
        readEntries(); int original=page.entryPageSize(); try { for(int size:List.of(10,20,50,100)){page.setEntryPageSize(size); Assert.assertEquals(page.entryPageSize(),size);} } finally {page.setEntryPageSize(original);} 
    }

    @Test(description="TC_AP_039 - Queue next and previous keep filters", groups={"autopush","uat","read"})
    public void tcAp039_queuePaginationKeepsFilter() {
        readEntries(); if(!page.entryNextEnabled()) throw new SkipException("Queue has one page"); String filter=page.entrySearchValue(); page.nextEntryPage(); page.previousEntryPage(); Assert.assertEquals(page.entrySearchValue(),filter);
    }

    @Test(description="TC_AP_040 - Batch name accepts identifiable test label", groups={"autopush","uat","mutation"})
    public void tcAp040_batchNamePersistsInCreatedBatch() {
        String client=prepareDisposableEntry(); String batchName=newBatchName(); createScheduled(batchName,client); try { page.monitor(); page.searchBatch(batchName); new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->page.batchListed(batchName)); Assert.assertTrue(page.batchListed(batchName)); } finally { cancelOwnedBatch(batchName); }
    }

    @Test(description="TC_AP_041 - Batch start timing options display conditional fields", groups={"autopush","uat","read"})
    public void tcAp041_launchTimingFieldsRender() {
        readEntries(); Assert.assertTrue(page.batchStartModes().containsAll(List.of("Start immediately","Start after a delay","Use daily time window"))); page.setBatchStartMode("Start immediately"); Assert.assertFalse(page.batchDelayVisible()); page.setBatchStartMode("Start after a delay"); Assert.assertTrue(page.batchDelayVisible()); page.setBatchStartMode("Use daily time window"); Assert.assertTrue(page.batchWindowVisible());
    }

    @Test(description="TC_AP_042 - Batch delay validation blocks invalid value", groups={"autopush","uat","mutation"})
    public void tcAp042_invalidBatchDelayRejected() {
        requireMutationOptIn(); throw new SkipException("Need confirmed minimum delay before attempting an invalid create request");
    }

    @Test(description="TC_AP_043 - Batch order can be tuned without changing saved workflow", groups={"autopush","uat","read"})
    public void tcAp043_batchOrderDoesNotChangeConfig() {
        openReadWorkflow(); page.configuration(); List<String> config=page.pushOrder(); page.entries(); List<String> before=page.batchPushOrder(); if(before.size()<2) throw new SkipException("Need two batch operations"); page.moveFirstBatchOperationLater(); Assert.assertNotEquals(page.batchPushOrder(),before); page.configuration(); Assert.assertEquals(page.pushOrder(),config);
    }

    @Test(description="TC_AP_044 - Create is blocked with zero selected entries", groups={"autopush","uat","mutation"})
    public void tcAp044_emptyBatchCannotBeCreated() {
        openDisposableWorkflow();
        page.entries();
        Assert.assertEquals(page.queueSelected(), 0);
        String name = newBatchName();
        page.setBatchName(name);
        page.createBatch();
        page.monitor();
        page.searchBatch(name);
        Assert.assertFalse(page.batchListed(name), "Empty batch was created");
    }

    @Test(description="TC_AP_045 - Create scheduled batch for one test entry", groups={"autopush","uat","mutation"})
    public void tcAp045_scheduledBatchCreatedAndCancelled() {
        String client=prepareDisposableEntry(); String name=newBatchName(); createScheduled(name,client); try { page.monitor(); page.searchBatch(name); new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->page.batchListed(name)); page.inspectBatch(name); Assert.assertTrue(page.batchSummary().contains("1"),"Expected one-entry batch"); } finally { cancelOwnedBatch(name); }
    }

    @Test(description="TC_AP_046 - Duplicate or ineligible entry cannot be batched twice", groups={"autopush","uat","read"})
    public void tcAp046_duplicateEntryCannotBeSelected() {
        readEntries(); String client=System.getProperty("autoPush.alreadyBatchedClientCode","MEON2"); page.searchEntries(client); if(!page.entryListed(client)) throw new SkipException("No already-batched fixture"); Assert.assertFalse(page.entryEligible(client)); Assert.assertEquals(page.queueSelected(),0);
    }

}
