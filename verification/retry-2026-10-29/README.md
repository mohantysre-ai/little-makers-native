# Little Makers retry — 29 October 2026

The published 3.6.1 source was freshly cloned and the complete `tools/test.sh` rule/content/regression suite rerun with JDK 21. It exited **0 / PASS**. See [raw output](rules.txt) and [scope](results.json).

This retry covers 1,200 module solvers, 10,814 rule/state checks, saved-state compatibility, audio PCM checks, content/platform/offline guards, quests, studios, all 264 school entries, and 175 viewport geometry cases.

**No new emulator or physical-device test was run in this retry.** The earlier emulator environment was not retained. The [previous device report](../../QA-3.6.1.md) remains historical evidence; it is not a fresh test result.

**Everything is not realistic.** The project still uses procedural meshes and 2D imagery. No bundled GLB/GLTF/FBX/OBJ model files or MP4/WebM videos were found. Native 3D rendering exists in Java, but a complete photorealistic character/environment and cinematic-story pipeline has not been implemented.

No application code changed and no new APK was generated. Physical-device performance, audible output, tactile feedback and the broader Android/OEM matrix remain outstanding.
