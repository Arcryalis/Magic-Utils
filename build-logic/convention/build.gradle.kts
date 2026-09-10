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
            id = "gwentest.android.hilt"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidHiltConventionPlugin"
        }

        create("androidLibrary") {
            id = "gwentest.library"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidLibraryConventionPlugin"
        }

        create("androidLibraryCompose") {
            id = "gwentest.library.compose"
            implementationClass = "com.arcryalis.gwentest.convention.AndroidLibraryComposeConventionPlugin"
        }

        create("kotlinLibrary") {
            id = "gwentest.kotlin.library"
            implementationClass = "com.arcryalis.gwentest.convention.KotlinLibraryConventionPlugin"
        }

        create("kotlinHilt") {
            id = "gwentest.kotlin.hilt"
            implementationClass = "com.arcryalis.gwentest.convention.KotlinHiltConventionPlugin"
        }
    }
}