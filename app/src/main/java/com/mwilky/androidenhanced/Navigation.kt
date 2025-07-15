package com.mwilky.androidenhanced


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.dataclasses.Screens
import com.mwilky.androidenhanced.ui.Buttons
import com.mwilky.androidenhanced.ui.HomeScreen
import com.mwilky.androidenhanced.ui.Lockscreen
import com.mwilky.androidenhanced.ui.LockscreenIcons
import com.mwilky.androidenhanced.ui.Logs
import com.mwilky.androidenhanced.ui.Misc
import com.mwilky.androidenhanced.ui.Notifications
import com.mwilky.androidenhanced.ui.OnboardingScreen
import com.mwilky.androidenhanced.ui.Quicksettings
import com.mwilky.androidenhanced.ui.QuicksettingsIcons
import com.mwilky.androidenhanced.ui.Settings
import com.mwilky.androidenhanced.ui.SplashScreen
import com.mwilky.androidenhanced.ui.Statusbar
import com.mwilky.androidenhanced.ui.StatusbarCollapsedIconManagement
import com.mwilky.androidenhanced.ui.StatusbarIconManagement
import com.mwilky.androidenhanced.ui.StatusbarIcons


@Composable
fun Navigation(context: Context, billingManager: BillingManager) {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(route = Screens.Home.route) {
            HomeScreen(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Logs.route) {
            Logs(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Settings.route) {
            Settings(
                navController = navController,
                deviceProtectedStorageContext = context,
                billingManager = billingManager
            )
        }
        composable(route = Screens.Statusbar.route) {
            Statusbar(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Misc.route) {
            Misc(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Buttons.route) {
            Buttons(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Notifications.route) {
            Notifications(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Lockscreen.route) {
            Lockscreen(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Quicksettings.route) {
            Quicksettings(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.QuicksettingsIcons.route) {
            QuicksettingsIcons(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.StatusbarIcons.route) {
            StatusbarIcons(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.LockscreenIcons.route) {
            LockscreenIcons(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Splash.route) {
            SplashScreen(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.Onboarding.route) {
            OnboardingScreen(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.StatusbarIconManagement.route) {
            StatusbarIconManagement(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
        composable(route = Screens.StatusbarCollapsedIconManagement.route) {
            StatusbarCollapsedIconManagement(
                navController = navController,
                deviceProtectedStorageContext = context
            )
        }
    }
}

