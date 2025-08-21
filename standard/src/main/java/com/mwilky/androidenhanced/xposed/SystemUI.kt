package com.mwilky.androidenhanced.xposed

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.provider.Settings
import android.service.quicksettings.Tile
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.ArraySet
import android.util.Log
import android.util.SparseArray
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.animation.addListener
import androidx.core.view.get
import com.mwilky.androidenhanced.HookedClasses.Companion.BATTERY_METER_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.BATTERY_METER_VIEW_CONTROLLER_TUNABLE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.BRIGHTNESS_MIRROR_HANDLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.BRIGHTNESS_SLIDER_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.BRIGHTNESS_UTILS_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CENTRAL_SURFACES_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CLOCK_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.COLLAPSED_STATUSBAR_FRAGMENT_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.COLORKT_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.COMPOSER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CONTINUATION_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CURRENT_TILES_INTERACT0R_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.DARK_ICON_DISPATCHER_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.EXPANDABLE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.FOOTER_ACTIONS_BUTTON_VIEW_MODEL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.FOOTER_ACTIONS_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.FOOTER_ACTIONS_VIEW_MODEL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.GLOBAL_ACTIONS_DIALOG_LITE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.HEADS_UP_APPEARANCE_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.HEADS_UP_APPEARANCE_CONTROLLER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.HEADS_UP_STATUSBAR_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.HEIGHT_EXPANSION_ANIMATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.INTERPOLATORS_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_COORDINATOR_ATTACH_1_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_COORDINATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_STATUSBAR_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.MODERN_SHADE_CARRIER_GROUP_MOBILE_VEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.MODIFIER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_ENTRY_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_SHADE_WINDOW_CONTROLLER_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_SHADE_WINDOW_STATE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_STACK_SCROLL_LAYOUT_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIF_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NUM_PAD_KEY_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PATH_INTERPOLATOR_BUILDER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PINNED_STATUS_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_ANIMATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_CUSTOMIZER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_CUSTOMIZER_CONTROLLER_3_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_CUSTOMIZER_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_FOOTER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_FOOTER_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_ICON_VIEW_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_PANEL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_PANEL_CONTROLLER_BASE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_PANEL_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_TILE_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_TILE_IMPL_H_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_TILE_VIEW_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QUICK_QS_PANEL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QUICK_QS_PANEL_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QUICK_SETTINGS_CONTROLLER_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.RECYCLER_VIEW_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.RECYCLER_VIEW_VIEW_HOLDER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.ROW_APPEARANCE_COORDINATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SCRIM_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SHADE_HEADER_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SIDE_LABEL_TILE_LAYOUT_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.STACK_COORDINATOR_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.STATUSBAR_ICON_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSTEM_UI_APPLICATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.TILE_ADAPTER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.TILE_ADAPTER_TILE_DECORATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.TINTED_ICON_MANAGER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.TOUCH_ANIMATOR_BUILDER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.UPDATE_PARENT_CLIPPING_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.USER_TILE_SPEC_REPOSITORY_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.VIEW_CLIPPING_UTIL_CLASS
import com.mwilky.androidenhanced.References.Companion.BatteryMeterView
import com.mwilky.androidenhanced.References.Companion.BrightnessControllerFactory
import com.mwilky.androidenhanced.References.Companion.BrightnessSliderControllerFactory
import com.mwilky.androidenhanced.References.Companion.CentralSurfacesImpl
import com.mwilky.androidenhanced.References.Companion.Clock
import com.mwilky.androidenhanced.References.Companion.CollapsedStatusBarFragment
import com.mwilky.androidenhanced.References.Companion.CurrentTilesInteractorImpl
import com.mwilky.androidenhanced.References.Companion.DarkIconDispatcherImpl
import com.mwilky.androidenhanced.References.Companion.HeightExpansionAnimator
import com.mwilky.androidenhanced.References.Companion.Interpolators
import com.mwilky.androidenhanced.References.Companion.KeyguardCoordinator
import com.mwilky.androidenhanced.References.Companion.KeyguardStatusBarView
import com.mwilky.androidenhanced.References.Companion.ModernShadeCarrierGroupMobileView
import com.mwilky.androidenhanced.References.Companion.NotifCollection
import com.mwilky.androidenhanced.References.Companion.NotifStats
import com.mwilky.androidenhanced.References.Companion.NotificationPanelView
import com.mwilky.androidenhanced.References.Companion.NotificationPanelViewController
import com.mwilky.androidenhanced.References.Companion.PathInterpolatorBuilderPathInterpolator
import com.mwilky.androidenhanced.References.Companion.PhoneStatusBarView
import com.mwilky.androidenhanced.References.Companion.PhoneStatusBarViewController
import com.mwilky.androidenhanced.References.Companion.QSAnimator
import com.mwilky.androidenhanced.References.Companion.QSCustomizerController3
import com.mwilky.androidenhanced.References.Companion.QSFooterView
import com.mwilky.androidenhanced.References.Companion.QSPanelController
import com.mwilky.androidenhanced.References.Companion.QSTileViewImpl
import com.mwilky.androidenhanced.References.Companion.QuickQSPanelController
import com.mwilky.androidenhanced.References.Companion.QuickQSPanelQQSSideLabelTileLayout
import com.mwilky.androidenhanced.References.Companion.RowAppearanceCoordinator
import com.mwilky.androidenhanced.References.Companion.ShadeController
import com.mwilky.androidenhanced.References.Companion.ShadeHeaderController
import com.mwilky.androidenhanced.References.Companion.SystemUIContext
import com.mwilky.androidenhanced.References.Companion.TileList
import com.mwilky.androidenhanced.References.Companion.TintedIconManager
import com.mwilky.androidenhanced.References.Companion.TouchAnimatorBuilder
import com.mwilky.androidenhanced.References.Companion.mBrightnessMirrorHandler
import com.mwilky.androidenhanced.References.Companion.mDefaultClockContainer
import com.mwilky.androidenhanced.References.Companion.mQQsBrightnessController
import com.mwilky.androidenhanced.References.Companion.mQQsBrightnessMirrorHandler
import com.mwilky.androidenhanced.References.Companion.mQQsBrightnessSliderController
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.QSCellMarginHorizontal
import com.mwilky.androidenhanced.Utils.Companion.QSCellMarginVertical
import com.mwilky.androidenhanced.Utils.Companion.QSTileHeight
import com.mwilky.androidenhanced.Utils.Companion.animateBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.applyAlpha
import com.mwilky.androidenhanced.Utils.Companion.getColorAttrDefaultColor
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.isDarkMode
import com.mwilky.androidenhanced.Utils.Companion.mDisableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.mDisableLockscreenQuicksettings
import com.mwilky.androidenhanced.Utils.Companion.mDoubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.mDualColorQsPanel
import com.mwilky.androidenhanced.Utils.Companion.mExpandAllNotifcations
import com.mwilky.androidenhanced.Utils.Companion.mExpandFirstNotification
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedAlarm
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedCallStrength
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedVolume
import com.mwilky.androidenhanced.Utils.Companion.mHideLockscreenStatusbar
import com.mwilky.androidenhanced.Utils.Companion.mHideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.mKeyguardShowing
import com.mwilky.androidenhanced.Utils.Companion.mNotificationScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.mNotificationSectionHeaders
import com.mwilky.androidenhanced.Utils.Companion.mQQsColumnsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQQsColumnsConfigLandscape
import com.mwilky.androidenhanced.Utils.Companion.mQQsRowsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.mQsClickVibration
import com.mwilky.androidenhanced.Utils.Companion.mQsColumnsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsColumnsConfigLandscape
import com.mwilky.androidenhanced.Utils.Companion.mQsRowsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.mQsStyleConfig
import com.mwilky.androidenhanced.Utils.Companion.mQuickPulldownConfig
import com.mwilky.androidenhanced.Utils.Companion.mScrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.mSmartPulldownConfig
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import com.mwilky.androidenhanced.Utils.Companion.safeHookAllConstructors
import com.mwilky.androidenhanced.Utils.Companion.safeHookMethod
import com.mwilky.androidenhanced.Utils.Companion.sendLogBroadcast
import com.mwilky.androidenhanced.Utils.Companion.setBrighnessSliderMargins
import com.mwilky.androidenhanced.Utils.Companion.setBrightnessView
import com.mwilky.androidenhanced.Utils.Companion.setStatusbarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.updateStatusbarIconColors
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import com.mwilky.androidenhanced.xposed.BroadcastReceiver.Companion.registerBroadcastReceiver
import com.mwilky.androidenhanced.xposed.premium.SystemUI.Companion.qsStyleHooks
import com.mwilky.androidenhanced.xposed.premium.SystemUI.Companion.statusbarIconColorHooks
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getFloatField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.newInstance
import de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField
import de.robv.android.xposed.XposedHelpers.setAdditionalStaticField
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setIntField
import de.robv.android.xposed.XposedHelpers.setObjectField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.nextDown
import kotlin.math.roundToInt

class SystemUI {

    companion object {

        private lateinit var doubleTapGesture: GestureDetector

        // Statusbar brightness control
        private var minimumBacklight: Float = 0f
        private var maximumBacklight: Float = 0f
        private var quickQsOffsetHeight: Int = 0
        private var displayId: Int = 0
        private var brightnessChanged = false
        private var linger = 0
        private var justPeeked = false
        private var initialTouchX = 0
        private var initialTouchY = 0
        private var currentBrightness = 0f
        private const val BRIGHTNESS_CONTROL_PADDING = 0.15f
        private const val BRIGHTNESS_CONTROL_LONG_PRESS_TIMEOUT = 750
        private const val BRIGHTNESS_CONTROL_LINGER_THRESHOLD = 20

        // Scramble Keypad
        private var keypadNumbers: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

        // Notification count
        private var numActiveNotifs: Int = 0

        // Disable QS on lockscreen
        private const val DISABLE2_QUICK_SETTINGS: Int = 0x00000001

        fun init(classLoader: ClassLoader) {

            // Register broadcast receivers
            findAndHookMethod(
                SYSTEM_UI_APPLICATION_CLASS, classLoader, "onCreate", object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        // Set SystemUIContext object
                        SystemUIContext = param.thisObject as Context

                        setAdditionalStaticField(
                            findClass(SYSTEM_UI_APPLICATION_CLASS, classLoader),
                            "mSentAllBootPrefs",
                            false
                        )

                        sendLogBroadcast(
                            SystemUIContext,
                            "Hook Success",
                            "${SystemUIContext.packageName} hooked successfully!",
                            LogEntryType.HOOKS
                        )

                        // Register receivers
                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.statusBarClockPosition,
                            param.thisObject.toString(),
                            0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.statusBarClockSeconds,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.doubleTapToSleep,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.statusbarBrightnessControl,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.gestureSleep, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.expandAllNotifications,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.autoExpandFirstNotif,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.notifSectionHeaders,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.notifScrimAlpha,
                            param.thisObject.toString(),
                            1.0f
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qsScrimAlpha, param.thisObject.toString(), 1.0f
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.hideLockscreenStatusBar,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.scrambleKeypad,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.disableLockscreenPowerMenu,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.disableQsLockscreen,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsClickVibration,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.hideQsFooterBuildNumber,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.smartPulldown, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.quickPulldown, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsBrightnessSliderPosition,
                            param.thisObject.toString(),
                            0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qqsBrightnessSlider,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qqsRows, param.thisObject.toString(), 2
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qqsColumns, param.thisObject.toString(), 2
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qqsColumnsLandscape,
                            param.thisObject.toString(),
                            4
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qsColumns, param.thisObject.toString(), 2
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsColumnsLandscape,
                            param.thisObject.toString(),
                            4
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qsRows, param.thisObject.toString(), 4
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.dualToneQsPanel,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.qsStyle, param.thisObject.toString(), 0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsIconContainerActiveShape,
                            param.thisObject.toString(),
                            0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsIconContainerInactiveShape,
                            param.thisObject.toString(),
                            0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsIconContainerUnavailableShape,
                            param.thisObject.toString(),
                            0
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.statusbarIconDarkColor,
                            param.thisObject.toString(),
                            -1728053248
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.useDualStatusbarColors,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.statusbarIconAccentColor,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.qsStatusbarIconAccentColor,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.lsStatusbarIconAccentColor,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarClockColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarBatteryIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarBatteryPercentColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarWifiIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarMobileIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarNotificationIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarOtherIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarDndIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarAirplaneIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarHotspotIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarBluetoothIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarClockColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarBatteryIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarBatteryPercentColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarWifiIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarMobileIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )
                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarCarrierColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarDateColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarOtherIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarDndIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarAirplaneIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarHotspotIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarBluetoothIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarBatteryIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarBatteryPercentColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarWifiIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarMobileIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarOtherIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarDndIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarAirplaneIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarHotspotIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarBluetoothIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarCarrierColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customStatusbarGlobalIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customQsStatusbarGlobalIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.customLsStatusbarGlobalIconColor,
                            param.thisObject.toString(),
                            Color.WHITE
                        )

                        registerBroadcastReceiver(
                            SystemUIContext, Utils.iconBlacklist, param.thisObject.toString(), ""
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.BOOTCOMPLETED,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.hideCollapsedAlarmIcon,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.hideCollapsedVolumeIcon,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            SystemUIContext,
                            Utils.hideCollapsedCallStrengthIcon,
                            param.thisObject.toString(),
                            true
                        )

                        /**
                         * These functions are purely for setting references, no tweaks logic is used here.
                         */
                        safeHookMethod(
                            context = SystemUIContext,
                            SHADE_HEADER_CONTROLLER_CLASS,
                            classLoader,
                            "onInit",
                            afterHook = { param ->
                                ShadeHeaderController = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS,
                            classLoader,
                            afterHook = { param ->
                                val view = getObjectField(param.thisObject, "mView") as View
                                PhoneStatusBarViewController = param.thisObject
                                PhoneStatusBarView = view
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            ROW_APPEARANCE_COORDINATOR_CLASS,
                            classLoader,
                            afterHook = { param ->
                                RowAppearanceCoordinator = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            NOTIFICATION_STACK_SCROLL_LAYOUT_CONTROLLER_CLASS,
                            classLoader,
                            afterHook = { param ->
                                NotifCollection =
                                    getObjectField(param.thisObject, "mNotifCollection")
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            KEYGUARD_COORDINATOR_CLASS,
                            classLoader,
                            afterHook = { param ->
                                KeyguardCoordinator = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            KEYGUARD_STATUSBAR_VIEW_CLASS,
                            classLoader,
                            "onFinishInflate",
                            afterHook = { param ->
                                KeyguardStatusBarView = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            QS_PANEL_CONTROLLER_CLASS,
                            classLoader,
                            "onViewAttached",
                            afterHook = { param ->
                                QSPanelController = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            QS_ANIMATOR_CLASS,
                            classLoader,
                            afterHook = { param ->
                                QSAnimator = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS,
                            classLoader,
                            "updateResources",
                            afterHook = { param ->
                                QuickQSPanelQQSSideLabelTileLayout = param.thisObject
                            })

                        @Suppress("UNCHECKED_CAST")
                        safeHookMethod(
                            context = SystemUIContext,
                            USER_TILE_SPEC_REPOSITORY_CLASS,
                            classLoader,
                            "loadTilesFromSettings",
                            Int::class.javaPrimitiveType,
                            findClass(CONTINUATION_IMPL_CLASS, classLoader),
                            afterHook = { param ->
                                TileList = param.result as MutableList<Any>
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            CURRENT_TILES_INTERACT0R_IMPL_CLASS,
                            classLoader,
                            afterHook = { param ->
                                CurrentTilesInteractorImpl = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            QS_TILE_VIEW_IMPL_CLASS,
                            classLoader,
                            afterHook = { param ->
                                QSTileViewImpl = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            DARK_ICON_DISPATCHER_IMPL_CLASS,
                            classLoader,
                            afterHook = { param ->
                                DarkIconDispatcherImpl = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            KEYGUARD_STATUSBAR_VIEW_CLASS,
                            classLoader,
                            "onThemeChanged",
                            findClass(TINTED_ICON_MANAGER_CLASS, classLoader),
                            beforeHook = { param ->
                                TintedIconManager = param.args[0]
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            MODERN_SHADE_CARRIER_GROUP_MOBILE_VEW_CLASS,
                            classLoader,
                            afterHook = { param ->
                                ModernShadeCarrierGroupMobileView = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            BATTERY_METER_VIEW_CLASS,
                            classLoader,
                            "onDarkChanged",
                            ArrayList::class.java,
                            Float::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            beforeHook = { param ->
                                BatteryMeterView = param.thisObject
                            })

                        safeHookAllConstructors(
                            context = SystemUIContext,
                            NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS,
                            classLoader,
                            afterHook = { param ->
                                val view = getObjectField(param.thisObject, "mView") as View
                                NotificationPanelViewController = param.thisObject
                                NotificationPanelView = view
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            CLOCK_CLASS,
                            classLoader,
                            "onAttachedToWindow",
                            afterHook = { param ->
                                Clock = param.thisObject as View
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            QS_FOOTER_VIEW_CLASS,
                            classLoader,
                            "setBuildText",
                            afterHook = { param ->
                                QSFooterView = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            STACK_COORDINATOR_CLASS,
                            classLoader,
                            "calculateNotifStats",
                            List::class.java,
                            afterHook = { param ->
                                NotifStats = param.result
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            QS_CUSTOMIZER_CONTROLLER_CLASS,
                            classLoader,
                            "onViewAttached",
                            afterHook = { param ->
                                QSCustomizerController3 =
                                    getObjectField(param.thisObject, "mConfigurationListener")
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            QUICK_QS_PANEL_CONTROLLER_CLASS,
                            classLoader,
                            "onInit",
                            afterHook = { param ->
                                QuickQSPanelController = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                            classLoader,
                            "onViewCreated",
                            View::class.java,
                            Bundle::class.java,
                            beforeHook = { param ->
                                CollapsedStatusBarFragment = param.thisObject
                            })

                        safeHookMethod(
                            context = SystemUIContext,
                            CENTRAL_SURFACES_IMPL_CLASS,
                            classLoader,
                            "start",
                            afterHook = { param ->
                                CentralSurfacesImpl = param.thisObject
                                ShadeController =
                                    getObjectField(param.thisObject, "mShadeController")
                            })

                        TouchAnimatorBuilder = findClass(TOUCH_ANIMATOR_BUILDER_CLASS, classLoader)
                        Interpolators = findClass(INTERPOLATORS_CLASS, classLoader)
                        PathInterpolatorBuilderPathInterpolator =
                            findClass(PATH_INTERPOLATOR_BUILDER_CLASS, classLoader)
                        HeightExpansionAnimator =
                            findClass(HEIGHT_EXPANSION_ANIMATOR_CLASS, classLoader)

                        // Hooks
                        clockPositionHooks(classLoader)
                        clockSecondsHooks(classLoader)
                        doubleTapToSleepHooks(classLoader)
                        statusbarBrightnessControlHooks(classLoader)
                        autoExpandFirstNotificationHooks(classLoader)
                        notificationSectionHeadersHooks(classLoader)
                        scrimAlphaHooks(classLoader)
                        hideLockscreenStatusbarHooks(classLoader)
                        scrambleKeypadHooks(classLoader)
                        blockLockscreenPowerMenuHooks(classLoader)
                        disableLockscreenQuicksettingsHooks(classLoader)
                        qsClickVibrationHooks(classLoader)
                        hideQsFooterBuildNumberHooks(classLoader)
                        qsPullDownHooks(classLoader)
                        qsBrightnessSliderHooks(classLoader)
                        qsTileLayoutHooks(classLoader)
                        dualToneQsPanelHooks(classLoader)
                        statusbarIconColorHooks(classLoader)
                        qsStyleHooks(classLoader)
                        hideCollapsedStatusbarIconHooks(classLoader)
                        hideStatusbarIconHooks(classLoader)
                    }
                })
        }

        /**
         * Hook functions
         */
        fun doubleTapToSleepHooks(classLoader: ClassLoader) {
            safeHookAllConstructors(
                context = SystemUIContext,
                NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS,
                classLoader,
                afterHook = { param ->
                    val view = getObjectField(param.thisObject, "mView") as View
                    val context = view.context as Context
                    doubleTapGesture = GestureDetector(context, object : SimpleOnGestureListener() {
                        override fun onDoubleTap(event: MotionEvent): Boolean {
                            val powerManager =
                                context.getSystemService(Context.POWER_SERVICE) as PowerManager
                            callMethod(powerManager, "goToSleep", event.eventTime)
                            return true
                        }
                    })
                })

            safeHookMethod(
                context = SystemUIContext,
                NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
                classLoader,
                "onTouch",
                View::class.java,
                MotionEvent::class.java,
                afterHook = { param ->
                    val event = param.args[1] as MotionEvent
                    if (mDoubleTapToSleep) {
                        doubleTapGesture.onTouchEvent(
                            event
                        )
                    }
                })

            safeHookMethod(
                context = SystemUIContext,
                PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS,
                classLoader,
                "onTouch",
                MotionEvent::class.java,
                afterHook = { param ->
                    val event = param.args[0] as MotionEvent
                    if (mDoubleTapToSleep) {
                        doubleTapGesture.onTouchEvent(event)
                    }
                })
        }

        fun clockSecondsHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                CLOCK_CLASS,
                classLoader,
                "updateShowSeconds",
                beforeHook = { param ->
                    setBooleanField(
                        param.thisObject, "mShowSeconds", mStatusbarClockSeconds
                    )
                })
        }

        fun autoExpandFirstNotificationHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS,
                classLoader,
                "onAfterRenderEntry",
                findClass(NOTIFICATION_ENTRY_CLASS, classLoader),
                findClass(NOTIF_VIEW_CONTROLLER_CLASS, classLoader),
                beforeHook = { param ->
                    setBooleanField(
                        RowAppearanceCoordinator,
                        "mAutoExpandFirstNotification",
                        mExpandFirstNotification
                    )
                })
        }

        fun notificationSectionHeadersHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                KEYGUARD_COORDINATOR_ATTACH_1_CLASS,
                classLoader,
                "accept",
                Object::class.java,
                afterHook = { param ->
                    updateNotificationSectionHeaders()
                })
        }

        fun hideLockscreenStatusbarHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "updateVisibilities",
                afterHook = { param ->
                    if (mHideLockscreenStatusbar) callMethod(
                        param.thisObject, "setVisibility", GONE
                    )
                })
        }

        fun scrambleKeypadHooks(classLoader: ClassLoader) {
            safeHookAllConstructors(
                context = SystemUIContext, NUM_PAD_KEY_CLASS, classLoader, afterHook = { param ->
                    if (mScrambleKeypad) {
                        val mDigitText = getObjectField(param.thisObject, "mDigitText")
                        val mDigit = getIntField(param.thisObject, "mDigit")
                        setObjectField(param.thisObject, "mDigit", keypadNumbers[mDigit])
                        callMethod(mDigitText, "setText", keypadNumbers[mDigit].toString())
                    }
                })

            safeHookMethod(
                context = SystemUIContext,
                KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                afterHook = { param ->
                    if (mScrambleKeypad) keypadNumbers.shuffle()
                })


        }

        fun blockLockscreenPowerMenuHooks(classLoader: ClassLoader) {
            /**
             * Gives us realtime value of whether keyguard is showing or not
             */
            safeHookMethod(
                context = SystemUIContext,
                NOTIFICATION_SHADE_WINDOW_CONTROLLER_IMPL_CLASS,
                classLoader,
                "apply",
                findClass(NOTIFICATION_SHADE_WINDOW_STATE_CLASS, classLoader),
                afterHook = { param ->
                    val keyguardShowing = getBooleanField(param.args[0], "keyguardShowing")
                    if (mKeyguardShowing != keyguardShowing) mKeyguardShowing = keyguardShowing

                })

            safeHookMethod(
                context = SystemUIContext,
                GLOBAL_ACTIONS_DIALOG_LITE_CLASS,
                classLoader,
                methodName = "showOrHideDialog",
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                findClass(EXPANDABLE_CLASS, classLoader),
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val expandable = param.args[2]

                    /**
                     * Expandable is provided when launched from qs footer so we only block this,
                     * otherwise we block hardware power button as well.
                     * Also check whether the device is locked, so we can still launch from qs
                     * footer when unlocked
                     */
                    if (expandable != null && mDisableLockscreenPowerMenu && mKeyguardShowing) {
                        param.result = null
                    }
                })
        }

        @SuppressLint("MissingPermission")
        fun qsClickVibrationHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                QS_TILE_IMPL_H_CLASS,
                classLoader,
                "handleMessage",
                Message::class.java,
                beforeHook = { param ->
                    if (mQsClickVibration) {
                        if (getIntField(param.args[0], "what") == 2) {
                            val mState =
                                getObjectField(getSurroundingThis(param.thisObject), "mState")
                            if (!getBooleanField(mState, "disabledByPolicy")) {
                                val mContext = getObjectField(
                                    getSurroundingThis(param.thisObject), "mContext"
                                ) as Context
                                initVibrator(mContext)
                                val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                                mVibrator.vibrate(vibrationEffect)
                            }
                        }
                    }
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_TILE_IMPL_CLASS,
                classLoader,
                "handleLongClick",
                findClass(EXPANDABLE_CLASS, classLoader),
                afterHook = { param ->
                    if (mQsClickVibration) {
                        val mContext = getObjectField(param.thisObject, "mContext") as Context
                        initVibrator(mContext)
                        val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                        mVibrator.vibrate(vibrationEffect)
                    }
                })
        }

        fun hideQsFooterBuildNumberHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                QS_FOOTER_VIEW_CLASS,
                classLoader,
                "setBuildText",
                afterHook = { param ->
                    val mBuildText = getObjectField(param.thisObject, "mBuildText") as TextView
                    if (mHideQsFooterBuildNumber) {
                        mBuildText.text = null
                        setBooleanField(
                            param.thisObject, "mShouldShowBuildText", false
                        )
                        mBuildText.isSelected = false
                    }
                })
        }

        fun dualToneQsPanelHooks(classLoader: ClassLoader) {
            /**
             * This sets the customizer background and toolbar colors
             */
            safeHookMethod(
                context = SystemUIContext,
                BRIGHTNESS_SLIDER_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                afterHook = { param ->
                    setBrightnessSliderColorsAfterInflate((param.thisObject as View))
                })

            /**
             * This sets the customizer background and toolbar colors
             */
            safeHookMethod(
                context = SystemUIContext,
                QS_CUSTOMIZER_CLASS,
                classLoader,
                "onConfigurationChanged",
                Configuration::class.java,
                afterHook = { param ->
                    setCustomizerColors(param.thisObject)
                })

            /**
             * This sets the customizer background and toolbar colors
             */
            safeHookAllConstructors(
                context = SystemUIContext, QS_CUSTOMIZER_CLASS, classLoader, afterHook = { param ->
                    setCustomizerColors(param.thisObject)
                })

            /**
             * This is the header text in the customizer and fixes the misaligned tiles with mQsStyle != 0
             */
            safeHookMethod(
                context = SystemUIContext,
                TILE_ADAPTER_CLASS,
                classLoader,
                "onBindViewHolder",
                findClass(RECYCLER_VIEW_VIEW_HOLDER_CLASS, classLoader),
                Int::class.javaPrimitiveType,
                afterHook = { param ->
                    val holder = param.args[0]
                    val itemView = getObjectField(holder, "itemView") as View
                    val itemViewType = getIntField(holder, "mItemViewType")

                    val resolvedOnSurfaceColor = itemView.context.getColor(
                        if (isDarkMode(itemView.context)) R.color.system_on_surface_dark else R.color.system_on_surface_light
                    )

                    val resolvedOnSurfaceVariantColor = itemView.context.getColor(
                        if (isDarkMode(itemView.context)) R.color.system_on_surface_variant_dark else R.color.system_on_surface_variant_light
                    )

                    // Edit type (unused tile view)
                    if (itemViewType == 1) {
                        (itemView).findViewById<TextView>(R.id.title)
                            ?.setTextColor(resolvedOnSurfaceVariantColor)
                    }
                    // Header type (current tiles view)
                    if (itemViewType == 3) {
                        (itemView).findViewById<TextView>(R.id.title)
                            ?.setTextColor(resolvedOnSurfaceColor)
                    }

                    // Fix misaligned tiles if mQsStyle != 0
                    val mTileView = getObjectField(holder, "mTileView")

                    if (mTileView != null) {
                        val secondaryLabel = getObjectField(mTileView, "secondaryLabel") as TextView

                        if (secondaryLabel.visibility != VISIBLE && mQsStyleConfig != 0) secondaryLabel.visibility =
                            INVISIBLE
                    }
                })

            /**
             * This handles the background behind the tiles not in use on the customizer
             */
            safeHookMethod(
                context = SystemUIContext,
                TILE_ADAPTER_TILE_DECORATION_CLASS,
                classLoader,
                "onDraw",
                Canvas::class.java,
                findClass(RECYCLER_VIEW_VIEW_CLASS, classLoader),
                beforeHook = { param ->
                    if (!mDualColorQsPanel) return@safeHookMethod

                    val mDrawable =
                        getObjectField(param.thisObject, "mDrawable") as GradientDrawable

                    val resolvedColor = SystemUIContext.getColor(
                        if (isDarkMode(SystemUIContext)) R.color.system_surface_container_high_dark
                        else R.color.system_surface_dim_light
                    )

                    mDrawable.setColor(resolvedColor)
                })

            /**
             * This sets the background of the QS footer to transparent
             */
            safeHookMethod(
                context = SystemUIContext,
                QS_IMPL_CLASS,
                classLoader,
                "setQsExpansion",
                Float::class.javaPrimitiveType,
                Float::class.javaPrimitiveType,
                Float::class.javaPrimitiveType,
                Float::class.javaPrimitiveType,
                afterHook = { param ->
                    if (mDualColorQsPanel) {
                        val mQSFooterActionsViewModel =
                            getObjectField(param.thisObject, "mQSFooterActionsViewModel")

                        if (mQSFooterActionsViewModel != null) {
                            val backgroundAlpha =
                                getObjectField(mQSFooterActionsViewModel, "_backgroundAlpha")

                            callMethod(backgroundAlpha, "setValue", 0f)
                        }
                    }
                })

            /**
             * This handles the settings button icon color in the QS footer
             */
            safeHookAllConstructors(
                context = SystemUIContext,
                FOOTER_ACTIONS_VIEW_MODEL_CLASS,
                classLoader,
                afterHook = { param ->
                    val settingsIconColor = SystemUIContext.getColor(
                        if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) R.color.system_on_surface_light
                        else R.color.system_on_surface_dark
                    )

                    val settingsButton = getObjectField(param.thisObject, "settings")
                    setObjectField(settingsButton, "iconTint", settingsIconColor)
                })


            /**
             * This is the power button icon color in the QS footer
             */
            safeHookMethod(
                context = SystemUIContext,
                FOOTER_ACTIONS_CLASS,
                classLoader,
                "IconButton",
                findClass(FOOTER_ACTIONS_BUTTON_VIEW_MODEL_CLASS, classLoader),
                Boolean::class.javaPrimitiveType,
                findClass(MODIFIER_CLASS, classLoader),
                findClass(COMPOSER_CLASS, classLoader),
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val model = param.args[0]
                    val id = getIntField(model, "id")

                    if (id == SystemUIContext.resources.getIdentifier(
                            "pm_lite", "id", SystemUIContext.packageName
                        )
                    ) {
                        val powerIconColor =
                            if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) SystemUIContext.getColor(
                                R.color.system_accent1_50
                            )
                            else getColorAttrDefaultColor(
                                SystemUIContext, SystemUIContext.resources.getIdentifier(
                                    "textColorOnAccent", "attr", "android"
                                )
                            )
                        setObjectField(model, "iconTint", powerIconColor)
                    }
                })

            /**
             * This overrides certain colors used in composables, this is for the dual color qs panel tweak
             */
            safeHookMethod(
                context = SystemUIContext,
                COLORKT_CLASS,
                classLoader,
                "colorAttr",
                Int::class.javaPrimitiveType,
                findClass(COMPOSER_CLASS, classLoader),
                beforeHook = { param ->
                    val attrId = param.args[0] as Int
                    var overriddenColor = Color.WHITE

                    overriddenColor = when (attrId) {

                        // This is the settings icon button background +
                        // the border of the foreground services composable
                        SystemUIContext.resources.getIdentifier(
                            "shadeInactive", "attr", SystemUIContext.packageName
                        ) -> {
                            if (mDualColorQsPanel) {
                                if (isDarkMode(SystemUIContext)) {
                                    SystemUIContext.getColor(R.color.system_neutral1_800)
                                } else {
                                    SystemUIContext.getColor(R.color.system_accent1_50)
                                }
                            } else {
                                overriddenColor
                            }
                        }
                        // This is the background of the foreground services button
                        SystemUIContext.resources.getIdentifier(
                            "underSurface", "attr", SystemUIContext.packageName
                        ) -> {
                            if (mDualColorQsPanel) {

                                if (isDarkMode(SystemUIContext)) {
                                    SystemUIContext.getColor(R.color.system_neutral1_900)
                                } else {
                                    SystemUIContext.getColor(R.color.system_neutral1_100)
                                }
                            } else {
                                overriddenColor
                            }
                        }
                        // This is the power button background
                        SystemUIContext.resources.getIdentifier(
                            "shadeActive", "attr", SystemUIContext.packageName
                        ) -> {
                            if (mDualColorQsPanel) {
                                if (isDarkMode(SystemUIContext)) {
                                    SystemUIContext.getColor(R.color.system_accent1_100)
                                } else {
                                    SystemUIContext.getColor(R.color.system_primary_light)
                                }
                            } else {
                                overriddenColor
                            }
                        }

                        // This is the foreground services text
                        SystemUIContext.resources.getIdentifier(
                            "onShadeInactiveVariant", "attr", SystemUIContext.packageName
                        ) -> {
                            if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) SystemUIContext.getColor(
                                R.color.system_on_surface_light
                            )
                            else SystemUIContext.getColor(R.color.system_on_surface_dark)
                        }

                        else -> overriddenColor
                    }

                    // If we've overridden a color then set the overridden, if not do stock behaviour
                    if (overriddenColor != Color.WHITE) {
                        val colorKt = findClass("androidx.compose.ui.graphics.ColorKt", classLoader)

                        param.result = callStaticMethod(colorKt, "Color", overriddenColor)
                    }
                })

            /**
             * This sets the colours of the icon in the quicksettings
             */
            safeHookMethod(
                context = SystemUIContext,
                QS_ICON_VIEW_IMPL_CLASS,
                classLoader,
                "setIcon",
                findClass("com.android.systemui.plugins.qs.QSTile.State", classLoader),
                Boolean::class.javaPrimitiveType,
                replaceWith = lambda@{ param ->
                    val iv = getObjectField(param.thisObject, "mIcon") as ImageView
                    val qsTilestate = param.args[0]
                    val allowAnimations = param.args[1] as Boolean
                    val state = getIntField(qsTilestate, "state")
                    val mState = getIntField(param.thisObject, "mState")
                    val disabledByPolicy = getBooleanField(qsTilestate, "disabledByPolicy")
                    val mDisabledByPolicy = getBooleanField(param.thisObject, "mDisabledByPolicy")
                    val mContext = callMethod(param.thisObject, "getContext") as Context
                    val mTint = getIntField(param.thisObject, "mTint")

                    if (state != mState || disabledByPolicy != mDisabledByPolicy) {

                        val color = getIconColorForState(mContext, state, disabledByPolicy)
                        setIntField(param.thisObject, "mState", state)
                        setBooleanField(param.thisObject, "mDisabledByPolicy", disabledByPolicy)

                        if (mTint != 0 && allowAnimations && shouldAnimate(
                                iv, param.thisObject
                            )
                        ) {
                            animateGrayScale(
                                mTint, color, iv, {
                                    callMethod(
                                        param.thisObject, "updateIcon", iv, qsTilestate, true
                                    )
                                }, param.thisObject
                            )
                        } else {
                            setTint(iv, color, param.thisObject)
                            callMethod(
                                param.thisObject, "updateIcon", iv, qsTilestate, allowAnimations
                            )
                        }
                    } else {
                        callMethod(
                            param.thisObject, "updateIcon", iv, qsTilestate, allowAnimations
                        )
                    }
                    return@lambda null
                })

            /**
             * This sets the colours of the qs tiles labels and backgrounds
             */
            safeHookAllConstructors(
                context = SystemUIContext,
                QS_TILE_VIEW_IMPL_CLASS,
                classLoader,
                afterHook = { param ->
                    setQSTileColors(param.thisObject as LinearLayout)
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_FOOTER_VIEW_CLASS,
                classLoader,
                "setBuildText",
                afterHook = { param ->
                    val mBuildText = getObjectField(param.thisObject, "mBuildText") as TextView
                    val mPageIndicator = getObjectField(param.thisObject, "mPageIndicator")
                    val tintColor = SystemUIContext.getColor(
                        if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) R.color.system_on_surface_light
                        else R.color.system_on_surface_dark
                    )

                    mBuildText.setTextColor(tintColor)
                    setObjectField(mPageIndicator, "mTint", ColorStateList.valueOf(tintColor))
                })
        }

        fun qsPullDownHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                STACK_COORDINATOR_CLASS,
                classLoader,
                "calculateNotifStats",
                List::class.java,
                afterHook = { param ->
                    val notifs = param.args[0] as List<*>
                    numActiveNotifs = notifs.size
                })

            safeHookMethod(
                context = SystemUIContext,
                QUICK_SETTINGS_CONTROLLER_IMPL_CLASS,
                classLoader,
                "isOpenQsEvent",
                MotionEvent::class.java,
                beforeHook = { param ->
                    val quickSettingsController = param.thisObject
                    val event = param.args[0] as MotionEvent

                    if (shouldFullyExpandDueQuickPulldown(
                            quickSettingsController, event
                        ) || shouldFullyExpandDueSmartPulldown()
                    ) {
                        param.result = true
                    }
                })
        }

        fun hideCollapsedStatusbarIconHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "updateBlockedIcons",
                replaceWith = lambda@{ param ->
                    val mMainExecutor =
                        getObjectField(param.thisObject, "mMainExecutor") as Executor

                    @Suppress("UNCHECKED_CAST")
                    val mBlockedIcons =
                        getObjectField(param.thisObject, "mBlockedIcons") as ArrayList<String>

                    mBlockedIcons.clear()

                    val blockListArray = SystemUIContext.resources.getStringArray(
                        SystemUIContext.resources.getIdentifier(
                            "config_collapsed_statusbar_icon_blocklist",
                            "array",
                            "com.android.systemui"
                        )
                    )

                    val blockList = blockListArray.toList()

                    val vibrateIconSlot = SystemUIContext.resources.getString(
                        SystemUIContext.resources.getIdentifier(
                            "status_bar_volume", "string", "android"
                        )
                    )

                    val showVibrateIcon = Settings.Secure.getInt(
                        SystemUIContext.contentResolver, "status_bar_show_vibrate_icon", 0
                    ) == 0

                    for (i in blockList.indices) {
                        when (blockList[i]) {
                            vibrateIconSlot -> {
                                if (showVibrateIcon && mHideCollapsedVolume) {
                                    mBlockedIcons.add(blockList[i])
                                }
                            }

                            "alarm_clock" -> {
                                if (mHideCollapsedAlarm) {
                                    mBlockedIcons.add(blockList[i])
                                }
                            }

                            "call_strength" -> {
                                if (mHideCollapsedCallStrength) {
                                    mBlockedIcons.add(blockList[i])
                                }
                            }

                            else -> {
                                mBlockedIcons.add(blockList[i])
                            }
                        }
                    }

                    mMainExecutor.execute {
                        val mDarkIconManager = getObjectField(param.thisObject, "mDarkIconManager")
                        callMethod(mDarkIconManager, "setBlockList", mBlockedIcons)
                        updateStatusbarIconColors()
                    }
                    return@lambda null
                })
        }

        fun hideStatusbarIconHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                BATTERY_METER_VIEW_CONTROLLER_TUNABLE_CLASS,
                classLoader,
                "onTuningChanged",
                String::class.java,
                String::class.java,
                replaceWith = lambda@{ param ->
                    val key = param.args[0] as String
                    val newValue = param.args[1] as String
                    val mView =
                        getObjectField(getSurroundingThis(param.thisObject), "mView") as View

                    // Set main view to visible regardless
                    mView.visibility = VISIBLE

                    @Suppress("UNCHECKED_CAST")
                    if ("icon_blacklist" == key) {
                        // This applies icon colors again when an icon is hidden/added
                        updateStatusbarIconColors()

                        val icons: ArraySet<String> = callStaticMethod(
                            findClass(STATUSBAR_ICON_CONTROLLER_CLASS, classLoader),
                            "getIconHideList",
                            SystemUIContext,
                            newValue
                        ) as ArraySet<String>

                        // Toggle visibility of icon not root view which includes percentage
                        val mBatteryIconView =
                            getObjectField(mView, "mBatteryIconView") as ImageView
                        mBatteryIconView.visibility =
                            if (icons.contains("battery")) GONE else VISIBLE

                        val startPadding = SystemUIContext.resources.getDimensionPixelSize(
                            SystemUIContext.resources.getIdentifier(
                                "battery_level_padding_start", "dimen", "com.android.systemui"
                            )
                        )
                        // Fix the padding at the start of the percentage view
                        val mBatteryPercentView =
                            getObjectField(mView, "mBatteryPercentView") as TextView

                        mBatteryPercentView.setPaddingRelative(
                            if (icons.contains("battery")) 0 else startPadding,
                            mBatteryPercentView.paddingTop,
                            mBatteryPercentView.paddingEnd,
                            mBatteryPercentView.paddingBottom
                        )
                    }
                })
        }

        fun qsTileLayoutHooks(classLoader: ClassLoader) {
            safeHookAllConstructors(
                context = SystemUIContext, TILE_ADAPTER_CLASS, classLoader, afterHook = { param ->
                    val mContext = getObjectField(param.thisObject, "mContext") as Context
                    setIntField(param.thisObject, "mNumColumns", getQsColumnCount(mContext, "QS"))
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_CUSTOMIZER_CONTROLLER_3_CLASS,
                classLoader,
                "onConfigChanged",
                Configuration::class.java,
                afterHook = { param ->
                    val tileAdapter = getObjectField(
                        getSurroundingThis(param.thisObject), "mTileAdapter"
                    )
                    val mContext = getObjectField(tileAdapter, "mContext") as Context

                    setIntField(tileAdapter, "mNumColumns", getQsColumnCount(mContext, "QS"))

                    val mView = getObjectField(getSurroundingThis(param.thisObject), "mView")

                    val mRecyclerView = getObjectField(mView, "mRecyclerView")

                    val layoutManager = callMethod(mRecyclerView, "getLayoutManager")

                    callMethod(
                        layoutManager, "setSpanCount", getIntField(
                            getObjectField(
                                getSurroundingThis(param.thisObject), "mTileAdapter"
                            ), "mNumColumns"
                        )
                    )
                })

            safeHookMethod(
                context = SystemUIContext,
                QUICK_QS_PANEL_CLASS,
                classLoader,
                "getOrCreateTileLayout",
                afterHook = { param ->
                    val mQQSSideLabelTileLayout = param.result

                    val mContext = getObjectField(param.thisObject, "mContext") as Context

                    callMethod(
                        mQQSSideLabelTileLayout, "setMaxColumns", getQsColumnCount(mContext, "QQS")
                    )
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_PANEL_CONTROLLER_BASE_CLASS,
                classLoader,
                "switchTileLayout",
                Boolean::class.javaPrimitiveType,
                afterHook = { param ->
                    val horizontal =
                        callMethod(param.thisObject, "shouldUseHorizontalLayout") as Boolean

                    val mView = getObjectField(param.thisObject, "mView") as View

                    val mTileLayout = getObjectField(mView, "mTileLayout") as View

                    callMethod(mTileLayout, "setMinRows", if (horizontal) 2 else 1)

                    if (mView.javaClass.name.equals(QUICK_QS_PANEL_CLASS)) {

                        callMethod(
                            mTileLayout, "setMaxColumns", if (horizontal) 2 else getQsColumnCount(
                                mView.context, "QQS"
                            )
                        )

                    } else if (mView.javaClass.name.equals(QS_PANEL_CLASS)) {

                        callMethod(
                            mTileLayout, "setMaxColumns", if (horizontal) 2 else getQsColumnCount(
                                mView.context, "QS"
                            )
                        )
                    }

                    val mUsingHorizontalLayoutChangedListener = getObjectField(
                        param.thisObject, "mUsingHorizontalLayoutChangedListener"
                    ) as Runnable?

                    mUsingHorizontalLayoutChangedListener?.run()
                })

            safeHookMethod(
                context = SystemUIContext,
                QUICK_QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                "onConfigurationChanged",
                replaceWith = lambda@{ param ->
                    setQQsPanelMaxTiles(param.thisObject)
                    return@lambda null
                })

            safeHookAllConstructors(
                context = SystemUIContext,
                QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                afterHook = { param ->
                    BrightnessSliderControllerFactory = param.args[12]
                    BrightnessControllerFactory = param.args[11]
                    mBrightnessMirrorHandler =
                        getObjectField(param.thisObject, "mBrightnessMirrorHandler")
                })

            safeHookMethod(
                context = SystemUIContext,
                SIDE_LABEL_TILE_LAYOUT_CLASS,
                classLoader,
                "updateResources",
                replaceWith = lambda@{ param ->
                    val sideLabelTileLayout = param.thisObject as ViewGroup

                    val mContext = getObjectField(param.thisObject, "mContext") as Context

                    val resources = sideLabelTileLayout.resources

                    val mIsSmallLandscapeLockscreenEnabled = getObjectField(
                        sideLabelTileLayout, "mIsSmallLandscapeLockscreenEnabled"
                    ) as Boolean

                    val mIsSmallLandscape = resources.getBoolean(
                        mContext.resources.getIdentifier(
                            "is_small_screen_landscape", "bool", "com.android.systemui"
                        )
                    )

                    val columns =
                        if (mIsSmallLandscapeLockscreenEnabled && mIsSmallLandscape) resources.getInteger(
                            mContext.resources.getIdentifier(
                                "small_land_lockscreen_quick_settings_num_columns",
                                "integer",
                                "com.android.systemui"
                            )
                        )
                        else maxOf(
                            getQsColumnCount(
                                mContext, "QS"
                            ), getQsColumnCount(
                                mContext, "QQS"
                            )
                        )

                    setIntField(
                        param.thisObject, "mResourceColumns", 1.coerceAtLeast(columns)
                    )

                    val mResourceCellHeightResId =
                        getIntField(param.thisObject, "mResourceCellHeightResId")

                    val mResourceCellHeight =
                        resources.getDimensionPixelSize(mResourceCellHeightResId)

                    setIntField(param.thisObject, "mResourceCellHeight", mResourceCellHeight)

                    val mCellMarginHorizontal = resources.getDimensionPixelSize(
                        mContext.resources.getIdentifier(
                            "qs_tile_margin_horizontal", "dimen", "com.android.systemui"
                        )
                    )

                    setIntField(
                        param.thisObject, "mCellMarginHorizontal", mCellMarginHorizontal
                    )

                    val mCellMarginVertical = resources.getDimensionPixelSize(
                        mContext.resources.getIdentifier(
                            "qs_tile_margin_vertical", "dimen", "com.android.systemui"
                        )
                    )

                    setIntField(param.thisObject, "mCellMarginVertical", mCellMarginVertical)

                    val rows =
                        if (mIsSmallLandscapeLockscreenEnabled && mIsSmallLandscape) resources.getInteger(
                            mContext.resources.getIdentifier(
                                "small_land_lockscreen_quick_settings_max_rows",
                                "integer",
                                "com.android.systemui"
                            )
                        )
                        else getQsRowCount(mContext, "QS")

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

                    val mTempTextView =
                        getObjectField(param.thisObject, "mTempTextView") as TextView

                    mTempTextView.dispatchConfigurationChanged(mContext.resources.configuration)

                    updateTileMargins(param.thisObject)

                    callMethod(param.thisObject, "estimateCellHeight")

                    val updateColumns = callMethod(param.thisObject, "updateColumns") as Boolean

                    var mReturn = false

                    if (updateColumns) {
                        callMethod(param.thisObject, "requestLayout")
                        mReturn = true
                    }

                    setIntField(
                        param.thisObject, "mMaxAllowedRows", rows
                    )

                    return@lambda mReturn
                })
        }

        fun qsBrightnessSliderHooks(classLoader: ClassLoader) {
            safeHookAllConstructors(
                context = SystemUIContext,
                QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                afterHook = { param ->
                    val mView = getObjectField(param.thisObject, "mView") as ViewGroup
                    val mBrightnessView = getObjectField(mView, "mBrightnessView") as View
                    setBrightnessView(mView, mBrightnessView)
                })

            safeHookMethod(
                context = SystemUIContext,
                QUICK_QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                "onInit",
                afterHook = { param ->
                    val mView = getObjectField(param.thisObject, "mView") as ViewGroup
                    val mContext = callMethod(mView, "getContext") as Context


                    mQQsBrightnessSliderController = callMethod(
                        BrightnessSliderControllerFactory, "create", mContext, mView
                    )

                    val mBrightnessView =
                        getObjectField(mQQsBrightnessSliderController, "mView") as View

                    mQQsBrightnessController = callMethod(
                        BrightnessControllerFactory, "create", mQQsBrightnessSliderController
                    )

                    setAdditionalInstanceField(
                        param.thisObject, "mQQsBrightnessController", mQQsBrightnessController
                    )


                    mQQsBrightnessMirrorHandler = newInstance(
                        findClass(BRIGHTNESS_MIRROR_HANDLER_CLASS, classLoader),
                        mQQsBrightnessController
                    )

                    setBrightnessView(mView, mBrightnessView)

                    callMethod(mQQsBrightnessSliderController, "init")

                    val brightnessMirrorController =
                        getObjectField(mQQsBrightnessMirrorHandler, "mirrorController")

                    if (brightnessMirrorController != null) {
                        val listener = getObjectField(
                            mQQsBrightnessMirrorHandler, "brightnessMirrorListener"
                        )

                        @Suppress("UNCHECKED_CAST")
                        val mBrightnessMirrorListeners = getObjectField(
                            brightnessMirrorController, "mBrightnessMirrorListeners"
                        ) as ArraySet<Any>

                        mBrightnessMirrorListeners.add(listener)
                    }

                    setQQsPanelMaxTiles(param.thisObject)
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_PANEL_CONTROLLER_BASE_CLASS,
                classLoader,
                "onViewDetached",
                afterHook = { param ->
                    val brightnessMirrorController =
                        getObjectField(mQQsBrightnessMirrorHandler, "mirrorController")
                    if (brightnessMirrorController != null) {
                        val listener = getObjectField(
                            mQQsBrightnessMirrorHandler, "brightnessMirrorListener"
                        )

                        @Suppress("UNCHECKED_CAST")
                        val mBrightnessMirrorListeners = getObjectField(
                            brightnessMirrorController, "mBrightnessMirrorListeners"
                        ) as ArraySet<Any>
                        mBrightnessMirrorListeners.remove(listener)
                    }
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_IMPL_CLASS,
                classLoader,
                "updateQsPanelControllerListening",
                afterHook = { param ->
                    val mListening = getBooleanField(param.thisObject, "mListening")
                    val mBackgroundHandler = getObjectField(
                        mQQsBrightnessController, "mBackgroundHandler"
                    ) as Handler
                    if (mListening) {
                        val mStartListeningRunnable =
                            getObjectField(mQQsBrightnessController, "mStartListeningRunnable")

                        mBackgroundHandler.post(mStartListeningRunnable as Runnable)
                        return@safeHookMethod
                    }
                    val mStopListeningRunnable =
                        getObjectField(mQQsBrightnessController, "mStopListeningRunnable")

                    mBackgroundHandler.post(mStopListeningRunnable as Runnable)
                    setBooleanField(mQQsBrightnessController, "mControlValueInitialized", false)
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_PANEL_CLASS,
                classLoader,
                "switchToParent",
                View::class.java,
                ViewGroup::class.java,
                Int::class.javaPrimitiveType,
                String::class.java,
                beforeHook = { param ->
                    val child = param.args[0] as View?
                    val parent = param.args[1] as ViewGroup?
                    val index = param.args[2] as Int
                    val tag = param.args[3] as String

                    if (mQsBrightnessSliderPosition != 1) return@safeHookMethod

                    if (parent == null) {
                        Log.w(
                            tag, "Trying to move view to null parent", IllegalStateException()
                        )
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
                                return@safeHookMethod
                            }
                            // Same parent, we are just changing indices
                            val currentIndex = parent.indexOfChild(mBrightnessView)
                            if (currentIndex == index) {
                                // We want to be in the same place. Nothing to do here
                                return@safeHookMethod
                            }
                            parent.removeView(mBrightnessView)
                            parent.addView(mBrightnessView, index)
                        }
                    }
                })
            /**
             * Applies the slider margins on rotation change
             */
            safeHookMethod(
                context = SystemUIContext,
                QS_PANEL_CONTROLLER_CLASS,
                classLoader,
                "maybeReinflateBrightnessSlider",
                afterHook = { param ->
                    val mView = getObjectField(param.thisObject, "mView") as View
                    setBrighnessSliderMargins(mView)
                })

            safeHookMethod(
                context = SystemUIContext,
                QS_ANIMATOR_CLASS,
                classLoader,
                "updateAnimators",
                replaceWith = lambda@{ param ->
                    setBooleanField(param.thisObject, "mNeedsAnimatorUpdate", false)
                    val firstPageBuilder = newInstance(TouchAnimatorBuilder)
                    val translationYBuilder = newInstance(TouchAnimatorBuilder)
                    val qqsTranslationYBuilder = newInstance(TouchAnimatorBuilder)
                    val translationXBuilder = newInstance(TouchAnimatorBuilder)
                    val nonFirstPageAlphaBuilder = newInstance(TouchAnimatorBuilder)
                    val quadraticInterpolatorBuilder = newInstance(TouchAnimatorBuilder)

                    setObjectField(
                        quadraticInterpolatorBuilder, "mInterpolator", getStaticObjectField(
                            Interpolators, "ACCELERATE"
                        )
                    )

                    val mHost = getObjectField(param.thisObject, "mHost")

                    val mNonFirstPageQSAnimators = getObjectField(
                        param.thisObject, "mNonFirstPageQSAnimators"
                    ) as SparseArray<*>

                    @Suppress("UNCHECKED_CAST")
                    val mAllViews = getObjectField(param.thisObject, "mAllViews") as ArrayList<View>

                    @Suppress("UNCHECKED_CAST")
                    val mAnimatedQsViews =
                        getObjectField(param.thisObject, "mAnimatedQsViews") as ArrayList<View>

                    val mQuickQsPanel = getObjectField(param.thisObject, "mQuickQsPanel")

                    val mQsPanelController = getObjectField(param.thisObject, "mQsPanelController")

                    val mQuickQSPanelController =
                        getObjectField(param.thisObject, "mQuickQSPanelController")

                    val mPagedLayout = getObjectField(param.thisObject, "mPagedLayout")

                    val mTmpLoc1 = getObjectField(param.thisObject, "mTmpLoc1") as IntArray

                    val mTmpLoc2 = getObjectField(param.thisObject, "mTmpLoc2") as IntArray

                    val interactor = getObjectField(mHost, "interactor")

                    // 1) Find the interface class
                    val currentTilesInterface = findClass(
                        "com.android.systemui.qs.pipeline.domain.interactor.CurrentTilesInteractor",
                        classLoader
                    )

                    // 2) Get the Method object from the interface
                    //    (not from the implementing class)
                    val method = currentTilesInterface.getMethod("getCurrentQSTiles")

                    // 3) Invoke that method on the instance
                    val tiles = method.invoke(interactor) as List<*>

                    var count = 0

                    callMethod(param.thisObject, "clearAnimationState")
                    mNonFirstPageQSAnimators.clear()
                    mAllViews.clear()
                    mAnimatedQsViews.clear()

                    setObjectField(param.thisObject, "mQQSTileHeightAnimator", null)

                    setObjectField(param.thisObject, "mOtherFirstPageTilesHeightAnimator", null)

                    var mQQSTileHeightAnimator =
                        getObjectField(param.thisObject, "mQQSTileHeightAnimator")
                    var mOtherFirstPageTilesHeightAnimator =
                        getObjectField(param.thisObject, "mOtherFirstPageTilesHeightAnimator")

                    setIntField(
                        param.thisObject, "mNumQuickTiles", getIntField(
                            mQuickQsPanel, "mMaxTiles"
                        )
                    )

                    val tileLayout = callMethod(mQsPanelController, "getTileLayout") as View
                    mAllViews.add(tileLayout)

                    setIntField(param.thisObject, "mLastQQSTileHeight", 0)

                    val mRecords = getObjectField(mQsPanelController, "mRecords") as ArrayList<*>

                    if (areThereTiles(mRecords)) {
                        for (tile in tiles) {

                            val tileView = getQsPanelTileView(tile, mQsPanelController)

                            if (tileView == null) {
                                Log.e(
                                    "QSAnimator",
                                    "tileView is null " + { callMethod(tile, "getTileSpec") })
                                continue
                            }

                            // Only animate tiles in the first page
                            if (mPagedLayout != null && count >= getNumTilesFirstPage(
                                    mPagedLayout
                                )
                            ) {
                                break
                            }

                            val mQsRootView = getObjectField(param.thisObject, "mQsRootView")

                            // This case: less tiles to animate in small displays.
                            val getNumVisibleTiles = callMethod(
                                callMethod(mQuickQSPanelController, "getTileLayout"),
                                "getNumVisibleTiles"
                            ) as Int

                            if (count < getNumVisibleTiles) {
                                // Quick tiles.
                                val quickTileView =
                                    getQuickQSPanelTileView(tile, mQuickQSPanelController)
                                        ?: continue

                                callMethod(
                                    param.thisObject,
                                    "getRelativePosition",
                                    mTmpLoc1,
                                    quickTileView,
                                    mQsRootView
                                )

                                callMethod(
                                    param.thisObject,
                                    "getRelativePosition",
                                    mTmpLoc2,
                                    tileView,
                                    mQsRootView
                                )

                                val yOffset: Int = mTmpLoc2[1] - mTmpLoc1[1]
                                val xOffset: Int = mTmpLoc2[0] - mTmpLoc1[0]

                                // Offset the translation animation on the views
                                // (that goes from 0 to getOffsetTranslation)
                                val f1 = floatArrayOf(0f, yOffset.toFloat())
                                val f2 = floatArrayOf((-yOffset).toFloat(), 0f)
                                val f3 = floatArrayOf(0f, xOffset.toFloat())
                                val f4 = floatArrayOf((-xOffset).toFloat(), 0f)

                                callMethod(
                                    qqsTranslationYBuilder,
                                    "addFloat",
                                    quickTileView,
                                    "translationY",
                                    f1
                                )

                                callMethod(
                                    translationYBuilder, "addFloat", tileView, "translationY", f2
                                )

                                callMethod(
                                    translationXBuilder,
                                    "addFloat",
                                    quickTileView,
                                    "translationX",
                                    f3
                                )

                                callMethod(
                                    translationXBuilder, "addFloat", tileView, "translationX", f4
                                )

                                if (mQQSTileHeightAnimator == null) {
                                    mQQSTileHeightAnimator = newInstance(
                                        HeightExpansionAnimator,
                                        param.thisObject,
                                        callMethod(quickTileView, "getMeasuredHeight"),
                                        callMethod(tileView, "getMeasuredHeight"),
                                    )
                                    setObjectField(
                                        param.thisObject,
                                        "mQQSTileHeightAnimator",
                                        mQQSTileHeightAnimator
                                    )

                                    setIntField(
                                        param.thisObject,
                                        "mLastQQSTileHeight",
                                        callMethod(quickTileView, "getMeasuredHeight") as Int
                                    )
                                }

                                val mQQSTileHeightAnimatorViews =
                                    getObjectField(mQQSTileHeightAnimator, "mViews")

                                callMethod(mQQSTileHeightAnimatorViews, "add", quickTileView)

                                // Icons
                                callMethod(
                                    param.thisObject,
                                    "translateContent",
                                    if (mQsStyleConfig == 0) {
                                        callMethod(quickTileView, "getIcon")
                                    } else {
                                        callMethod(quickTileView, "getIconWithBackground")
                                    },
                                    if (mQsStyleConfig == 0) {
                                        callMethod(tileView, "getIcon")
                                    } else {
                                        callMethod(tileView, "getIconWithBackground")
                                    },
                                    mQsRootView,
                                    xOffset,
                                    yOffset,
                                    mTmpLoc1,
                                    translationXBuilder,
                                    translationYBuilder,
                                    qqsTranslationYBuilder
                                )

                                // Label Containers
                                callMethod(
                                    param.thisObject,
                                    "translateContent",
                                    callMethod(quickTileView, "getLabelContainer"),
                                    callMethod(tileView, "getLabelContainer"),
                                    mQsRootView,
                                    xOffset,
                                    yOffset,
                                    mTmpLoc1,
                                    translationXBuilder,
                                    translationYBuilder,
                                    qqsTranslationYBuilder
                                )

                                // Secondary Icon
                                callMethod(
                                    param.thisObject,
                                    "translateContent",
                                    callMethod(quickTileView, "getSecondaryIcon"),
                                    callMethod(tileView, "getSecondaryIcon"),
                                    mQsRootView,
                                    xOffset,
                                    yOffset,
                                    mTmpLoc1,
                                    translationXBuilder,
                                    translationYBuilder,
                                    qqsTranslationYBuilder
                                )

                                // Secondary labels on tiles not in QQS have two alpha animation applied:
                                // * on the tile themselves
                                // * on TileLayout
                                // Therefore, we use a quadratic interpolator animator to animate the alpha
                                // for tiles in QQS to match.
                                val f5 = floatArrayOf(0f, 1f)
                                val f6 = floatArrayOf(0f, 0f)

                                callMethod(
                                    quadraticInterpolatorBuilder,
                                    "addFloat",
                                    callMethod(quickTileView, "getSecondaryLabel"),
                                    "alpha",
                                    f5
                                )

                                callMethod(
                                    nonFirstPageAlphaBuilder,
                                    "addFloat",
                                    callMethod(quickTileView, "getSecondaryLabel"),
                                    "alpha",
                                    f6
                                )

                                mAnimatedQsViews.add(tileView as View)
                                mAllViews.add(quickTileView as View)
                                mAllViews.add(
                                    callMethod(quickTileView, "getSecondaryIcon") as View
                                )
                            } else if (!isIconInAnimatedRow(count, param.thisObject)) {
                                // Pretend there's a corresponding QQS tile (for the position) that we are
                                // expanding from.
                                val qqsLayout = callMethod(mQuickQSPanelController, "getTileLayout")

                                callMethod(
                                    param.thisObject,
                                    "getRelativePosition",
                                    mTmpLoc1,
                                    qqsLayout,
                                    mQsRootView
                                )
                                setObjectField(param.thisObject, "mQQSTop", mTmpLoc1[1])

                                callMethod(
                                    param.thisObject,
                                    "getRelativePosition",
                                    mTmpLoc2,
                                    tileView,
                                    mQsRootView
                                )


                                val diff: Int = mTmpLoc2[1] - (mTmpLoc1[1] + getPhantomTopPosition(
                                    count, qqsLayout
                                ))

                                val f10 = floatArrayOf((-diff).toFloat(), 0f)

                                callMethod(
                                    translationYBuilder, "addFloat", tileView, "translationY", f10
                                )

                                if (mOtherFirstPageTilesHeightAnimator == null) {
                                    val mLastQQSTileHeight =
                                        getIntField(param.thisObject, "mLastQQSTileHeight")

                                    mOtherFirstPageTilesHeightAnimator = newInstance(
                                        HeightExpansionAnimator,
                                        param.thisObject,
                                        mLastQQSTileHeight,
                                        callMethod(tileView, "getMeasuredHeight")
                                    )

                                    setObjectField(
                                        param.thisObject,
                                        "mOtherFirstPageTilesHeightAnimator",
                                        mOtherFirstPageTilesHeightAnimator
                                    )
                                }
                                val f6 = floatArrayOf(0f, 1f)

                                val mOtherFirstPageTilesHeightAnimatorViews =
                                    getObjectField(mOtherFirstPageTilesHeightAnimator, "mViews")

                                callMethod(
                                    mOtherFirstPageTilesHeightAnimatorViews, "add", tileView
                                )
                                callMethod(tileView, "setClipChildren", true)
                                callMethod(tileView, "setClipToPadding", true)

                                if (mQsStyleConfig == 0) {
                                    callMethod(
                                        firstPageBuilder,
                                        "addFloat",
                                        callMethod(tileView, "getSecondaryLabel"),
                                        "alpha",
                                        f6
                                    )

                                    mAllViews.add(
                                        callMethod(tileView, "getSecondaryLabel") as View
                                    )
                                } else {
                                    callMethod(
                                        firstPageBuilder,
                                        "addFloat",
                                        callMethod(tileView, "getLabelContainer"),
                                        "alpha",
                                        f6
                                    )

                                    mAllViews.add(
                                        callMethod(tileView, "getLabelContainer") as View
                                    )
                                }
                            }

                            mAllViews.add(tileView as View)
                            count++
                        }
                        val mCurrentPage = getIntField(param.thisObject, "mCurrentPage")
                        if (mCurrentPage != 0) {
                            callMethod(
                                param.thisObject, "addNonFirstPageAnimators", mCurrentPage
                            )
                        }
                    }

                    animateBrightnessSlider(param.thisObject)

                    val f7 = floatArrayOf(0f, 1f)

                    val mFirstPageAnimator = firstPageBuilder
                    callMethod(mFirstPageAnimator, "addFloat", tileLayout, "alpha", f7)
                    callMethod(
                        mFirstPageAnimator,
                        "addFloat",
                        callMethod(quadraticInterpolatorBuilder, "build"),
                        "position",
                        f7
                    )

                    setObjectField(mFirstPageAnimator, "mListener", param.thisObject)
                    setObjectField(
                        param.thisObject,
                        "mFirstPageAnimator",
                        callMethod(mFirstPageAnimator, "build")
                    )

                    // Fade in the media player as we reach the final position
                    val builder: Any = newInstance(TouchAnimatorBuilder)
                    setObjectField(builder, "mStartDelay", 0.86f)
                    val shouldUseHorizontalLayout =
                        callMethod(mQsPanelController, "shouldUseHorizontalLayout") as Boolean
                    val hostView = getObjectField(
                        getObjectField(mQsPanelController, "mMediaHost"), "hostView"
                    )

                    if (shouldUseHorizontalLayout && hostView != null) {
                        callMethod(builder, "addFloat", hostView, "alpha", f7)
                    } else {
                        // In portrait, media view should always be visible
                        callMethod(hostView, "setAlpha", 1.0f)
                    }

                    setObjectField(
                        param.thisObject, "mAllPagesDelayedAnimator", callMethod(builder, "build")
                    )

                    val mQSExpansionPathInterpolator =
                        getObjectField(param.thisObject, "mQSExpansionPathInterpolator")

                    setObjectField(
                        translationYBuilder,
                        "mInterpolator",
                        callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                    )

                    setObjectField(
                        qqsTranslationYBuilder,
                        "mInterpolator",
                        callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                    )

                    val pathInterpolatorBuilder =
                        getObjectField(mQSExpansionPathInterpolator, "pathInterpolatorBuilder")

                    val mDist = getObjectField(pathInterpolatorBuilder, "mDist")

                    val mX = getObjectField(pathInterpolatorBuilder, "mX")

                    val pathInterpolatorBuilderPathInterpolator = newInstance(
                        PathInterpolatorBuilderPathInterpolator, mDist, mX
                    )

                    setObjectField(
                        translationXBuilder,
                        "mInterpolator",
                        pathInterpolatorBuilderPathInterpolator
                    )

                    val mOnFirstPage = getBooleanField(param.thisObject, "mOnFirstPage")
                    if (mOnFirstPage) {
                        // Only recreate this animator if we're in the first page. That way we know that
                        // the first page is attached and has the proper positions/measures.
                        setObjectField(
                            param.thisObject,
                            "mQQSTranslationYAnimator",
                            callMethod(qqsTranslationYBuilder, "build")
                        )
                    }

                    setObjectField(
                        param.thisObject,
                        "mTranslationYAnimator",
                        callMethod(translationYBuilder, "build")
                    )

                    setObjectField(
                        param.thisObject,
                        "mTranslationXAnimator",
                        callMethod(translationXBuilder, "build")
                    )

                    if (mQQSTileHeightAnimator != null) {
                        val mAnimator = getObjectField(mQQSTileHeightAnimator, "mAnimator")
                        callMethod(
                            mAnimator,
                            "setInterpolator",
                            callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                        )

                    }

                    mOtherFirstPageTilesHeightAnimator =
                        getObjectField(param.thisObject, "mOtherFirstPageTilesHeightAnimator")

                    if (mOtherFirstPageTilesHeightAnimator != null) {
                        val mAnimator =
                            getObjectField(mOtherFirstPageTilesHeightAnimator, "mAnimator")
                        callMethod(
                            mAnimator,
                            "setInterpolator",
                            callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                        )
                    }

                    val f8 = floatArrayOf(1f, 0f)
                    val f9 = floatArrayOf(0f, 1f)

                    val mNonfirstPageAlphaAnimator = newInstance(TouchAnimatorBuilder)
                    val mNonFirstPageListener =
                        getObjectField(param.thisObject, "mNonFirstPageListener")

                    callMethod(
                        mNonfirstPageAlphaAnimator, "addFloat", mQuickQsPanel, "alpha", f8
                    )
                    callMethod(mNonfirstPageAlphaAnimator, "addFloat", tileLayout, "alpha", f9)
                    setObjectField(
                        mNonfirstPageAlphaAnimator, "mListener", mNonFirstPageListener
                    )
                    setObjectField(mNonfirstPageAlphaAnimator, "mEndDelay", 0.9f)
                    setObjectField(
                        param.thisObject,
                        "mNonfirstPageAlphaAnimator",
                        callMethod(mNonfirstPageAlphaAnimator, "build")
                    )
                    return@lambda null
                })
        }

        fun disableLockscreenQuicksettingsHooks(classLoader: ClassLoader) {
            /**
             * Gives us realtime value of whether keyguard is showing or not
             */
            safeHookMethod(
                context = SystemUIContext,
                QS_IMPL_CLASS,
                classLoader,
                "disable",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                replaceWith = lambda@{ param ->
                    val mContext = callMethod(param.thisObject, "getContext") as Context
                    val displayId = param.args[0] as Int
                    var state2 = param.args[2] as Int

                    if (displayId != mContext.display.displayId) return@lambda null

                    val mContainer = getObjectField(param.thisObject, "mContainer")
                    val mHeader = getObjectField(param.thisObject, "mHeader")
                    val mFooter = getObjectField(param.thisObject, "mFooter")
                    val mFooterView = getObjectField(mFooter, "mView") as FrameLayout
                    val mQuickQSPanel = getObjectField(mHeader, "mHeaderQsPanel") as View

                    val mRemoteInputQuickSettingsDisabler =
                        getObjectField(param.thisObject, "mRemoteInputQuickSettingsDisabler")

                    state2 = adjustDisableFlags(
                        mRemoteInputQuickSettingsDisabler, state2
                    )
                    state2 = adjustQsDisableFlags(state2)

                    val disabled = (state2 and DISABLE2_QUICK_SETTINGS) != 0
                    val mQsDisabled = getBooleanField(param.thisObject, "mQsDisabled")
                    if (disabled == mQsDisabled) return@lambda null

                    setBooleanField(param.thisObject, "mQsDisabled", disabled)
                    setBooleanField(mContainer, "mQsDisabled", disabled)
                    setBooleanField(mHeader, "mQsDisabled", disabled)
                    setBooleanField(mQuickQSPanel, "mDisabledByPolicy", disabled)
                    if (disabled) {
                        mQuickQSPanel.visibility = GONE
                    } else {
                        mQuickQSPanel.visibility = VISIBLE
                    }
                    callMethod(mHeader, "updateResources")

                    setBooleanField(mFooterView, "mQsDisabled", disabled)
                    mFooterView.post(
                        newInstance(
                            findClass(
                                QS_FOOTER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS, classLoader
                            ), mFooterView
                        ) as Runnable?
                    )

                    callMethod(param.thisObject, "updateQsState")

                    return@lambda null

                })

            safeHookMethod(
                context = SystemUIContext,
                GLOBAL_ACTIONS_DIALOG_LITE_CLASS,
                classLoader,
                methodName = "showOrHideDialog",
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                findClass(EXPANDABLE_CLASS, classLoader),
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val expandable = param.args[2]

                    /**
                     * Expandable is provided when launched from qs footer so we only block this,
                     * otherwise we block hardware power button as well.
                     * Also check whether the device is locked, so we can still launch from qs
                     * footer when unlocked
                     */
                    if (expandable != null && mDisableLockscreenPowerMenu && mKeyguardShowing) {
                        param.result = null
                    }
                })
        }

        fun scrimAlphaHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                SCRIM_CONTROLLER_CLASS,
                classLoader,
                "updateScrimColor",
                View::class.java,
                Int::class.javaPrimitiveType,
                Float::class.javaPrimitiveType,
                beforeHook = { param ->
                    val mScrimBehind = getObjectField(param.thisObject, "mScrimBehind")
                    val mNotificationsScrim =
                        getObjectField(param.thisObject, "mNotificationsScrim")
                    val scrimView = param.args[0]
                    val scrimState = getObjectField(param.thisObject, "mState").toString()

                    val mContext = callMethod(
                        getObjectField(param.thisObject, "mScrimBehind"), "getContext"
                    ) as Context

                    if (mDualColorQsPanel) {
                        if (scrimState == "UNLOCKED" || scrimState == "SHADE_LOCKED") {
                            if (scrimView == mScrimBehind) {
                                // Set the QS scrim to material color so it takes wallpaper hue
                                param.args[1] = mContext.getColor(
                                    if (!isDarkMode(mContext)) R.color.system_neutral1_100
                                    else R.color.system_neutral1_900
                                )
                            }
                        }
                    }

                    // Scrim alpha
                    if (scrimState == "UNLOCKED" || scrimState == "SHADE_LOCKED") {

                        // QS scrim
                        if (scrimView == mScrimBehind) {
                            param.args[2] = param.args[2] as Float * mQsScrimAlpha
                        }

                        // Notification scrim
                        if (scrimView == mNotificationsScrim) {
                            param.args[2] = param.args[2] as Float * mNotificationScrimAlpha

                            // If QS scrim is less that 1, we need the notif scrim to be slightly transparent to show the transparency, so set it to the lowers of either 1 down
                            // from full opacity, or the notification scrim alpha
                            if (mQsScrimAlpha < 1.0f) {
                                param.args[2] = param.args[2] as Float * minOf(
                                    mNotificationScrimAlpha, 1.0f.nextDown()
                                )
                            }
                        }
                    }
                })
        }

        fun clockPositionHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = SystemUIContext,
                COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "onViewCreated",
                View::class.java,
                Bundle::class.java,
                afterHook = { param ->
                    val mClockView = getObjectField(param.thisObject, "mClockView") as View
                    mDefaultClockContainer = mClockView.parent
                    setStatusbarClockPosition()
                })

            safeHookMethod(
                context = SystemUIContext,
                COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "animateHiddenState",
                View::class.java,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                beforeHook = { param ->
                    val view: View = param.args[0] as View

                    val mClockView: View =
                        getObjectField(CollapsedStatusBarFragment, "mClockView") as View

                    if (view == mClockView && mStatusbarClockPosition != 0) {
                        param.result = null
                    }
                })

            safeHookMethod(
                context = SystemUIContext,
                HEADS_UP_APPEARANCE_CONTROLLER_CLASS,
                classLoader,
                "setPinnedStatus",
                findClass(PINNED_STATUS_CLASS, classLoader),
                replaceWith = lambda@{ param ->
                    val mPinnedStatus = getObjectField(param.thisObject, "mPinnedStatus")
                    val pinnedStatus = param.args[0]

                    if (mPinnedStatus != pinnedStatus) {

                        setObjectField(param.thisObject, "mPinnedStatus", pinnedStatus)

                        val mView = getObjectField(param.thisObject, "mView") as View

                        val isPinned = getBooleanField(pinnedStatus, "isPinned")

                        val mContext = mView.context

                        val mClockView = getObjectField(param.thisObject, "mClockView") as View

                        val mParentClippingParams =
                            getObjectField(param.thisObject, "mParentClippingParams")

                        val mOperatorNameViewOptional = getObjectField(
                            param.thisObject, "mOperatorNameViewOptional"
                        )

                        val mStatusBarStateController = getObjectField(
                            param.thisObject, "mStatusBarStateController"
                        )

                        val mCommandQueue = getObjectField(param.thisObject, "mCommandQueue")

                        if (isPinned) {
                            callStaticMethod(
                                findClass(VIEW_CLIPPING_UTIL_CLASS, classLoader),
                                "setClippingDeactivated",
                                mView,
                                true,
                                mParentClippingParams
                            )

                            if (mView.javaClass.name == HEADS_UP_STATUSBAR_VIEW_CLASS) {
                                mView.visibility = VISIBLE
                            }

                            callMethod(param.thisObject, "show", mView)

                            if (mStatusbarClockPosition == 0) {
                                callMethod(
                                    param.thisObject, "hide", mClockView, 4, null
                                )
                            }

                            callMethod(
                                mOperatorNameViewOptional, "ifPresent", newInstance(
                                    findClass(
                                        HEADS_UP_APPEARANCE_CONTROLLER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS,
                                        classLoader
                                    ), param.thisObject, 1
                                )
                            )

                        } else {
                            callMethod(param.thisObject, "show", mClockView)

                            callMethod(
                                mOperatorNameViewOptional, "ifPresent", newInstance(
                                    findClass(
                                        HEADS_UP_APPEARANCE_CONTROLLER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS,
                                        classLoader
                                    ), param.thisObject, 2
                                )
                            )
                            callMethod(
                                param.thisObject, "hide", mView, 8, newInstance(
                                    findClass(
                                        UPDATE_PARENT_CLIPPING_CLASS, classLoader
                                    ), param.thisObject, 0
                                )
                            )
                        }

                        if (callMethod(
                                mStatusBarStateController, "getState"
                            ) as Int != 0
                        ) {
                            callMethod(
                                mCommandQueue,
                                "recomputeDisableFlags",
                                callMethod(mContext, "getDisplayId"),
                                false
                            )
                        }
                    }
                    return@lambda null
                })
        }

        fun statusbarBrightnessControlHooks(classLoader: ClassLoader) {
            // In Android 16 long pressing statusbar expands the shade, we need to disable this if brightness control is enabled
            safeHookMethod(
                context = SystemUIContext,
                NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS,
                classLoader,
                "onStatusBarLongPress",
                MotionEvent::class.java,
                beforeHook = { param ->
                    if (mStatusbarBrightnessControl)
                        param.result = null
                })

            safeHookMethod(
                context = SystemUIContext,
                CENTRAL_SURFACES_IMPL_CLASS,
                classLoader,
                "start",
                afterHook = { param ->
                    val context = getObjectField(param.thisObject, "mContext") as Context

                    val powerManager =
                        getObjectField(param.thisObject, "mPowerManager") as PowerManager

                    // Set statusbar brightness control variables
                    displayId = getIntField(param.thisObject, "mDisplayId")
                    minimumBacklight =
                        callMethod(powerManager, "getBrightnessConstraint", 0) as Float
                    maximumBacklight =
                        callMethod(powerManager, "getBrightnessConstraint", 1) as Float
                    quickQsOffsetHeight = context.resources.getDimensionPixelSize(
                        context.resources.getIdentifier(
                            "quick_qs_offset_height", "dimen", "android"
                        )
                    )
                })

            safeHookMethod(
                context = SystemUIContext,
                NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
                classLoader,
                "onTouch",
                View::class.java,
                MotionEvent::class.java,
                beforeHook = { param ->
                    val event = param.args[1] as MotionEvent
                    val mCommandQueue = getObjectField(
                        getSurroundingThis(param.thisObject), "mCommandQueue"
                    )
                    val commandQueuePanelsEnabled =
                        callMethod(mCommandQueue, "panelsEnabled") as Boolean

                    if (mStatusbarBrightnessControl) {
                        brightnessControl(
                            event
                        )
                        if (!commandQueuePanelsEnabled) param.result = null
                    }
                },
                afterHook = { param ->
                    val event = param.args[1] as MotionEvent

                    val upOrCancel =
                        event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL

                    onBrightnessChanged(upOrCancel, SystemUIContext)
                })

            safeHookMethod(
                context = SystemUIContext,
                PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS,
                classLoader,
                "onTouch",
                MotionEvent::class.java,
                beforeHook = { param ->
                    val event = param.args[0] as MotionEvent
                    val centralSurfaces = getObjectField(param.thisObject, "centralSurfaces")
                    val mCommandQueue = getObjectField(centralSurfaces, "mCommandQueue")
                    val commandQueuePanelsEnabled =
                        callMethod(mCommandQueue, "panelsEnabled") as Boolean

                    if (mStatusbarBrightnessControl) {
                        brightnessControl(
                            event
                        )
                        if (!commandQueuePanelsEnabled) param.result = null
                    }
                },
                afterHook = { param ->
                    val event = param.args[0] as MotionEvent

                    val upOrCancel =
                        event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL

                    onBrightnessChanged(upOrCancel, SystemUIContext)
                })
        }

        /**
         * Additional functions
         */
        fun setQSTileColors(qsTileViewImpl: LinearLayout) {
            try {
                /**
                 * Tile background colors
                 */
                val colorActive = getIntField(qsTileViewImpl, "colorActive")
                val colorInactive = getIntField(qsTileViewImpl, "colorInactive")
                val colorUnavailable = getIntField(qsTileViewImpl, "colorUnavailable")

                val colorActiveResolved = if (mQsStyleConfig == 2) {
                    SystemUIContext.getColor(R.color.transparent)
                } else {
                    if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) {
                        SystemUIContext.getColor(R.color.system_primary_light)
                    } else {
                        colorActive
                    }

                }

                val colorInactiveResolved = if (mQsStyleConfig == 2) {
                    SystemUIContext.getColor(R.color.transparent)
                } else {
                    if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) {
                        SystemUIContext.getColor(R.color.system_accent1_50)
                    } else {
                        colorInactive
                    }
                }

                val colorUnavailableResolved = if (mQsStyleConfig == 2) {
                    SystemUIContext.getColor(R.color.transparent)
                } else {
                    if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) {
                        applyAlpha(0.3f, colorInactiveResolved)
                    } else {
                        colorUnavailable
                    }
                }

                setIntField(qsTileViewImpl, "colorActive", colorActiveResolved)
                setIntField(qsTileViewImpl, "colorInactive", colorInactiveResolved)
                setIntField(qsTileViewImpl, "colorUnavailable", colorUnavailableResolved)

                /**
                 * Tile label colors
                 */
                when (mQsStyleConfig) {
                    2, 1 -> {
                        val colorLabelActive = SystemUIContext.getColor(
                            if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) R.color.system_on_surface_light
                            else R.color.system_on_surface_dark
                        )
                        setIntField(qsTileViewImpl, "colorLabelActive", colorLabelActive)

                        setIntField(
                            qsTileViewImpl, "colorLabelInactive", applyAlpha(0.8f, colorLabelActive)
                        )

                        setIntField(
                            qsTileViewImpl,
                            "colorLabelUnavailable",
                            applyAlpha(0.3f, colorLabelActive)
                        )

                        val colorSecondaryLabelActive = SystemUIContext.getColor(
                            if (mDualColorQsPanel && !isDarkMode(SystemUIContext)) R.color.system_on_surface_variant_light
                            else R.color.system_on_surface_variant_dark
                        )
                        setIntField(
                            qsTileViewImpl, "colorSecondaryLabelActive", colorSecondaryLabelActive
                        )

                        setIntField(
                            qsTileViewImpl,
                            "colorSecondaryLabelInactive",
                            applyAlpha(0.8f, colorSecondaryLabelActive)
                        )

                        setIntField(
                            qsTileViewImpl,
                            "colorSecondaryLabelUnavailable",
                            applyAlpha(0.3f, colorSecondaryLabelActive)
                        )
                    }

                    0 -> {
                        if (!mDualColorQsPanel || isDarkMode(SystemUIContext)) return

                        val colorLabelActive = SystemUIContext.getColor(R.color.system_accent1_10)
                        setIntField(qsTileViewImpl, "colorLabelActive", colorLabelActive)

                        val colorSecondaryLabelActive = applyAlpha(0.8f, colorLabelActive)
                        setIntField(
                            qsTileViewImpl, "colorSecondaryLabelActive", colorSecondaryLabelActive
                        )

                        val colorLabelInactive =
                            applyAlpha(
                                0.8f,
                                SystemUIContext.getColor(R.color.system_on_surface_light)
                            )
                        setIntField(qsTileViewImpl, "colorLabelInactive", colorLabelInactive)

                        val colorSecondaryLabelInactive = applyAlpha(
                            0.8f, SystemUIContext.getColor(R.color.system_on_surface_variant_light)
                        )
                        setIntField(
                            qsTileViewImpl,
                            "colorSecondaryLabelInactive",
                            colorSecondaryLabelInactive
                        )

                        val colorLabelUnavailable = applyAlpha(0.5f, colorLabelInactive)
                        setIntField(qsTileViewImpl, "colorLabelUnavailable", colorLabelUnavailable)

                        val colorSecondaryLabelUnavailable =
                            applyAlpha(0.5f, colorSecondaryLabelInactive)
                        setIntField(
                            qsTileViewImpl,
                            "colorSecondaryLabelUnavailable",
                            colorSecondaryLabelUnavailable
                        )
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private fun animateGrayScale(
            fromColor: Int,
            toColor: Int,
            iv: ImageView,
            endRunnable: Runnable,
            qSIconViewImpl: Any,
        ) {
            try {
                val mColorAnimator =
                    getObjectField(qSIconViewImpl, "mColorAnimator") as ValueAnimator
                val mAnimationEnabled = getBooleanField(qSIconViewImpl, "mAnimationEnabled")
                mColorAnimator.cancel()

                if (mAnimationEnabled && ValueAnimator.areAnimatorsEnabled()) {
                    val values = PropertyValuesHolder.ofInt("color", fromColor, toColor)
                    values.setEvaluator(ArgbEvaluator())
                    mColorAnimator.setValues(values)
                    mColorAnimator.removeAllListeners()
                    mColorAnimator.addUpdateListener { animation ->
                        setTint(
                            iv, animation.animatedValue as Int, qSIconViewImpl
                        )
                    }

                    val mRunnable = endRunnable

                    val endRunnableAnimatorListener = object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            mRunnable.run()
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            mRunnable.run()
                        }
                    }
                    mColorAnimator.addListener { endRunnableAnimatorListener }
                    mColorAnimator.start()

                } else {
                    setTint(iv, toColor, qSIconViewImpl)
                    endRunnable.run()
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private fun shouldAnimate(iv: ImageView, qSIconViewImpl: Any): Boolean {
            try {
                val mAnimationEnabled = getBooleanField(qSIconViewImpl, "mAnimationEnabled")
                return mAnimationEnabled && iv.isShown && iv.drawable != null
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        private fun setTint(iv: ImageView, color: Int, qSIconViewImpl: Any) {
            try {
                iv.imageTintList = ColorStateList.valueOf(color)
                setIntField(qSIconViewImpl, "mTint", color)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        @SuppressLint("DiscouragedApi")
        private fun getIconColorForState(
            context: Context, state: Int, disabledByPolicy: Boolean
        ): Int {
            try {
                return if (disabledByPolicy || state == Tile.STATE_UNAVAILABLE) {
                    if (mQsStyleConfig == 2) {
                        applyAlpha(
                            0.25f,
                            getColorAttrDefaultColor(context, R.attr.textColorTertiary)
                        )
                    } else {
                        getColorAttrDefaultColor(context, R.attr.textColorTertiary)

                    }
                } else if (state == Tile.STATE_INACTIVE) {
                    if (mQsStyleConfig == 2) {
                        if (mDualColorQsPanel && !isDarkMode(context)) {
                            applyAlpha(0.5f, context.getColor(R.color.system_on_surface_light))
                        } else {
                            applyAlpha(
                                0.7f,
                                getColorAttrDefaultColor(context, R.attr.textColorPrimary)
                            )
                        }
                    } else {
                        getColorAttrDefaultColor(
                            context,
                            if (mDualColorQsPanel && !isDarkMode(context)) context.resources.getIdentifier(
                                "textColorOnAccent", "attr", "android"
                            )
                            else R.attr.textColorPrimary
                        )
                    }
                } else if (state == Tile.STATE_ACTIVE) {
                    // We want to set the icon to the accent color for this style
                    if (mQsStyleConfig == 2) {
                        if (mDualColorQsPanel && !isDarkMode(context)) {
                            context.getColor(R.color.system_primary_light)
                        } else {
                            getColorAttrDefaultColor(context, R.attr.colorAccent)
                        }
                    } else {
                        getColorAttrDefaultColor(
                            context,
                            if (mDualColorQsPanel && !isDarkMode(context)) R.attr.textColorPrimary
                            else context.resources.getIdentifier(
                                "textColorOnAccent", "attr", "android"
                            )
                        )
                    }
                } else {
                    Log.e("QSIconView", "Invalid state $state")
                    0
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return getColorAttrDefaultColor(
                    context,
                    context.resources.getIdentifier(
                        "textColorOnAccent", "attr", "android"
                    )
                )
            }
        }

        fun setCustomizerColors(customizer: Any) {
            try {
                val mainView = customizer as ViewGroup

                if (!mDualColorQsPanel) return

                val resolvedBackgroundColor = mainView.context.getColor(
                    if (isDarkMode(mainView.context)) R.color.system_neutral1_900 else R.color.system_neutral1_100
                )

                val resolvedOnSurfaceColor = mainView.context.getColor(
                    if (isDarkMode(mainView.context)) R.color.system_on_surface_dark else R.color.system_on_surface_light
                )

                val mToolbar = getObjectField(customizer, "mToolbar") as Toolbar
                mToolbar.setBackgroundColor(resolvedBackgroundColor)
                mToolbar.setTitleTextColor(resolvedOnSurfaceColor)
                mToolbar.navigationIcon?.setTint(resolvedOnSurfaceColor)
                val reset = mToolbar.menu[0]

                // Change the menu item title color
                val spanString = SpannableString(reset.title)
                spanString.setSpan(
                    ForegroundColorSpan(resolvedOnSurfaceColor), 0, spanString.length, 0
                )
                reset.title = spanString

                // This sets the header background to the same as QS background color
                for (i in 0 until mainView.childCount) {
                    mainView.getChildAt(i).setBackgroundColor(resolvedBackgroundColor)
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun setBrightnessSliderColorsAfterInflate(brightnessSliderView: View) {
            try {

                val mContext = brightnessSliderView.context

                val mSlider = getObjectField(brightnessSliderView, "mSlider")

                val mSliderProgressDrawable =
                    callMethod(mSlider, "getProgressDrawable") as LayerDrawable

                val mProgressDrawable =
                    getObjectField(brightnessSliderView, "mProgressDrawable") as GradientDrawable

                val mBrightnessProgress =
                    mSliderProgressDrawable.findDrawableByLayerId(R.id.progress) as DrawableWrapper

                val mBrightnessProgressDrawable = mBrightnessProgress.drawable as LayerDrawable

                val mSliderIcon = mBrightnessProgressDrawable.findDrawableByLayerId(
                    mContext.resources.getIdentifier(
                        "slider_icon", "id", mContext.packageName
                    )
                )

                // If the tweak is enabled then set the container color
                if (mDualColorQsPanel && !isDarkMode(mContext)) {
                    mSliderProgressDrawable.findDrawableByLayerId(R.id.background)
                        .setTint(
                            applyAlpha(
                                0.3f,
                                mContext.getColor(R.color.system_on_surface_light)
                            )
                        )

                    mProgressDrawable.setColor(mContext.getColor(R.color.system_primary_light))

                    mSliderIcon.setTint(getColorAttrDefaultColor(mContext, R.attr.textColorPrimary))
                } else {
                    mSliderProgressDrawable.findDrawableByLayerId(R.id.background)
                        .setTint(
                            applyAlpha(
                                0.3f,
                                mContext.getColor(R.color.system_on_surface_dark)
                            )
                        )

                    mProgressDrawable.setColor(mContext.getColor(R.color.system_accent1_100))

                    mSliderIcon.setTint(mContext.getColor(R.color.system_on_primary_dark))
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun getQsPanelTileView(tile: Any?, mQsPanelController: Any): Any? {
            try {
                val mRecords = getObjectField(mQsPanelController, "mRecords") as ArrayList<*>
                for (record in mRecords) {
                    val recordtile = getObjectField(record, "tile")
                    if (recordtile === tile) {
                        return getObjectField(record, "tileView")
                    }
                }
                return null
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return null
            }
        }

        fun getQuickQSPanelTileView(tile: Any?, mQuickQSPanelController: Any): Any? {
            try {
                val mRecords = getObjectField(mQuickQSPanelController, "mRecords") as ArrayList<*>
                for (record in mRecords) {
                    val recordtile = getObjectField(record, "tile")
                    if (recordtile == tile) {
                        return getObjectField(record, "tileView")
                    }
                }
                return null
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return null
            }
        }

        fun areThereTiles(mRecords: ArrayList<*>): Boolean {
            try {
                return mRecords.isNotEmpty()
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        fun getPhantomTopPosition(index: Int, qqsLayout: Any): Int {
            try {
                val mColumns = getIntField(qqsLayout, "mColumns")
                val row = index / mColumns
                return getRowTop(row, qqsLayout)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 0
            }
        }

        fun getRowTop(row: Int, qqsLayout: Any): Int {
            try {
                val mSquishinessFraction = getFloatField(qqsLayout, "mSquishinessFraction")
                val mCellHeight = getIntField(qqsLayout, "mCellHeight")
                val mCellMarginVertical = getIntField(qqsLayout, "mCellMarginVertical")
                val scale: Float = constrainSquishiness(mSquishinessFraction)
                return (row * (mCellHeight * scale.toInt() + mCellMarginVertical))
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 0
            }
        }

        fun updateTileMargins(sideLabelTileLayout: Any) {
            try {
                val mContext = getObjectField(sideLabelTileLayout, "mContext") as Context

                if (mQsStyleConfig != 0) {
                    val mResourceCellHeight = mContext.resources.getDimensionPixelSize(QSTileHeight)
                    setIntField(sideLabelTileLayout, "mResourceCellHeight", mResourceCellHeight)

                    setIntField(
                        sideLabelTileLayout,
                        "mCellMarginHorizontal",
                        mContext.resources.getDimensionPixelSize(QSCellMarginHorizontal)
                    )

                    setIntField(
                        sideLabelTileLayout,
                        "mCellMarginVertical",
                        mContext.resources.getDimensionPixelSize(QSCellMarginVertical)
                    )
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private fun constrainSquishiness(squish: Float): Float {
            try {
                return 0.1f + squish * 0.9f
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 0f
            }
        }

        private fun getNumTilesFirstPage(pagedTileLayout: Any): Int {
            try {
                val mPages = getObjectField(pagedTileLayout, "mPages") as ArrayList<*>
                val firstPage = callMethod(mPages, "get", 0)
                val firstPageTiles = getObjectField(firstPage, "mRecords")
                return if (mPages.isEmpty()) 0 else callMethod(firstPageTiles, "size") as Int
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 0
            }
        }

        fun getQsRowCount(mContext: Context, mView: String): Int {
            try {

                val configuration = mContext.resources.configuration

                if (mView == "QS") {

                    return if (configuration.orientation == ORIENTATION_PORTRAIT) mQsRowsConfig else 2

                } else if (mView == "QQS") {

                    return if (configuration.orientation == ORIENTATION_PORTRAIT) mQQsRowsConfig else 1
                }

                return 4
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 4
            }
        }

        fun getQsColumnCount(mContext: Context, mView: String): Int {
            try {

                val configuration = mContext.resources.configuration

                if (mView == "QS") {

                    return if (configuration.orientation == ORIENTATION_PORTRAIT) mQsColumnsConfig else mQsColumnsConfigLandscape

                } else if (mView == "QQS") {

                    return if (configuration.orientation == ORIENTATION_PORTRAIT) mQQsColumnsConfig else mQQsColumnsConfigLandscape
                }

                return 2
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 2
            }
        }

        fun setQQsPanelMaxTiles(qqsPanelController: Any) {
            try {
                val mView = getObjectField(qqsPanelController, "mView") as View

                val mMediaHost = getObjectField(qqsPanelController, "mMediaHost")

                if (callMethod(mMediaHost, "getVisible") as Boolean) {

                    // We need to hardcode 2 columns and 2 rows when landscape and media is playing
                    if (mView.resources.configuration.orientation == ORIENTATION_LANDSCAPE) {

                        setIntField(
                            QuickQSPanelQQSSideLabelTileLayout, "mMaxAllowedRows", 2
                        )

                        setIntField(mView, "mMaxTiles", 4)
                        callMethod(qqsPanelController, "setTiles")

                    } else {

                        setIntField(
                            QuickQSPanelQQSSideLabelTileLayout,
                            "mMaxAllowedRows",
                            getQsRowCount(mView.context, "QQS")
                        )

                        val totalTiles = getQsRowCount(
                            mView.context, "QQS"
                        ) * getQsColumnCount(
                            mView.context, "QQS"
                        )

                        val maxTiles = getIntField(mView, "mMaxTiles")
                        if (maxTiles != totalTiles) {
                            setIntField(mView, "mMaxTiles", totalTiles)
                            callMethod(qqsPanelController, "setTiles")
                        }
                    }
                } else {

                    setIntField(
                        QuickQSPanelQQSSideLabelTileLayout,
                        "mMaxAllowedRows",
                        getQsRowCount(mView.context, "QQS")
                    )

                    val totalTiles = getQsRowCount(
                        mView.context, "QQS"
                    ) * getQsColumnCount(
                        mView.context, "QQS"
                    )

                    val maxTiles = getIntField(mView, "mMaxTiles")
                    if (maxTiles != totalTiles) {
                        setIntField(mView, "mMaxTiles", totalTiles)
                        callMethod(qqsPanelController, "setTiles")
                    }
                }

                callMethod(qqsPanelController, "updateMediaExpansion")
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private fun getColumnCount(pagedTileLayout: Any): Int {
            try {
                val mPages = getObjectField(pagedTileLayout, "mPages") as ArrayList<*>
                val firstPage = callMethod(mPages, "get", 0)
                val firstPageColumns = getIntField(firstPage, "mColumns")
                return if (mPages.isEmpty()) 0 else firstPageColumns
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return 0
            }
        }

        private fun isIconInAnimatedRow(count: Int, qSAnimator: Any): Boolean {
            try {
                val mPagedLayout = getObjectField(qSAnimator, "mPagedLayout") ?: return false
                val columnCount: Int = getColumnCount(mPagedLayout)
                val mNumQuickTiles = getIntField(qSAnimator, "mNumQuickTiles")
                return count < (mNumQuickTiles + columnCount - 1) / columnCount * columnCount
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        fun updateFirstNotificationExpansion() {
            try {
                val mAlwaysExpandNonGroupedNotification =
                    getBooleanField(RowAppearanceCoordinator, "mAlwaysExpandNonGroupedNotification")

                val notificationEntries = getObjectField(
                    NotifCollection, "mReadOnlyNotificationSet"
                ) as Collection<Any?>

                val entryToExpand = getObjectField(RowAppearanceCoordinator, "entryToExpand")

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

                                val shouldWeExpand =
                                    (mAlwaysExpandNonGroupedNotification || (mExpandFirstNotification && notificationEntry == entryToExpand))

                                // If there is a difference between if the notification is expanded
                                // and whether it should be or not, then set the opposite
                                if (shouldWeExpand != isSystemExpanded) {

                                    val isExpanded = callMethod(
                                        expandableNotificationRow, "isExpanded", false
                                    )

                                    setBooleanField(
                                        expandableNotificationRow,
                                        "mIsSystemExpanded",
                                        shouldWeExpand
                                    )

                                    callMethod(
                                        expandableNotificationRow, "notifyHeightChanged", false
                                    )

                                    callMethod(
                                        expandableNotificationRow,
                                        "onExpansionChanged",
                                        false,
                                        isExpanded
                                    )

                                    val mIsSummaryWithChildren = getBooleanField(
                                        expandableNotificationRow, "mIsSummaryWithChildren"
                                    )

                                    if (mIsSummaryWithChildren) {

                                        val mChildrenContainer = getObjectField(
                                            expandableNotificationRow, "mChildrenContainer"
                                        )

                                        callMethod(mChildrenContainer, "updateGroupOverflow")
                                        callMethod(mChildrenContainer, "updateExpansionStates")

                                    }
                                }

                                val mAssistantFeedbackController = getObjectField(
                                    RowAppearanceCoordinator, "mAssistantFeedbackController"
                                )

                                val feedbackIcon = callMethod(
                                    getObjectField(mAssistantFeedbackController, "mIcons"),
                                    "get",
                                    callMethod(
                                        mAssistantFeedbackController,
                                        "getFeedbackStatus",
                                        getObjectField(notificationEntry, "mRanking")
                                    )
                                )

                                val mIsSummaryWithChildren = getBooleanField(
                                    expandableNotificationRow, "mIsSummaryWithChildren"
                                )

                                if (mIsSummaryWithChildren) {
                                    val mChildrenContainer = getObjectField(
                                        expandableNotificationRow, "mChildrenContainer"
                                    )

                                    val mGroupHeaderWrapper =
                                        getObjectField(mChildrenContainer, "mGroupHeaderWrapper")

                                    if (mGroupHeaderWrapper != null) callMethod(
                                        mGroupHeaderWrapper, "setFeedbackIcon", feedbackIcon
                                    )

                                    val mMinimizedGroupHeaderWrapper = getObjectField(
                                        mChildrenContainer, "mMinimizedGroupHeaderWrapper"
                                    )

                                    if (mMinimizedGroupHeaderWrapper != null) callMethod(
                                        mMinimizedGroupHeaderWrapper,
                                        "setFeedbackIcon",
                                        feedbackIcon
                                    )
                                }

                                val mPrivateLayout =
                                    getObjectField(expandableNotificationRow, "mPrivateLayout")

                                if (getObjectField(
                                        mPrivateLayout, "mContractedChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPrivateLayout, "mContractedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                                if (getObjectField(
                                        mPrivateLayout, "mExpandedChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPrivateLayout, "mExpandedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                                if (getObjectField(
                                        mPrivateLayout, "mHeadsUpChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPrivateLayout, "mHeadsUpWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                                val mPublicLayout =
                                    getObjectField(expandableNotificationRow, "mPublicLayout")

                                if (getObjectField(
                                        mPublicLayout, "mContractedChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPublicLayout, "mContractedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                                if (getObjectField(
                                        mPublicLayout, "mExpandedChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPublicLayout, "mExpandedWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )

                                if (getObjectField(
                                        mPublicLayout, "mHeadsUpChild"
                                    ) != null
                                ) callMethod(
                                    getObjectField(mPublicLayout, "mHeadsUpWrapper"),
                                    "setFeedbackIcon",
                                    feedbackIcon
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun updateNotificationSectionHeaders() {
            try {
                val state = callMethod(
                    getObjectField(KeyguardCoordinator, "statusBarStateController"), "getState"
                )

                // If in normal shade view then hide the headers if required
                if (state == 0) {

                    val sectionHeaderVisibilityProvider =
                        getObjectField(KeyguardCoordinator, "sectionHeaderVisibilityProvider")
                    val neverShowSectionHeaders =
                        getBooleanField(sectionHeaderVisibilityProvider, "neverShowSectionHeaders")

                    // If we arent always hiding headers then set according to tweak value
                    if (!neverShowSectionHeaders) {

                        val areSectionHeadersVisible = getBooleanField(
                            sectionHeaderVisibilityProvider, "sectionHeadersVisible"
                        )

                        if (areSectionHeadersVisible != mNotificationSectionHeaders) {

                            setBooleanField(
                                sectionHeaderVisibilityProvider,
                                "sectionHeadersVisible",
                                mNotificationSectionHeaders
                            )
                            callMethod(
                                getObjectField(KeyguardCoordinator, "notifFilter"),
                                "invalidateList",
                                "onStatusBarStateChanged"
                            )

                        }
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun updateNotificationExpansion() {
            try {
                setBooleanField(
                    RowAppearanceCoordinator,
                    "mAlwaysExpandNonGroupedNotification",
                    mExpandAllNotifcations
                )

                val notificationEntries = getObjectField(
                    NotifCollection, "mReadOnlyNotificationSet"
                ) as Collection<Any?>

                for (notificationEntry in notificationEntries.toTypedArray()) {
                    val expandableNotifictionRowController = getObjectField(
                        notificationEntry, "mRowController"
                    )
                    if (expandableNotifictionRowController != null) {
                        val expandableNotifictionRow = getObjectField(
                            expandableNotifictionRowController, "mView"
                        )
                        if (expandableNotifictionRow != null) {
                            callMethod(
                                expandableNotifictionRow,
                                "setUserExpanded",
                                mExpandAllNotifcations,
                                mExpandAllNotifcations
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private fun adjustBrightness(x: Int, context: Context) {
            try {
                brightnessChanged = true
                val displayWidth = getFloatField(
                    getObjectField(CentralSurfacesImpl, "mDisplayMetrics"), "widthPixels"
                )
                val raw = x / displayWidth
                val padded = 0.85f.coerceAtMost(BRIGHTNESS_CONTROL_PADDING.coerceAtLeast(raw))
                val value = (padded - BRIGHTNESS_CONTROL_PADDING) / 0.7f
                val linearFloat = callStaticMethod(
                    findClass(BRIGHTNESS_UTILS_CLASS, SystemUIContext.classLoader),
                    "convertGammaToLinearFloat",
                    (65535.0f * value).roundToInt(),
                    minimumBacklight,
                    maximumBacklight
                ) as Float
                currentBrightness = linearFloat
                val displayManager =
                    context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                callMethod(
                    displayManager, "setTemporaryBrightness", displayId, linearFloat
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val brightnessControl = BrightnessControl(linearFloat)
                    brightnessControl.adjustBrightness()
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun brightnessControl(event: MotionEvent) {
            try {
                val mHandler = getObjectField(
                    getObjectField(CentralSurfacesImpl, "mCommandQueue"), "mHandler"
                ) as Handler
                val context = getObjectField(CentralSurfacesImpl, "mContext") as Context
                val action = event.action
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                if (action == MotionEvent.ACTION_DOWN) {
                    if (y < quickQsOffsetHeight) {
                        linger = 0
                        initialTouchX = x
                        initialTouchY = y
                        justPeeked = true
                        mHandler.removeCallbacks(longPressBrightnessChange)
                        mHandler.postDelayed(
                            longPressBrightnessChange,
                            BRIGHTNESS_CONTROL_LONG_PRESS_TIMEOUT.toLong()
                        )
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    val i = quickQsOffsetHeight
                    if (y < i && justPeeked) {
                        if (linger > 20) {
                            adjustBrightness(x, context)
                            return
                        }
                        val xDiff = abs(x - initialTouchX)
                        val yDiff = abs(y - initialTouchY)
                        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
                        if (xDiff > yDiff) {
                            linger++
                        }
                        if (xDiff > touchSlop || yDiff > touchSlop) {
                            mHandler.removeCallbacks(longPressBrightnessChange)
                            return
                        }
                        return
                    }
                    if (y > i) {
                        justPeeked = false
                    }
                    mHandler.removeCallbacks(longPressBrightnessChange)
                } else if (action == 1 || action == 3) {
                    mHandler.removeCallbacks(longPressBrightnessChange)
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        private val longPressBrightnessChange: Runnable = Runnable {
            try {
                callMethod(
                    PhoneStatusBarView, "performHapticFeedback", HapticFeedbackConstants.LONG_PRESS
                )
                adjustBrightness(initialTouchX, SystemUIContext)
                linger = BRIGHTNESS_CONTROL_LINGER_THRESHOLD + 1
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun onBrightnessChanged(upOrCancel: Boolean, context: Context) {
            try {
                if (brightnessChanged && upOrCancel) {
                    brightnessChanged = false
                    val isExpandedVisible = getBooleanField(ShadeController, "mExpandedVisible")
                    if (justPeeked && isExpandedVisible) {
                        callMethod(
                            NotificationPanelViewController, "fling", 10, 1.0f, false, false
                        )
                    }
                    val displayManager =
                        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                    callMethod(
                        displayManager, "setBrightness", displayId, currentBrightness
                    )
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }

        fun adjustDisableFlags(mRemoteInputQuickSettingsDisabler: Any, state: Int): Int {
            try {
                var mutableState = state
                val remoteInputActive =
                    getBooleanField(mRemoteInputQuickSettingsDisabler, "remoteInputActive")
                val isLandscape = getBooleanField(mRemoteInputQuickSettingsDisabler, "isLandscape")
                val shouldUseSplitNotificationShade = getBooleanField(
                    mRemoteInputQuickSettingsDisabler, "shouldUseSplitNotificationShade"
                )
                if (remoteInputActive && isLandscape && !shouldUseSplitNotificationShade) {
                    mutableState = state or DISABLE2_QUICK_SETTINGS
                }
                return mutableState
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return -1
            }
        }

        private fun adjustQsDisableFlags(state2: Int): Int {
            try {
                return if (mDisableLockscreenQuicksettings && mKeyguardShowing) {
                    DISABLE2_QUICK_SETTINGS
                } else {
                    state2
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return state2
            }
        }

        private fun shouldFullyExpandDueQuickPulldown(
            quickSettingsController: Any, event: MotionEvent
        ): Boolean {
            try {
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
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        private fun shouldFullyExpandDueSmartPulldown(): Boolean {
            try {
                val hasNonClearableAlertingNotifs = getBooleanField(
                    NotifStats, "hasNonClearableAlertingNotifs"
                )
                val hasClearableAlertingNotifs = getBooleanField(
                    NotifStats, "hasClearableAlertingNotifs"
                )

                return when (mSmartPulldownConfig) {
                    1 -> !hasNonClearableAlertingNotifs && !hasClearableAlertingNotifs
                    2 -> numActiveNotifs == 0
                    else -> false
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
                return false
            }
        }
    }

    class BrightnessControl(private val value: Float) {
        fun adjustBrightness() {
            try {
                val context = getObjectField(CentralSurfacesImpl, "mContext") as Context
                Settings.System.putFloat(
                    context.contentResolver, "screen_brightness_float", value
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext,
                    "Function Error",
                    "$funcName - ${e.toString()}",
                    LogEntryType.ERROR
                )
            }
        }
    }
}