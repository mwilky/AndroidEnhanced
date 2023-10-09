package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField


class Quicksettings {

    companion object {
        //Hook Classes
        private const val QS_TILE_IMPL_CLASS = "com.android.systemui.qs.tileimpl.QSTileImpl"
        private const val QS_FOOTER_VIEW_CLASS = "com.android.systemui.qs.QSFooterView"
        private const val QUICK_SETTINGS_CONTROLLER_CLASS = "com.android.systemui.shade.QuickSettingsController"

        //Class Objects
        lateinit var QSFooterView: Any

        //Tweak Variables
        var mClickVibrationEnabled: Boolean = false
        var mHideQSFooterBuildNumberEnabled: Boolean = false
        var mSmartPulldownConfig: Int = 0
        var mQuickPulldownConfig: Int = 0

        fun init(classLoader: ClassLoader?) {

            //Hook Constructors

            //QS tile click vibration
            findAndHookMethod(
                QS_TILE_IMPL_CLASS, classLoader,
                "click",
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

            //Quick/Smart pulldown
            findAndHookMethod(
                QUICK_SETTINGS_CONTROLLER_CLASS,
                classLoader,
                "isOpenQsEvent",
                MotionEvent::class.java,
                isOpenQsEventHook
            )

        }

        // Hooked functions
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
            override fun afterHookedMethod(param: MethodHookParam) {
                QSFooterView = param.thisObject
                if (mHideQSFooterBuildNumberEnabled) {
                    val mBuildText = getObjectField(param.thisObject, "mBuildText")
                            as TextView

                    mBuildText.text = null
                    setBooleanField(
                        param.thisObject,
                        "mShouldShowBuildText",
                        false
                    )
                    mBuildText.isSelected = false
                }
            }
        }

        //Smart/Quick pulldown
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
        //Smart/Quick pulldown
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

        //Smart/Quick pulldown
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