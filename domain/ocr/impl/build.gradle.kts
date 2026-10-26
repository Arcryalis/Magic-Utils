plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.domain.ocr.impl"
}

dependencies {
    implementation(projects.domain.ocr.api)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.mlkit.text.recognition)

    testImplementation(libs.kotlinx.coroutines.test)
}
