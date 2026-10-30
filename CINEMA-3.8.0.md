# 3.8.0: interactive offline cinema and material rendering

This release implements a real native MP4 player with interactive learning choices, not a simulated video or an external streaming embed. Open **Story shelf → Discovery cinema**, **Playroom → Discovery cinema**, or the Moon expedition in the science mission list.

## Delivered

- One Moon expedition film: four distinct eight-second clips and two discovery paths. A route plays three clips. This is 32 seconds of unique bundled footage, not a library of hundreds of films.
- Child-controlled Play/Pause, captions, offline voice when installed, watch-before-choice gating, gentle incorrect-answer recovery, lifecycle pause/resume, decoder-error retry and local Back navigation.
- First completion awards one star and five coins through the existing wallet. Replays do not farm rewards. Parent reset clears cinema progress while retaining protected settings.
- An original material atlas provides wood, stone, clay and sand surface detail in all six native 3D renderers. Native fish geometry includes a shaped body, forked tail, paired fins and eyes, procedural scales and animated swimming. Clay lighting follows sculpted normals.
- No Internet permission, WebView, external player, ads, competitor assets or runtime media service was added. Existing database schema remains unchanged.

## Actual validation

All 13 named Android 7/API 24 instrumentation suites passed: rendering, creative input/persistence, wardrobe/aquarium, school trail, visual navigation, adventures, quests, studios, motion, Playroom routing, readability, parent controls, and cinema. The rendering sweep covered 36 screens and constructed all 630 scripted-story step frames. Runtime material checks require successful texture upload and GL_NO_ERROR, rather than accepting a matte fallback as a textured pass.

The cinema suite passed 59 checks against Android MediaPlayer: decoded frames, advancing time, clip completion, pause/recreation, route/quiz gates, shared rewards, dark labels, reduced mode, intentional decoder failure/retry and Back to the calling Story shelf. The final rebuilt APK was installed and this suite passed again. The final rebuilt APK also passed the 36-screen rendering sweep again. The only post-batch APK change was production-source checksum metadata; runtime code and media bytes did not change.

Host checks cover 1,200 solver fixtures, saved-state rules, content/platform guards, 53 branching-state checks, 36 real MP4 codec/hash/fast-start/moving-frame checks, and 24,356 numerical fish/normal assertions. These are test assertions, not counts of unique games.

An initial cinema test exposed a real resource-namespace mismatch. Explicit allowlisted R.raw/R.drawable mappings fixed it. The failing log is retained alongside passing evidence. The decoder-error screenshot is an intentionally injected invalid URI test.

Android 16/API 36 remains **unverified**: the software emulator showed a blank screen before a usable OS boot. This is not a passing compatibility result. No physical-device performance, audible audio/haptics, or Google Play approval is certified.

See [machine-readable outcomes](verification/qa38/results.json), [section inventory](verification/qa38/section-inventory.json), and [actual cinema captures](verification/qa38/cinema-contact.jpg). Each inventory entry explicitly records the remaining realism gap. Listing a verification suite in the inventory is a coverage mapping; its outcome is in results.json.

## Visual and content limits

The Moon clips are detailed scientific lunar-data visualization, not live camera footage or animal character cinema. Existing 210 scripted stories are not converted into videos. Drawing books remain intentional 2D line art; stylized native environments, characters and UI remain elsewhere. Added albedo texture does not make a stylized mesh photorealistic. The whole-app photorealism request remains incomplete.

An original animal-video pilot could not be generated: the connected video service required a Basic plan and rejected the request before creating a job. No animal film is included or counted.

## Media provenance and reproducibility

The four title-free, silent lunar clips are derived from NASA SVS 4619. Source URLs, credit, in-points, codec settings and SHA256 checksums are in [media_manifest.json](app/src/main/assets/cinema/media_manifest.json). NASA and its contributors do not endorse the app. The narrated source soundtrack/music was not used. Source notes and the reviewed regeneration command are in [art-source/cinema](art-source/cinema/README.md) and tools/prepare-cinema.py.

The original generated material artwork is documented, including its exact prompt, in [art-source/materials](art-source/materials/README.md). It is not represented as a camera photograph or a scanned PBR material set.

## Build and artifact

Use JDK 21, Android platform/build-tools 36 and `bash tools/build-sdk.sh`. Run host checks with `bash tools/test.sh`; build the disposable-profile instrumentation APK with `bash tools/build-render-check.sh`. Instrumentation resets test profiles and must not run against a child's real data.

Final debug APK: version 3.8.0-native (36), 26,713,010 bytes, SHA256 `1d4bdaa7c41e8827c0226f4ae8a108b57aaae8884179eee6544ec288b42e922a`. APK v2/v3 signature verification passed. This is a debug/test APK, not a Play-signed release AAB. Signing keys are not published.
