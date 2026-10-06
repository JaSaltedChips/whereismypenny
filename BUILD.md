# Where's My Penny — Build Guide

Source is authored on Windows and built on a separate **Ubuntu 26.04 LTS** machine.
Do not hard-code paths; everything in this guide uses the project directory as root.

---

## 1. Prerequisites (Ubuntu Linux build machine)

### Java Development Kit

Android Studio bundles a JDK. Use the bundled JDK by default; do not set `JAVA_HOME` manually.
If you need a system JDK for other tooling:

```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version  # must print 17.x
```

### Git

```bash
sudo apt install git
```

### Android Studio

Download the latest stable release from the official page:
https://developer.android.com/studio/install

```bash
# Example — verify the current filename at the link above before running
tar -xzf android-studio-*.tar.gz
sudo mv android-studio /opt/
/opt/android-studio/bin/studio.sh
```

During first launch:
1. Complete the setup wizard. Choose **Standard** installation.
2. The wizard downloads the Android SDK, platform tools, and a default emulator image.
3. Note the SDK location shown at the end (typically `~/Android/Sdk`).

---

## 2. Clone the project

```bash
git clone <your-repo-url> wheresmypenny
cd wheresmypenny
```

---

## 3. Bootstrap the Gradle wrapper

The `gradle-wrapper.jar` binary is not committed (it is a build tool, not source).
Android Studio regenerates it automatically when you open the project for the first time.

**Option A — let Android Studio handle it (recommended for first-time setup):**

1. Open Android Studio → **Open** → select the `wheresmypenny` folder.
2. Android Studio detects the missing wrapper jar and offers to create it. Accept.
3. It also syncs Gradle and downloads all dependencies declared in `gradle/libs.versions.toml`.

**Option B — command line only:**

```bash
# Install the Ubuntu system Gradle (used once only to generate the wrapper)
sudo apt install gradle

cd wheresmypenny
gradle wrapper --gradle-version=8.9 --distribution-type=bin

# Make the wrapper executable (required after git clone on Linux)
chmod +x gradlew
```

After either option, verify:

```bash
./gradlew --version  # should print Gradle 8.9
```

---

## 4. Confirm SDK path

Create `local.properties` in the project root (already in `.gitignore` — never commit it):

```
sdk.dir=/home/<your-username>/Android/Sdk
```

Android Studio sets this automatically. If you use the command line only, create it manually.

---

## 5. Build the debug APK

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

---

## 6. Run unit tests

```bash
./gradlew :app:testDebugUnitTest
```

The `BalanceCalculatorTest` suite must pass. It verifies the core accounting examples
from `decisions.md`. Do not proceed to later milestones if these tests fail.

---

## 7. Create and start an emulator

Inside Android Studio:

1. **Device Manager** → **Create Virtual Device**.
2. Choose a phone definition with a screen ≥ 360dp wide (e.g. Pixel 6).
3. Select a system image: **API 26** minimum (Oreo). Download if not present.
4. Finish and click **▶ Play** to start the emulator.

Or via command line once the AVD is created:

```bash
# List available AVDs
$ANDROID_SDK/emulator/emulator -list-avds

# Start one (replace <avd-name> with the name from the list)
$ANDROID_SDK/emulator/emulator -avd <avd-name> &
```

---

## 8. Install and run on the emulator

```bash
./gradlew installDebug
```

Or use Android Studio's **▶ Run** button (Shift+F10).

### Acceptance check for Milestone 2

- App launches without crashing.
- Bottom navigation bar shows four tabs: Overview, Transactions, Reports, Settings.
- FAB (+ button) navigates to the Add Transaction placeholder screen.
- Each tab shows a "coming soon" placeholder.
- Rotating the device keeps the current tab selected.

---

## 9. Check the merged manifest

After every dependency update, inspect the merged manifest to confirm no unintended
permissions were introduced:

```bash
./gradlew :app:processDebugManifest
cat app/build/intermediates/merged_manifests/debug/AndroidManifest.xml | grep permission
```

The output must be empty (no `<uses-permission>` lines). The app must not declare
`android.permission.INTERNET` in any build type.

---

## 10. Dependency version updates

All versions are pinned in `gradle/libs.versions.toml`. Before the first build,
check for newer stable releases:

- AGP: https://developer.android.com/build/releases/gradle-plugin
- Kotlin: https://kotlinlang.org/docs/releases.html
- Compose BOM: https://developer.android.com/jetpack/compose/bom/bom-mapping
- Room: https://developer.android.com/jetpack/androidx/releases/room
- Navigation: https://developer.android.com/jetpack/androidx/releases/navigation

Update all versions together; Compose BOM pins its own component versions automatically.

---

## 11. Release build (Milestone 12)

Release signing requires a keystore that is **never committed to git**.
Store it outside the repository directory and reference it from a properties file
(also excluded from git). See Android documentation on app signing:
https://developer.android.com/studio/publish/app-signing

---

## Notes for Windows development machine

- Edit source files on Windows; push via Git.
- `gradlew.bat` is included for reference; builds run on Linux only.
- File paths in Kotlin source must use forward slashes; the project contains none.
- All string resources use `res/values/strings.xml` — never hardcode text in Kotlin files.
