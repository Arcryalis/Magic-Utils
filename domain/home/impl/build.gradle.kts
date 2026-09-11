plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

//android {
//    compileSdk = 36
//}

dependencies {
    implementation(projects.domain.home.api)

    implementation(libs.kotlinx.couroutines.core)
    implementation(libs.retrofit.core)
//    implementation(libs.plugins.gwentest.room)
}