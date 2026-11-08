# Privacy Policy

**CFootprint** — IntelliJ IDEA plugin for tracking Codeforces progress

Last updated: 2026-11-08

## Summary

CFootprint does not collect, transmit, or sell any personal data. All data stays on your machine. The only network requests the plugin makes are to the public Codeforces API, using the handle you explicitly provide.

## Data the plugin accesses

### Codeforces handle

You enter your Codeforces handle in the plugin's tool window. It is stored locally using the IntelliJ Platform's `PropertiesComponent` so the plugin can restore it the next time you open the IDE. It is never sent anywhere except to the Codeforces API, as part of the request to fetch your public submission history.

### Public Codeforces data

When you click **Load**, the plugin requests the following from the public Codeforces API (`codeforces.com/api`):

- The global problem set and contest list (public data, no user identity involved)
- Your public submission history for the handle you entered

This data is fetched only to display your progress. It is written to local files and a local embedded database on your machine.

### Locally stored data

The plugin stores the following on your computer:

- Fetched JSON responses from the Codeforces API
- An embedded Xodus database containing problem metadata, contest metadata, and your submission status per handle
- Your handle and fetch timestamps in the IDE configuration

None of this data leaves your machine. There is no telemetry, analytics, or crash reporting.

## What the plugin does not do

- It does not collect personal information beyond the handle you enter.
- It does not transmit your data to any server other than the Codeforces API.
- It does not use cookies, tracking pixels, or advertising identifiers.
- It does not share data with third parties.
- It does not access files outside its own data directory and the IDE configuration.

## Network requests

All network requests go to `https://codeforces.com/api/`. These requests are subject to [Codeforces' own privacy policy](https://codeforces.com/privacy). The plugin does not proxy, log, or redirect these requests.

Requests are rate-limited and cached locally (6 hours for problem/contest data, incremental for submissions) to minimize load on Codeforces servers.

## Data removal

To remove all data stored by the plugin:

1. Uninstall the plugin from `Settings → Plugins`.
2. Delete the plugin's data directory (the location is shown in the plugin's settings or can be found under the IDE system directory).
3. Clear the saved handle via `Settings → Tools → CFootprint`, or remove it from the IDE configuration files.

Uninstalling the plugin does not automatically delete the local database; you may remove it manually.

## Changes

If this policy changes, the updated version will be included with the plugin release and the "Last updated" date above will be revised.

## Contact

For questions about this policy, open an issue in the plugin's repository.

[CFootprint Github](https://github.com/Azathorpe/CFootprint)