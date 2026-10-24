# Little Makers native 2.3.0 — creative-world port

## Implemented
- The original web `world.svg`, companion and destination illustrations form an interactive native village. World / Books / Play / Creations navigation stays visible.
- All 1,053 web picture catalog entries are bundled with both coloring outlines and thumbnails; browse in paginated shelves. Manual strokes, brush colors/sizes, undo, local drafts and a paginated private gallery are implemented.
- Texture scratch-to-reveal uses those same picture entries. This is a native adaptation, not an exact translation of every web tracing exercise.
- Native OpenGL ES 2 clay mesh: pull, flatten, roll, rotate, color and saved geometry. Canvas fallback remains for unsupported GLES2 devices. Mesh mechanics and lighting differ from the web Three.js engine.
- All 210 exported web story episodes retain their original steps, choices, endings and props. Native scenes use the original Kenney characters/background props with subtle motion, drag placement and accessible placement/Next scene buttons. These are interactive sprite scenes, not 210 prerecorded videos. Voice narration and automatic story replay are not ported.
- Shape matching, a touch-following fish pond, connect dots and a letter practice notebook are native adaptations. Existing 20 mini-game types / 1,200 levels remain available under Play.
- First completion of story endings, scratch reveals, dot pages and shape matching shares the same atomic wallet ledger as mini-game wins. Free painting does not earn coins for repeatedly pressing Save.
- Dark/light/system themes, global sound and reduced-motion settings are retained. Family reset includes creative drafts and artwork. No network permission, external app links, browser views or runtime libraries were added.

## Remaining parity work
This is a review build of the core creative-world translation, not a claim of complete web parity. The original web onboarding/avatar flow, complete playroom mechanics, contextual paint cause-and-effect effects, automatic story replay, narration, exact book templates and every web sorting/puzzle variant still need individual native ports. Clay mechanics and artwork placement are adaptations. The new freehand drawing surfaces do not provide full nonvisual drawing alternatives.

## Validation
Existing 1,200-level solvability and 10,814 rule/state checks pass; 400 legacy save fixtures pass; audio and navigation checks pass; 2,437 schema/palette/source checks pass. A new asset regression checks all 1,053 picture entries and all 210 story episodes (2,736 asset/step checks), including every referenced prop and branch ending. APK compilation and v2/v3 signature verification pass.

Android 7 / API 24 software emulator: native launch, village navigation, picture shelves, manual brush strokes, saving a creation and viewing it in the gallery, native GLES clay rendering and a story placement interaction were exercised. Captures are actual emulator output. Full cross-version, physical-device, frame-rate, TalkBack and every-episode interaction testing remain pending.

## Install and open
The source ZIP opens directly at its root: File > Open the folder containing settings.gradle and app. Let Gradle sync. Select the Little Makers Android App configuration (or create Android App, module app), select a device, then Run. Do not run gradlew.bat as the app configuration.

This APK uses a development key created for this build session. Existing 2.1.1/2.2.0 installs may use a different key and cannot be overwritten. Rebuild with your original debug/upload key to preserve upgrade compatibility; do not uninstall an app with important local progress.

Publisher identity, support email, public privacy URL and upload signing remain unset. Release builds are intentionally blocked until configured. This is not a signed Play upload or a guarantee of Play approval.
