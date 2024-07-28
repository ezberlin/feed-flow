import java.net.URI

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven {
            url = URI.create("https://jitpack.io")
            content {
                includeModule("com.github.Dansoftowner", "jSystemThemeDetector")
                // TODO: Delete when/if https://github.com/adrielcafe/lyricist/pull/45
                //  and https://github.com/adrielcafe/lyricist/pull/46 gets merged
                includeModule("com.github.prof18", "lyricist")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "FeedFlow"
include(":androidApp")
include(":shared")
include(":sharedUI")
include(":desktopApp")
include(":i18n")
include(":core")
include("database")
include(":feedSync:database")
include("feedSync:dropbox")
include("feedSync:icloud")

// includeBuild("../../Android/RSS-Parser") {
//    dependencySubstitution {
//        substitute(module("com.prof18.rssparser:rssparser")).using(project(":rssparser"))
//    }
// }
