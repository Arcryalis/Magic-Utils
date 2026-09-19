plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.scheme"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.ui)
    implementation(libs.navigation3.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.material.icons.core)
    
    implementation(projects.ui.core)
    implementation(projects.domain.card.api)
    implementation(projects.data.card.api)

    testFixtures(projects.domain.card.api)
}