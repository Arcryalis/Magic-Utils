plugins {
    alias(libs.plugins.gwentest.kotlin.library)
}

android {
    namespace = "com.arcryalis.gwentest.network"

    testFixtures {
        enable = true
    }
}