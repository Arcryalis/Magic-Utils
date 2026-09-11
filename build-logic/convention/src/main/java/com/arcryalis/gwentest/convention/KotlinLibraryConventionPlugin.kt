package com.arcryalis.gwentest.convention

import com.arcryalis.gwentest.support.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            configureKotlinJvm()

            dependencies {
                add("testImplementation", kotlin("test"))
            }
        }
    }
}