plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.gwentest.room)
}

android {
    namespace = "com.arcryalis.gwentest.home.impl"
}

dependencies {
    implementation(projects.domain.home.api)

    implementation(libs.kotlinx.couroutines.core)
    implementation(libs.retrofit.core)
}