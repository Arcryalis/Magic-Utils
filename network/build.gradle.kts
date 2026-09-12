plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.network"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
}