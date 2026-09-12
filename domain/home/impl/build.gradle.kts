plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.home.impl"
}

dependencies {
    implementation(projects.domain.home.api)
    implementation(projects.data.card.api)

    implementation(libs.kotlinx.couroutines.core)
}