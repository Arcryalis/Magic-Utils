plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.data.card.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.coil.compose)

    implementation(projects.data.card.api)
    implementation(projects.data.local.api)
    implementation(projects.data.remote.api)
    implementation(projects.network.api)

    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(testFixtures(projects.data.card.api))
    testImplementation(testFixtures(projects.data.local.api))
    testImplementation(testFixtures(projects.data.remote.api))
    testImplementation(testFixtures(projects.network.api))
}