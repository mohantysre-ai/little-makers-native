#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "$0")/.." && pwd)"
sdk_root="${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT}"
java_root="${JAVA_HOME:?Set JAVA_HOME}"
build_tools="$sdk_root/build-tools/36.0.0"
test_build="$project_root/build/render-check"
mkdir -p "$test_build/classes" "$test_build/dex"
"$build_tools/aapt2" link -I "$sdk_root/platforms/android-36/android.jar" --manifest "$project_root/tests/device/AndroidManifest.xml" -o "$test_build/base.apk"
"$java_root/bin/javac" --release 8 -classpath "$sdk_root/platforms/android-36/android.jar" -d "$test_build/classes" "$project_root/tests/device/"*.java
"$java_root/bin/jar" cf "$test_build/classes.jar" -C "$test_build/classes" .
PATH="$java_root/bin:$PATH" "$build_tools/d8" --lib "$sdk_root/platforms/android-36/android.jar" --min-api 24 --output "$test_build/dex" "$test_build/classes.jar"
python3 - "$test_build" <<'PY'
import zipfile,sys
from pathlib import Path
p=Path(sys.argv[1])
with zipfile.ZipFile(p/'base.apk','a',zipfile.ZIP_DEFLATED) as z:z.write(p/'dex/classes.dex','classes.dex')
PY
"$build_tools/zipalign" -f 4 "$test_build/base.apk" "$test_build/aligned.apk"
PATH="$java_root/bin:$PATH" "$build_tools/apksigner" sign --ks "$project_root/build/debug.keystore" --ks-pass pass:android --out "$test_build/render-check.apk" "$test_build/aligned.apk"
echo 'Install app debug APK and render-check.apk on an isolated emulator, then run adb shell am instrument -w com.littlemakers.village.rendercheck/.RenderCheck'
