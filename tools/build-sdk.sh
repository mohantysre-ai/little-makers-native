#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "$0")/.." && pwd)"
sdk_root="${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT}"
java_root="${JAVA_HOME:?Set JAVA_HOME to a JDK}"
build_root="$project_root/build"
build_tools="$sdk_root/build-tools/36.0.0"
android_jar="$sdk_root/platforms/android-36/android.jar"
mkdir -p "$build_root/generated" "$build_root/classes" "$build_root/dex"
"$build_tools/aapt2" compile --dir "$project_root/app/src/main/res" -o "$build_root/resources.zip"
"$build_tools/aapt2" link --debug-mode -A "$project_root/app/src/main/assets" -I "$android_jar" --manifest "$project_root/app/src/main/AndroidManifest.xml" --rename-manifest-package com.littlemakers.village --min-sdk-version 24 --target-sdk-version 36 --version-code 23 --version-name 2.6.0-native --java "$build_root/generated" -o "$build_root/base.apk" "$build_root/resources.zip"
find "$project_root/app/src/main/java" "$build_root/generated" -name '*.java' > "$build_root/sources.txt"
"$java_root/bin/javac" --release 8 -classpath "$android_jar" -d "$build_root/classes" @"$build_root/sources.txt"
"$java_root/bin/jar" cf "$build_root/classes.jar" -C "$build_root/classes" .
PATH="$java_root/bin:$PATH" "$build_tools/d8" --lib "$android_jar" --min-api 24 --output "$build_root/dex" "$build_root/classes.jar"
python3 - "$build_root" <<'PY'
import sys,zipfile,shutil
from pathlib import Path
p=Path(sys.argv[1]);shutil.copyfile(p/'base.apk',p/'unsigned.apk')
with zipfile.ZipFile(p/'unsigned.apk','a',zipfile.ZIP_DEFLATED) as z:
 for dex in (p/'dex').glob('*.dex'):z.write(dex,dex.name)
PY
"$build_tools/zipalign" -f -p 4 "$build_root/unsigned.apk" "$build_root/aligned.apk"
if [ ! -f "$build_root/debug.keystore" ]; then "$java_root/bin/keytool" -genkeypair -keystore "$build_root/debug.keystore" -storepass android -keypass android -alias androiddebugkey -dname 'CN=Little Makers Development' -keyalg RSA -validity 3650 -noprompt; fi
PATH="$java_root/bin:$PATH" "$build_tools/apksigner" sign --ks "$build_root/debug.keystore" --ks-pass pass:android --key-pass pass:android --out "$build_root/little-makers-native-debug.apk" "$build_root/aligned.apk"
PATH="$java_root/bin:$PATH" "$build_tools/apksigner" verify --verbose "$build_root/little-makers-native-debug.apk"
echo 'Native debug APK built. Production signing and device testing remain separate.'
