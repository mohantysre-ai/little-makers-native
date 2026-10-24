# Native Little Makers 2.1.1 — release preparation

Use this directory for the native app. The repository's root `release/` directory belongs to the old web/Capacitor edition and must not be used to describe this native build.

## Prepared

- API 36 predictive Back with legacy support for older devices.
- Full offline privacy page accessible without a PIN; no external links added.
- Parent-gated reset of local gameplay/economy records, with explicit typed confirmation.
- Explicit no-backup/cloud-transfer rules and an earned-coins/no-cash disclosure.
- Accurate native listing and Data safety/Families/IARC worksheet.
- Native-matching original icon/feature graphic. Genuine screenshots still require capture.
- Release signing configuration reads credentials from environment; missing publisher fields or upload-key credentials block release builds.

## Finish publisher configuration

Edit `publisher-config.json`: legal/developer name, monitored adult-support/privacy email and final public HTTPS privacy URL. Do not use the private game preview as the public privacy URL. Run:

```bash
python3 tools/prepare-policy.py --release
```

Publish `release/policies/privacy.html` to your public policy host without a login or geo restriction. Verify it while signed out and compare it with the in-app copy. This host is separate from the offline app. No public policy was deployed in this task because publisher/contact details are missing.

## Build with your registered upload key

Use the upload key already registered for your existing app. For a new app, configure Play App Signing and keep the upload key private. Never send private key/password values in chat or commit them to Git. Set these environment variables locally:

- `LM_UPLOAD_KEYSTORE`: absolute path to your keystore
- `LM_UPLOAD_KEY_ALIAS`: its alias
- `LM_UPLOAD_STORE_PASSWORD`, `LM_UPLOAD_KEY_PASSWORD`: secrets from your local secure storage
- `LM_PYTHON`: optional Python executable, e.g. `python` on Windows

In Android Studio run the `bundleRelease` Gradle task, or use the included checksum-verified Gradle wrapper:

```bash
./gradlew :app:bundleRelease
```

On Windows, use `gradlew.bat :app:bundleRelease` from the native project directory.

Expected output: `app/build/outputs/bundle/release/app-release.aab`. Gradle/AGP require dependency access on the build machine. The runtime app needs no network. Release signing is configured but has not been exercised with your private upload key. The delivered APK is development-signed and is not a Play production artifact.

## Account and device work

1. Install on Android 7 baseline, Android 16, low/mid-range phones and tablets. Run `DEVICE_TEST_PLAN.md`, including gesture Back, process death, rotation, TalkBack, reset, audio, offline launch and 16KB-device checks.
2. Capture at least two genuine native-app screenshots. Use `tools/capture-device.sh`; do not use legacy web screenshots or AI-rendered images as actual screenshots. A preview video is optional; use genuine gameplay if you provide one.
3. Review final artifact permissions, network behavior, signing certificate, package/version and Play pre-launch report.
4. Complete target audience (intended ages 4–8), Families, Data safety, ads, IARC rating and app access. Use `DATA-SAFETY.md` as an evidence worksheet, not as a submitted form.
5. Confirm developer verification, correct package and production eligibility. If your personal account is subject to Google's new-account testing requirement, satisfy its required closed test before applying for production access.
6. Submit only after publisher policy and test evidence are complete. Do not claim guaranteed approval, legal certification, a top-five ranking or thousands of independent game engines.

Remaining identity, keys, hosted policy and Console declarations require publisher input/access. This kit does not certify COPPA/GDPR or replace a review of your distribution countries' laws.
