# Native 2.1.1 validation — 12 October 2026

Passed:
- All 1,200 game modules solvable; 10,814 rule/state assertions.
- All 400 original-version saved-state fixtures compatible.
- Distinct tap/win PCM with bounded amplitude and envelope.
- Official Gradle wrapper/distribution checksums verified; AGP/Gradle project task configuration succeeded. Both custom release tasks were executed: missing publisher fields and signing credentials were correctly rejected.
- Tap-game scroll cancellation avoids accidental answers; tactile exercises retain gesture capture. Canvas accessibility host-source and clipped visibility were corrected.
- API 24 emulator: install/launch, catalog selection, tap-game scrolling, exposed virtual accessibility nodes, six-item counting win and dashboard refresh to 1 star/7 coins passed. See DEVICE-SMOKE-RESULT.json.
- Shared Back routing for game/catalog/shop/parent/privacy/Home; SDK 33 callback is guarded for older Android.
- 2,437 schema/palette/source checks, including exact shipped reset SQL. Reset clears five gameplay tables, zeroes coins/stars and keeps the wallet row.
- In-app and HTML privacy copies match, native listing lengths/feature claims are validated, backup/transfer exclusions exist, release-policy/signing guards are wired.
- API 36 resource/Java/DEX compilation, APK alignment and development signature verification.
- Exact APK declares no permissions and contains no `.so` libraries; offline privacy text is bundled.
- Original native icon: 512×512 RGBA; feature graphic: 1024×500 RGB, within size limits.

Not completed:
- Publisher name, support email and public privacy URL. Generated policy is visibly a development copy; production policy preparation rejects missing fields.
- Signed AAB using the publisher's registered upload key. Gradle release signing configuration is prepared; it has not been exercised with that key.
- Physical-device/Android 16 gesture runtime, full UI/audio/accessibility/performance verification, network observation and Play pre-launch report.
- Final production-release recapture and optional gameplay video. Two authentic native screenshots were captured on the API 24 development emulator; see media/capture-status.json. Marketing icon/feature graphic are separately labeled.
- Play Console declarations, account verification/eligibility, IARC rating and any required closed testing.
- Jurisdiction-specific legal review; no COPPA/GDPR certification or approval guarantee.

The old root release files remain scoped to the web/Capacitor edition. Use this native release kit exclusively for the native build.
