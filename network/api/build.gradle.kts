plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.network"

    testFixtures {
        enable = true
    }
}

dependencies {
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
}