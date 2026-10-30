# Little Makers 3.9.0: interactive Story Theater

Story Tree now opens the existing 210 catalog entries in a native real-time 3D theater. This changes the default story-tile destination; it is not just an additional hidden menu option. The theater is also available directly from Playroom. Discovery cinema remains a separate MP4 player.

## What changed

- A bounded perspective camera, slow orbit and improved scene framing replace the fixed board view in theater mode. Existing puzzle and hand-drawing coordinate systems are preserved.
- Children touch the highlighted object in the actual projected scene. The accessible Help control performs the same action. They move to the next scene and choose an ending themselves; elapsed camera time cannot complete a learning action.
- Captions, optional installed offline narration, replay, Pause motion and parent reduced motion are integrated. Playback/voice stops in the background. The compact options panel follows the app's light/dark palette, including white dark-mode labels.
- Scene and ending progress survive recreation. A parent reset invalidates open theater state. Rewards use the existing `story-N` IDs, so alternate endings and the original hand-play mode cannot duplicate first-clear coins.
- New original rabbit, fox and bear surface meshes have anatomical proportions, coat shading, small glossy eyes and breathing motion. CPU meshes are cached; GPU buffers upload only for the species actually shown. Original source and limits are documented in [wildlife source notes](art-source/wildlife/README.md).
- Story scenes occupy more of the screen; secondary actions are in Story options. Animated picture-menu rows expand for larger system fonts.

Open **Story Tree → any story**. Use **⋯ → Make scenes by hand** for the original drag-and-place mode. Back returns to the story shelf.

## Validation

All **14 device suites passed** against the final APK. The new theater passed 889 checks; the renderer passed 36 screens with GL_NO_ERROR and built all 630 older story-step frames. Host checks passed, including 846,477 new state/camera/geometry assertions (mostly per-vertex checks).

The final outcomes and exact artifact identity are recorded in [results.json](verification/qa39/results.json). Host checks cover state transitions, malformed restores, camera fit at multiple aspect ratios, animal geometry, existing learning rules and bundled MP4 integrity. Large assertion counts include per-vertex numerical checks; they are not game or story counts.

Device instrumentation exercises actual GLES rendering and projected touch input, scene progression, recreation, first-clear/replay rewards, both themes, the options panel, reduced motion, motion pause/resume, all 210 story-frame constructions, default-tile navigation and background parent-reset handling. The regression sweep additionally checks the existing native rendering, visual menus, learning trail, Playroom, parent controls, creative tools, text layouts and MP4 cinema. Consult the results file for the actual pass/fail status rather than assuming coverage implies success.

[Actual theater screenshots](verification/qa39/story-theater-contact.jpg) are emulator captures, not promotional mockups. Early harness failures are retained: a null content description and ActivityMonitor returns were replaced by null-safe lookup and checks against the actually resumed activity. No app navigation or GL assertion was removed to obtain a pass. Captures wait for the outgoing window to settle before taking visual evidence.

## Remaining production work

**Whole-app photorealism is not complete.** The 210 catalog records now have interactive rendered scenes; they are not 210 newly produced cinematic films. Discovery cinema still contains one Moon film and four clips. Existing stylized architecture, vegetation, several companions, 2D art pages and flat backdrops remain. The original content count was not inflated by calling routes or test assertions new films.

The connected video account reported zero credits on a free plan. No additional video job was submitted and no new animal film is bundled. Production film footage, voice performances, sound design and higher-fidelity world/character assets remain outstanding. See the [explicit production-gap inventory](verification/qa39/production-gaps.json).

Testing uses a muted Android 7/API 24 software emulator. Android 16/API 36, physical-device GPU performance, audible narration/haptics and Play Store approval are not certified by these checks. The app targets SDK 36 with minimum SDK 24. This artifact is a signed debug APK; production signing and publisher configuration remain separate.

Final APK: 26,725,298 bytes, SHA256 `0166a9575590c0301c548c143970cd7ba6f679864e55e5def77801e7544008cb`, version code 37. APK v2/v3 signatures passed and the previous development signing identity is retained.
