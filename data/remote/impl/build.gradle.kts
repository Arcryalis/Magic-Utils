plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

android {
    namespace = "com.arcryalis.gwentest.data.remote.impl"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.network.response.adapter)

    implementation(projects.data.remote.api)
}
