package com.mwilky.androidenhanced.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    companion object {

        const val SYSTEMUI_PREFS = "sysui_prefs"
        const val FRAMEWORK_PREFS = "framework_prefs"

        //Package to hook
        const val SYSTEMUI_PACKAGE = "com.android.systemui"
        const val FRAMEWORK_PACKAGE = "android"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        when(lpparam?.packageName) {
            SYSTEMUI_PACKAGE -> {
                SystemUIStatusbar.init(lpparam.classLoader)
            }
        }
    }

}