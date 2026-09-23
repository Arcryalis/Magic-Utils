plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.game"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.material.icons.core)
    implementation(libs.coil.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(projects.ui.core)
    implementation(projects.data.card.api)
    implementation(projects.domain.card.api)
}