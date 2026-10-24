# Device acceptance checklist — pending execution

Use API 24, 28, 31, 34 and 36 phones/tablets, including a low-memory device and a mid-range GPU. The current APK targets 36 with minimum 24.

1. Fresh install and upgrade from native 2.0.0: open every category and all 1,200 modules; inspect portrait/landscape and small/narrow displays. Check visible bounds, text wrapping and 64dp targets. Navigate all six catalog pages in each of 20 sections and verify Play 11–60 opens the correct added module. Confirm prior native saves/wallet survive the upgrade.
2. In light, dark and system modes inspect dashboard, category, each of 20 game layouts, win state, pause, replay, shop, parent PIN dialog/error, settings, full offline privacy page and reset confirmation. Repeat at 1.0×, 1.3× and 2.0× font scaling. Confirm contrast and no text clipping; Canvas currently caps visual font scaling at 1.35×, so full large-font accommodation remains work to validate/improve.
3. Solve one of every mechanic by touch and TalkBack. Test wrong answers, canceled gestures, mismatch/reset, undo and keyboard navigation. Record any inaccessible or ambiguous control.
4. Solve a module: balance increases once. Replay/undo/re-solve/restart: no duplicate reward. Buy/equip cosmetic: exact debit, dashboard appearance updates, insufficient balance remains unchanged.
5. Background/foreground, rotate, force-stop/reopen and simulate process death mid-game. Progress persists; robot resumes stopped after process recreation; no duplicate ticks/rewards. Parent space requires PIN after backgrounding. On Android 13–16, gesture Back must invoke the registered native callback; legacy Back must work on Android 7–12. From Home, Back must exit instead of rebuilding the same Home screen.
6. Hear tap, rhythm note and win melody on speaker/headphones. Mute and pause stop all tracks. Enable reduced motion and Android animation scale 0; confirm all nonessential movement stops.
7. Capture frame timing with Android Studio profiler; verify the 60fps goal during interactions and particles. Record CPU/memory/audio allocation. Current ambient motion redraws at 25Hz; frame-rate performance is unmeasured.
8. Install offline and airplane mode: all catalog/game/shop operations work. Confirm manifest has no permissions and no external navigation.
9. Upgrade using production signing key; verify native DB survives. Prior web saves need a separately designed migration before promising continuity.
10. Confirm privacy is accessible without PIN and reset requires both the adult gate and the exact confirmation; settings/PIN survive reset. Capture actual app screenshots and gameplay video only after these checks pass. Document failures and fixes before release.
