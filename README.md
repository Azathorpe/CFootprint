# CFootprint

<p align="center">
  <a href="README.md"><img src="https://img.shields.io/badge/Language-English-blue.svg" alt="English"></a>
  <a href="README_zh.md"><img src="https://img.shields.io/badge/语言-简体中文-red.svg" alt="简体中文"></a>
</p>


An IntelliJ IDEA plugin for tracking your Codeforces problem-solving progress. Problems are displayed in a matrix view: contests as rows, problem indices as columns, with each cell showing submission status and difficulty at a glance.

## Features

- **Matrix view**: rows are contests, columns are problem indices (A, B, C…). Sub-problems sharing an index (C1, C2) are stacked within the same cell.
- **Pagination**: a fixed number of contests per page, with page-size selection, previous/next navigation, and jump-to-page.
- **Status coloring**: cell background reflects submission status — green for accepted (AC), yellow for attempted but unsolved, default for unsolved.
- **Difficulty coloring**: optional toggle that colors the text according to Codeforces rating tiers (gray / green / cyan / blue / violet / orange / red / dark red).
- **Multi-dimensional filtering**: filter by problem name, rating range, problem type, tags, and contest division.
- **Quick navigation**: click any problem to open its Codeforces page in the browser.
- **Local caching**: contest and problem data are not re-fetched within 6 hours; user submissions support incremental updates to avoid unnecessary API calls.

## Storage

Built on [Xodus](https://github.com/JetBrains/xodus), an embedded database requiring no separate service.

| Entity | Description |
|---|---|
| `Contest` | Contest metadata (contestId, name, division) |
| `Problem` | Problem metadata (contestId, index, name, type, points, rating) |
| `ProblemStatistics` | Solved count per problem |
| `UserProblemStatus` | Per-handle submission status (handle, contestId, index, verdict) |

User status is stored per handle, so switching accounts never overwrites another user's data.

### Caching strategy

- **Problems / contest list**: on startup, a timestamp check skips the network request if the last fetch was less than 6 hours ago, reading directly from the local database.
- **User submissions**: the first load fetches everything; subsequent loads for the same handle within 6 hours only parse the local JSON without hitting the API again.
- Incremental merge rule: problems already marked AC locally are never downgraded by later non-AC submissions.

## Usage

1. Open the **CFootprint** tool window from the right toolbar in IDEA.
2. Enter your Codeforces handle at the top and click **Load**.
3. The first load fetches your submissions and writes them to the local database; on subsequent IDE launches, the last handle and status are restored automatically.
4. Use the filter bar to narrow problems by name, rating, type, tags, or division.
5. Click any problem to open its Codeforces page.

### Settings

- **Difficulty coloring**: a toolbar checkbox controls whether text is colored by rating tier.
- **Font**: adjust font family and size offset under `Settings → Tools → CFootprint`.

## Build

```bash
./gradlew buildPlugin
```

The artifact is produced under build/distributions/.

## Tech stack
- IntelliJ Platform SDK

- Xodus (embedded database)

- Fastjson2 (JSON parsing)

- Swing / JetBrains UI components

## Notes
- The plugin respects Codeforces API rate limits; both problem and user data are cached locally to avoid unnecessary load on the server.

- User submission status is stored per handle, supporting multiple accounts on the same machine.

- Problem data is sourced from the public Codeforces API; all problem content remains the property of Codeforces.