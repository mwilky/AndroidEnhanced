package com.mwilky.androidenhanced.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getObjectField


class Notifications {

    companion object {
        //Hook Classes
        private const val NOTIFICATION_MANAGER_SERVICE_CLASS =
            "com.android.server.notification.NotificationManagerService"

        private const val NOTIFICATION_RECORD_CLASS =
            "com.android.server.notification.NotificationRecord"

        //Class Objects

        //Tweak Variables
        var mMuteScreenOnNotificationsEnabled: Boolean = false

        fun initFramework(classLoader: ClassLoader?) {

            // NotificationRecord class
            val notificationRecord = findClass(
                NOTIFICATION_RECORD_CLASS,
                classLoader
            )

            //HOOK UPDATE METHOD
            findAndHookMethod(
                NOTIFICATION_MANAGER_SERVICE_CLASS,
                classLoader,
                "buzzBeepBlinkLocked",
                notificationRecord,
                buzzBeepBlinkLockedHook
            )


        }

        // Hooked functions
        private val buzzBeepBlinkLockedHook: XC_MethodHook? = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val mScreenOn = getBooleanField(param.thisObject, "mScreenOn")
                val mSystemReady = getBooleanField(param.thisObject, "mSystemReady")
                val mAudioManager = getObjectField(param.thisObject, "mAudioManager")
                val skipSound =
                    mScreenOn && mMuteScreenOnNotificationsEnabled && mSystemReady
                            && mAudioManager != null
                if (skipSound)
                    param.result = 0
            }
        }

        //Additional functions

    }
}