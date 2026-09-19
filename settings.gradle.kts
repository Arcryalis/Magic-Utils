pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "GwenTest"
include(":app")

include(":ui:core")
include(":ui:home")
include(":ui:scheme")

include(":domain:card:api")
include(":domain:card:impl")

include(":data:card:api")
include(":data:card:impl")
include(":data:local:api")
include(":data:local:impl")
include(":data:remote:api")
include(":data:remote:impl")

include(":network:impl")
include(":network:api")
