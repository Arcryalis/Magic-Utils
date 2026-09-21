// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}

// Runs all unit tests across every module.
// Usage: ./gradlew allUnitTests
tasks.register("allUnitTests") {
    group = "verification"
    description = "Runs all unit tests in every module."
    dependsOn(
        subprojects.mapNotNull { subproject ->
            subproject.tasks.findByName("testDebugUnitTest")
        }
    )
}