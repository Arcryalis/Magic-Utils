package com.arcryalis.gwentest.support

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")


internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = AppConfig.COMPILE_SDK

        defaultConfig.minSdk = AppConfig.MIN_SDK
        compileOptions.sourceCompatibility = AppConfig.sourceCompatibility
        compileOptions.targetCompatibility = AppConfig.targetCompatibility

        configureKotlin()
    }

    configureUnitTestJvmArgs(commonExtension)
}

private fun Project.configureUnitTestJvmArgs(commonExtension: CommonExtension) {
    // Robolectric requires these to access internal OpenJDK classes when running on Java 17+.
    commonExtension.testOptions.unitTests.all {
        it.jvmArgs(
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "--add-opens=java.base/java.util=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-opens=java.base/java.net=ALL-UNNAMED",
                "--add-opens=java.base/java.security=ALL-UNNAMED",
                "--add-opens=java.base/java.text=ALL-UNNAMED",
                "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
                "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        )
    }
}

private fun Project.configureKotlin() {
    // https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(AppConfig.jvmTarget)
        }
    }

    dependencies {
        add("testImplementation", kotlin("test"))
        add("testImplementation", libs.findLibrary("junit").get())
    }
}