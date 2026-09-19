plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.domain.card"

    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    api(projects.data.card.api)

    testFixturesImplementation(libs.kotlinx.coroutines.core)
}