plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.data.card"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}

