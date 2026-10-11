// Build do núcleo compartilhado (Kotlin Multiplatform). O app Android continua com o build próprio em android/.
rootProject.name = "cinema-history-shared"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

include(":shared")
