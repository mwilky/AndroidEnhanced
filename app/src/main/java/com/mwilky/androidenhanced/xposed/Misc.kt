package com.mwilky.androidenhanced.xposed

import android.content.Context
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setIntField

class Misc {

    companion object {
        //Hook Classes
        private const val DisplayRotationClass =
            "com.android.server.wm.DisplayRotation"

        lateinit var DisplayRotationObject: Any

        //Tweak Variables
        var mAllowAllRotations: Boolean = false

        fun init(classLoader: ClassLoader?) {


            val DisplayRotation = findClass(
                DisplayRotationClass,
                classLoader
            )
            hookAllConstructors(
                DisplayRotation,
                constructor_hook
            )
            //Allow all rotations
            XposedHelpers.findAndHookMethod(
                DisplayRotationClass,
                classLoader,
                "getAllowAllRotations",
                getAllowAllRotations_hook
            )
        }

        private val constructor_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                DisplayRotationObject = param.thisObject

                val mContext: Context = getObjectField(
                    param.thisObject,
                    "mContext"
                ) as Context

                //Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    mContext, allowAllRotations,
                    param.thisObject.toString()
                )
            }
        }

        private val getAllowAllRotations_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                param.result = mAllowAllRotations

                val intResult: Int = if (mAllowAllRotations) 1 else 0

                setIntField(param.thisObject, "mAllowAllRotations", intResult)
            }
        }

        fun updateAllowAllRotations(value: Boolean) {

            val intResult: Int = if (value) 1 else 0

            setIntField(DisplayRotationObject, "mAllowAllRotations", intResult)
        }

    }
}