package com.mwilky.androidenhanced.dataclasses

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Onboarding : Screens("onboarding")
    object Home : Screens("home")
    object Tweaks : Screens("tweaks")
    object Logs : Screens("logs")
    object Settings : Screens("settings")
    object Statusbar : Screens("statusbar")
    object StatusbarIcons : Screens("statusbar icon colours")
    object StatusbarIconManagement : Screens("hide statusbar icons")
    object StatusbarCollapsedIconManagement : Screens("hide collapsed statusbar icons")
    object Misc : Screens("miscellaneous")
    object Buttons : Screens("buttons")
    object Notifications : Screens("notifications")
    object Lockscreen : Screens("lockscreen")
    object LockscreenIcons : Screens("lockscreen icon colours")
    object Quicksettings : Screens("quicksettings")
    object QuicksettingsIcons : Screens("quicksettings icon colours")

}