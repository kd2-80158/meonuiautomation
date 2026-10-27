# API Manager UI functional coverage

Target: `https://api.meon.co.in` by default; override with `-DapiManager.baseUrl=https://<test-host>`.
The automated suite never submits create/edit/delete, publish, dispatch, payment, batch, or live API-call actions on the default target.

## Page Object Model structure

Each sidebar destination has its own page object under `com.ui.product.apimanager.pages`
and matching TestNG test class under `com.ui.cloud.apimanager.pages`.
The 27 resource pages extend `ApiManagerResourcePage`; their tests extend
`ApiManagerResourceContractTest`, which contains reusable read-only assertions.
The seven non-resource screens have dedicated page objects and tests with their
own locators and methods. Login remains a separate page and test class.

## Implemented safe checks

- Dashboard: primary metrics and navigation.
- API Management: filters, search control, create/edit dialogs opened then cancelled.
- All 27 `/resources/...` pages in the sidebar: heading/navigation, search/refresh, applicable status filters, pagination, record detail view, and create dialog opened then cancelled. Read-only or empty pages skip controls that are not available.
- Usage Logs, Vendor Monitoring, Audit Logs: filters and range controls.
- Runtime Tester: live/sandbox UI switch only; no API call.
- Bulk Operations: upload/create/export controls present; no batch created.
- Login: valid sign-in and password masking. Negative sign-in attempts are disabled unless explicitly enabled on a disposable target.

## Run

Set `API_MANAGER_EMAIL` and `API_MANAGER_PASSWORD` in the local shell or CI secret store; do not put credentials in source control. Then run:

`mvn -Papimanager-readonly -Dbrowser=firefox -DisHeadless=true test`

To run one page, use for example:

`mvn -Dtest=ApiVersionsPageTest -Dbrowser=firefox -DisHeadless=true test`

Full-page screenshots are disabled for API Manager tests because several pages
display API keys or vendor credentials. Existing historical screenshots may
still be present in `logs/screenshots` and should be treated as sensitive.

## Latest production read-only run (26 October 2026)

The page-wise suite ran 170 checks: 116 passed, 52 skipped for unavailable
controls, and 2 failed. The failures were reproduced in the browser:

- Alert Templates: an impossible search term leaves all four records visible.
- Alert Destinations: the Inactive filter leaves active records visible.

The suite intentionally remains red until these behaviors are fixed or the
product requirements show that the controls should work differently.

To run negative login tests against a disposable environment only, set `-DapiManager.allowNegativeLogin=true` and provide `API_MANAGER_INVALID_EMAIL` and `API_MANAGER_INVALID_PASSWORD`.

## Remaining for a full CRUD/E2E suite

The user selected production read-only coverage. Create/update/delete workflows,
template validate/publish, wallet credit/payment operations, alert dispatch,
runtime API calls, and bulk uploads are intentionally out of scope on this target.

## Optimization impact checks

`ApiManagerOptimizationReadOnlyTest` is a separate, production-safe class covering the
five UI surfaces named in the performance impact analysis: billing ledger, API operations,
usage logs, vendor list/monitoring, and alert rules. Run it with:

`mvn -Dtest=ApiManagerOptimizationReadOnlyTest -Dbrowser=firefox -DisHeadless=true test`

These are **UI contract smoke checks only**. They do not prove the claimed N+1 query
reductions or the 210/210 backend test result. The refund dry-run POST, rate-card bulk
upsert, monthly invoice task, vendor-health task, and observability maintenance task
need the backend repository plus disposable data and appropriate authorization. The
backend's `tests/test_n_plus_one_optimizations.py` should assert bounded SQL query
counts for batch sizes such as 1 and 50, and verify duplicate IDs, financial totals,
status transitions, and rollback behavior. Do not run mutating scenarios on the
production target used by this UI suite.

Each optimization check writes a cropped screenshot of an allowlisted page heading to
`logs/evidence/api-manager-optimization/` and attaches it to `logs/report.html`, together
with an assertion note. A failure also captures the current allowlisted heading when
visible. Full-page screenshots, record rows, URLs, API keys, and credentials are not
included in this evidence.
