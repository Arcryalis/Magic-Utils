plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.domain.home"
}

dependencies {
    implementation(libs.kotlinx.couroutines.core)

    implementation(projects.data.card.api)
}