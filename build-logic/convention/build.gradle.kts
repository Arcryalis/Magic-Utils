plugins {
    `kotlin-dsl`
}

group = "com.arcryalis.gwentest.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("androidApplicationCompose") {
            id = "gwentest.application.compose"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidApplicationComposeConventionPlugin"
        }
        create("androidApplication") {
            id = "gwentest.application"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidApplicationConventionPlugin"
        }

        create("androidHilt") {
            id = "gwentest.kotlin.hilt"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidHiltConventionPlugin"
        }
    }
}