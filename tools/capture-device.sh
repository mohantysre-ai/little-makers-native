#!/usr/bin/env bash
# Captures genuine installed native UI; never creates mock screenshots.
set -euo pipefail
project_root="$(cd "$(dirname "$0")/.." && pwd)"
adb_bin="${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT}/platform-tools/adb"
if [ -z "${ANDROID_SERIAL:-}" ]; then echo 'Set ANDROID_SERIAL to the intended adb device.' >&2; exit 1; fi
"$adb_bin" -s "$ANDROID_SERIAL" shell am start -n com.littlemakers.village/com.littlemakers.village.nativeapp.MainActivity
mkdir -p "$project_root/release/media/screenshots"
read -r -p 'Show the Home screen on the device, then press Enter. '
"$adb_bin" -s "$ANDROID_SERIAL" exec-out screencap -p > "$project_root/release/media/screenshots/01-home.png"
read -r -p 'Open a game and arrange its real gameplay view, then press Enter. '
"$adb_bin" -s "$ANDROID_SERIAL" exec-out screencap -p > "$project_root/release/media/screenshots/02-game.png"
read -r -p 'Set dark mode and show a game, then press Enter. '
"$adb_bin" -s "$ANDROID_SERIAL" exec-out screencap -p > "$project_root/release/media/screenshots/03-dark-game.png"
echo 'Review screenshots for correct installed version, privacy, native UI and Play dimensions before upload.'
