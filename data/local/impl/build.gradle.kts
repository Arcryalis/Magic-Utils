plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
    alias(libs.plugins.gwentest.room)
}

android {
    namespace = "com.arcryalis.gwentest.data.local.impl"
}

dependencies {
    implementation(libs.kotlinx.couroutines.core)

    implementation(projects.data.card.api)
    implementation(projects.data.local.api)
}
