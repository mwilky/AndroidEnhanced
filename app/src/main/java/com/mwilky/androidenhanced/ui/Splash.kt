package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY

@Composable
fun SplashScreen(navController: NavController, deviceProtectedStorageContext: Context) {
    //Get SharedPreferences
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            PREFS, MODE_PRIVATE
        )

    LogManager.init(deviceProtectedStorageContext)

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