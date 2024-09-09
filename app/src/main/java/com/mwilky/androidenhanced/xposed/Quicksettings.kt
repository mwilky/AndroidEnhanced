package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Handler
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.util.ArraySet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
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
        private const val BRIGHTNESS_MIRROR_HANDLER_CLASS =
            "com.android.systemui.settings.brightness.BrightnessMirrorHandler"
        private const val BRIGHTNESS_CONTROLLER_CLASS =
            "com.android.systemui.settings.brightness.BrightnessController"
        private val QS_IMPL_CLASS = "com.android.systemui.qs.QSImpl"
        private const val QS_ANIMATOR_CLASS =
            "com.android.systemui.qs.QSAnimator"
        private const val PAGED_TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.PagedTileLayout"
        private const val QUICK_QS_PANEL_CLASS =
            "com.android.systemui.qs.QuickQSPanel"






        //Class Objects
        lateinit var QuickQSPanelQQSSideLabelTileLayout: Any
        lateinit var QSTileHost: Any
        lateinit var QSFooterView: Any
        lateinit var QSPanelController: Any
        lateinit var QSPanel: Any
        lateinit var QuickQSPanelController: Any
        lateinit var QSPanelControllerBase: Any
        lateinit var BrightnessMirrorHandler: Any
        lateinit var BrightnessMirrorHandlerClass: Class<*>
        lateinit var BrightnessSliderControllerFactory: Any
        lateinit var BrightnessControllerClass: Class<*>
        lateinit var BrightnessControllerFactory: Any
        lateinit var PagedTileLayout: Any
        lateinit var mQsCustomizerController3: Any

        //Tweak Variables
        var mClickVibrationEnabled: Boolean = false
        var mHideQSFooterBuildNumberEnabled: Boolean = false
        var mSmartPulldownConfig: Int = 0
        var mQuickPulldownConfig: Int = 0
        var mQQsRowsConfig: Int = 2
        var mQsColumnsConfig: Int = 2
        var mQsColumnsConfigLandscape: Int = 4
        var mQQsColumnsConfig: Int = 2
        var mQQsColumnsConfigLandscape: Int = 4
        var mQsRowsConfig: Int = 4
        var mQsBrightnessSliderPositionConfig: Int = 0
        var mQQsBrightnessSliderEnabled: Boolean = false
        var mQsStyleConfig: Int = 0

        //Qqs Brightness
        lateinit var mQQsBrightnessController: Any
        lateinit var mQQsBrightnessSliderController: Any
        lateinit var mQQsBrightnessMirrorHandler: Any
        lateinit var mQsAnimator: Any

        lateinit var mBrightnessMirrorHandler: Any

        fun init(classLoader: ClassLoader?) {

            // PagedTileLayout CLASS
            val pagedTileLayout = findClass(
                PAGED_TILE_LAYOUT_CLASS, classLoader
            )

            // HOOK CONSTRUCTOR to set object
            hookAllConstructors(
                pagedTileLayout, ConstructorHookPagedTileLayout
            )

            // QSTileHost CLASS
            val qSAnimator = findClass(
                QS_ANIMATOR_CLASS, classLoader
            )

            // HOOK CONSTRUCTOR TO SET mReloadTiles
            hookAllConstructors(
                qSAnimator, ConstructorHookQSAnimator
            )

            // QSTileHost CLASS
            val qSTileHost = findClass(
                QS_TILE_HOST_CLASS, classLoader
            )

            // HOOK CONSTRUCTOR TO SET mReloadTiles
            hookAllConstructors(
                qSTileHost, ConstructorHookQSTileHost
            )

            // QSPanelController CLASS
            val qsPanelController = findClass(
                QS_PANEL_CONTROLLER_CLASS, classLoader
            )

            hookAllConstructors(
                qsPanelController, ConstructorHookQSPanelController
            )

            // BrightnessMirrorHandler CLASS
            val brightnessMirrorHandler = findClass(
                BRIGHTNESS_MIRROR_HANDLER_CLASS, classLoader
            )

            BrightnessMirrorHandlerClass = findClass(BRIGHTNESS_MIRROR_HANDLER_CLASS, classLoader)

            hookAllConstructors(
                brightnessMirrorHandler, ConstructorHookBrightnessMirrorHandler
            )

            BrightnessControllerClass = findClass(BRIGHTNESS_CONTROLLER_CLASS, classLoader)

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
                QUICK_QS_PANEL_CONTROLLER_CLASS, classLoader,
                "onViewDetached",
                onViewDetachedHookQuickQSPanelController
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
            val tileAdapter = findClass(TILE_ADAPTER_CLASS, classLoader)

            // HOOK CONSTRUCTOR
            hookAllConstructors(tileAdapter, ConstructorHookTileAdapter)

            val qsCustomizer3 = findClass(QS_CUSTOMIZER_CONTROLLER_3_CLASS, classLoader)

            // HOOK CONSTRUCTOR
            hookAllConstructors(qsCustomizer3, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {

                    mQsCustomizerController3 = param.thisObject

                }
            })

            val configuration = "android.content.res.Configuration"
            findAndHookMethod(
                QS_CUSTOMIZER_CONTROLLER_3_CLASS, classLoader,
                "onConfigChanged",
                configuration,
                onConfigChangedHookQSCustomizerController3
            )

            findAndHookMethod(
                QS_PANEL_CLASS,
                classLoader,
                "switchToParent",
                View::class.java,
                ViewGroup::class.java,
                Int::class.javaPrimitiveType,
                String::class.java,
                switchToParentHook
            )

            findAndHookMethod(
                QS_IMPL_CLASS,
                classLoader,
                "updateQsPanelControllerListening",
                updateQsPanelControllerListeningHook
            )

            findAndHookMethod(
                QUICK_QS_PANEL_CLASS,
                classLoader,
                "getOrCreateTileLayout",
                getOrCreateTileLayoutHook
            )

            findAndHookMethod(
                QS_PANEL_CONTROLLER_BASE_CLASS,
                classLoader,
                "switchTileLayout",
                Boolean::class.javaPrimitiveType,
                switchTileLayoutHook
            )
        }

        // Hooked functions
        private val ConstructorHookQSTileHost: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSTileHost = param.thisObject
            }
        }

        private val ConstructorHookQSAnimator: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                mQsAnimator = param.thisObject
            }
        }

        private val ConstructorHookPagedTileLayout: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                PagedTileLayout = param.thisObject
            }
        }

        private val ConstructorHookQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                BrightnessSliderControllerFactory = param.args[12]
                BrightnessControllerFactory = param.args[11]
                val mView = getObjectField(param.thisObject, "mView") as ViewGroup
                val mBrightnessView = getObjectField(mView, "mBrightnessView") as View
                mBrightnessMirrorHandler = getObjectField(param.thisObject, "mBrightnessMirrorHandler")
                setBrightnessView(mView, mBrightnessView)
            }
        }

        private val ConstructorHookBrightnessMirrorHandler: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                BrightnessMirrorHandler = param.thisObject
            }
        }

        //Set the object and share with premiums mods
        private val onViewAttachedHookQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSPanelController = param.thisObject
                QuicksettingsPremium.QSPanelController = QSPanelController
            }
        }

        // Set the object
        private val onFinishInflateHookQSPanel: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QSPanel = param.thisObject
            }
        }

        private val onViewAttachedHookQuickQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                QuickQSPanelController = param.thisObject
                QuicksettingsPremium.QuickQSPanelController = QuickQSPanelController

                val mView = getObjectField(param.thisObject, "mView") as ViewGroup
                val mContext = callMethod(mView, "getContext") as Context


                mQQsBrightnessSliderController =
                    callMethod(
                        BrightnessSliderControllerFactory,
                        "create",
                        mContext,
                        mView
                    )

                val mBrightnessView =
                    getObjectField(mQQsBrightnessSliderController, "mView") as View

                mQQsBrightnessController =
                    callMethod(
                        BrightnessControllerFactory,
                        "create",
                        mQQsBrightnessSliderController
                    )


                mQQsBrightnessMirrorHandler =
                    newInstance(BrightnessMirrorHandlerClass, mQQsBrightnessController)

                setBrightnessView(mView, mBrightnessView)

                callMethod(mQQsBrightnessSliderController, "init$10")



                val brightnessMirrorController =
                    getObjectField(mQQsBrightnessMirrorHandler, "mirrorController")

                if (brightnessMirrorController != null) {
                    val listener =
                        getObjectField(mQQsBrightnessMirrorHandler, "brightnessMirrorListener")
                    val mBrightnessMirrorListeners =
                        getObjectField(brightnessMirrorController, "mBrightnessMirrorListeners")
                                as ArraySet<Any>

                    mBrightnessMirrorListeners.add(listener)
                }
            }
        }

        private val onViewDetachedHookQuickQSPanelController: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                QuickQSPanelController = param.thisObject
                QuicksettingsPremium.QuickQSPanelController = QuickQSPanelController

                val brightnessMirrorController =
                    getObjectField(mQQsBrightnessMirrorHandler, "mirrorController")

                if (brightnessMirrorController != null) {
                    val listener =
                        getObjectField(mQQsBrightnessMirrorHandler, "brightnessMirrorListener")
                    val mBrightnessMirrorListeners =
                        getObjectField(brightnessMirrorController, "mBrightnessMirrorListeners")
                                as ArraySet<Any>

                    mBrightnessMirrorListeners.remove(listener)
                }
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

        private val onConfigurationChangedQuickQSPanelController: XC_MethodHook =
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    val mView = getObjectField(param.thisObject, "mView") as View
                    val maxTiles  = getIntField(mView, "mMaxTiles")

                    val mMediaHost = getObjectField(param.thisObject, "mMediaHost")

                    if (callMethod(mMediaHost, "getVisible") as Boolean) {

                        // We need to hardcode 2 columns and 2 rows when landscape and media is playing
                        if (mView.resources.configuration.orientation == ORIENTATION_LANDSCAPE) {

                            setIntField(
                                QuickQSPanelQQSSideLabelTileLayout,
                                "mMaxAllowedRows",
                                2
                            )

                            setIntField(mView, "mMaxTiles", 4)
                            callMethod(param.thisObject, "setTiles")

                        } else {

                            setIntField(
                                QuickQSPanelQQSSideLabelTileLayout,
                                "mMaxAllowedRows",
                                getQsRowCount(mView.context, "QQS")
                            )

                            val totalTiles =
                                getQsRowCount(
                                    mView.context,
                                    "QQS"
                                ) * getQsColumnCount(
                                    mView.context,
                                    "QQS"
                                )

                            if (maxTiles != totalTiles) {
                                setIntField(mView, "mMaxTiles", totalTiles)
                                callMethod(param.thisObject, "setTiles")
                            }
                        }
                    } else {

                        setIntField(
                            QuickQSPanelQQSSideLabelTileLayout,
                            "mMaxAllowedRows",
                            getQsRowCount(mView.context, "QQS")
                        )

                        val totalTiles =
                            getQsRowCount(
                                mView.context,
                                "QQS"
                            ) * getQsColumnCount(
                                mView.context,
                                "QQS"
                            )

                        if (maxTiles != totalTiles) {
                            setIntField(mView, "mMaxTiles", totalTiles)
                            callMethod(param.thisObject, "setTiles")
                        }
                    }

                    callMethod(param.thisObject, "updateMediaExpansion")

                    return null
                }
            }

        private val ConstructorHookTileAdapter: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                setIntField(
                    param.thisObject,
                    "mNumColumns",
                    getQsColumnCount(mContext, "QS")
                )
            }
        }

        private val updateResourceHookSidelabelTileLayout: XC_MethodHook =
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {

                    val sideLabelTileLayout = param.thisObject as ViewGroup

                    val mContext = getObjectField(param.thisObject, "mContext") as Context

                    val resources = sideLabelTileLayout.resources

                    val mIsSmallLandscapeLockscreenEnabled =
                        getObjectField(sideLabelTileLayout, "mIsSmallLandscapeLockscreenEnabled")
                                as Boolean

                    val mIsSmallLandscape =
                        resources.getBoolean(
                            mContext.resources.getIdentifier(
                                "is_small_screen_landscape",
                                "bool",
                                "com.android.systemui"
                            )
                        )

                    val columns =
                        if (mIsSmallLandscapeLockscreenEnabled && mIsSmallLandscape)
                            resources.getInteger(
                                mContext.resources.getIdentifier(
                                    "small_land_lockscreen_quick_settings_num_columns",
                                    "integer",
                                    "com.android.systemui"
                                )
                            )
                        else
                            maxOf(
                                getQsColumnCount(mContext, "QS"),
                                getQsColumnCount(mContext, "QQS")
                            )

                    setIntField(
                        param.thisObject, "mResourceColumns", 1.coerceAtLeast(columns)
                    )

                    val mResourceCellHeightResId =
                        getIntField(param.thisObject, "mResourceCellHeightResId")

                    val mResourceCellHeight =
                        resources.getDimensionPixelSize(mResourceCellHeightResId)

                    setIntField(param.thisObject, "mResourceCellHeight", mResourceCellHeight)

                    val mCellMarginHorizontal =
                        resources.getDimensionPixelSize(
                            mContext.resources.getIdentifier(
                                "qs_tile_margin_horizontal",
                                "dimen",
                                "com.android.systemui"
                            )
                        )

                    setIntField(param.thisObject, "mCellMarginHorizontal", mCellMarginHorizontal)

                    val mCellMarginVertical =
                        resources.getDimensionPixelSize(
                            mContext.resources.getIdentifier(
                                "qs_tile_margin_vertical",
                                "dimen",
                                "com.android.systemui"
                            )
                        )

                    setIntField(param.thisObject, "mCellMarginVertical", mCellMarginVertical)

                    val rows =
                        if (mIsSmallLandscapeLockscreenEnabled && mIsSmallLandscape)
                            resources.getInteger(
                                mContext.resources.getIdentifier(
                                    "small_land_lockscreen_quick_settings_max_rows",
                                    "integer",
                                    "com.android.systemui"
                                )
                            )
                        else
                            getQsRowCount(mContext, "QS")

                    setIntField(
                        param.thisObject, "mMaxAllowedRows", 1.coerceAtLeast(rows)
                    )

                    val mLessRows = getBooleanField(param.thisObject, "mLessRows")

                    if (mLessRows) {
                        val mMinRows = getIntField(param.thisObject, "mMinRows")

                        val mMaxAllowedRows = getIntField(param.thisObject, "mMaxAllowedRows")

                        setIntField(
                            param.thisObject,
                            "mMaxAllowedRows",
                            mMinRows.coerceAtLeast(mMaxAllowedRows - 1)
                        )

                    }

                    val mTempTextView = getObjectField(param.thisObject, "mTempTextView")
                            as TextView

                    mTempTextView.dispatchConfigurationChanged(mContext.resources.configuration)

                    QuicksettingsPremium.Companion.updateTileMargins(param.thisObject)

                    callMethod(param.thisObject, "estimateCellHeight")

                    val updateColumns = callMethod(param.thisObject, "updateColumns")
                            as Boolean

                    var mReturn = false

                    if (updateColumns) {
                        callMethod(param.thisObject, "requestLayout")
                        mReturn = true
                    }

                    setIntField(
                        param.thisObject, "mMaxAllowedRows", rows
                    )

                    return mReturn
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
            }
        }

        private val onConfigChangedHookQSCustomizerController3: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val tileAdapter = getObjectField(
                    getSurroundingThis(param.thisObject), "mTileAdapter"
                )
                val mContext = getObjectField(tileAdapter, "mContext") as Context

                setIntField(tileAdapter, "mNumColumns", getQsColumnCount(mContext, "QS"))

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

        private val switchToParentHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val child = param.args[0] as View?
                val parent = param.args[1] as ViewGroup?
                val index = param.args[2] as Int
                val tag = param.args[3] as String

                if (mQsBrightnessSliderPositionConfig != 1) return

                if (parent == null) {
                    Log.w(tag, "Trying to move view to null parent", IllegalStateException())
                    param.result = null
                }
                val mFooter = getObjectField(parent, "mFooter") as View?

                // Footer has been passed, let's add the brightness slider before it
                if (child == mFooter) {

                    val mBrightnessView = getObjectField(parent, "mBrightnessView") as View?

                    if (mBrightnessView != null) {

                        val currentParent = mBrightnessView.parent as ViewGroup
                        if (currentParent !== parent) {
                            currentParent.removeView(mBrightnessView)
                            parent!!.addView(mBrightnessView, index)
                            return
                        }
                        // Same parent, we are just changing indices
                        val currentIndex = parent.indexOfChild(mBrightnessView)
                        if (currentIndex == index) {
                            // We want to be in the same place. Nothing to do here
                            return
                        }
                        parent.removeView(mBrightnessView)
                        parent.addView(mBrightnessView, index)

                    }
                }
            }
        }

        private val updateQsPanelControllerListeningHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mListening = getBooleanField(param.thisObject, "mListening")
                val mBackgroundHandler =
                    getObjectField(mQQsBrightnessController, "mBackgroundHandler")
                            as Handler
                if (mListening) {
                    val mStartListeningRunnable =
                        getObjectField(mQQsBrightnessController, "mStartListeningRunnable")

                    mBackgroundHandler.post(mStartListeningRunnable as Runnable)
                    return
                }
                val mStopListeningRunnable =
                    getObjectField(mQQsBrightnessController, "mStopListeningRunnable")

                mBackgroundHandler.post(mStopListeningRunnable as Runnable)
                setBooleanField(mQQsBrightnessController, "mControlValueInitialized", false)
            }
        }

        private val getOrCreateTileLayoutHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val mQQSSideLabelTileLayout = param.result

                val mContext = getObjectField(param.thisObject, "mContext") as Context

                callMethod(
                    mQQSSideLabelTileLayout,
                    "setMaxColumns",
                    getQsColumnCount(mContext, "QQS")
                )
            }
        }

        private val switchTileLayoutHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val horizontal = callMethod(param.thisObject, "shouldUseHorizontalLayout")
                        as Boolean

                val mView = getObjectField(param.thisObject, "mView") as View

                val mTileLayout = getObjectField(mView, "mTileLayout") as View

                callMethod(mTileLayout, "setMinRows", if (horizontal) 2 else 1)

                if (mView.javaClass.name.equals(QUICK_QS_PANEL_CLASS)) {

                    callMethod(
                        mTileLayout,
                        "setMaxColumns",
                        if (horizontal) 2 else getQsColumnCount(mView.context, "QQS")
                    )

                } else if (mView.javaClass.name.equals(QS_PANEL_CLASS)) {

                    callMethod(
                        mTileLayout,
                        "setMaxColumns",
                        if (horizontal) 2 else getQsColumnCount(mView.context, "QS")
                    )
                }

                val mUsingHorizontalLayoutChangedListener =
                    getObjectField(param.thisObject, "mUsingHorizontalLayoutChangedListener")
                            as Runnable?

                mUsingHorizontalLayoutChangedListener?.run()

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

        fun setBrightnessView(parentView: ViewGroup, view: View) {
            val mBrightnessView = getObjectField(parentView, "mBrightnessView") as ViewGroup?
            var mMovableContentStartIndex = getIntField(parentView, "mMovableContentStartIndex")

            if (mBrightnessView != null) {
                parentView.removeView(mBrightnessView)
                mMovableContentStartIndex--
                setIntField(parentView, "mMovableContentStartIndex", mMovableContentStartIndex)
            }

            setObjectField(parentView, "mBrightnessView", view)
            callMethod(parentView, "setBrightnessViewMargin")

            when (mQsBrightnessSliderPositionConfig) {
                0 -> {
                    parentView.addView(view, 0)
                }

                1 -> {
                    parentView.addView(view, 1)
                }
                2 -> {
                    parentView.removeView(view)
                    return
                }
            }

            mMovableContentStartIndex++
            setIntField(parentView, "mMovableContentStartIndex", mMovableContentStartIndex)

            setBrighnessSliderMargins(parentView)

            //Check whether it is QQS slider or QS
            if (parentView.javaClass.name == "com.android.systemui.qs.QuickQSPanel") {
                val mBrightnessView = getObjectField(parentView, "mBrightnessView")
                        as View
                if (!mQQsBrightnessSliderEnabled) {
                    mBrightnessView.visibility=View.GONE

                } else {
                    mBrightnessView.visibility=View.VISIBLE
                }
                callMethod(mQQsBrightnessMirrorHandler, "updateBrightnessMirror")
            } else {
                callMethod(mBrightnessMirrorHandler, "updateBrightnessMirror")
            }
        }

        @SuppressLint("DiscouragedApi")
        fun setBrighnessSliderMargins(parentView: View) {
            val mContext = getObjectField(parentView, "mContext") as Context
            val mBrightnessView = getObjectField(parentView, "mBrightnessView")
                    as View?
            if (mBrightnessView != null) {
                val top: Int = mContext.resources.getDimensionPixelSize(
                    mContext.resources.getIdentifier(
                        "qs_brightness_margin_top",
                        "dimen",
                        "com.android.systemui"
                    )
                )
                val bottom: Int = mContext.resources.getDimensionPixelSize(
                    mContext.resources.getIdentifier(
                        "qs_brightness_margin_bottom",
                        "dimen",
                        "com.android.systemui"
                    )
                )

                val lp = mBrightnessView.layoutParams as MarginLayoutParams

                when (mQsBrightnessSliderPositionConfig) {
                    0 -> {
                        //Slightly alter the padding for when using modified QS style so it sits more central
                        when (mQsStyleConfig) {
                            0 -> {
                                lp.topMargin =  top
                                lp.bottomMargin = bottom
                            }
                            else -> {
                                lp.topMargin =
                                    if (parentView.javaClass.name == "com.android.systemui.qs.QuickQSPanel")
                                        top * 3 else top
                                lp.bottomMargin =
                                    if (parentView.javaClass.name == "com.android.systemui.qs.QuickQSPanel")
                                        0 else bottom
                            }
                        }
                    }
                    1 -> {
                        lp.topMargin = bottom
                        lp.bottomMargin = top
                    }
                }
                mBrightnessView.layoutParams = lp
            }
        }

        fun getQsColumnCount(mContext: Context, mView: String): Int {

            val configuration = mContext.resources.configuration

            if (mView == "QS") {

                return if (configuration.orientation == ORIENTATION_PORTRAIT) mQsColumnsConfig else mQsColumnsConfigLandscape

            } else if (mView == "QQS") {

                return if (configuration.orientation == ORIENTATION_PORTRAIT) mQQsColumnsConfig else mQQsColumnsConfigLandscape
            }

            return 2
        }

        fun getQsRowCount(mContext: Context, mView: String): Int {

            val configuration = mContext.resources.configuration

            if (mView == "QS") {

                return if (configuration.orientation == ORIENTATION_PORTRAIT) mQsRowsConfig else 2

            } else if (mView == "QQS") {

                return if (configuration.orientation == ORIENTATION_PORTRAIT) mQQsRowsConfig else 1
            }

            return 4
        }

    }
}