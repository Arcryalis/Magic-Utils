plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.data.remote"

    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
}
