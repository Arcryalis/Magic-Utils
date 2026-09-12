plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.core"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(projects.domain.home.api)
}