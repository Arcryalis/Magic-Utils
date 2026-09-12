# Fix Project Build Failures

The project currently fails to build due to invalid SDK configurations and non-standard Gradle DSL usage. This plan aims to stabilize the build system by correcting these configurations.

## User Review Required

> [!IMPORTANT]
> I am downgrading the `compileSdk` and `targetSdk` from 37 to 35. API 37 is not currently a stable or common preview release for Android, and it's causing the Android Gradle Plugin to fail when initializing internal services.

## Proposed Changes

### Build Logic & Configuration

#### [MODIFY] [AppConfig.kt](file:///D:/Documents/Android projects/AndroidGwenTest/build-logic/convention/src/main/java/com/arcryalis/gwentest/support/AppConfig.kt)
- Update `COMPILE_SDK` and `TARGET_SDK` from 37 to 35.

#### [MODIFY] [app/build.gradle.kts](file:///D:/Documents/Android projects/AndroidGwenTest/app/build.gradle.kts)
- Remove the `compileSdk { version = release(37) }` block. The `gwentest.application` convention plugin already configures `compileSdk` using the value from `AppConfig`.

### Directory Structure Cleanup

#### [MODIFY] [data/remote/impl](file:///D:/Documents/Android projects/AndroidGwenTest/data/remote/impl/src/main/java/com/arcryalis/gwentest/remote.impl)
- I will rename the directory `remote.impl` to `remote/impl` to follow standard Java/Kotlin package-to-directory mapping conventions and avoid potential issues with annotation processors (like Hilt/KSP).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the project builds successfully.
- Run `./gradlew :data:remote:impl:assembleDebug` to verify the remote data implementation module builds.

### Manual Verification
- None required beyond successful build completion.
