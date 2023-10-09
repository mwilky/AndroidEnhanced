package com.mwilky.androidenhanced.xposed
import de.robv.android.xposed.IXposedHookLoadPackage

import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    companion object {

        //Package to hook
        const val SYSTEMUI_PACKAGE = "com.android.systemui"
        const val FRAMEWORK_PACKAGE = "android"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        when(lpparam?.packageName) {
            FRAMEWORK_PACKAGE -> {
                Buttons.init(lpparam.classLoader)
                Misc.init(lpparam.classLoader)
                Lockscreen.initFramework(lpparam.classLoader)
                Notifications.initFramework(lpparam.classLoader)
            }
            SYSTEMUI_PACKAGE -> {
                SystemUIApplication.init(lpparam.classLoader)
                Statusbar.init(lpparam.classLoader)
                Lockscreen.initSystemUI(lpparam.classLoader)
                Quicksettings.init(lpparam.classLoader)
            }
        }
    }

}