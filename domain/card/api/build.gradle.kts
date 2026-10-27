plugins {
    alias(libs.plugins.gwentest.kotlin.library)
}

android {
    namespace = "com.arcryalis.gwentest.domain.card"

    testFixtures {
        enable = true
    }
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(projects.data.card.api)
}