plugins {
    alias(libs.plugins.gwentest.android.library)
    alias(libs.plugins.gwentest.android.library.compose)
    alias(libs.plugins.gwentest.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arcryalis.gwentest.core"

    testFixtures {
        enable = true
    }
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material.icons.core)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.data.card.api)

    testFixturesImplementation(libs.kotlinx.coroutines.test)
    testFixturesImplementation(libs.junit)
}
