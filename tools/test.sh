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
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/SceneFrame.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/StoryModels.java" "$project_root/tests/SceneFrameTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.SceneFrameTest
python3 "$project_root/tests/check_3d_coverage.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/AdventurePath.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/AdventureState.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/HabitatState.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/VillageSceneState.java" "$project_root/tests/AdventureStateTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.AdventureStateTest
python3 "$project_root/tests/check_adventure.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/LearningQuest.java" "$project_root/tests/LearningQuestTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.LearningQuestTest
python3 "$project_root/tests/check_quests.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/StoryModels.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/ExplorerCatalog.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/ExplorerState.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/TangramState.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/MakerState.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/TraceTrack.java" "$project_root/tests/ExplorerTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.ExplorerTest
python3 "$project_root/tests/check_studios.py"
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/VillageRoutes.java" "$project_root/tests/VillageRoutesTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.VillageRoutesTest
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/SchoolContent.java" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/CompanionCast.java" "$project_root/tests/SchoolContentTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.SchoolContentTest
"$java_root/bin/javac" -d "$project_root/build/tests" "$project_root/app/src/main/java/com/littlemakers/village/nativeapp/StageProjection.java" "$project_root/tests/StageProjectionTest.java"
"$java_root/bin/java" -cp "$project_root/build/tests" com.littlemakers.village.nativeapp.StageProjectionTest
