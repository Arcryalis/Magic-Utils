plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.home"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.coil.compose)
    implementation(libs.material.icons.core)

    implementation(projects.ui.core)
    implementation(projects.ui.game)
    implementation(projects.domain.card.api)
    implementation(projects.data.card.api)

    testImplementation(testFixtures(projects.ui.core))
    testImplementation(testFixtures(projects.domain.card.api))
    testImplementation(testFixtures(projects.data.card.api))
    testImplementation(libs.kotlinx.coroutines.test)
}
