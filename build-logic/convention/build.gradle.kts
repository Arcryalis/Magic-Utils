plugins {
    `kotlin-dsl`
}

group = "com.arcryalis.gwentest.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("androidApplicationCompose") {
            id = "gwentest.application.compose"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidApplicationComposeConventionPlugin"
        }

        create("androidHilt") {
            id = "gwentest.kotlin.hilt"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidHiltConventionPlugin"
        }
    }
}