# Native 3D village update — 2.4.0

See [MOTION-2.4.0.md](MOTION-2.4.0.md) for the real 3D scene, motion controls, validation and remaining 2D content.

# Native creative-world review build — 2.3.0

Start with [PORT-STATUS.md](PORT-STATUS.md) for implemented screens, verification and remaining parity work. Open this folder itself in Android Studio; it contains settings.gradle and the app module.

# Little Makers — native Android edition

Version 2.1.1-native, versionCode 18. Open **this folder** in Android Studio. This is a new Android-framework implementation, separate from the repository's web/Capacitor edition in `android/`.

## What is implemented

- 1,200 catalog modules: **20 original mechanics × 60 levels**. This adds 1,000 levels (50 per section) to the original 200. These are puzzle/activity variations, not 1,200 independent game engines or unique curricula. The original 200 module IDs, rules, rewards and version-2 save format are retained. Catalog pages show ten modules at a time.
- Native `Activity`, standard Android widgets and custom `GameView`/`FriendView`. Every game uses the same theme, original locally generated fonts, rounded gradient buttons and Canvas artwork. No WebView, game-app launcher, advertising, analytics, runtime libraries or network permission.
- Counting, addition, subtraction, multiplication, division, shape/color matching, patterns, memory, size ordering, A–Z and 0–9 letter/number trails, 60 word-building boards, mazes, directional robot programs, two/three-color bottle sorting, L/line/T/square block rotation/placement, horizontal/vertical number merging, rhythm copying, ordered bridge assembly and resource planning. Introductory quantities stay at or below 16 items. Letter trails include repeated characters with different path density/scale.
- Global SQLite wallet/progress/inventory/ledger. First completion of each module gives one star and 5–9 coins in one transaction. Replaying does not farm rewards. Eight skins/accessories can be purchased/equipped with local coins; the dashboard refreshes immediately on return.
- White application text in dark mode, dark application text in light mode. Light/dark/system settings; all seven text/background palette combinations in each mode exceed 4.5:1 contrast. Illustration facial features retain their artwork colors.
- Gentle character motion, press/release motion, brief non-flashing win particles, robot step movement, original synthesized tap/note/win audio, haptics, pause, mute and reduced motion. System animation-duration setting is respected.
- Six-digit local parent PIN. Settings close when the activity leaves the foreground. Full offline privacy policy opens from Home without a PIN. Parent-gated typed-confirmation reset removes progress/economy records and keeps settings/PIN. No external links, real-money purchases, accounts or cloud synchronization.
- API 33–36 uses the native OnBackInvokedDispatcher; API 24–32 uses the same navigation decisions through the legacy callback. Tap games permit vertical scrolling; tactile tracing and matching gestures retain their drag behavior. System Back returns a game to its catalog, other internal destinations to Home, and exits from Home.
- Android backup and supported cloud/device transfer exclusions are explicitly configured.
- Canvas virtual accessibility buttons and directional keyboard activation. Canvas touch/accessibility bounds are at least 64dp with a minimum 320dp scene width; narrow screens can scroll the scene. Native widget buttons are at least 64dp tall. TalkBack usability still needs device verification.

## Source map

| File | Responsibility |
|---|---|
| MainActivity.java | Dashboard, categorized grid, game navigation, shop, parent gate/settings, lifecycle |
| GameCatalog.java | Exactly 1,200 stable module IDs and fixed reward definitions |
| ContentPack.java | Deterministic generators and curated extra word/letter content for the 1,000 additions |
| GameRules.java | Deterministic independent rules and bounded saved-state format |
| GameView.java / Artwork.java | Native Canvas mechanics, original graphics, interactions/accessibility, motion |
| ThemeManager.java | Shared palette, original local font loading compatible with API 24 |
| GameDatabase.java / res/raw/schema.sql | Single local SQLite database/schema |
| PlayerWallet.java / ProgressManager.java | Atomic purchases, first-clear reward, progress and cosmetic equipment |
| SoundEngine.java / AudioSynth.java | Native AudioTrack and original bounded PCM sound synthesis |
| tools/make_fonts.py | Original font generation; fontTools is a build-time tool only |

## Build and test

Requires JDK 17+ (direct SDK build was checked using JDK 21), Android SDK platform 36 and build-tools 36.0.0. Android Studio uses AGP 8.13.0; The official Gradle 8.13 wrapper is bundled with checksum verification for the wrapper JAR and distribution. Android Studio can import this folder directly; dependency downloads require network access. The direct SDK script avoids Gradle and has been exercised successfully.

```bash
export JAVA_HOME=/absolute/path/to/jdk
export ANDROID_SDK_ROOT=/absolute/path/to/android-sdk
./tools/test.sh
./tools/build-sdk.sh
```

The direct build creates `build/little-makers-native-debug.apk`, signed using a local development key in the ignored build directory. **This is an installable development APK, not a Google Play release bundle.** Do not distribute the debug key as a production key. Use `release/START-HERE.md`: release signing reads upload-key environment variables and release builds reject missing publisher policy fields or signing credentials. Complete those locally before building a Play release bundle.

This build reuses the development certificate from the previously delivered native 2.0.0 APK. The package ID is `com.littlemakers.village`. Existing installations signed by another key cannot be updated by this development APK. Use your existing release key for an upgrade; do not uninstall casually because app data may be lost. The native database is a new local profile: older web/Capacitor localStorage progress and art are not migrated.

## Validation performed

- All 1,200 modules solved by independent JVM rule tests. Checks include deterministic state, 50 distinct added board specifications in each section, wrong-input gates, save/reopen, corrupt-state rejection, geometry bounds and undo persistence. Board specification differences can include visual arrangements, repeated letter paths or quantities; they are not 1,000 unrelated learning objectives.
- 400 serialized initial/completed fixtures from the original native 2.0.0 load byte-for-byte unchanged, covering all 200 original IDs.
- Original tap and win PCM differ, have bounded amplitude and start with an attack envelope.
- 2,437 schema/palette/source checks: first-clear reward deduplication, rollback of negative balance, durable records after reopen, 14 text/background contrasts and prohibited runtime dependency audit. SQLite checks exercise the shipped schema using Python SQLite; they do not run the Android database classes on a device.
- Shared legacy/predictive navigation decisions, matching offline/HTML privacy text, native listing accuracy/lengths, protected reset, backup exclusions and release guards passed.
- Android SDK 36 Java/resource compilation, DEX creation, APK alignment and signature verification passed.

Not verified: device/emulator rendering, Android 7–16 execution, TalkBack interaction, large-font layout, rotation, actual speaker playback, lifecycle stress or measured 60fps. Two genuine development-build screenshots were captured on the API 24 emulator, alongside a basic counting/wallet/accessibility-node smoke test. Physical-device acceptance and gameplay video are still pending. Minimum supported version is Android 7.0/API 24; this does not run on every historical Android version.

## Scope and release limitations

This edition does **not** yet port the web app's 3D clay, large coloring library, story-tree animations or notebook. It does not reproduce 1,200 commercial games, full fluid/rigid-body physics, a level editor or thousands of distinct lessons. The native mechanics above are playable educational boards, with reusable rules and deliberately limited introductory vocabularies.

Publisher details and the public policy URL are currently unconfigured. The offline policy is clearly marked as a development copy. Before a production release: finalize those fields and publish the matching policy, test on physical devices/emulators, review age/curriculum suitability and PIN recovery, add any needed legacy-save migration, produce genuine screenshots/video, create a release-signed AAB, and complete publisher-specific policy/store declarations. No store approval, ranking or legal compliance is guaranteed.

## Assets

All bundled illustrations/particles are original Canvas code or `res/drawable/app_icon.xml`. Both custom geometric font files are generated from our local glyph matrix; lowercase maps to uppercase forms. No third-party character, game artwork or downloaded audio is bundled. Font regeneration needs Python/fontTools but the APK does not.
