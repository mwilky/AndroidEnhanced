package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.supportedDevices

@Composable
fun SplashScreen(navController: NavController, deviceProtectedStorageContext: Context) {
    //Get SharedPreferences
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            PREFS, MODE_PRIVATE
        )

    LogManager.init(deviceProtectedStorageContext)

    //Log supported devices
    logSupportedDevices()

    // Go to Home Screens if onboarding is complete
    if (sharedPreferences.getBoolean(ISONBOARDINGCOMPLETEDKEY, false)) {
        navController.navigate(Screens.Home.route) {
            popUpTo(Screens.Splash.route) {
                inclusive = true
            }
        }
    } else {
        navController.navigate(Screens.Onboarding.route) {
            popUpTo(Screens.Splash.route) {
                inclusive = true
            }
        }
    }
}

fun logSupportedDevices() {
    val isSupported = supportedDevices.any { Build.MODEL.contains(it, ignoreCase = true) }

    if (isSupported) {
        // Log that the device is supported
        LogManager.log("Splash", "Device ${Build.MODEL} is supported.")
    } else {
        // Log that the device is not supported and list supported devices
        LogManager.log("Splash", "Device ${Build.MODEL} is not supported. Supported devices are: ${supportedDevices.joinToString(", ")}")
    }
}