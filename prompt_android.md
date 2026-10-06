# Android Expense Monitor: Agent Implementation Brief

## 1. Your Role and Working Agreement

Act as a senior Android engineer, product designer, and security-conscious implementation partner. Build a professional personal expense tracker suitable for public Google Play distribution.

The owner is new to Android development. Explain unfamiliar concepts briefly when they become relevant. Do not assume knowledge of Gradle, Compose, Room, signing, or Android lifecycle behavior. Make conservative technical decisions, explain important tradeoffs, and ask before making choices that materially change the product or risk data loss.

This document defines the desired product and development milestones. It is not authorization to implement the entire application in one change. Begin by checking the available environment and confirming unresolved product decisions. Deliver small, buildable increments with focused validation. Do not claim code has been compiled or tested unless it actually has.

## 2. Fixed Product Requirements

- Android only. Do not create an iOS application.
- Fully usable offline after installation, including first-time setup, transaction entry, reports, and backup restoration.
- Free to download and use, with no ads, subscriptions, purchases, or paywalls.
- No backend, registration, login, financial-data synchronization, or bank connection.
- No analytics, advertising SDKs, remote crash-reporting SDKs, or automatic update/download services inside the app.
- No app password, PIN, biometric lock, or password-recovery feature for the initial release.
- Intended for users worldwide, not just the owner or German households.
- Direct transaction entry. Batch parsing, batch paste, and phone-note parsing are not required.
- Every rendered graph must have an accessible option to share that graph as an image.
- Preserve the existing desktop application without changing its behavior.

Internet may be required for obtaining tools, dependencies, store installation, and updates. It must not be required for installed application functionality. User-initiated sharing may hand an image or backup to another application that uses the internet; our app does not upload it itself.

## 3. Relationship to the Existing Application

The Python + FastAPI + SQLite application is a working reference and inspiration, not a required code dependency or exact design specification.

- Reuse accounting examples and tests as behavioral evidence where useful.
- Reimplement behavior naturally in Kotlin rather than embedding Python or running a local web server.
- Do not reproduce desktop workflows when a simpler mobile interaction is better.
- No requirement to reuse Python code, HTML templates, batch parsing, or the existing database schema.
- Desktop-data import is optional later work, not a blocker for the initial release.
- Never use the owner's real financial data in fixtures, screenshots, source control, or store materials.

## 4. Technical Direction

Use Kotlin, Jetpack Compose, Material 3 foundations, and Room over SQLite. Use Android Studio for builds, debugging, and emulator testing.

- Start with one app module and straightforward separation of UI, domain calculations, and persistence.
- Use ViewModels, lifecycle-aware state collection, coroutines, and reactive database queries where appropriate.
- Keep business rules independent of Android UI classes so they can be unit-tested.
- Do not perform database operations, backup processing, or image encoding on the main thread.
- Select maintained dependencies deliberately. Avoid adding a library when existing platform APIs are sufficient.
- Use a maintained Compose-compatible chart library if it meets rendering, accessibility, offline, license, and image-export requirements. Validate export feasibility before committing to it.
- Pin compatible tool/dependency versions and include the Gradle Wrapper.
- Do not add speculative multi-module architecture, a network layer, a server, or unnecessary dependency-injection complexity.

Source can be authored on Windows and pulled through Git onto Linux for builds and tests. Do not hard-code drive letters, SDK paths, or machine-specific locations. Respect filename case and executable permissions for the Gradle launcher. Exclude local SDK configuration and build output from Git.

Before scaffolding, identify the project location, application ID, available JDK/SDK, minimum Android version, and build-validation path. A stable application ID must be agreed before publication.

## 5. Product Decisions to Resolve

Record decisions explicitly rather than silently assuming:

1. Account types and credit-card balance presentation.
2. Refunds, corrections, opening balances, and future-dated entries.
3. One ledger or multiple separate ledgers in the first release.
4. Backup recovery, optional backup encryption, and system backup policy.
5. Supported Android versions and initial translations.

Recommended currency policy: one user-selected currency per ledger in version 1, with no exchange rates or mixed-currency totals. This is a recommendation pending confirmation, not permission to implement multi-ledger support automatically.

## 6. Accounting and Persistence Rules

- Store monetary amounts as integer minor units using an appropriately bounded type such as `Long`; never use `Float` or `Double` for stored money or authoritative accounting.
- Respect currencies with zero, two, or three fractional digits. Reject unsupported precision with clear validation; do not silently round entered amounts.
- Parse input using locale-aware, exact decimal handling. Validate overflow and excessively large values.
- Persist the ledger currency explicitly. Changing a label must not silently reinterpret existing monetary amounts in another currency.
- Income increases the receiving asset account; expenses reduce the paying asset account.
- Transfers affect both accounts atomically and count as neither income nor expense.
- Model credit-card liabilities explicitly. Purchases increase money owed; repayments reduce it without being counted as another expense.
- Define refund handling so reports are not inflated by treating an expense reversal as unrelated income.
- Derive balances consistently from opening balances and transactions. If using cached totals, update them transactionally and test reconciliation.
- Use calendar dates for financial entry dates; do not allow timezone conversion to shift an entered date.
- Use stable identifiers, foreign keys, indexes, and explicit ordering for queries.
- Use database transactions for all multi-record financial operations.
- Protect against duplicate submissions caused by repeated taps or interrupted navigation.
- Archive accounts/categories with historical references rather than deleting their history accidentally.
- Database migrations must preserve data. Never enable destructive migration fallback in production.
- Changing or deleting a transaction must update all affected balances and reports consistently.

## 7. Professional, Restrained User Experience

The app must feel professional, visually toned, easy to use, and minimally intrusive. It is an everyday financial tool, not a marketing page.

- Use restrained colors, clear hierarchy, compact summaries, and readable typography.
- Avoid oversized headings, excessive cards, decorative gradients, busy backgrounds, and unnecessary animation.
- Prefer familiar navigation and standard Android interaction patterns.
- Make everyday expense entry quick, with useful defaults that remain editable.
- Use appropriate numeric keyboards, clear field labels, and inline validation.
- Do not interrupt users with review prompts, promotions, newsletter requests, permission requests, or repeated onboarding.
- No notifications in the initial scope. Future reminders must be explicitly enabled and configurable.
- Never ask for permissions before the relevant user action requires them.
- Preserve unsaved entry state across normal configuration changes and communicate save failures accurately.
- Provide clear empty, error, loading, and success states without unnecessary popups.
- Use confirmations or undo for destructive actions; do not add confirmations to every routine action.
- Support system light/dark themes, edge-to-edge insets, large text, small screens, and keyboard visibility.
- Use accessible contrast, meaningful semantics, comfortable touch targets, and TalkBack labels for icon controls.
- Never rely on color alone for income, expenses, liabilities, or chart series.
- Keep all visible strings in Android resources; support pluralization and locale-aware formatting.
- Do not hard-code German institutions, euro symbols, personal account labels, or German-only categories.
- Choose an initial UI language explicitly, with translation-ready and right-to-left-aware layouts.

## 8. Charts and Image Sharing

Graphs must be attractive, restrained, readable, and financially accurate. Use them when they add information, not as decoration.

### Rendering

- Choose chart types appropriate to the comparison: monthly trends, category spending, income versus expenses, and similar useful views.
- Render crisp labels, consistent series colors, readable legends, appropriate axes, and sufficient spacing.
- Display the currency, reporting period, and relevant totals clearly.
- Handle no-data, all-zero, negative, single-point, and large-range datasets without misleading or broken charts.
- Define how refunds/negative category values are represented; do not force them into a pie chart that cannot represent them accurately.
- Do not truncate labels, overlap legends, distort bar baselines, or rely on tiny text.
- Provide an accessible textual summary or table for the same information.
- Use reliable responsive dimensions. Validate rendering in both themes, with large text, and on small screens.

### Mandatory Sharing for Every Graph

- Every graph instance must have a discoverable Share Image control associated with it, including graphs on dashboards, reports, or detail screens.
- Provide an accessible label such as "Share spending by category chart" and a standard share icon.
- Share a PNG image using Android's system share sheet and MIME type `image/png`.
- Export the chart, title, period, currency, and necessary legend/context, not a screenshot of the whole app, surrounding transactions, navigation, or system status bar.
- Use a controlled export background so the image remains readable outside the app regardless of the active theme.
- Export at a documented resolution suitable for viewing and sharing, without excessive memory use or clipped content.
- Show the exact image in a preview before handing it to other apps. Allow cancellation.
- Use the same filtered dataset and accounting calculations as the displayed chart. Do not export stale data or omit off-screen legend items.
- Do not include account identifiers, transaction notes, device information, or other unnecessary personal data in images, filenames, or metadata.
- Sharing must be initiated explicitly by the user; never upload automatically.
- Create export files in a dedicated private cache subdirectory and expose them through a narrowly scoped FileProvider `content://` URI.
- Grant temporary read access only; do not use `file://` URIs, public storage, broad provider paths, or write grants.
- Clean up stale exports on a bounded schedule without deleting files immediately while a recipient may still be reading them.
- Handle export failures and unavailable sharing targets without losing records or crashing.
- Test the actual shared image, not just whether the share button launches an intent.

Chart sharing reveals financial information to the selected recipient. Explain that briefly in the preview, without recurring intrusive warnings. Once another app receives an image, this app cannot retract it or control that app's handling of it.

## 9. Security and Privacy Requirements

Treat amounts, balances, account names, categories, and notes as private financial information. Security must be proportionate, explicit, and tested.

### Network and Permissions

- Do not declare `android.permission.INTERNET` in the release manifest. Inspect the final merged manifest for dependency-added permissions.
- No hidden network requests, WebViews loading remote content, remote fonts, telemetry, or dynamic code downloads.
- Request no contacts, location, microphone, camera, notification, or broad storage permissions for the agreed initial scope.
- Use the Storage Access Framework and system share sheet for user-selected files and destinations.

### Device Storage and Keys

- Store the database and private preferences in internal app storage, not public/shared folders.
- No app password or biometric gate in the initial release. Do not add them without approval.
- An app lock is not encryption. Private storage/device encryption also must not be described as protection against every compromised-device threat.
- Assess additional database encryption before public release. Room does not provide it automatically. Document the selected protection and its limitations.
- If encryption is selected, use a maintained vetted implementation and Android Keystore-backed key protection where appropriate. Do not invent cryptography or hard-code keys.
- Define key invalidation, device loss, reinstall, and recovery behavior. Keystore-bound keys are not portable backups.
- Never silently delete or recreate financial data when a key, database, or migration fails.
- No secrets, signing material, financial records, or sensitive fixtures in source control.

### Android Components and File Handling

- Export no component unnecessarily. The launcher entry point is an expected exception; explicitly configure exported states.
- Keep FileProvider non-exported with temporary URI grants and narrowly scoped paths.
- Use immutable PendingIntents if any are introduced and validate external intents/input.
- Disable debugging in release builds. Use explicit security configuration and disable cleartext traffic as defense in depth.
- Use parameterized Room queries; never concatenate untrusted values into SQL.
- Validate transaction fields, identifiers, amounts, dates, category types, and same-account transfers.
- Do not trust file extensions or claimed MIME types when importing backups.
- Do not load executable content or arbitrarily deserialize objects from imported files.

### Backups and Restore

- Define a versioned backup format with strict schema validation, size limits, record limits, and referential-integrity checks.
- Avoid unsafe archive extraction; if archives are used, defend against path traversal and decompression bombs.
- Validate the full backup before replacing live data. Restore atomically or through a recoverable staging procedure.
- Obtain explicit confirmation for replacement and preserve the current data on any failure.
- Decide whether backups are encrypted, optionally password-protected, or plaintext. Clearly disclose plaintext risk if allowed.
- If password encryption is implemented, use established authenticated encryption and a vetted password-based key derivation design; never store the password or log it.
- Backup passwords, if offered, are separate from an app login and require an agreed recovery policy.
- Explicitly configure Android cloud backup/device-transfer rules for sensitive records and keys. Test supported behavior and document manufacturer/platform limits.
- No automatic app-managed cloud backup. User-selected document providers may upload exported files under the user's own settings.

### Privacy and Operational Safety

- Never log transaction descriptions, amounts, balances, backups, credentials, or encryption keys, including in error reports.
- Use sanitized diagnostics. Android/store-provided diagnostics must also be considered when writing privacy disclosures.
- Do not place sensitive text on the clipboard automatically.
- Decide and document whether to hide sensitive content in recent-app previews. Do not indiscriminately block screenshots or break intentional chart sharing without a product decision.
- Provide "Delete all local data" with explicit confirmation. Remove records and app-managed exports where feasible; explain that external exports and recipient copies remain outside our control.
- Do not promise forensic secure erasure from flash storage.
- Audit dependencies and licenses; keep release dependencies maintained and resolve applicable vulnerabilities.
- Protect publisher accounts with two-factor authentication and manage signing keys outside the repository.
- Make store privacy claims only after auditing the final release and dependencies. Do not assume "offline" automatically proves every privacy claim.

## 10. Development Milestones and Acceptance Criteria

### Milestone 1: Product Rules

Agree account types, liability presentation, refund behavior, opening dates, currency policy, ledger scope, and backup/recovery policy. Write representative expected accounting outcomes.

Acceptance: unresolved choices are recorded; core examples have agreed balances and report totals.

### Milestone 2: Android Foundation

Create the separate Android project, Compose navigation, Room setup, unit-test infrastructure, and portable build instructions.

Acceptance: clean checkout builds and runs in the available emulator; machine-specific files are excluded.

### Milestone 3: Setup and Accounts

Implement currency selection, account creation, opening balances/dates, editing, and archiving.

Acceptance: a new user can configure their own finances without personal or country-specific assumptions.

### Milestone 4: Direct Entry

Implement expenses, income, transfers, dates, categories, optional descriptions, validation, and duplicate-submit protection.

Acceptance: everyday entry is efficient and balances remain correct, including credit-card scenarios.

### Milestone 5: History and Corrections

Implement history, detail, search, filters, editing, deletion, and safe correction flows.

Acceptance: changes update all affected balances/reports and preserve unrelated records.

### Milestone 6: Dashboard and Reports

Implement useful balance summaries, monthly totals, category breakdowns, trends, and polished charts.

Acceptance: totals reconcile with transactions; transfers do not inflate income/expenses; every chart supports verified PNG sharing.

### Milestone 7: Categories and Preferences

Implement category management and a small, justified settings surface, including theme behavior.

Acceptance: personalization preserves historical meaning and requires no unnecessary setup.

### Milestone 8: Backup, Restore, and CSV Export

Implement the agreed full backup format, safe restoration, and useful CSV export.

Acceptance: a fresh installation restores all relevant records/settings; malformed or incompatible backups leave current data intact. CSV output must prevent spreadsheet formula injection in user-controlled text without corrupting monetary values.

### Milestone 9: Worldwide Usability

Validate locales, supported currency precision, translation readiness, accessibility, large text, small screens, and selected right-to-left cases.

Acceptance: supported configurations have no clipped controls or misleading formatting; charts and their shared images remain readable.

### Milestone 10: Reliability and Security

Validate migrations, process interruption, large datasets, low-storage errors, offline behavior, final permissions, dependency privacy, storage protection, and backup handling.

Acceptance: no destructive production migration, unintended networking, sensitive logging, or silently lost records.

### Milestone 11: Physical-Device Beta

Use borrowed phones or trusted testers if the owner has no Android phone. Exercise actual file providers, chart sharing, lifecycle behavior, and upgrades on real devices.

Acceptance: critical defects are fixed, meaningful feedback is addressed, and required Play testing is complete.

### Milestone 12: Publication and Maintenance

Prepare publisher verification, signing, privacy/support pages, fictional-data screenshots, store declarations, review instructions, and release procedures.

Acceptance: publication requirements are met and future releases can be built, tested, and signed reliably. Maintain OS compatibility, dependencies, security fixes, and migration coverage afterward.

## 11. Testing Expectations

- Unit tests: exact monetary parsing, currency precision, balances, liabilities, refunds, transfers, edits, deletions, dates, and report reconciliation.
- Database tests: persistence, constraints, atomic updates, rollback, and every supported migration path.
- Backup tests: round trips, fresh-install restore, corruption, wrong password if applicable, unsupported versions, oversized input, and partial-failure recovery.
- UI tests: setup, direct entry, error messages, repeated save taps, history corrections, empty states, and state restoration.
- Chart tests: no data, single point, negative/refund data, long labels, different themes, large text, filtered periods, and decoded exported PNG contents/dimensions.
- Sharing tests: each chart has a share action; correct MIME/URI/grants; no unrelated screen data; recipient access and stale-cache cleanup.
- Offline checks: first launch and all core workflows with networking disabled, plus release merged-manifest inspection.
- Performance checks: representative multi-year histories, responsive scrolling/reporting, bounded export memory, and no main-thread storage work.
- Manual real-device checks: file pickers/providers, share targets, screen sizes, accessibility, app interruptions, and upgrades.

Use fictional data only. Report exact commands, results, and any environment limitations. An emulator is sufficient for early development, not the entire release validation.

## 12. Google Play and Organizational Requirements

- Keep developer accounts and signing ownership with the owner.
- Verify current registration fees, identity/device verification, target API levels, content declarations, and relevant financial-app policy before submission.
- As of the planning reference date, new personal accounts created after November 13, 2023 require at least 12 closed-test participants opted in continuously for at least 14 days before applying for production access. Recheck the current rule; eligibility does not guarantee approval.
- Physical-device verification may require access to a suitable Android phone. Plan this before publication, not before writing the first prototype.
- Provide a support email and public privacy/support page even without a backend.
- Assess publisher identity, rights to assets/code, applicable business/tax obligations, GDPR handling of support messages, and EU trader disclosures where relevant.
- Do not claim that free distribution exempts the publisher from legal or store obligations.
- Keep real financial data out of store listings and tester fixtures.

Official references:

- Android setup: https://developer.android.com/studio/install
- Compose: https://developer.android.com/develop/ui/compose
- Room: https://developer.android.com/training/data-storage/room
- Secure sharing: https://developer.android.com/training/secure-file-sharing
- Android security: https://developer.android.com/privacy-and-security/security-best-practices
- Play testing: https://support.google.com/googleplay/android-developer/answer/14151465
- Play Data safety: https://support.google.com/googleplay/android-developer/answer/10787469

## 13. Out of Scope Unless Explicitly Approved

iOS, Python embedding, batch parsing, cloud sync, bank APIs, payments, exchange-rate services, ads, paid features, account registration, app passwords/biometrics, third-party AI, receipt scanning, reminders, widgets, budgets, recurring entries, PDF reports, and desktop-data import.

These exclusions do not prohibit the mandatory chart image-sharing feature, CSV export, backup/restore, or consideration of backup encryption.

## 14. Agent Delivery Checklist

- Confirm the current milestone and product decisions before coding.
- Inspect nearby existing implementation before editing; preserve unrelated user changes.
- Implement the smallest coherent increment, then run focused checks.
- Keep the desktop app untouched and mobile changes isolated.
- Document setup and Windows-to-Linux build steps as the project takes shape.
- Audit every introduced dependency, permission, exported component, and data destination.
- Verify every new graph's appearance, accessibility, and image-sharing output.
- Do not declare a milestone complete while required acceptance checks remain unverified.
- Summarize what changed, what passed, and what remains blocked or undecided in plain language.

Planning reference date: October 6, 2026. Store requirements and tool versions must be rechecked when implementation or submission reaches them.