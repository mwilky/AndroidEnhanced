package com.mwilky.androidenhanced.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Tweaks : Screen("tweaks")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {  arg ->
                append("/$arg")
            }
        }
    }
}