package com.mwilky.androidenhanced

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mwilky.androidenhanced.ui.Screen
import com.mwilky.androidenhanced.ui.HomeScreen
import com.mwilky.androidenhanced.ui.OnboardingScreen
import com.mwilky.androidenhanced.ui.SplashScreen
import com.mwilky.androidenhanced.ui.Tweaks
import com.mwilky.androidenhanced.ui.TweaksSelectionDialog

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController, context = context)
        }
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController, context = context)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController, context = context)
        }
        composable(
            route = Screen.Tweaks.route + "/{screen}",
            arguments = listOf(
                navArgument("screen") {
                    type = NavType.StringType
                }
            )
        ) {entry ->
            entry.arguments?.getString("screen")?.let {
                Tweaks(
                    navController = navController,
                    context = context,
                    screen = it
                )
            }
        }
    }
}