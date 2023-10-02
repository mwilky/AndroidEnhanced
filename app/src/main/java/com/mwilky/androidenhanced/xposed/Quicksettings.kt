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
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField


class Quicksettings {

    companion object {
        //Hook Classes
        private const val QS_TILE_IMPL_CLASS = "com.android.systemui.qs.tileimpl.QSTileImpl"
        private const val QS_FOOTER_VIEW_CLASS = "com.android.systemui.qs.QSFooterView"
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
            findAndHookMethod(
                PHONE_STATUS_BAR_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
                classLoader,
                "onTouchEvent",
                MotionEvent::class.java,
                onTouchEventHookPhoneStatusBarViewController
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
                    XposedHelpers.callMethod(
                        mBuildText,
                        "setText",
                        null as CharSequence?
                    )
                    XposedHelpers.setBooleanField(
                        param.thisObject,
                        "mShouldShowBuildText",
                        false
                    )
                    XposedHelpers.callMethod(
                        mBuildText,
                        "setSelected",
                        false
                    )
                    param.result = null
                }
            }
        }

        private val onTouchEventHookPhoneStatusBarViewController: XC_MethodHook =
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    //TODO: check for new unread notifications for smart pulldown (notifications with the bell)
                    val motionEvent = param.args[0] as MotionEvent
                    val phoneStatusBarViewController =
                        XposedHelpers.getSurroundingThis(param.thisObject)
                    val centralSurface =
                        getObjectField(phoneStatusBarViewController, "centralSurfaces")
                    val notificationPanelViewController =
                        XposedHelpers.callMethod(
                            centralSurface,
                            "getNotificationPanelViewController"
                        )


                    //QUICK
                    val measuredWidth = XposedHelpers.callMethod(
                        getObjectField(
                            notificationPanelViewController, "mView"
                        ), "getMeasuredWidth"
                    ) as Int

                    val x = motionEvent.x
                    val f = 0.25f * measuredWidth
                    val showQSQuick =
                        (if (mQuickPulldownConfig != 1) !(if (mQuickPulldownConfig != 2) mQuickPulldownConfig != 3 else if (XposedHelpers.callMethod(
                                getObjectField(
                                    notificationPanelViewController, "mView"
                                ), "isLayoutRtl"
                            ) as Boolean
                        ) measuredWidth - f >= x else x >= f) else !if (XposedHelpers.callMethod(
                                getObjectField(
                                    notificationPanelViewController, "mView"
                                ), "isLayoutRtl"
                            ) as Boolean
                        ) x >= f else measuredWidth - f >= x) and (XposedHelpers.getIntField(
                            notificationPanelViewController, "mBarState"
                        ) == 0)

                    //SMART
                    val notificationStackScrollLayoutController = getObjectField(
                        notificationPanelViewController,
                        "mNotificationStackScrollLayoutController"
                    )

                    val numActiveNotifs = XposedHelpers.getIntField(
                        getObjectField(
                            notificationStackScrollLayoutController,
                            "mNotifStats"
                        ), "numActiveNotifs"
                    )
                    val hasNonClearableAlertingNotifs = XposedHelpers.getBooleanField(
                        getObjectField(
                            notificationStackScrollLayoutController,
                            "mNotifStats"
                        ), "hasNonClearableAlertingNotifs"
                    )
                    val hasClearableAlertingNotifs = XposedHelpers.getBooleanField(
                        getObjectField(
                            notificationStackScrollLayoutController,
                            "mNotifStats"
                        ), "hasClearableAlertingNotifs"
                    )

                    var showQSSmart = false

                    //NO HIGH PRIORITY NOTIFICATIONS
                    if (mSmartPulldownConfig == 1) {
                        if (!hasNonClearableAlertingNotifs && !hasClearableAlertingNotifs) {
                            showQSSmart = true
                        }
                        //NO NOTIFICATIONS
                    } else if (mSmartPulldownConfig == 2) {
                        if (numActiveNotifs == 0) {
                            showQSSmart = true
                        }
                    }

                    //OPEN WITH QS IF VALUES ARE TRUE
                    if (showQSQuick || showQSSmart) {
                        XposedHelpers.callMethod(
                            getObjectField(notificationPanelViewController, "mMetricsLogger"),
                            "count",
                            "panel_open_qs", 1
                        )
                        XposedHelpers.callMethod(notificationPanelViewController, "expandWithQs")
                    }
                }
            }


        //Additional functions
    }
}