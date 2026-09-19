plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.network"
}

dependencies {
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
}