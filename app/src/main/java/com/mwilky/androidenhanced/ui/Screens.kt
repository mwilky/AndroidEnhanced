package com.mwilky.androidenhanced.ui

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Onboarding : Screens("onboarding")
    object Home : Screens("home")
    object Tweaks : Screens("tweaks")
    object Logs : Screens("logs")
    object Settings : Screens("settings")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {  arg ->
                append("/$arg")
            }
        }
    }
}