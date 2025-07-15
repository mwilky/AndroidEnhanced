package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.navigation.NavController
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.isDeviceSupported
import com.mwilky.androidenhanced.Utils.Companion.supportedDevices
import com.mwilky.androidenhanced.dataclasses.Screens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplashScreen(navController: NavController, deviceProtectedStorageContext: Context) {
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    // Read once, outside LaunchedEffect
    val onboardingComplete = sharedPreferences.getBoolean(ISONBOARDINGCOMPLETEDKEY, false)

    // Trigger navigation after 2 seconds
    LaunchedEffect(Unit) {
        logSupportedDevices()
        delay(2000)

        if (onboardingComplete) {
            navController.navigate(Screens.Home.route) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screens.Onboarding.route) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            }
        }
    }

    // Loading indicator
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(modifier = Modifier.scale(2.5f))
    }
}

fun logSupportedDevices() {
    val isSupported = isDeviceSupported()

    if (isSupported) {
        LogManager.log("Splash", "Device ${Build.MODEL} is supported.")
    } else {
        LogManager.log("Splash", "Device ${Build.MODEL} is not supported. Supported devices are: ${supportedDevices.joinToString(", ")}")
    }
}