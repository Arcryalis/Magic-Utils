plugins {
    alias(libs.plugins.gwentest.android.application)
    alias(libs.plugins.gwentest.android.application.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gwentest.android.hilt)
}

android {
    namespace = "com.arcryalis.gwentest"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.arcryalis.gwentest"
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }
}

dependencies {
    implementation(projects.ui.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}