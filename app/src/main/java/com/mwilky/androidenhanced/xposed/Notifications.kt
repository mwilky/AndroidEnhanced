package com.mwilky.androidenhanced.xposed

import com.mwilky.androidenhanced.BroadcastUtils.Companion.updateNotificationSectionHeaders
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_COORDINATOR_ATTACH_1_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_COORDINATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_ATTENTION_HELPER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_ENTRY_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_RECORD_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIF_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.ROW_APPEARANCE_COORDINATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField


class Notifications {

    companion object {

        //Class Objects
        lateinit var mRowAppearanceCoordinator: Any
        lateinit var mNotifCollection: Any
        // This is null sometimes we call it so we can't have as lateinit type
        var mRowAppearanceCoordinatorAttach2: Any? = null
        lateinit var mNotifViewController: Any
        // This is null sometimes we call it so we can't have as lateinit type
        var mKeyguardCoordinator: Any? = null

        //Tweak Variables
        var mMuteScreenOnNotificationsEnabled: Boolean = false
        var mExpandedNotificationsEnabled: Boolean = false
        var mAutoExpandFirstNotificationEnabled: Boolean = true
        var mNotificationSectionHeadersEnabled: Boolean = true

        fun initFramework(classLoader: ClassLoader?) {

            // Silent screen on notifications
            findAndHookMethod(NOTIFICATION_ATTENTION_HELPER_CLASS,
                classLoader,
                "buzzBeepBlinkLocked",
                NOTIFICATION_RECORD_CLASS,
                "$NOTIFICATION_ATTENTION_HELPER_CLASS\$Signals",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val mScreenOn = getBooleanField(param.thisObject, "mScreenOn")
                        val mSystemReady = getBooleanField(param.thisObject, "mSystemReady")
                        val mAudioManager = getObjectField(param.thisObject, "mAudioManager")
                        val skipSound =
                            mScreenOn && mMuteScreenOnNotificationsEnabled && mSystemReady && mAudioManager != null

                        if (skipSound) param.result = 0
                    }
                })
        }

        fun initSystemUI(classLoader: ClassLoader?) {
            // Class references
            val rowAppearanceCoordinator = findClass(
                ROW_APPEARANCE_COORDINATOR_CLASS, classLoader
            )
            val notifCollection = findClass(
                "com.android.systemui.statusbar.notification.collection.NotifCollection",
                classLoader
            )

            //Hook Constructors
            //Expanded notifications
            hookAllConstructors(rowAppearanceCoordinator, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    mRowAppearanceCoordinator = param.thisObject
                }
            })

            //Expanded notifications
            hookAllConstructors(notifCollection, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    mNotifCollection = param.thisObject
                }
            })

            // Notification section headers
            hookAllConstructors(
                findClass(KEYGUARD_COORDINATOR_CLASS, classLoader),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        mKeyguardCoordinator = param.thisObject
                    }
                })

            // Hooked methods
            // Auto expand first notification
            findAndHookMethod(ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS,
                classLoader,
                "onAfterRenderEntry",
                NOTIFICATION_ENTRY_CLASS,
                NOTIF_VIEW_CONTROLLER_CLASS,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        // Objects to be used elsewhere
                        mRowAppearanceCoordinatorAttach2 = param.thisObject
                        mNotifViewController = param.args[1]

                        val rowAppearController = getObjectField(param.thisObject, "\$tmp0")

                        setBooleanField(
                            rowAppearController,
                            "mAutoExpandFirstNotification",
                            mAutoExpandFirstNotificationEnabled
                        )

                    }
                })

            // Notification section headers
            findAndHookMethod(KEYGUARD_COORDINATOR_ATTACH_1_CLASS,
                classLoader,
                "accept",
                Object::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        val classId = getObjectField(param.thisObject, "\$r8\$classId")

                        if (classId == 0) mKeyguardCoordinator?.let {
                            updateNotificationSectionHeaders(
                                it
                            )
                        }

                    }
                })
        }
    }
}