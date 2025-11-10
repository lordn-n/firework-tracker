pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo.chrynan.codes/releases/")
    }
}

rootProject.name = "FireworksTracker"
include(":composeApp")
