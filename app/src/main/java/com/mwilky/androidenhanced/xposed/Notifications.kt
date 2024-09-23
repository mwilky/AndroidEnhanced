package com.mwilky.androidenhanced.xposed

import com.mwilky.androidenhanced.BroadcastUtils.Companion.updateNotificationSectionHeaders
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField


class Notifications {

    companion object {
        //Hook Classes
        private const val NOTIFICATION_ATTENTION_HELPER_CLASS =
            "com.android.server.notification.NotificationAttentionHelper"

        private const val NOTIFICATION_RECORD_CLASS =
            "com.android.server.notification.NotificationRecord"

        private const val ROW_APPEARANCE_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator"

        private const val ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator\$attach\$2"

        private const val NOTIFICATION_ENTRY_CLASS =
            "com.android.systemui.statusbar.notification.collection.NotificationEntry"

        private const val NOTIF_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.notification.collection.render.NotifViewController"

        private const val KEYGUARD_COORDINATOR_ATTACH_1_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator\$attach\$1"

        private const val KEYGUARD_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator"

        //Class Objects
        lateinit var mRowAppearanceCoordinator: Any
        lateinit var mNotifCollection: Any

        var mRowAppearanceCoordinatorAttach2: Any? = null
        lateinit var mNotifViewController: Any

        var mKeyguardCoordinator: Any? = null

        //Tweak Variables
        var mMuteScreenOnNotificationsEnabled: Boolean = false
        var mExpandedNotifications: Boolean = false
        var mAutoExpandFirstNotificationEnabled: Boolean = true
        var mNotificationSectionHeadersEnabled: Boolean = true

        fun initFramework(classLoader: ClassLoader?) {

            // Silent screen on notifications
            val notificationRecord = findClass(
                NOTIFICATION_RECORD_CLASS,
                classLoader
            )

            // Silent screen on notifications
            findAndHookMethod(
                NOTIFICATION_ATTENTION_HELPER_CLASS,
                classLoader,
                "buzzBeepBlinkLocked",
                notificationRecord,
                "$NOTIFICATION_ATTENTION_HELPER_CLASS\$Signals",
                buzzBeepBlinkLockedHook
            )
        }

        fun initSystemUI(classLoader: ClassLoader?) {

            //Hook Constructors
            //Expanded notifications
            val rowAppearanceCoordinator = findClass(
                ROW_APPEARANCE_COORDINATOR_CLASS,
                classLoader
            )
            hookAllConstructors(rowAppearanceCoordinator, RowAppearanceCoordinatorConstructorHook)

            //Expanded notifications
            val notifCollection = findClass(
                "com.android.systemui.statusbar.notification.collection.NotifCollection",
                classLoader
            )
            hookAllConstructors(notifCollection, NotifCollectionConstructorHook)

            findAndHookMethod(
                ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS,
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
                }
            )

            findAndHookMethod(
                KEYGUARD_COORDINATOR_ATTACH_1_CLASS,
                classLoader,
                "accept",
                Object::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        val classId = getObjectField(param.thisObject, "\$r8\$classId")

                        if (classId == 0)
                            mKeyguardCoordinator?.let { updateNotificationSectionHeaders(it) }

                    }
                }
            )

            hookAllConstructors(
                findClass(KEYGUARD_COORDINATOR_CLASS, classLoader),
                object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {

                    mKeyguardCoordinator = param.thisObject
                }
            }
            )

        }

        // Hooked functions
        private val RowAppearanceCoordinatorConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                mRowAppearanceCoordinator = param.thisObject
            }
        }

        private val NotifCollectionConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                mNotifCollection = param.thisObject
            }
        }

        private val buzzBeepBlinkLockedHook: XC_MethodHook = object : XC_MethodHook() {
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
    }
}