package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getObjectField

class Lockscreen {

    companion object {
        //Hook Classes
        private const val KEYGUARD_STATUSBAR_VIEW_CLASS =
            "com.android.systemui.statusbar.phone.KeyguardStatusBarView"

        //Class Objects
        lateinit var keyguardStatusBarView: Any

        //Tweak Variables
        var hideLockscreenStatusbar: Boolean = false

        fun init(classLoader: ClassLoader?) {

            //Hide lockscreen statusbar
            findAndHookMethod(
                KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "updateVisibilities",
                updateVisibilitiesHook
            )

            //Hide lockscreen statusbar
            findAndHookMethod(
                KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                onFinishInflateHook
            )

        }

        //Hooked functions
        private val onFinishInflateHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                keyguardStatusBarView = param.thisObject

                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                // Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.hideLockscreenStatusBar,
                    param.thisObject.toString()
                )

            }
        }

        private val updateVisibilitiesHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                if (hideLockscreenStatusbar) {
                    callMethod(param.thisObject, "setVisibility", View.GONE)
                }
            }
        }

        //Additional functions
    }
}