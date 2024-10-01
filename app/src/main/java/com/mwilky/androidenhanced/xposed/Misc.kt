package com.mwilky.androidenhanced.xposed

import android.content.Context
import com.mwilky.androidenhanced.BroadcastUtils.Companion.registerBroadcastReceiver
import com.mwilky.androidenhanced.HookedClasses.Companion.DISPLAY_ROTATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.WINDOW_MANAGER_SERVICE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.WINDOW_STATE_CLASS
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

        lateinit var DisplayRotationObject: Any

        //Tweak Variables
        var mAllowAllRotations: Boolean = false
        var mDisableSecureScreenshots = false

        fun init(classLoader: ClassLoader?) {

            // Class references
            val displayRotation = findClass(
                DISPLAY_ROTATION_CLASS, classLoader
            )
            val windowManagerService = findClass(
                WINDOW_MANAGER_SERVICE_CLASS, classLoader
            )

            // Hook the constructors to register the receiver
            hookAllConstructors(displayRotation, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {

                    DisplayRotationObject = param.thisObject

                    val mContext: Context = getObjectField(
                        param.thisObject, "mContext"
                    ) as Context

                    //Register broadcast receiver to receive values
                    registerBroadcastReceiver(
                        mContext, allowAllRotations, param.thisObject.toString(), false
                    )
                }
            })

            // Hook the constructors to register the receiver
            hookAllConstructors(windowManagerService, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {

                    val mContext: Context = getObjectField(
                        param.thisObject, "mContext"
                    ) as Context

                    //Register broadcast receiver to receive values
                    registerBroadcastReceiver(
                        mContext, disableSecureScreenshots, param.thisObject.toString(), false
                    )
                }
            })

            //Allow all rotations
            findAndHookMethod(DISPLAY_ROTATION_CLASS,
                classLoader,
                "getAllowAllRotations",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val intResult: Int = if (mAllowAllRotations) 1 else 0

                        setIntField(param.thisObject, "mAllowAllRotations", intResult)

                        param.result = intResult
                    }
                })

            //Secure screenshot
            findAndHookMethod(WINDOW_STATE_CLASS,
                classLoader,
                "isSecureLocked",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val result = param.result as Boolean
                        if (result) {
                            if (mDisableSecureScreenshots) {
                                param.result = false
                            }
                        }
                    }
                })
        }

        //Additional functions
        //Called when the broadcast is sent to set the values
        fun updateAllowAllRotations(value: Boolean) {

            val intResult: Int = if (value) 1 else 0

            setIntField(DisplayRotationObject, "mAllowAllRotations", intResult)

        }

    }
}