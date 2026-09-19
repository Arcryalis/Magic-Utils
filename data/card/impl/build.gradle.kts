plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.data.card.impl"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.coil.compose)

    implementation(projects.data.card.api)
    implementation(projects.data.local.api)
    implementation(projects.data.remote.api)
    implementation(projects.network)
}