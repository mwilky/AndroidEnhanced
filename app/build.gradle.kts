plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
}

val premiumDir = file("../../android-enhanced-premium/premium")
val hasPremiumModule = premiumDir.exists()


android {
    namespace = "com.mwilky.androidenhanced"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mwilky.androidenhanced"
        minSdk = 35
        targetSdk = 36
        versionCode = 2201
        versionName = "2.2.1"
        buildConfigField("Boolean", "HAS_PREMIUM_MODULE", hasPremiumModule.toString())

    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":standard"))
    implementation(project(":shared"))

    if (hasPremiumModule) {
        implementation(project(":premium"))
    }

    compileOnly(libs.xposed.api)

    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(platform(libs.compose.bom))
    implementation(libs.colorpicker.compose)
    implementation(libs.compose.material.icons.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.material3)
    implementation(libs.material)
    implementation(libs.splashscreen)
    implementation(libs.gson)
    implementation(libs.billing.ktx)
    debugImplementation(libs.androidx.ui.tooling)
}
