package com.mwilky.androidenhanced.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField


class Notifications {

    companion object {
        //Hook Classes
        private const val NOTIFICATION_MANAGER_SERVICE_CLASS =
            "com.android.server.notification.NotificationManagerService"

        private const val NOTIFICATION_RECORD_CLASS =
            "com.android.server.notification.NotificationRecord"

        private const val ROW_APPEARANCE_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator"

        //Class Objects
        lateinit var mRowAppearanceCoordinator: Any
        lateinit var mNotifCollection: Any

        //Tweak Variables
        var mMuteScreenOnNotificationsEnabled: Boolean = false
        var mExpandedNotifications: Boolean = false

        fun initFramework(classLoader: ClassLoader?) {

            // Silent screen on notifications
            val notificationRecord = findClass(
                NOTIFICATION_RECORD_CLASS,
                classLoader
            )

            // Silent screen on notifications
            findAndHookMethod(
                NOTIFICATION_MANAGER_SERVICE_CLASS,
                classLoader,
                "buzzBeepBlinkLocked",
                notificationRecord,
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

        //Additional functions
        fun updateNotificationExpansion() {
            setBooleanField(
                mRowAppearanceCoordinator,
                "mAlwaysExpandNonGroupedNotification",
                mExpandedNotifications
            )

            val notificationEntries = getObjectField(
                mNotifCollection,
                "mReadOnlyNotificationSet"
            ) as Collection<Any?>

            for (notificationEntry in notificationEntries.toTypedArray()) {
                val expandableNotifictionRowController =
                    getObjectField(
                        notificationEntry,
                        "mRowController"
                    )
                if (expandableNotifictionRowController != null) {
                    val expandableNotifictionRow = getObjectField(
                        expandableNotifictionRowController,
                        "mView"
                    )
                    if (expandableNotifictionRow != null) {
                        callMethod(
                            expandableNotifictionRow,
                            "setUserExpanded",
                            mExpandedNotifications,
                            mExpandedNotifications
                        )
                    }
                }
            }
        }

    }
}