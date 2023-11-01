package com.mwilky.androidenhanced


import android.content.Context

import androidx.compose.runtime.Composable

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mwilky.androidenhanced.ui.Screens
import com.mwilky.androidenhanced.ui.HomeScreen
import com.mwilky.androidenhanced.ui.Logs
import com.mwilky.androidenhanced.ui.OnboardingScreen
import com.mwilky.androidenhanced.ui.Settings
import com.mwilky.androidenhanced.ui.SplashScreen
import com.mwilky.androidenhanced.ui.Tweaks


@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(route = Screens.Splash.route) {
            SplashScreen(
                navController = navController,
                context = context
            )
        }
        composable(route = Screens.Onboarding.route) {
            OnboardingScreen(
                navController = navController,
                context = context
            )
        }
        composable(route = Screens.Home.route) {
            HomeScreen(
                navController = navController,
                context = context
            )
        }
        composable(route = Screens.Logs.route) {
            Logs(
                navController = navController,
                context = context
            )
        }
        composable(route = Screens.Settings.route) {
            Settings(
                navController = navController,
                context = context
            )
        }
        composable(
            route = Screens.Tweaks.route + "/{screen}",
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