# Expense Monitor: Android and iOS Development Guide

This guide explains how to turn the existing desktop expense tracker into a mobile app. It covers both platforms for comparison; it does not mean that both must be built at once.

## 1. Product Requirements

- Works without internet after installation.
- Free to download and use.
- No ads, subscriptions, or in-app purchases.
- No user registration or financial-data backend.
- No advertising, analytics, or remote crash-reporting SDKs.
- Financial records remain on the device unless the user explicitly exports them.
- Preserve the existing Python desktop application.

Internet access is still needed to download development tools, obtain dependencies, install store releases, and receive updates. Offline operation refers to the installed app's everyday functionality.

## 2. What We Can Reuse

The current application uses Python, FastAPI, HTML templates, and SQLite. Mobile development is a port, not simply packaging the existing server.

| Existing asset | Mobile use |
| --- | --- |
| Accounting rules | Port and verify income, expenses, transfers, and liabilities |
| Parser behavior | Port batch validation and duplicate warnings |
| Automated tests | Use as behavioral specifications and shared test fixtures |
| SQLite data model | Adapt to mobile persistence and versioned migrations |
| Reports | Reimplement calculations and mobile presentation |
| HTML screens | Use as a functional reference; rebuild mobile screens |

Use integer minor units, such as euro cents, for monetary calculations. Do not use binary floating-point values for money. Use a defined import/export format to move desktop records; do not assume the mobile app can directly open every existing database version.

Keep mobile work in a separate project directory or repository. Never commit real financial databases, exports, PIN hashes, signing keys, or credentials.

## 3. Choose a Technology

| Target and environment | Recommended approach |
| --- | --- |
| Android only, developing on Linux | Kotlin, Jetpack Compose, and Room |
| iOS only, with access to a Mac | Swift, SwiftUI, and local persistence |
| iOS while writing code on Linux | React Native with Expo and hosted macOS builds |
| Both platforms with shared code | Evaluate React Native/Expo or Flutter |

Kotlin and SwiftUI produce separate native applications. Choosing both means maintaining two implementations of business rules and testing their equivalence.

Cross-platform frameworks can share much of the application code, but still require platform-specific configuration, signing, and testing. Flutter and React Native do not remove Apple's macOS build requirement.

Choose the target and workflow before scaffolding. If both stores become a firm requirement, reconsider code sharing before building a substantial native implementation.

## 4. Android Development on Linux

### Step 1: Install the Tools

1. Install Git and Android Studio using the official Linux instructions.
2. Use Android Studio's bundled JDK initially.
3. Install the Android SDK, SDK Platform Tools, and Android Emulator through SDK Manager.
4. Enable hardware virtualization in the computer's firmware and configure KVM on Linux.
5. Create an Android Virtual Device through Device Manager.

About 16 GB RAM is a practical starting point for Android Studio plus an emulator; actual requirements depend on the project and virtual device. Confirm current requirements before choosing hardware.

Official setup: https://developer.android.com/studio/install

### Step 2: Create the Android Project

1. Create a Kotlin project with a Jetpack Compose activity.
2. Choose a stable application ID and supported Android versions.
3. Add Room for local SQLite persistence.
4. Separate accounting logic from UI and storage so it can be unit-tested.
5. Add database migration tests from the beginning.
6. Do not request the `android.permission.INTERNET` permission.
7. Check the final merged manifest, including permissions contributed by dependencies.

Room does not encrypt the database by itself. Encryption, if required, needs a compatible implementation and a tested key-recovery strategy.

### Step 3: Build the First Working Workflow

1. Create accounts and opening balances.
2. Record income, expenses, and transfers.
3. Show transaction history and correct balances.
4. Verify credit-card purchases and repayments without double-counting expenses.
5. Restart the app and confirm that records persist.
6. Repeat the workflow with the emulator's network disabled.

### Step 4: Add the Remaining Features

- Categories, transaction editing, filtering, and deletion.
- Monthly reports and charts.
- Batch-paste entry with previews and duplicate warnings.
- CSV export and desktop-data import.
- Full backup and restore, preferably with encrypted backup files.
- Optional biometric/device-credential lock.
- A clear action to delete all local records.

Use Android's system file picker and share sheet instead of requesting broad storage access. PDF export can follow after the accounting and backup workflows are reliable.

### Step 5: Test Without Owning an Android Phone

The emulator supports development, UI tests, network-disabled tests, different screen sizes, and simulated fingerprint events. It does not fully reproduce real biometric hardware, manufacturer behavior, performance, or document-provider integrations.

Before release, arrange access to real Android devices through borrowed phones or trusted testers. You do not necessarily need to buy a phone.

New personal Google Play accounts also require device verification using the Play Console mobile app on a suitable physical, non-rooted Android device. The current requirement specifies Android 10 or later; an emulator is not sufficient. Verify the latest rule before enrollment and sign in yourself without sharing credentials.

Device verification: https://support.google.com/googleplay/android-developer/answer/14316361

### Step 6: Publish on Google Play

1. Register a developer account under your ownership and complete identity verification.
2. Pay the current US$25 one-time registration fee, subject to applicable payment terms.
3. Choose personal or organization registration based on your actual publisher status and applicable policies.
4. Configure release signing and Play App Signing; securely manage upload keys and recovery information.
5. Produce a signed Android App Bundle using the target API level required at submission.
6. Prepare screenshots with fictional records, the description, icon, support contact, privacy policy, content rating, and applicable declarations.
7. Complete the Data safety form based on the final app and every dependency.
8. Run internal and closed testing, address feedback, and apply for production access where required.
9. Submit the production release and monitor review feedback.

For personal accounts created after November 13, 2023, the current closed-test requirement is at least 12 testers opted in continuously for at least 14 days before applying for production access. They do not need to keep the app open or online continuously. Meaningful testing and feedback are expected; meeting the duration alone does not guarantee approval. Recruit extra testers to allow for dropouts.

Registration: https://support.google.com/googleplay/android-developer/answer/6112435

Testing: https://support.google.com/googleplay/android-developer/answer/14151465

## 5. iOS Development

### Route A: Native Development with a Mac

1. Obtain access to a Mac that supports the Xcode version and iOS SDK needed for development and submission.
2. Install Xcode and its iOS simulator components.
3. Create a Swift/SwiftUI iOS project with a stable bundle identifier.
4. Choose local persistence, such as SQLite through a maintained library, Core Data, or SwiftData where supported. Do not enable cloud synchronization.
5. Port and test the accounting rules independently of the UI.
6. Implement the same core workflows described in the Android section.
7. Add file export/import, optional Face ID/device authentication, and protected local storage.
8. Connect your iPhone, trust the Mac, configure signing, and enable Developer Mode when required.
9. Test on your iPhone and in the simulator, including airplane mode and backup restoration.

A free Apple Account can install development builds through Xcode using a Personal Team, with limitations. Free provisioning profiles expire after 7 days, so periodic rebuilding and reinstalling is necessary. This is not a permanent public distribution method.

Xcode: https://developer.apple.com/xcode/

Membership comparison: https://developer.apple.com/support/compare-memberships/

### Route B: Write Code on Linux and Build in the Cloud

Xcode and the iOS simulator do not run natively on Linux. Having an iPhone does not replace the Apple build tools.

1. Install Git and the Node.js version supported by the selected Expo release on Linux.
2. Create a React Native/Expo project using TypeScript.
3. Use a supported local SQLite integration and separate domain logic from screens.
4. Use Expo Go for compatible early prototypes, recognizing its native-module limitations.
5. Configure a hosted macOS build service, such as Expo EAS Build, for standalone iOS builds.
6. Enroll in Apple's paid developer program for TestFlight or other supported distribution of your own device builds.
7. Configure signing through the service without putting credentials or private keys into source control.
8. Install builds on your iPhone through TestFlight or properly provisioned development distribution.
9. Test the standalone build, not just the app running inside Expo Go.

Cloud build services may have usage limits, queues, or paid plans. Debugging native failures is harder without interactive Xcode access; occasional access to a Mac may still be necessary. A hosted Mac is a development resource, not a backend for users' financial records.

Disable any configured over-the-air update checks and other automatic network services if the release promises no background network requests. Verify actual behavior rather than assuming that local storage makes the entire app offline.

Expo builds: https://docs.expo.dev/build/introduction/

### Publish on the Apple App Store

1. Enroll in the Apple Developer Program with two-factor authentication and verified publisher details.
2. Budget for US$99 per membership year; regional pricing varies.
3. Choose individual or organization enrollment as appropriate. Individual enrollment displays your legal name as seller; organizations generally need a recognized legal entity and D-U-N-S number.
4. Create the app record in App Store Connect and configure signing.
5. Upload a signed build produced using the supported Apple toolchain.
6. Distribute a beta through TestFlight. External testing requires TestFlight review, including approval of the first external build.
7. Prepare the listing, support URL, privacy-policy URL, age rating, privacy disclosures, and reviewer instructions.
8. Address applicable privacy-manifest, required-reason API, SDK, and encryption/export-compliance requirements.
9. Select free pricing, submit for App Review, and respond to any review questions.

Apple does not impose Google's 12-testers-for-14-days requirement. Testing is still necessary, and approval is not guaranteed. TestFlight builds expire and are not a permanent replacement for a production release.

Enrollment: https://developer.apple.com/programs/enroll/

TestFlight: https://developer.apple.com/testflight/

Review guidelines: https://developer.apple.com/app-store/review/guidelines/

## 6. Security, Backups, and Privacy on Both Platforms

- A PIN or biometric screen lock is not database encryption.
- Use platform key storage: Android Keystore or Apple Keychain, as appropriate.
- Select storage protection and encryption deliberately; test behavior when the phone is locked.
- Define whether losing a device or encryption key makes records unrecoverable.
- Protect backup files and document any recovery password requirement.
- Test restoration on a fresh installation, not just export creation.
- Explicitly configure operating-system backup behavior. No app-level sync does not automatically exclude financial data from system cloud backups.
- Android backup exclusions and iOS backup exclusions have platform-specific limits; verify behavior and avoid claiming that the app controls every user-initiated device backup.
- Let users choose export destinations. A system file picker may expose cloud providers even though the app itself has no cloud service.
- Do not include transaction descriptions, balances, or account names in diagnostic logs.
- Keep permissions minimal and bundle required fonts, icons, and chart assets.
- Check all dependencies before declaring that no data is collected or shared.

Data loss after uninstalling or losing a phone must be clearly addressed through a reliable backup workflow. No backend means there is no server-side recovery copy.

## 7. Organizational and Legal Preparation

1. Define the publisher and confirm ownership of the code, name, artwork, and other assets.
2. Set up a support email and a public support/privacy website; free static hosting may be sufficient.
3. Write an accurate privacy policy even if the app does not transmit financial data.
4. Assess applicable German/EU business, tax, Impressum, trader-status, and GDPR obligations, including support messages you receive. Free distribution does not automatically remove all obligations.
5. Check current financial-app classification and publisher-entity rules for the exact scope. A manual expense diary is not the same as a banking service, but store policies must still be assessed.
6. Keep bank connections, payments, financial advice, and third-party AI outside the initial scope; adding them requires a separate assessment.
7. Plan for support, dependency updates, security fixes, and changing store requirements.

No billing integration or store purchase commission is needed while the app has no paid downloads or digital purchases. Publisher membership, hardware, and build-service costs can still apply.

## 8. Recommended Milestones

1. Choose one platform and confirm access to its build environment.
2. Define the database, import format, monetary rules, and representative accounting test fixtures.
3. Build accounts, income, expenses, transfers, history, and balances.
4. Validate everything without network access and after app restart.
5. Add reports, batch entry, editing, filtering, and desktop-data import.
6. Implement protected storage, app locking, backup/restore, and local-data deletion.
7. Test upgrades, migrations, interruptions, accessibility, and real devices.
8. Complete store accounts, privacy documentation, beta testing, and production submission.

For the current situation, Android can be developed on Linux using an emulator, with physical-device access arranged before publication. iOS can be tested on the existing iPhone, but requires either a Mac workflow or a hosted macOS build workflow.

## 9. Release Checklist

- [ ] Core workflows work with no network connection.
- [ ] Accounting tests pass, including credit-card repayments and transfers.
- [ ] Data survives app restarts and supported version upgrades.
- [ ] Backup restores successfully on a fresh installation.
- [ ] Storage, keys, and recovery behavior have been reviewed.
- [ ] System backup behavior matches the documented privacy promise.
- [ ] No ads, billing, analytics, or unintended network services are included.
- [ ] Dependencies and final permissions have been audited.
- [ ] Real-device testing has been completed.
- [ ] Screenshots and test data contain no personal financial information.
- [ ] Privacy policy, support contact, and store declarations are complete.
- [ ] Publisher verification and required beta testing are complete.
- [ ] Signing credentials are secured and recovery procedures documented.
- [ ] Current target API/SDK and submission policies have been checked.

Policy and price reference date: October 6, 2026. Requirements and fees can change; consult the official links immediately before registration and submission.