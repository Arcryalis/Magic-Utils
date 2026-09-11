plugins {
    alias(libs.plugins.gwentest.kotlin.library)
    alias(libs.plugins.gwentest.kotlin.hilt)
}

//android {
//    compileSdk = 36
//kotlin {
//    compilerOptions {
//        jvmTarget.set(JvmTarget.JVM_17)
//    }
//}

dependencies {
    implementation(libs.kotlinx.couroutines.core)
}