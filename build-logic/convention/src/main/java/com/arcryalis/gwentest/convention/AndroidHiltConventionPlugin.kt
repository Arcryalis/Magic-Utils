package com.arcryalis.gwentest.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.arcryalis.gwentest.support.AppConfig
import com.arcryalis.gwentest.support.configureKotlinAndroid
import com.arcryalis.gwentest.support.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            pluginManager.withPlugin("com.android.application") {
                val extension = extensions.getByType<ApplicationExtension>()
                configureKotlinAndroid(extension)
                extension.defaultConfig.targetSdk = AppConfig.TARGET_SDK
            }

            pluginManager.withPlugin("com.android.library") {
                val extension = extensions.getByType<LibraryExtension>()
                configureKotlinAndroid(extension)
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}