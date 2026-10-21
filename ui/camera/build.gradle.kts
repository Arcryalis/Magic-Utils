plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.camera"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.ui.core)

    testImplementation(testFixtures(projects.ui.core))
    testImplementation(libs.kotlinx.coroutines.test)
}
