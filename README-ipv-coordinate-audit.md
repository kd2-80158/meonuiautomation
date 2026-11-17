# Face Finder IPV coordinate audit

The read-only TestNG class `com.ui.cloud.facefinder.ipv.IpvCoordinateAuditTest` follows the existing ReKYC Google sign-in pattern, opens Face Finder Superadmin, searches for the configured company (default **Globe Capital Market Ltd**), enters its Admin workspace through **Visit Admin**, and audits each IPV Logs page. It reads backend JSON responses from Chrome DevTools performance events and compares them with the rendered transaction rows and coordinates. It does not require separate `IPV_TOKEN`, `IPV_USERNAME`, or `IPV_PASSWORD` values and does not modify records.

The test waits at least five seconds after each navigation, click, or text-entry action. Google credentials come from the existing ignored `src/test/resources/config/credentials.json` via `JSONUtility`; they are entered only into the Google sign-in flow and never written to logs. CAPTCHA, MFA, or another Google challenge is not bypassed.

## Run from Maven

```sh
mvn -Dbrowser=chrome -DisHeadless=false -Dsurefire.suiteXmlFiles=src/test/resources/facefinder-ipv-coordinate-audit.xml test
```

## Run from Eclipse / RemoteTestNG

Run `src/test/resources/facefinder-ipv-coordinate-audit.xml` as a TestNG suite. The test selects Chrome because backend response capture uses Chrome DevTools Protocol.

## Configuration

JVM system properties use the `ipv.` prefix:

| Property | Default | Purpose |
| --- | --- | --- |
| `ipv.baseUrl` | `https://face-finder.meon.co.in` | Face Finder environment |
| `ipv.company` | `globe capital market ltd` | Exact company to select; set this for another tenant |
| `ipv.companySearch` | `globe capital` | Broad search text entered in the directory before selecting the exact company |
| `ipv.searchAttempts` | `3` | Number of search and result-click attempts before failing with a company-access error |
| `ipv.pageSize` | `100` | Preferred UI/API page size to reduce page actions for large tenants |
| `ipv.logsPath` | `/backend/get_ipv_logs` | IPV list API path captured from the authenticated page |
| `ipv.status` | `captured` | Dashboard log filter to audit |
| `ipv.rowsPath` | `data.data` | API rows dot path |
| `ipv.totalPath` | `data.total_items` | API record count dot path |
| `ipv.totalPagesPath` | `data.total_pages` | API page count dot path |
| `ipv.currentPagePath` | `data.current_page` | API current page dot path |
| `ipv.latitudeFields` | common latitude paths | Comma-separated candidate API paths |
| `ipv.longitudeFields` | common longitude paths | Comma-separated candidate API paths |
| `ipv.idFields` | common transaction ID paths | Comma-separated transaction ID paths |
| `ipv.maxPages` | `10000` | Upper bound to prevent runaway pagination |
| `ipv.actionDelaySeconds` | `5` | Minimum post-action wait; bounded to five seconds or more |
| `ipv.allowNullIsland` | `false` | Set true only if `(0,0)` is valid for the target company |

The test fails if the scan is incomplete or a backend/frontend row has a missing, nonnumeric, out-of-range, or suspicious zero coordinate. It also detects row omissions and frontend coordinate values that differ from the API. Failure details list only affected transaction IDs, coordinate values, page, and issue type; raw API payloads and authentication headers are not logged.
