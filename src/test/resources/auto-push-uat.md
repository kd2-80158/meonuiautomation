# reKYC UAT Auto Push UI tests

Page object: `com.ui.product.rekyc.pages.admin.meontechnologies.AutoPushPage`.
The 62 spreadsheet cases (`TC_AP_001`–`TC_AP_062`) map one-to-one to named TestNG methods
in five classes: workflow, configuration, entries/batch, batch monitor, and API feedback.
Locators and page actions stay in the page object; assertions stay in the test classes.

The page has three panels. Configuration includes enablement, immediate/delayed/daily-window start,
automatic Verify-entry selection, default failure handling (with retry limits and jitter), and
per-operation push order/failure policies. Entries & batch includes client/date filters,
eligibility, selection, name, timing and batch sequence. Batch monitor includes search, status,
pagination, details, timeline, pause/resume, retry and cancel where permitted by batch state.

There is no visible delete operation for a batch. Cancelling a not-yet-started test batch is the
closest supported cleanup and is not equivalent to deletion.

## End-to-end journeys added

`AutoPushEndToEndTest` contains separate Start now, Start after (30 seconds), and Daily time
window cases. Each case checks the named client's Maker & Checker Verify row, maps the queue's
push summary to available Maker & Checker buttons, saves Auto Push with newly verified auto-add
disabled, verifies the new activity and duplicate-batch protection, then watches the activity
for up to 10 minutes. It expands step-by-step details and adds screenshots plus a checkpoint
video to `logs/report.html`. Original workflow settings and push order are restored in `finally`.

The read-only `alreadyBatchedClientCanBeTraced` case covers MEON1/MEON2 when the queue says
`Already in batch`. The separate retry case requires an explicit failed UAT activity code and
never retries an arbitrary latest activity. `AutoPushTelemetryTest` records same-origin XHR/fetch
HTTP status and browser console errors for Setup, Verified clients, and Live activity. Run that
case in Chrome; Firefox does not expose Chrome performance logs.

Run one fresh-eligible client scenario (reset a test fixture between timing scenarios; a client
already in a batch cannot be launched again):

```sh
mvn -Dbrowser=chrome -Dtest=AutoPushEndToEndTest#startNowJourney \
  -DautoPush.mutate=true -DautoPush.testWorkflow=modification \
  -DautoPush.testClientCode=MEON1 test
```

For the current already-batched fixture, run the non-mutating trace case instead. To retry a
failed client, use `-Dtest=AutoPushEndToEndTest#retryFailedOwnedActivity` with
`-DautoPush.retryBatchCode=AP-...`, `-DautoPush.mutate=true`, and the matching test client.
The suite now opens `/v1/company/superadmin` in the new WebDriver browser, signs in with the
existing `KycAdmin` credentials from `config/credentials.json`, visits the MEON tenant Admin
tab, and only then opens Auto Push. A Selenium window does not inherit a separate Chrome tab's
session. Google MFA or a missing runtime credentials file still requires manual intervention.

Run read checks (Chrome opens a fresh browser; sign in to the UAT admin in that browser):

```sh
mvn -Pautopush-uat -Dbrowser=chrome test
```

For real UAT mutation tests, provide a disposable workflow with a configured Maker/Checker
Verify push and an eligible disposable entry. This changes workflow policy temporarily, then
restores it; it also creates and cancels a delayed test batch. Never point these settings at
production or an operational customer workflow.

```sh
mvn -Pautopush-uat -Dbrowser=chrome -DautoPush.mutate=true \
  -DautoPush.testWorkflow=YOUR_DISPOSABLE_WORKFLOW \
  -DautoPush.testClientCode=YOUR_ELIGIBLE_TEST_CLIENT test
```

At inspection time, `custom_test`, `modification_test`, `modification_testing`, and
`ipv_test` had invalid Maker/Checker configuration and no Verify entries. The selected
`modification` workflow had no eligible unbatched entries. The mutating tests therefore
have not been run against these workflows. Tests that need a disposable workflow, an
eligible entry, a specific batch state, or an agreed validation contract skip with an
explicit reason when that prerequisite is absent. In particular, `TC_AP_012`, `014`,
`018`, `033`, `042`, and `062` await confirmed rules or a controlled server-error
fixture; they are not counted as passing coverage.

On 27 October 2026, MEON2 was Verified in the `modification` Maker & Checker view and showed
`Already in batch` in Auto Push. MEON1 was no longer in the visible Verify table in the latest
Firefox run, so the read-only trace defaults to MEON2. New launch journeys should skip until a
genuinely eligible test entry is available; do not clear verification or force a duplicate merely
to make the test pass.
