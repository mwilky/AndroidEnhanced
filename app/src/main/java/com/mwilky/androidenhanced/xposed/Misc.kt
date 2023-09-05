package com.mwilky.androidenhanced.xposed

import android.content.Context
import com.mwilky.androidenhanced.BroadcastUtils.Companion.registerBroadcastReceiver
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setIntField

class Misc {

    companion object {
        //Hook Classes
        private const val DisplayRotationClass =
            "com.android.server.wm.DisplayRotation"
        private const val WindowStateClass =
            "com.android.server.wm.WindowState"
        private const val WindowManagerServiceClass =
            "com.android.server.wm.WindowManagerService"

        lateinit var DisplayRotationObject: Any

        //Tweak Variables
        var mAllowAllRotations: Boolean = false
        var mDisableSecureScreenshots = false

        fun init(classLoader: ClassLoader?) {

            //Hook the constructors to register the receiver
            val displayRotation = findClass(
                DisplayRotationClass,
                classLoader
            )
            hookAllConstructors(
                displayRotation,
                constructor_hook_DisplayRotation
            )

            //Allow all rotations function
            findAndHookMethod(
                DisplayRotationClass,
                classLoader,
                "getAllowAllRotations",
                getAllowAllRotations_hook
            )

            //Hook the constructors to register the receiver
            val windowManagerService = findClass(
                WindowManagerServiceClass,
                classLoader
            )
            hookAllConstructors(
                windowManagerService,
                constructor_hook_WindowManagerService
            )

            //Secure screenshot function
            findAndHookMethod(
                WindowStateClass,
                classLoader,
                "isSecureLocked",
                isSecureLocked_hook)
        }

        //Constructor
        private val constructor_hook_DisplayRotation: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                DisplayRotationObject = param.thisObject

                val mContext: Context = getObjectField(
                    param.thisObject,
                    "mContext"
                ) as Context

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(
                    mContext, allowAllRotations,
                    param.thisObject.toString()
                )
            }
        }

        //Constructor
        private val constructor_hook_WindowManagerService: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val mContext: Context = getObjectField(
                    param.thisObject,
                    "mContext"
                ) as Context

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(
                    mContext, disableSecureScreenshots,
                    param.thisObject.toString()
                )
            }
        }

        //Sets the value to the field and also sets the functions return value
        private val getAllowAllRotations_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                param.result = mAllowAllRotations

                val intResult: Int = if (mAllowAllRotations) 1 else 0

                setIntField(param.thisObject, "mAllowAllRotations", intResult)
            }
        }

        private val isSecureLocked_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val result = param.result as Boolean
                if (result) {
                    if (mDisableSecureScreenshots) {
                        param.result = false
                    }
                }
            }
        }

        //Additional functions (not hooks/replacements)
        //Called when the broadcast is sent to set the values
        fun updateAllowAllRotations(value: Boolean) {

            val intResult: Int = if (value) 1 else 0

            setIntField(DisplayRotationObject, "mAllowAllRotations", intResult)

        }

    }
}