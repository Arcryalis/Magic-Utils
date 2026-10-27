plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.network.impl"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.network.response.adapter)
    implementation(libs.coil)

    implementation(projects.network.api)

    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
}