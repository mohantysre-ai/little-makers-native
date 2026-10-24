#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "$0")/.." && pwd)"
java_root="${JAVA_HOME:?Set JAVA_HOME to a JDK}"
mkdir -p "$project_root/build/tests"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/GameCatalog.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/GameRules.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/ContentPack.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/AudioSynth.java" "$project_root/tests/RulesTest.java" "$project_root/tests/AudioTest.java" "$project_root/tests/NavigationTest.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/GesturePolicy.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/NavigationPolicy.java" "$project_root/tests/CompatibilityTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.RulesTest
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.AudioTest
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.CompatibilityTest "$project_root/tests/legacy-v200.tsv"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.NavigationTest
python3 "$project_root/tests/check_platform.py"
python3 "$project_root/tests/check_release.py"

"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/VillageSceneState.java" "$project_root/tests/VillageMotionTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.VillageMotionTest
python3 "$project_root/tests/check_web_port.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/BackdropCrop.java" "$project_root/tests/BackdropCropTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.BackdropCropTest
python3 "$project_root/tests/check_branding.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/ShapePlayState.java" "$project_root/tests/ShapePlayTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.ShapePlayTest
