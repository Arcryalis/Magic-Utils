plugins {
    alias(libs.plugins.gwentest.kotlin.library)
}

android {
    namespace = "com.arcryalis.gwentest.domain.ocr"

    testFixtures {
        enable = true
    }
}
