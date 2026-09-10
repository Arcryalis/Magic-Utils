package com.arcryalis.gwentest.support

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")


internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *>) {
//    extensions.configure<BaseExtension> {
    commonExtension.apply {
        compileSdk = AppConfig.compileSdk
//        compileSdkVersion(AppConfig.compileSdk)
        defaultConfig {
            minSdk = AppConfig.minSdk
//            targetSdk = AppConfig.minSdk
        }

        compileOptions {
            sourceCompatibility = AppConfig.sourceCompatibility
            targetCompatibility = AppConfig.targetCompatibility

        }
    }

    configureKotlin()
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
    }
}