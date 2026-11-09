package com.arcryalis.gwentest.convention

import com.arcryalis.gwentest.support.libs
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.diffplug.spotless")
            }

            val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
            val composeRules = libs.findLibrary("compose-rules-ktlint").get().get()
            val composeRulesCoordinates = "${composeRules.module}:${composeRules.versionConstraint.requiredVersion}"
            val editorConfig = rootProject.file(".editorconfig")
            val isRoot = this == rootProject

            extensions.configure<SpotlessExtension> {
                kotlin {
                    if (isRoot) {
                        target("build-logic/**/*.kt")
                    } else {
                        target("src/**/*.kt")
                    }
                    targetExclude("**/build/**")
                    ktlint(ktlintVersion)
                        .setEditorConfigPath(editorConfig)
                        .customRuleSets(listOf(composeRulesCoordinates))
                }

                kotlinGradle {
                    if (isRoot) {
                        target("*.gradle.kts", "build-logic/**/*.gradle.kts")
                    } else {
                        target("*.gradle.kts")
                    }
                    targetExclude("**/build/**")
                    ktlint(ktlintVersion)
                        .setEditorConfigPath(editorConfig)
                }
            }
        }
    }
}
