plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mwilky.androidenhanced.standard"
    compileSdk = 36

    defaultConfig {
        minSdk = 35
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
    implementation(project(":shared"))
    implementation(project(":premium"))
    compileOnly(libs.xposed.api)

    implementation(platform(libs.compose.bom))


    implementation(libs.androidx.core.ktx)
    implementation(libs.activity.ktx)
}
