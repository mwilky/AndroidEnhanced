package com.mwilky.androidenhanced.ui

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.mwilky.androidenhanced.DataStoreManager
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun SplashScreen(navController: NavController, context: Context) {
    // This will remember the result of isCurrentDeviceSupported()
    val isDeviceSupported = remember { mutableStateOf(false) }

    val dataStoreManager = DataStoreManager(context)

    // Collect the Flow using LaunchedEffect with a key "collectSupportedStatus"
    LaunchedEffect("collectSupportedStatus") {
        // Get the result of isCurrentDeviceSupported()
        isDeviceSupported.value = isCurrentDeviceSupported()

        // Save the value to DataStore
        dataStoreManager.saveIsDeviceSupported(isDeviceSupported.value)

        // Read the value of onboardingCompletedKey from DataStore
        val onboardingCompleted = dataStoreManager.dataStore.data.map { preferences ->
            preferences[DataStoreManager.onboardingCompletedKey] ?: false
        }.first()

        // Go to Home Screen if onboarding is complete
        if (onboardingCompleted) {
            navController.navigate(Screen.Home.route)
        } else {
            navController.navigate(Screen.Onboarding.route)
        }

        // Observe the isCurrentDeviceSupported() changes and update the state
        dataStoreManager.isDeviceSupportedFlow.collect { isSupported ->
            isDeviceSupported.value = isSupported
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