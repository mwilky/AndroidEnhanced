package com.mwilky.androidenhanced.xposed

import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookConstructor
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
            })
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

        fun updateFirstNotificationExpansion(mRowAppearanceCoordinatorAttach2: Any) {

            val rowAppearanceCoordinator =
                getObjectField(mRowAppearanceCoordinatorAttach2, "\$tmp0")

            val mAlwaysExpandNonGroupedNotification =
                getBooleanField(rowAppearanceCoordinator, "mAlwaysExpandNonGroupedNotification")

            val notificationEntries = getObjectField(
                mNotifCollection,
                "mReadOnlyNotificationSet"
            ) as Collection<Any?>

            val entryToExpand = getObjectField(rowAppearanceCoordinator, "entryToExpand")

            for (notificationEntry in notificationEntries.toTypedArray()) {

                if (notificationEntry == entryToExpand) {

                    val expandableNotificationRowController =
                        getObjectField(notificationEntry, "mRowController")

                    if (expandableNotificationRowController != null) {

                        val expandableNotificationRow =
                            getObjectField(expandableNotificationRowController, "mView")

                        if (expandableNotificationRow != null) {

                            val isSystemExpanded =
                                getBooleanField(expandableNotificationRow, "mIsSystemExpanded")

                            val shouldWeExpand = (
                                    mAlwaysExpandNonGroupedNotification
                                        || (mAutoExpandFirstNotificationEnabled &&
                                        notificationEntry == entryToExpand)
                                    )

                            // If there is a difference between if the notification is expanded
                            // and whether it should be or not, then set the opposite
                            if (shouldWeExpand != isSystemExpanded) {

                                val isExpanded =
                                    callMethod(
                                        expandableNotificationRow,
                                        "isExpanded",
                                        false
                                    )

                                setBooleanField(
                                    expandableNotificationRow ,
                                    "mIsSystemExpanded",
                                    shouldWeExpand
                                )

                                callMethod(
                                    expandableNotificationRow,
                                    "notifyHeightChanged",
                                    false
                                )

                                callMethod(
                                    expandableNotificationRow,
                                    "onExpansionChanged",
                                    false,
                                    isExpanded
                                )

                                val mIsSummaryWithChildren = getBooleanField(expandableNotificationRow, "mIsSummaryWithChildren")
                                if (mIsSummaryWithChildren) {

                                    val mChildrenContainer =
                                        getObjectField(expandableNotificationRow, "mChildrenContainer")

                                    callMethod(mChildrenContainer, "updateGroupOverflow")
                                    callMethod(mChildrenContainer, "updateExpansionStates")

                                }
                            }

                            val mAssistantFeedbackController =
                                getObjectField(rowAppearanceCoordinator, "mAssistantFeedbackController")

                            val feedbackIcon = callMethod(
                                getObjectField(mAssistantFeedbackController, "mIcons"),
                                "get",
                                callMethod(
                                    mAssistantFeedbackController,
                                    "getFeedbackStatus",
                                    notificationEntry
                                )
                            )

                            val mIsSummaryWithChildren =
                                getBooleanField(expandableNotificationRow, "mIsSummaryWithChildren")

                            if (mIsSummaryWithChildren) {
                                val mChildrenContainer =
                                    getObjectField(expandableNotificationRow, "mChildrenContainer")

                                val mNotificationHeaderWrapper =
                                    getObjectField(mChildrenContainer, "mNotificationHeaderWrapper")

                                if (mNotificationHeaderWrapper != null)
                                    callMethod(
                                        mNotificationHeaderWrapper,
                                        "setFeedbackIcon",
                                        feedbackIcon
                                    )

                                val mNotificationHeaderWrapperLowPriority =
                                    getObjectField(
                                        mChildrenContainer,
                                        "mNotificationHeaderWrapperLowPriority"
                                    )

                                if (mNotificationHeaderWrapperLowPriority != null)
                                    callMethod(
                                        mNotificationHeaderWrapperLowPriority,
                                        "setFeedbackIcon",
                                        feedbackIcon
                                    )
                            }

                            val mPrivateLayout =
                                getObjectField(expandableNotificationRow, "mPrivateLayout")

                            if (getObjectField(mPrivateLayout, "mContractedChild") != null)
                                callMethod(
                                    getObjectField(mPrivateLayout, "mContractedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                            if (getObjectField(mPrivateLayout, "mExpandedChild") != null)
                                callMethod(
                                    getObjectField(mPrivateLayout, "mExpandedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                            if (getObjectField(mPrivateLayout, "mHeadsUpChild") != null)
                                callMethod(
                                    getObjectField(mPrivateLayout, "mHeadsUpWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                            val mPublicLayout =
                                getObjectField(expandableNotificationRow, "mPublicLayout")

                            if (getObjectField(mPublicLayout, "mContractedChild") != null)
                                callMethod(
                                    getObjectField(mPublicLayout, "mContractedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                            if (getObjectField(mPublicLayout, "mExpandedChild") != null)
                                callMethod(
                                    getObjectField(mPublicLayout, "mExpandedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                            if (getObjectField(mPublicLayout, "mHeadsUpChild") != null)
                                callMethod(
                                    getObjectField(mPublicLayout, "mHeadsUpWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )
                        }
                    }
                }

            }
        }

        fun updateNotificationSectionHeaders(mKeyguardCoordinator: Any) {

            var state = callMethod(
                getObjectField(mKeyguardCoordinator, "statusBarStateController"),
                "getState"
            )

            // If in normal shade view then hide the headers if required
            if (state == 0) {

                val sectionHeaderVisibilityProvider = getObjectField(mKeyguardCoordinator, "sectionHeaderVisibilityProvider")
                val neverShowSectionHeaders = getBooleanField(sectionHeaderVisibilityProvider, "neverShowSectionHeaders")

                // If we arent always hiding headers then set according to tweak value
                if (!neverShowSectionHeaders) {

                    val areSectionHeadersVisible = getBooleanField(sectionHeaderVisibilityProvider, "sectionHeadersVisible")

                    if (areSectionHeadersVisible != mNotificationSectionHeadersEnabled) {

                        setBooleanField(sectionHeaderVisibilityProvider, "sectionHeadersVisible", mNotificationSectionHeadersEnabled)
                        callMethod(
                            getObjectField(mKeyguardCoordinator, "notifFilter"),
                            "invalidateList",
                            "onStatusBarStateChanged"
                        )

                    }
                }
            }
        }

    }
}