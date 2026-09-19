plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.domain.card.impl"
}

dependencies {
    implementation(projects.domain.card.api)
    implementation(projects.data.card.api)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(testFixtures(projects.domain.card.api))
}