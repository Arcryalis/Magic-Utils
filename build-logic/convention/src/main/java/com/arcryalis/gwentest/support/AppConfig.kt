package com.arcryalis.gwentest.support

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object AppConfig {
    const val compileSdk = 35
    const val minSdk = 24
    const val targetSdk = 35

    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17
    val jvmTarget = JvmTarget.JVM_17
}