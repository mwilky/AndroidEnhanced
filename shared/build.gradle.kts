plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mwilky.androidenhanced.shared"
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
    // Example AndroidX dependency if needed
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    compileOnly(libs.xposed.api)

    implementation(libs.gson)

}
