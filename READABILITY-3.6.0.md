# Little Makers 3.6.0-native (33)

## Readability and contrast

The supplied Sky Words screenshot showed stretched answer typography, text mixed into scenery, and an overly prominent detailed landscape. School answer labels now render in screen pixels rather than the anisotropically scaled 640×520 scene. Each answer has its own opaque theme-aware panel, border, measured wrapping and uniform font sizing. Labels stay in a dedicated lower band while crystals move independently. The landing instruction has a separate panel too. The same implementation applies to all 264 school entries.

School prompts use 18sp instead of 20sp, metadata uses 12sp, and explanations use 14sp. System font scaling remains enabled. Mission content scrolls when space is insufficient; a minimum 300dp scene prevents large text from collapsing the play area. Bottom actions remain outside the scroll, retain 64dp minimum touch targets, and may grow vertically as their labels wrap. Full answer text is also exposed by the existing accessibility nodes. The label fitter can reduce visual text to 10dp for very long answers; it is not a complete large-text accessibility certification.

The shared forest shader reduces saturation and mixes in a neutral theme-aware veil. This affects every scene using ForestBackdrop, including learning worlds, land stories and the village. Foreground meshes retain their colors and directional lighting. Plastic-like specular highlights are reduced in the shared stage renderer.

## Animal rendering

Original procedural mesh revisions cover the pond fish, aquarium fish and shared story/game fish: streamlined body, counter-shaded belly, forked animated tail, dorsal/pectoral fins, gill marking and small lateral eyes. Aquarium fish face their movement direction. Rabbit/fox models now use separate heads, muzzles, limbs and tails; the owl uses folded wings and facial discs; reptiles use elongated bodies, necks and four limbs. Story rabbit and bear models have also been revised. Existing IDs, rewards, offline operation and saved data remain compatible.

These are anatomy-oriented procedural 3D improvements. They are **not photorealistic, scanned, rigged high-resolution characters**, and the app still contains existing stylized graphics, illustrations and interfaces. No claim that every screen is realistic or “4D” is made. A complete photorealistic asset and animation replacement remains outstanding.

## Validation

- Native APK compiled and its development signature verified.
- Separate device instrumentation APK compiled.
- Full rules/regression suite passed, including all 264 school entries, 1200 legacy module solvers, saved-state fixtures and 175 viewport-proportion combinations.
- `git diff --check` passed.
- **No fresh emulator/device visual run for 3.6.0.** Prior 3.5 captures do not validate the updated labels or revised meshes. Small-screen, large-font, landscape and GPU visual validation remain pending. Physical FPS/thermal performance is unmeasured.

The APK retains the recent development signing identity. Minimum Android API24, target API36. This is a debug build; Play release signing is separate.
