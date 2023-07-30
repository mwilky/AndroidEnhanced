package com.mwilky.androidenhanced

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController, context = context)
        }
        // Use AnimatedVisibility to show/hide the HomeScreen based on the back stack
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController, context = context)
        }
        composable(route = Screen.Home.route) {
            //Don't allow going back to onboarding
            BackHandler(true) {
            }
            HomeScreen(navController = navController, context = context)
        }
    }
}