package com.mwilky.androidenhanced.xposed

import android.R
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK

import android.view.MotionEvent
import android.view.View

import android.widget.LinearLayout
import android.widget.TextView
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.mReloadTiles
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import com.mwilky.androidenhanced.UtilsPremium

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge

import de.robv.android.xposed.XposedBridge.hookAllConstructors

import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findAndHookConstructor
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getFloatField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.newInstance
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setIntField
import de.robv.android.xposed.XposedHelpers.setObjectField


class Quicksettings {

    companion object {
        //Hook Classes
        private const val TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.TileLayout"
        private const val QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.QuickQSPanel\$QQSSideLabelTileLayout"
        private const val SIDE_LABEL_TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.SideLabelTileLayout"
        private const val QS_TILE_IMPL_CLASS =
            "com.android.systemui.qs.tileimpl.QSTileImpl"
        private const val QS_FOOTER_VIEW_CLASS =
            "com.android.systemui.qs.QSFooterView"
        private const val QUICK_SETTINGS_CONTROLLER_CLASS =
            "com.android.systemui.shade.QuickSettingsController"
        private const val TILE_ADAPTER_CLASS =
            "com.android.systemui.qs.customize.TileAdapter"
        private const val QS_PANEL_CONTROLLER_CLASS =
            "com.android.systemui.qs.QSPanelController"
        private const val QS_PANEL_CLASS =
            "com.android.systemui.qs.QSPanel"
        private const val QS_PANEL_CONTROLLER_BASE_CLASS =
            "com.android.systemui.qs.QSPanelControllerBase"
        private const val QUICK_QS_PANEL_CONTROLLER_CLASS =
            "com.android.systemui.qs.QuickQSPanelController"
        private const val QS_TILE_HOST_CLASS =
            "com.android.systemui.qs.QSTileHost"
        private const val QS_CUSTOMIZER_CONTROLLER_3_CLASS =
            "com.android.systemui.qs.customize.QSCustomizerController\$3"





        //Class Objects
        lateinit var QuickQSPanelQQSSideLabelTileLayout: Any
        lateinit var QSTileHost: Any
        lateinit var QSFooterView: Any
        lateinit var QSPanelController: Any
        lateinit var QSPanel: Any
        lateinit var QuickQSPanelController: Any
        lateinit var QSPanelControllerBase: Any

        //Tweak Variables
        var mClickVibrationEnabled: Boolean = false
        var mHideQSFooterBuildNumberEnabled: Boolean = false
        var mSmartPulldownConfig: Int = 0
        var mQuickPulldownConfig: Int = 0
        var mQQsRowsConfig: Int = 2
        var mQsColumnsConfig: Int = 2
        var mQsRowsConfig: Int = 4

        fun init(classLoader: ClassLoader?) {

            // QSTileHost CLASS
            val qSTileHost = findClass(
                QS_TILE_HOST_CLASS, classLoader
            )

            // HOOK CONSTRUCTOR TO SET mReloadTiles
            hookAllConstructors(
                qSTileHost, ConstructorHookQSTileHost
            )

            findAndHookMethod(
                QS_PANEL_CONTROLLER_CLASS, classLoader,
                "onViewAttached",
                onViewAttachedHookQSPanelController
            )

            findAndHookMethod(
                QS_PANEL_CLASS, classLoader,
                "onFinishInflate",
                onFinishInflateHookQSPanel
            )

            findAndHookMethod(
                QS_PANEL_CLASS, classLoader,
                "onFinishInflate",
                onFinishInflateHookQSPanel
            )

            findAndHookMethod(
                QUICK_QS_PANEL_CONTROLLER_CLASS, classLoader,
                "onViewAttached",
                onViewAttachedHookQuickQSPanelController
            )

            findAndHookMethod(
                QS_PANEL_CONTROLLER_BASE_CLASS, classLoader,
                "onViewAttached",
                onViewAttachedHookQSPanelControllerBase
            )

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

            findAndHookMethod(
                SIDE_LABEL_TILE_LAYOUT_CLASS,
                classLoader,
                "updateResources",
                updateResourceHookSidelabelTileLayout
            )

            findAndHookConstructor(
                QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS,
                classLoader,
                Context::class.java,
                ConstructorHookQuickQSPanelSideLabelTileLayout
            )

            findAndHookMethod(
                TILE_LAYOUT_CLASS,
                classLoader,
                "updateResources",
                updateResourceHookTileLayout
            )

            findAndHookMethod(
                QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS,
                classLoader,
                "updateResources",
                updateResourceHookQuickQSPanelQQSSidelabelTileLayout
            )

            findAndHookMethod(
                QUICK_QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                "onConfigurationChanged",
                onConfigurationChangedQuickQSPanelController
            )

            // Tile Adapter CLASS
            val tileAdapter = findClass(
                TILE_ADAPTER_CLASS, classLoader
            )

            // HOOK CONSTRUCTOR TO SET mReloadTiles
            hookAllConstructors(
                tileAdapter, ConstructorHookTileAdapter
            )

            val configuration = "android.content.res.Configuration"
            findAndHookMethod(
                QS_CUSTOMIZER_CONTROLLER_3_CLASS, classLoader,
                "onConfigChanged",
                configuration,
                onConfigChangedHookQSCustomizerController3
            )


        }

        // Hooked functions

        private val ConstructorHookQSTileHost: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSTileHost = param.thisObject
                mReloadTiles = false
            }
        }

        //Set the object and share with premiums mods
        private val onViewAttachedHookQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSPanelController = param.thisObject
                QuicksettingsPremium.QSPanelController = QSPanelController
            }
        }

        //Set the object and share with premiums mods
        private val onFinishInflateHookQSPanel: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSPanel = param.thisObject
                QuicksettingsPremium.QSPanel = QSPanel
            }
        }

        private val onViewAttachedHookQuickQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QuickQSPanelController = param.thisObject
                QuicksettingsPremium.QuickQSPanelController = QuickQSPanelController
            }
        }

        private val onViewAttachedHookQSPanelControllerBase: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSPanelControllerBase = param.thisObject
                QuicksettingsPremium.QSPanelControllerBase = QSPanelControllerBase
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

        private val onConfigurationChangedQuickQSPanelController: XC_MethodHook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                val mView = getObjectField(param.thisObject, "mView")
                val maxTiles  = getIntField(mView, "mMaxTiles")

                if (maxTiles != mQsColumnsConfig * mQQsRowsConfig) {
                    setIntField(mView, "mMaxTiles", mQsColumnsConfig * mQQsRowsConfig)
                    callMethod(param.thisObject, "setTiles")
                }
                callMethod(param.thisObject, "updateMediaExpansion")
                return null
            }
        }

        private val ConstructorHookTileAdapter: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                setIntField(param.thisObject, "mNumColumns", mQsColumnsConfig)
            }
        }

        private val ConstructorHookQuickQSPanelSideLabelTileLayout: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                callMethod(param.thisObject, "setMaxColumns", mQsColumnsConfig)
            }
        }

        private val updateResourceHookSidelabelTileLayout: XC_MethodHook = object
            : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                setIntField(param.thisObject, "mMaxAllowedRows", mQsRowsConfig);
            }
        }

        private val updateResourceHookQuickQSPanelQQSSidelabelTileLayout: XC_MethodHook = object
            : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QuickQSPanelQQSSideLabelTileLayout = param.thisObject
                if (QuicksettingsPremium.QuickQSPanelQQSSideLabelTileLayout == null) {
                    QuicksettingsPremium.QuickQSPanelQQSSideLabelTileLayout =
                        QuickQSPanelQQSSideLabelTileLayout
                }
                setIntField(param.thisObject, "mMaxAllowedRows", mQQsRowsConfig)
            }
        }

        private val updateResourceHookTileLayout: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                setIntField(
                    param.thisObject, "mResourceColumns", 1.coerceAtLeast(mQsColumnsConfig)
                )
                setIntField(
                    param.thisObject, "mMaxAllowedRows", 1.coerceAtLeast(mQsRowsConfig)
                )

                if (QuicksettingsPremium.updateColumns(param.thisObject)) {
                    XposedBridge.log("${UtilsPremium.TAG}: requestLayout called")
                    callMethod(param.thisObject, "requestLayout")
                    param.result=true
                } else {
                    param.result = false
                }
            }
        }

        private val onConfigChangedHookQSCustomizerController3: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                setIntField(
                    getObjectField(
                        getSurroundingThis(param.thisObject), "mTileAdapter"
                    ),
                    "mNumColumns", mQsColumnsConfig
                )

                val mView =  getObjectField(
                    getSurroundingThis(param.thisObject), "mView"
                )

                val mRecyclerView = getObjectField(mView, "mRecyclerView")

                val layoutManager = callMethod(mRecyclerView, "getLayoutManager")

                callMethod(
                    layoutManager, "setSpanCount",
                    getIntField(
                        getObjectField(
                            getSurroundingThis(param.thisObject), "mTileAdapter"
                        ),
                        "mNumColumns"
                    )
                )
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