package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.ISDEVICESUPPORTEDKEY
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun SplashScreen(navController: NavController, context: Context) {
    //Get SharedPreferences
    val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            PREFS, MODE_PRIVATE
        )

    //Save isDeviceSupported to SharedPreferences
    LaunchedEffect(Unit) {
        sharedPreferences.edit().putBoolean(ISDEVICESUPPORTEDKEY, isCurrentDeviceSupported())
            .apply()
    }

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


private suspend fun isCurrentDeviceSupported(): Boolean {
    val currentDeviceName = Build.MODEL

    // URL of the online file containing the supported device names
    val onlineFileURL =
        "https://raw.githubusercontent.com/mwilky/renovate-helpers/master/supported_devices"

    return withContext(Dispatchers.IO) {
        try {
            // Fetch the content of the online file
            val url = URL(onlineFileURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000 // Set connection timeout to 5 seconds
            connection.requestMethod = "GET"

            // Read the online file content
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val content = reader.readText()

            // Extract the device names and check if the current device is supported
            val supportedDevices = content.split(",").map { it.trim() }
            if (DEBUG) Log.d(TAG, "Supported device from online repo are: $supportedDevices")
            val isSupported = currentDeviceName in supportedDevices

            // Close resources
            reader.close()
            connection.disconnect()

            // Log the result
            if (DEBUG) {
                val resultMessage = if (isSupported) {
                    "Your device (${currentDeviceName}) is supported."
                } else {
                    "Your device (${currentDeviceName}) is not supported."
                }
                Log.d(TAG, resultMessage)
            }

            isSupported
        } catch (e: Exception) {
            // Handle any errors (e.g., network issues, parsing errors, etc.)
            e.printStackTrace()
            Log.e(TAG, "Error occurred while checking device support. " +
                    "Do you have network connectivity?")
            false
        }
    }
}