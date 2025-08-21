package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import com.mwilky.androidenhanced.HookedClasses.Companion.LAUNCHER_APPLICATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.WORKSPACE_TOUCH_LISTENER_CLASS
import com.mwilky.androidenhanced.References.Companion.LauncherContext
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.gestureSleep
import com.mwilky.androidenhanced.Utils.Companion.mDoubleTapSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.sendLogBroadcast
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import com.mwilky.androidenhanced.xposed.BroadcastReceiver.Companion.registerBroadcastReceiver
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField

class Launcher {

    companion object {

        fun init(classLoader: ClassLoader) {

            // Register broadcast receivers
            findAndHookMethod(
                LAUNCHER_APPLICATION_CLASS, classLoader, "onCreate", object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        LauncherContext = param.thisObject as Context

                        sendLogBroadcast(
                            LauncherContext,
                            "Hook Success",
                            "${LauncherContext.packageName} hooked successfully!",
                            LogEntryType.HOOKS
                        )

                        registerBroadcastReceiver(
                            LauncherContext,
                            doubleTapToSleepLauncher,
                            param.thisObject.toString(),
                            false
                        )
                    }
                })

            // Hooks
            doubleTapToSleepHooks(classLoader)
        }

        fun doubleTapToSleepHooks(classLoader: ClassLoader) {
            // Add the gesture detector
            hookAllConstructors(
                findClass(WORKSPACE_TOUCH_LISTENER_CLASS, classLoader), object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        val mWorkspace = getObjectField(param.thisObject, "mWorkspace")
                        val mContext = callMethod(mWorkspace, "getContext") as Context

                        val mGestureDetector =
                            getObjectField(param.thisObject, "mGestureDetector") as GestureDetector

                        val sharedPreferences = mContext.createDeviceProtectedStorageContext()
                            .getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
                        mDoubleTapSleepLauncher =
                            sharedPreferences.getBoolean(doubleTapToSleepLauncher, false)

                        val onDoubleTapListener = object : GestureDetector.OnDoubleTapListener {
                            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                return false
                            }

                            override fun onDoubleTap(e: MotionEvent): Boolean {
                                if (mDoubleTapSleepLauncher) {
                                    sendDoubleTapBroadcast(mContext)
                                    return true
                                }
                                return false
                            }

                            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                                return false
                            }
                        }
                        mGestureDetector.setOnDoubleTapListener(onDoubleTapListener)
                    }
                })
        }

        private fun sendDoubleTapBroadcast(context: Context) {
            val intent = Intent()
            intent.action = gestureSleep
            context.sendBroadcast(intent)
        }
    }
}