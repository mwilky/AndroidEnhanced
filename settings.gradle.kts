pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info/") }
    }
}

rootProject.name = "AndroidEnhanced"
include(":app", ":shared", ":standard")
val premiumDir = file("../android-enhanced-premium/premium")
if (premiumDir.exists()) {
    include(":premium")
    project(":premium").projectDir = premiumDir
}