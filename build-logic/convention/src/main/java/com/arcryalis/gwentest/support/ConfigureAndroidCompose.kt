package com.arcryalis.gwentest.support

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

private val composeBomConfigurations = setOf(
    "implementation",
    "testImplementation",
    "testFixturesImplementation",
    "testFixturesApi",
    "androidTestImplementation",
)

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    commonExtension.apply {
        buildFeatures.compose = true

        composeOptions.kotlinCompilerExtensionVersion = libs.findVersion("androidxComposeCompiler").get().toString()
    }

    // The BOM is a platform dependency, so version-less Compose artifacts only resolve on
    // classpaths that carry it. Every source set that can reference Compose needs it, including
    // testFixtures, whose classpath does not inherit main's `implementation` dependencies.
    // Configurations are matched lazily because testFixtures* ones are only created once the
    // module opts into test fixtures, which happens after this plugin is applied.
    val bom = libs.findLibrary("androidx-compose-bom").get()
    val dependencyHandler = dependencies
    configurations.configureEach {
        if (name in composeBomConfigurations) {
            dependencies.add(dependencyHandler.platform(bom.get()) {})
        }
    }
}
