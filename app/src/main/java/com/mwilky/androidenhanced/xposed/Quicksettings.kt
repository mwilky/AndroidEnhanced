package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.setBooleanField


class Quicksettings {

    companion object {
        //Hook Classes
        private const val QS_TILE_IMPL_CLASS = "com.android.systemui.qs.tileimpl.QSTileImpl"
        private const val QS_FOOTER_VIEW_CLASS = "com.android.systemui.qs.QSFooterView"
        private const val QUICK_SETTINGS_CONTROLLER_CLASS = "com.android.systemui.shade.QuickSettingsController"
        private const val PHONE_STATUS_BAR_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS =
            "com.android.systemui.statusbar.phone.PhoneStatusBarViewController\$PhoneStatusBarViewTouchHandler"

        //Class Objects
        lateinit var QSFooterView: Any

        //Tweak Variables
        var mClickVibrationEnabled: Boolean = false
        var mHideQSFooterBuildNumberEnabled: Boolean = false
        var mSmartPulldownConfig: Int = 0
        var mQuickPulldownConfig: Int = 0

        fun init(classLoader: ClassLoader?) {

            //Hook Constructors
            val qsTileImpl = findClass(QS_TILE_IMPL_CLASS, classLoader)
            hookAllConstructors(qsTileImpl, QSTileImplConstructorHook)

            //QS tile click vibration
            findAndHookMethod(
                QS_TILE_IMPL_CLASS, classLoader, "click",
                View::class.java,
                clickHook
            )

            //QS tile click vibration
            findAndHookMethod(
                QS_TILE_IMPL_CLASS,
                classLoader,
                "longClick",
                View::class.java,
                longClickHook
            )


            //Hide QS footer build number
            findAndHookMethod(
                QS_FOOTER_VIEW_CLASS,
                classLoader,
                "setBuildText",
                setBuildTextHook
            )

            // Quick and smart pulldown
//            findAndHookMethod(
//                PHONE_STATUS_BAR_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
//                classLoader,
//                "onTouchEvent",
//                MotionEvent::class.java,
//                onTouchEventHook
//            )

            findAndHookMethod(
                QUICK_SETTINGS_CONTROLLER_CLASS,
                classLoader,
                "isOpenQsEvent",
                MotionEvent::class.java,
                isOpenQsEventHook
            )

        }

        // Hooked functions
        // register the receiver
        private val QSTileImplConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.qsTileVibration,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.hideQsFooterBuildNumber,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.smartPulldown,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.quickPulldown,
                    param.thisObject.toString()
                )

            }
        }

        //vibrate on short press
        private val clickHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (mClickVibrationEnabled) {
                    val mContext = getObjectField(param.thisObject, "mContext")
                            as Context
                    initVibrator(mContext)
                    val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                    mVibrator.vibrate(vibrationEffect)
                }
            }
        }

        //vibrate on long press
        private val longClickHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (mClickVibrationEnabled) {
                    val mContext = getObjectField(param.thisObject, "mContext")
                            as Context
                    initVibrator(mContext)
                    val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                    mVibrator.vibrate(vibrationEffect)
                }
            }
        }

        // HIDE THE VIEW
        private val setBuildTextHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                QSFooterView = param.thisObject
                if (mHideQSFooterBuildNumberEnabled) {
                    val mBuildText = getObjectField(param.thisObject, "mBuildText")
                            as TextView
                    callMethod(
                        mBuildText,
                        "setText",
                        null as CharSequence?
                    )
                    setBooleanField(
                        param.thisObject,
                        "mShouldShowBuildText",
                        false
                    )
                    callMethod(
                        mBuildText,
                        "setSelected",
                        false
                    )
                    param.result = null
                }
            }
        }

        private val isOpenQsEventHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val quickSettingsController = param.thisObject
                val event = param.args[0] as MotionEvent

                if (shouldFullyExpandDueQuickPulldown(quickSettingsController, event) ||
                    shouldFullyExpandDueSmartPulldown(quickSettingsController)
                    ) {
                    param.result = true
                }
            }
        }

        //Additional functions
        //Evaluate quick pulldown
        private fun shouldFullyExpandDueQuickPulldown(
            quickSettingsController: Any,
            event: MotionEvent
        ): Boolean {
            val mQs = getObjectField(quickSettingsController, "mQs")
            val mView = callMethod(mQs, "getView") as View
            val isLayoutRtl = callMethod(mView, "isLayoutRtl") as Boolean
            val measuredWidth = mView.measuredWidth
            val x = event.x
            val region = 0.25f * measuredWidth
            val mBarState = getIntField(quickSettingsController, "mBarState")

            return when (mQuickPulldownConfig) {
                0 -> false
                1 -> if (isLayoutRtl) x < region else measuredWidth - region < x
                2 -> if (isLayoutRtl) measuredWidth - region < x else x < region
                3 -> true
                else -> false
            } && mBarState == 0
        }

        //Evaluate smart pulldown
        private fun shouldFullyExpandDueSmartPulldown(quickSettingsController: Any) : Boolean {

            val notificationStackScrollLayoutController = getObjectField(
                quickSettingsController,
                "mNotificationStackScrollLayoutController"
            )

            val numActiveNotifs = getIntField(
                getObjectField(
                    notificationStackScrollLayoutController,
                    "mNotifStats"
                ), "numActiveNotifs"
            )
            val hasNonClearableAlertingNotifs = getBooleanField(
                getObjectField(
                    notificationStackScrollLayoutController,
                    "mNotifStats"
                ), "hasNonClearableAlertingNotifs"
            )
            val hasClearableAlertingNotifs = getBooleanField(
                getObjectField(
                    notificationStackScrollLayoutController,
                    "mNotifStats"
                ), "hasClearableAlertingNotifs"
            )

            return when (mSmartPulldownConfig) {
                1 -> !hasNonClearableAlertingNotifs && !hasClearableAlertingNotifs
                2 -> numActiveNotifs == 0
                else -> false
            }
        }
    }
}