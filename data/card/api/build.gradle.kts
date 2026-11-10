plugins {
    alias(libs.plugins.gwentest.kotlin.library)
}

android {
    namespace = "com.arcryalis.gwentest.data.card"

    testFixtures {
        enable = true
    }
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}

