plugins {
    alias(libs.plugins.gwentest.android.application)
    alias(libs.plugins.gwentest.android.application.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gwentest.android.hilt)
    alias(libs.plugins.kotlin.serialization)
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

    implementation(projects.domain.home.api)
    implementation(projects.domain.home.impl)
    implementation(projects.data.card.api)
    implementation(projects.data.card.impl)
    implementation(projects.data.local.api)
    implementation(projects.data.local.impl)
    implementation(projects.data.remote.api)
    implementation(projects.data.remote.impl)
    implementation(projects.network)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)
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