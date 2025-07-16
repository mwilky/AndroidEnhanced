package com.mwilky.androidenhanced

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.provider.Settings.System.FONT_SCALE
import android.util.ArrayMap
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.mwilky.androidenhanced.HookedClasses.Companion.QUICK_QS_PANEL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.STATUSBAR_ICON_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSTEM_UI_APPLICATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSUI_DARK_ICON_DISPATCHER_DARK_CHANGE_CLASS
import com.mwilky.androidenhanced.References.Companion.BatteryMeterView
import com.mwilky.androidenhanced.References.Companion.CollapsedStatusBarFragment
import com.mwilky.androidenhanced.References.Companion.CurrentTilesInteractorImpl
import com.mwilky.androidenhanced.References.Companion.DarkIconDispatcherImpl
import com.mwilky.androidenhanced.References.Companion.DisplayRotation
import com.mwilky.androidenhanced.References.Companion.Interpolators
import com.mwilky.androidenhanced.References.Companion.KeyguardStatusBarView
import com.mwilky.androidenhanced.References.Companion.ModernShadeCarrierGroupMobileView
import com.mwilky.androidenhanced.References.Companion.QSAnimator
import com.mwilky.androidenhanced.References.Companion.QSCustomizerController3
import com.mwilky.androidenhanced.References.Companion.QSPanelController
import com.mwilky.androidenhanced.References.Companion.QuickQSPanelController
import com.mwilky.androidenhanced.References.Companion.QuickQSPanelQQSSideLabelTileLayout
import com.mwilky.androidenhanced.References.Companion.ShadeHeaderController
import com.mwilky.androidenhanced.References.Companion.SystemUIContext
import com.mwilky.androidenhanced.References.Companion.TileList
import com.mwilky.androidenhanced.References.Companion.TintedIconManager
import com.mwilky.androidenhanced.References.Companion.TouchAnimatorBuilder
import com.mwilky.androidenhanced.References.Companion.mBrightnessMirrorHandler
import com.mwilky.androidenhanced.References.Companion.mDefaultClockContainer
import com.mwilky.androidenhanced.References.Companion.mQQsBrightnessMirrorHandler
import com.mwilky.androidenhanced.Utils.Companion.WAKE_REASON_LIFT
import com.mwilky.androidenhanced.Utils.Companion.WAKE_REASON_TAP
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import com.mwilky.androidenhanced.shared.R
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.hookMethod
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.findMethodExactIfExists
import de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField
import de.robv.android.xposed.XposedHelpers.getAdditionalStaticField
import de.robv.android.xposed.XposedHelpers.getFloatField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticObjectField
import de.robv.android.xposed.XposedHelpers.newInstance
import de.robv.android.xposed.XposedHelpers.setIntField
import de.robv.android.xposed.XposedHelpers.setObjectField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class Utils() {

    companion object {

        var TAG = "Android Enhanced:"
        const val SHAREDPREFS = "prefs"

        val supportedDevices: List<String> = listOf("Pixel 9", "Pixel 8", "Pixel 7", "Pixel 6")
        var mIsInitialBoot = true

        // App related Keys
        const val ISONBOARDINGCOMPLETEDKEY = "isOnboardingComplete"
        const val LASTBACKUP = "lastBackupDate"
        const val LOGSKEY = "logs"
        const val BOOTCOMPLETED = "bootCompleted"
        const val ISPREMIUM = "isPremium"
        const val PREVIOUSISPREMIUM = "previousIsPremium"
        const val ISSUBSCRIPTION = "isSubscription"
        const val ISONETIMEPURCHASE = "isOneTimePurchase"
        const val BOOTTIME = "bootTime"
        const val UNSUPPORTEDDEVICEDIALOGSHOWN = "unsupportedDeviceDialogShown"


        // Constants
        const val MSG_TOGGLE_TORCH = 100

        /**
         * Wake up reason code: Waking up due to power button press.
         * @hide
         */
        const val WAKE_REASON_POWER_BUTTON: Int = 1

        /**
         * Wake up reason code: Waking up because an application requested it.
         * @hide
         */
        const val WAKE_REASON_APPLICATION: Int = 2

        /**
         * Wake up reason code: Waking up due to being plugged in or docked on a wireless charger.
         * @hide
         */
        const val WAKE_REASON_PLUGGED_IN: Int = 3

        /**
         * Wake up reason code: Waking up due to a user performed gesture. This includes user
         * interactions with UI on the screen such as the notification shade. This does not include
         * [WAKE_REASON_TAP] or [WAKE_REASON_LIFT].
         * @hide
         */
        const val WAKE_REASON_GESTURE: Int = 4

        /**
         * Wake up reason code: Waking up due to the camera being launched.
         * @hide
         */
        const val WAKE_REASON_CAMERA_LAUNCH: Int = 5

        /**
         * Wake up reason code: Waking up due to the user single or double tapping on the screen. This
         * wake reason is used when the user is not tapping on a specific UI element; rather, the device
         * wakes up due to a generic tap on the screen.
         * @hide
         */
        const val WAKE_REASON_TAP: Int = 15

        /**
         * Wake up reason code: Waking up due to a user performed lift gesture.
         * @hide
         */
        const val WAKE_REASON_LIFT: Int = 16

        /**
         * Wake up reason code: Waking up due to a user interacting with a biometric.
         * @hide
         */
        const val WAKE_REASON_BIOMETRIC: Int = 17

        const val MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK = 200

        //Tweaks Keys
        //Gestures
        const val gestureSleep = "gesture_Sleep"

        //Launcher
        const val doubleTapToSleepLauncher = "bool_DoubleTapToSleeplauncher"

        //Framework
        const val torchPowerScreenOff = "bool_LongPressPowerTorchScreenOff"
        const val torchAutoOffScreenOn = "bool_TorchAutoOffScreenOn"
        const val torchAutoOffScreenOnLift = "bool_TorchAutoOffScreenOnLift"
        const val torchAutoOffScreenOnBiometric = "bool_TorchAutoOffScreenOnBiometric"
        const val torchAutoOffScreenOnPlugIn = "bool_TorchAutoOffScreenOnPlugIn"
        const val torchAutoOffScreenOnPowerButton = "bool_TorchAutoOffScreenOnPowerButton"
        const val torchAutoOffScreenOnApplication = "bool_TorchAutoOffScreenOnApplication"
        const val torchAutoOffScreenOnTap = "bool_TorchAutoOffScreenOnTap"
        const val torchAutoOffScreenOnCameraLaunch = "bool_TorchAutoOffScreenOnCameraLaunch"
        const val torchAutoOffScreenOnGesture = "bool_TorchAutoOffScreenOnGesture"
        const val torchAutoOffScreenOnOther = "bool_TorchAutoOffScreenOnOther"
        const val volKeyMediaControl = "bool_VolKeyMediaControl"
        const val allowAllRotations = "bool_AllowAllRotations"
        const val disableSecureScreenshots = "bool_DisableSecureScreenshots"
        const val disableCameraScreenOff = "bool_DisableCameraScreenOff"

        //SystemUI
        const val doubleTapToSleep = "bool_DoubleTapToSleep"
        const val statusbarBrightnessControl = "bool_StatusbarBrightnessControl"
        const val statusBarClockPosition = "int_StatusbarClockPosition"
        const val statusBarClockSeconds = "bool_StatusbarClockSeconds"
        const val hideLockscreenStatusBar = "bool_HideLockscreenStatusbar"
        const val scrambleKeypad = "bool_ScrambleKeypad"
        const val disableLockscreenPowerMenu = "bool_DisableLockscreenPowerMenu"
        const val qsClickVibration = "bool_QsTileVibration"
        const val disableQsLockscreen = "bool_DisableQsLockscreen"
        const val hideQsFooterBuildNumber = "bool_HideQsFooterBuildNumber"
        const val smartPulldown = "int_SmartPulldown"
        const val quickPulldown = "int_QuickPulldown"
        const val muteScreenOnNotifications = "bool_MuteScreenOnNotifications"
        const val expandAllNotifications = "bool_ExpandAllNotifications"
        const val qsStyle = "int_QsStyle"
        const val qsIconContainerActiveShape = "int_QsIconContainerActiveShape"
        const val qsIconContainerInactiveShape = "int_QsIconContainerInactiveShape"
        const val qsIconContainerUnavailableShape = "int_QsIconContainerUnavailableShape"
        const val qqsRows = "int_QQsRows"
        const val qsRows = "int_QsRows"
        const val qsColumns = "int_QSColumns"
        const val qsColumnsLandscape = "int_QSColumnsLandscape"
        const val qqsColumns = "int_QQSColumns"
        const val qqsColumnsLandscape = "int_QQSColumnsLandscape"
        const val qsBrightnessSliderPosition = "int_QSBrightnessSliderPosition"
        const val qqsBrightnessSlider = "boolean_QQSBrightnesSlider"
        const val customStatusbarClockColor = "int_CustomStatusbarClockColor"
        const val customStatusbarBatteryIconColor = "int_CustomStatusbarBatteryIconColor"
        const val customStatusbarBatteryPercentColor = "int_CustomStatusbarBatteryPercentColor"
        const val customStatusbarWifiIconColor = "int_CustomStatusbarWifiIconColor"
        const val customStatusbarMobileIconColor = "int_CustomStatusbarMobileIconColor"
        const val customStatusbarNotificationIconColor = "int_CustomStatusbarNotificationIconColor"
        const val customStatusbarOtherIconColor = "int_CustomStatusbarOtherIconColor"
        const val customStatusbarDndIconColor = "int_CustomStatusbarDndIconColor"
        const val customStatusbarAirplaneIconColor = "int_CustomStatusbarAirplaneIconColor"
        const val customStatusbarHotspotIconColor = "int_CustomStatusbarHotspotIconColor"
        const val customStatusbarBluetoothIconColor = "int_CustomStatusbarBluetoothIconColor"
        const val customLsStatusbarBatteryIconColor = "int_CustomLsStatusbarBatteryIconColor"
        const val customLsStatusbarBatteryPercentColor = "int_CustomLsStatusbarBatteryPercentColor"
        const val customLsStatusbarWifiIconColor = "int_CustomLsStatusbarWifiIconColor"
        const val customLsStatusbarMobileIconColor = "int_CustomLsStatusbarMobileIconColor"
        const val customLsStatusbarOtherIconColor = "int_CustomLsStatusbarOtherIconColor"
        const val customLsStatusbarDndIconColor = "int_CustomLsStatusbarDndIconColor"
        const val customLsStatusbarAirplaneIconColor = "int_CustomLsStatusbarAirplaneIconColor"
        const val customLsStatusbarHotspotIconColor = "int_CustomLsStatusbarHotspotIconColor"
        const val customLsStatusbarBluetoothIconColor = "int_CustomLsStatusbarBluetoothIconColor"
        const val customLsStatusbarCarrierColor = "int_CustomLsStatusbarBCarrierColor"
        const val customQsStatusbarClockColor = "int_CustomQsStatusbarClockColor"
        const val customQsStatusbarBatteryIconColor = "int_CustomQsStatusbarBatteryIconColor"
        const val customQsStatusbarBatteryPercentColor = "int_CustomQsStatusbarBatteryPercentColor"
        const val customQsStatusbarWifiIconColor = "int_CustomQsStatusbarWifiIconColor"
        const val customQsStatusbarMobileIconColor = "int_CustomQsStatusbarMobileIconColor"
        const val customQsStatusbarCarrierColor = "int_CustomQsStatusbarCarrierColor"
        const val customQsStatusbarDateColor = "int_CustomQsStatusbarDateColor"
        const val customQsStatusbarOtherIconColor = "int_CustomQsStatusbarOtherIconColor"
        const val customQsStatusbarDndIconColor = "int_CustomQsStatusbarDndIconColor"
        const val customQsStatusbarAirplaneIconColor = "int_CustomQsStatusbarAirplaneIconColor"
        const val customQsStatusbarHotspotIconColor = "int_CustomQsStatusbarHotspotIconColor"
        const val customQsStatusbarBluetoothIconColor = "int_CustomQsStatusbarBluetoothIconColor"
        const val customStatusbarGlobalIconColor = "int_CustomStatusbarGlobalIconColor"
        const val customQsStatusbarGlobalIconColor = "int_CustomQsStatusbarGlobalIconColor"
        const val customLsStatusbarGlobalIconColor = "int_CustomLsStatusbarGlobalIconColor"
        const val hideCollapsedAlarmIcon = "bool_HideCollapsedAlarmIcon"
        const val hideCollapsedVolumeIcon = "bool_HideCollapsedVolumeIcon"
        const val hideCollapsedCallStrengthIcon = "bool_HideCollapsedCallStrengthIcon"
        const val iconBlacklist = "str_IconBlacklist"
        const val statusbarIconAccentColor = "bool_StatusbarIconAccentColor"
        const val qsStatusbarIconAccentColor = "bool_QsStatusbarIconAccentColor"
        const val lsStatusbarIconAccentColor = "bool_LsStatusbarIconAccentColor"
        const val statusbarIconDarkColor = "int_StatusbarIconDarkColor"
        const val useDualStatusbarColors = "bool_DualStatusbarColors"
        const val notifSectionHeaders = "bool_NotifSectionHeaders"
        const val autoExpandFirstNotif = "bool_AutoExpandFirstNotif"
        const val dualToneQsPanel = "bool_DualToneQsPanel"
        const val qsScrimAlpha = "QsScrimAlpha"
        const val notifScrimAlpha = "NotifScrimAlpha"

        // Initial values
        var mStatusbarClockPosition: Int = 0
        var mStatusbarClockSeconds: Boolean = false
        var mDoubleTapToSleep: Boolean = false
        var mStatusbarIconUseAccentColor = false
        var mQsStatusbarIconUseAccentColor = false
        var mLsStatusbarIconUseAccentColor = false
        var mStatusbarBrightnessControl = false
        var mExpandAllNotifcations = false
        var mExpandFirstNotification = true
        var mNotificationSectionHeaders = true
        var mNotificationScrimAlpha: Float = 1.0f
        var mQsScrimAlpha: Float = 1.0f
        var mDualColorQsPanel = false
        var mHideLockscreenStatusbar = false
        var mScrambleKeypad = false
        var mHideQsFooterBuildNumber: Boolean = false
        var mDisableCameraGestureWhenLocked: Boolean = false
        var mDisableSecureScreenshots = false
        var mAllowAllRotations = false
        var mDisableLockscreenPowerMenu = false
        var mDisableLockscreenQuicksettings = false
        var mQsClickVibration: Boolean = false
        var mSmartPulldownConfig: Int = 0
        var mQuickPulldownConfig: Int = 0
        var mQsBrightnessSliderPosition: Int = 0
        var mQQsRowsConfig: Int = 2
        var mQsColumnsConfig: Int = 2
        var mQsColumnsConfigLandscape: Int = 4
        var mQQsColumnsConfig: Int = 2
        var mQQsColumnsConfigLandscape: Int = 4
        var mQsRowsConfig: Int = 4
        var mQsStyleConfig: Int = 0
        var mQQsBrightnessSlider: Boolean = false
        var mQsIconContainerActiveShapeConfig: Int = 0
        var mQsIconContainerInactiveShapeConfig: Int = 0
        var mQsIconContainerUnavailableShapeConfig: Int = 0
        var mDoubleTapSleepLauncher = false
        var mTorchPowerScreenOff = false
        var mTorchPowerScreenOffLift = true
        var mTorchPowerScreenOffBiometric = true
        var mTorchPowerScreenOffPlugIn = true
        var mTorchPowerScreenOffPowerButton = true
        var mTorchPowerScreenOffApplication = true
        var mTorchPowerScreenOffCameraLaunch = true
        var mTorchPowerScreenOffGesture = true
        var mTorchPowerScreenOffOther = true
        var mTorchPowerScreenOffTap = true
        var mTorchAutoOff = false
        var mVolKeyMedia = false
        var mMuteScreenOnNotifications = false
        var mStatusbarClockColor: Int = Color.WHITE
        var mStatusbarBatteryIconColor: Int = Color.WHITE
        var mStatusbarBatteryPercentColor: Int = Color.WHITE
        var mStatusbarIconColor: Int = Color.WHITE
        var mStatusbarNotificationColor: Int = Color.WHITE
        var mStatusbarWifiColor: Int = Color.WHITE
        var mStatusbarMobileColor: Int = Color.WHITE
        var mStatusbarDndColor: Int = Color.WHITE
        var mStatusbarAirplaneColor: Int = Color.WHITE
        var mStatusbarHotspotColor: Int = Color.WHITE
        var mStatusbarBluetoothColor: Int = Color.WHITE
        var mStatusbarDarkIconColor: Int = -1728053248
        var mDualStatusbarColors: Boolean = true
        var mLsStatusbarBatteryIconColor: Int = Color.WHITE
        var mLsStatusbarBatteryPercentColor: Int = Color.WHITE
        var mLsStatusbarIconColor: Int = Color.WHITE
        var mLsStatusbarWifiColor: Int = Color.WHITE
        var mLsStatusbarMobileColor: Int = Color.WHITE
        var mLsStatusbarDndColor: Int = Color.WHITE
        var mLsStatusbarAirplaneColor: Int = Color.WHITE
        var mLsStatusbarHotspotColor: Int = Color.WHITE
        var mLsStatusbarBluetoothColor: Int = Color.WHITE
        var mLsStatusbarCarrierColor: Int = Color.WHITE
        var mQsStatusbarClockColor: Int = Color.WHITE
        var mQsStatusbarBatteryIconColor: Int = Color.WHITE
        var mQsStatusbarBatteryPercentColor: Int = Color.WHITE
        var mQsStatusbarIconColor: Int = Color.WHITE
        var mQsStatusbarCarrierColor: Int = Color.WHITE
        var mQsStatusbarDateColor: Int = Color.WHITE
        var mQsStatusbarWifiColor: Int = Color.WHITE
        var mQsStatusbarMobileColor: Int = Color.WHITE
        var mQsStatusbarDndColor: Int = Color.WHITE
        var mQsStatusbarAirplaneColor: Int = Color.WHITE
        var mQsStatusbarHotspotColor: Int = Color.WHITE
        var mQsStatusbarBluetoothColor: Int = Color.WHITE
        var mStatusbarGlobalColor: Int = Color.WHITE
        var mQsStatusbarGlobalColor: Int = Color.WHITE
        var mLsStatusbarGlobalColor: Int = Color.WHITE
        var mStatusbarClockColorAdapted: Int = Color.WHITE
        var mStatusbarBatteryIconColorAdapted: Int = Color.WHITE
        var mStatusbarBatteryPercentColorAdapted: Int = Color.WHITE
        var mStatusbarNotificationColorAdapted: Int = Color.WHITE
        var mStatusbarWifiColorAdapted: Int = Color.WHITE
        var mStatusbarMobileColorAdapted: Int = Color.WHITE
        var mStatusbarIconColorAdapted: Int = Color.WHITE
        var mStatusbarDndColorAdapted: Int = Color.WHITE
        var mStatusbarAirplaneColorAdapted: Int = Color.WHITE
        var mStatusbarHotspotColorAdapted: Int = Color.WHITE
        var mStatusbarBluetoothColorAdapted: Int = Color.WHITE
        var mBatteryDarkIntensity: Float = 0f
        var mHideCollapsedAlarm: Boolean = true
        var mHideCollapsedVolume: Boolean = true
        var mHideCollapsedCallStrength: Boolean = true


        // Resources
        var QSQuickTileSize = 0
        var QSLabelContainerMargin = 0
        var QSTilePadding = 0
        var QSTileTextSize = 0
        var QSTileHeight = 0
        var QSCellMarginHorizontal = 0
        var QSCellMarginVertical = 0
        var QSTileTextLineHeight = 0
        var iconContainerBackground = 0
        var iconContainerBackgroundShape = 0

        // Flag to show whether on keyguard
        var mKeyguardShowing: Boolean = false

        lateinit var mVibrator: Vibrator

        fun initVibrator(context: Context) {
            if (!::mVibrator.isInitialized) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                mVibrator = vibratorManager.defaultVibrator
            }
        }

        fun isDeviceSupported(): Boolean {
            return supportedDevices.any { Build.MODEL.contains(it, ignoreCase = true) }
        }

        @SuppressLint("DiscouragedPrivateApi")
        fun lockDevice(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            try {
                val m = PowerManager::class.java.getDeclaredMethod(
                    "goToSleep",
                    Long::class.javaPrimitiveType
                )
                m.isAccessible = true
                m.invoke(pm, SystemClock.uptimeMillis())
            } catch (t: Throwable) {
            }
        }

        fun isDarkMode(mContext: Context): Boolean {
            return mContext.resources.configuration.isNightModeActive
        }

        fun setIconBlacklist(mContext: Context, iconBlacklist: String) {
            try {

                val currentBlockedIcons: String =
                    Settings.Secure.getString(mContext.contentResolver, "icon_blacklist") ?: ""

                val blockedIconsList = currentBlockedIcons.split(",")
                    .filter { it.isNotBlank() } // Filter out any blank strings
                    .map { it.trim() }.toMutableList()

                val newIconsList =
                    iconBlacklist.split(",").filter { it.isNotBlank() }.map { it.trim() }

                // We don't want to toggle values on the initial boot.
                for (icon in newIconsList) {
                    if (blockedIconsList.contains(icon)) {
                        blockedIconsList.remove(icon)
                    } else {
                        blockedIconsList.add(icon)
                    }
                }

                val updatedBlockedIcons = blockedIconsList.joinToString(",")

                Settings.Secure.putString(
                    mContext.contentResolver,
                    "icon_blacklist",
                    if (mIsInitialBoot) iconBlacklist else updatedBlockedIcons
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun applyAlpha(f: Float, i: Int): Int {
            return Color.argb(
                (f * Color.alpha(i)).toInt(), Color.red(i), Color.green(i), Color.blue(i)
            )
        }

        fun getColorAttrDefaultColor(context: Context, i: Int): Int {
            val obtainStyledAttributes = context.obtainStyledAttributes(intArrayOf(i))
            val color = obtainStyledAttributes.getColor(0, 0)
            obtainStyledAttributes.recycle()
            return color
        }

        fun convertLastBackupDate(dateFromSharedPrefs: String, context: Context): String {
            // Check if the savedDateStr is not empty
            if (dateFromSharedPrefs.isNotEmpty()) {
                try {
                    // Create a SimpleDateFormat for the "yyyyMMdd_HHmmss" format
                    val originalFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

                    // Parse the saved string into a Date object
                    val date = originalFormat.parse(dateFromSharedPrefs)

                    // Create a new SimpleDateFormat for the desired format
                    val desiredFormat =
                        SimpleDateFormat("HH:mm EEE dd MMM yyyy", Locale.getDefault())

                    // Format the Date object into the desired format
                    return desiredFormat.format(date)

                    // You can use formattedDate as needed in your app
                } catch (e: ParseException) {
                    e.printStackTrace()
                    // Handle parsing errors, if any
                    return context.resources.getString(R.string.never)
                }
            } else {
                return context.resources.getString(R.string.never)
            }
        }

        fun getIconColorForSlotName(
            slot: String, mContext: Context, location: String = "HOME"
        ): Int {
            try {
                val accentColor = if (isDarkMode(mContext)) getColorAttrDefaultColor(
                    mContext, mContext.resources.getIdentifier(
                        "colorAccent", "attr", "android"
                    )
                ) else mContext.getColor(android.R.color.system_primary_light)


                when (location) {
                    "QS" -> {
                        if (mQsStatusbarIconUseAccentColor) {

                            return accentColor
                        }

                        //Return the value of the Global color if it is anything but default.
                        if (mQsStatusbarGlobalColor != -1) return mQsStatusbarGlobalColor

                        val colorMap = mapOf(
                            "zen" to mQsStatusbarDndColor,
                            "airplane" to mQsStatusbarAirplaneColor,
                            "hotspot" to mQsStatusbarHotspotColor,
                            "bluetooth" to mQsStatusbarBluetoothColor,
                            "wifi" to mQsStatusbarWifiColor,
                            "mobile" to mQsStatusbarMobileColor,
                            "clock" to mQsStatusbarClockColor,
                            "battery" to mQsStatusbarBatteryIconColor,
                            "battery_percent" to mQsStatusbarBatteryPercentColor,
                            "carrier" to mQsStatusbarCarrierColor,
                            "date" to mQsStatusbarDateColor
                        )

                        var returnColor = colorMap[slot] ?: mQsStatusbarIconColor

                        // If white icons and in light theme and dual color qs panel is enabled, return dark.
                        if (returnColor == -1 && mDualColorQsPanel && !isDarkMode(
                                mContext
                            )
                        ) {
                            returnColor = -1728053248
                        }

                        return returnColor
                    }

                    "KEYGUARD" -> {
                        if (mLsStatusbarIconUseAccentColor) return accentColor

                        //Return the value of the Global color if it is anything but default.
                        if (mLsStatusbarGlobalColor != -1) return mLsStatusbarGlobalColor

                        val colorMap = mapOf(
                            "zen" to mLsStatusbarDndColor,
                            "airplane" to mLsStatusbarAirplaneColor,
                            "hotspot" to mLsStatusbarHotspotColor,
                            "bluetooth" to mLsStatusbarBluetoothColor,
                            "wifi" to mLsStatusbarWifiColor,
                            "mobile" to mLsStatusbarMobileColor,
                            "battery" to mLsStatusbarBatteryIconColor,
                            "battery_percent" to mLsStatusbarBatteryPercentColor,
                            "carrier" to mLsStatusbarCarrierColor,
                        )

                        return colorMap[slot] ?: mQsStatusbarIconColor
                    }

                    else -> {
                        if (mStatusbarIconUseAccentColor) return accentColor

                        //Return the value of the Global color if it is anything but default.
                        if (mStatusbarGlobalColor != -1) return mStatusbarGlobalColor

                        val colorMap = mapOf(
                            "zen" to mStatusbarDndColor,
                            "airplane" to mStatusbarAirplaneColor,
                            "hotspot" to mStatusbarHotspotColor,
                            "bluetooth" to mStatusbarBluetoothColor,
                            "wifi" to mStatusbarWifiColor,
                            "mobile" to mStatusbarMobileColor,
                            "clock" to mStatusbarClockColor,
                            "battery" to mStatusbarBatteryIconColor,
                            "battery_percent" to mStatusbarBatteryPercentColor,
                            "notifications" to mStatusbarNotificationColor
                        )

                        return colorMap[slot] ?: mStatusbarIconColor
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
                return Color.WHITE
            }
        }

        fun safeHookAllConstructors(
            context: Context,
            className: String,
            classLoader: ClassLoader,
            beforeHook: ((MethodHookParam) -> Unit)? = null,
            afterHook: ((MethodHookParam) -> Unit)? = null
        ): Boolean {
            try {
                // First, check if the class exists
                val targetClass = findClass(className, classLoader)

                // Check if the class has any constructors
                val constructors = targetClass.declaredConstructors
                if (constructors.isEmpty()) {
                    sendLogBroadcast(
                        context,
                        "Hook Error",
                        "No constructors found in class $className",
                        LogEntryType.ERROR
                    )
                    return false
                }

                // Hook all constructors
                hookAllConstructors(targetClass, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        try {
                            beforeHook?.invoke(param)
                        } catch (e: Exception) {
                            sendLogBroadcast(
                                context,
                                "Hook Runtime Error",
                                "Error in beforeHook for constructor in $className: ${e.message}",
                                LogEntryType.ERROR
                            )
                        }
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            afterHook?.invoke(param)
                        } catch (e: Exception) {
                            sendLogBroadcast(
                                context,
                                "Hook Runtime Error",
                                "Error in afterHook for constructor in $className: ${e.message}",
                                LogEntryType.ERROR
                            )
                        }
                    }
                })

                sendLogBroadcast(
                    context,
                    "Hook Success",
                    "All constructors (${constructors.size}) hooked successfully in $className",
                    LogEntryType.SUCCESS
                )
                return true

            } catch (e: ClassNotFoundError) {
                sendLogBroadcast(
                    context,
                    "Hook Error",
                    "Class $className not found: ${e.message}",
                    LogEntryType.ERROR
                )
                return false
            } catch (e: Exception) {
                sendLogBroadcast(
                    context,
                    "Hook Error",
                    "Unexpected error hooking constructors in $className: ${e.javaClass.simpleName} - ${e.message}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        @JvmOverloads
        fun sendLogBroadcast(
            context: Context,
            title: String,
            summary: String,
            type: LogEntryType = LogEntryType.DEFAULT
        ) {
            val intent = Intent("com.mwilky.androidenhanced.LOG")
            intent.setPackage("com.mwilky.androidenhanced")
            intent.putExtra("title", title)
            intent.putExtra("summary", summary)
            intent.putExtra("type", type)
            context.sendBroadcast(intent)

        }

        fun safeHookMethod(
            context: Context,
            className: String,
            classLoader: ClassLoader,
            methodName: String,
            vararg parameterTypes: Class<*>?,
            beforeHook: ((MethodHookParam) -> Unit)? = null,
            afterHook: ((MethodHookParam) -> Unit)? = null,
            replaceWith: ((MethodHookParam) -> Any?)? = null
        ): Boolean {
            try {
                // First, check if the method exists before trying to hook it
                val targetClass = findClass(className, classLoader)
                val method = findMethodExactIfExists(
                    targetClass, methodName, *(parameterTypes.filterNotNull().toTypedArray())
                )

                if (method != null) {
                    // Method exists, safe to hook
                    val hook = if (replaceWith != null) {
                        // Use XC_MethodReplacement for full method replacement
                        object : XC_MethodReplacement() {
                            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                                return try {
                                    // whatever you compute here...
                                    replaceWith(param)
                                } catch (e: Exception) {
                                    sendLogBroadcast(
                                        context,
                                        "Hook Runtime Error",
                                        "Error in replaceWith for $methodName: ${e.message}",
                                        LogEntryType.ERROR
                                    )
                                    // Return appropriate default value based on method return type
                                    getDefaultReturnValue(method.returnType)
                                }
                            }
                        }
                    } else {
                        // Use regular XC_MethodHook for before/after hooks
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                try {
                                    beforeHook?.invoke(param)
                                } catch (e: Exception) {
                                    sendLogBroadcast(
                                        context,
                                        "Hook Runtime Error",
                                        "Error in beforeHook for $methodName: ${e.message}",
                                        LogEntryType.ERROR
                                    )
                                }
                            }

                            override fun afterHookedMethod(param: MethodHookParam) {
                                try {
                                    afterHook?.invoke(param)
                                } catch (e: Exception) {
                                    sendLogBroadcast(
                                        context,
                                        "Hook Runtime Error",
                                        "Error in afterHook for $methodName: ${e.message}",
                                        LogEntryType.ERROR
                                    )
                                }
                            }
                        }
                    }

                    hookMethod(method, hook)

                    val hookType = if (replaceWith != null) "replaced" else "hooked"
                    sendLogBroadcast(
                        context,
                        "Hook Success",
                        "Method $methodName found and $hookType successfully in $className",
                        LogEntryType.SUCCESS
                    )
                    return true
                } else {
                    // Method doesn't exist
                    sendLogBroadcast(
                        context,
                        "Hook Error",
                        "Method $methodName not found in $className with signature: ${
                            parameterTypes.filterNotNull().joinToString { it.simpleName }
                        }",
                        LogEntryType.ERROR)
                    return false
                }

            } catch (e: ClassNotFoundError) {
                sendLogBroadcast(
                    context,
                    "Hook Error",
                    "Class $className not found: ${e.message}",
                    LogEntryType.ERROR
                )
                return false
            } catch (e: Exception) {
                sendLogBroadcast(
                    context,
                    "Hook Error",
                    "Unexpected error hooking $methodName in $className: ${e.javaClass.simpleName} - ${e.message}",
                    LogEntryType.ERROR
                )
                return false
            }
        }

        // Helper function to get default return values for different types
        private fun getDefaultReturnValue(returnType: Class<*>): Any? {
            return when (returnType) {
                Void.TYPE -> null
                Boolean::class.javaPrimitiveType -> false
                Byte::class.javaPrimitiveType -> 0.toByte()
                Short::class.javaPrimitiveType -> 0.toShort()
                Int::class.javaPrimitiveType -> 0
                Long::class.javaPrimitiveType -> 0L
                Float::class.javaPrimitiveType -> 0.0f
                Double::class.javaPrimitiveType -> 0.0
                Char::class.javaPrimitiveType -> '\u0000'
                else -> null
            }
        }

        fun updateQsTileLayout() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                callMethod(QuickQSPanelController, "onConfigurationChanged")
                callMethod(
                    QuickQSPanelQQSSideLabelTileLayout,
                    "onConfigurationChanged",
                    SystemUIContext.resources.configuration
                )
                callMethod(QuickQSPanelController, "switchTileLayout", true)
                callMethod(
                    QSCustomizerController3,
                    "onConfigChanged",
                    SystemUIContext.resources.configuration
                )
                callMethod(QSPanelController, "onConfigurationChanged")
                reloadTiles()
                callMethod(QSPanelController, "switchTileLayout", true)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateBrightnessSlider() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                var mView = getObjectField(QSPanelController, "mView") as ViewGroup
                var mBrightnessView = getObjectField(mView, "mBrightnessView") as View
                setBrightnessView(mView, mBrightnessView)

                //QQS
                mView = getObjectField(QuickQSPanelController, "mView") as ViewGroup
                mBrightnessView = getObjectField(mView, "mBrightnessView") as View

                setBrightnessView(mView, mBrightnessView)

                animateBrightnessSlider(QSAnimator)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun animateBrightnessSlider(qSAnimator: Any) {
            try {
                setObjectField(qSAnimator, "mBrightnessTranslationAnimator", null)
                setObjectField(qSAnimator, "mBrightnessOpacityAnimator", null)

                val mQsPanelController = getObjectField(qSAnimator, "mQsPanelController")
                val mQuickQSPanelController = getObjectField(qSAnimator, "mQuickQSPanelController")
                val mAnimatedQsViews =
                    getObjectField(qSAnimator, "mAnimatedQsViews") as ArrayList<View>
                val mAllViews = getObjectField(qSAnimator, "mAllViews") as ArrayList<View>
                val mMediaHost = getObjectField(mQuickQSPanelController, "mMediaHost")

                val qsBrightness: View? = getObjectField(
                    getObjectField(mQsPanelController, "mView"), "mBrightnessView"
                ) as View?

                val qqsBrightness: View? = getObjectField(
                    getObjectField(mQuickQSPanelController, "mView"), "mBrightnessView"
                ) as View?

                if (mQsBrightnessSliderPosition == 2) {
                    if (qsBrightness != null) {
                        qsBrightness.visibility = GONE
                    }
                    if (qqsBrightness != null) {
                        qqsBrightness.visibility = GONE
                    }
                }

                // We need to add animators for both sliders in this mode
                if (mQQsBrightnessSlider) {

                    if (qqsBrightness != null) {

                        // Remove in case added prior
                        mAnimatedQsViews.remove(qsBrightness)
                        // This is the view that animates from qqs to qs
                        mAnimatedQsViews.add(qsBrightness!!)
                        mAllViews.add(qqsBrightness)
                        mAllViews.add(qsBrightness)

                        val translationY: Int =
                            getRelativeTranslationY(qSAnimator, qsBrightness, qqsBrightness)
                        val qqsToQs = floatArrayOf(0f, translationY.toFloat())
                        val qsToQqs = floatArrayOf(-translationY.toFloat(), 0f)
                        val qsScale = floatArrayOf(1f, 1f)
                        val mQSExpansionPathInterpolator =
                            getObjectField(qSAnimator, "mQSExpansionPathInterpolator")
                        val mBrightnessTranslationAnimator = newInstance(TouchAnimatorBuilder)

                        callMethod(
                            mBrightnessTranslationAnimator,
                            "addFloat",
                            qqsBrightness,
                            "translationY",
                            qqsToQs
                        )
                        callMethod(
                            mBrightnessTranslationAnimator,
                            "addFloat",
                            qsBrightness,
                            "translationY",
                            qsToQqs
                        )
                        callMethod(
                            mBrightnessTranslationAnimator,
                            "addFloat",
                            qsBrightness,
                            "sliderScaleY",
                            qsScale
                        )
                        setObjectField(
                            mBrightnessTranslationAnimator,
                            "mInterpolator",
                            callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                        )

                        //Slow down the brightness sliders animation if media player is visible and
                        // slider position is at the bottom
                        val getVisible = callMethod(mMediaHost, "getVisible") as Boolean
                        if (mQsBrightnessSliderPosition == 1 && mQQsBrightnessSlider && getVisible) {
                            setObjectField(
                                mBrightnessTranslationAnimator,
                                "mInterpolator",
                                getStaticObjectField(
                                    Interpolators, "ALPHA_OUT"
                                )
                            )
                        }

                        setObjectField(
                            qSAnimator,
                            "mBrightnessTranslationAnimator",
                            callMethod(mBrightnessTranslationAnimator, "build")
                        )
                    }

                } else if (qqsBrightness != null && qqsBrightness.isVisible) {
                    // animating in split shade mode
                    mAnimatedQsViews.add(qsBrightness!!)
                    mAllViews.add(qqsBrightness)
                    val translationY: Int =
                        getRelativeTranslationY(qSAnimator, qsBrightness, qqsBrightness)

                    val f1 = floatArrayOf(0.3f, 1f)
                    val f2 = floatArrayOf(0f, translationY.toFloat())
                    val mQSExpansionPathInterpolator =
                        getObjectField(qSAnimator, "mQSExpansionPathInterpolator")
                    val mBrightnessTranslationAnimator = newInstance(TouchAnimatorBuilder)
                    // we need to animate qs brightness even if animation will not be visible,
                    // as we might start from sliderScaleY set to 0.3 if device was in collapsed QS
                    // portrait orientation before
                    callMethod(
                        mBrightnessTranslationAnimator, "addFloat", qsBrightness, "sliderScaleY", f1
                    )
                    callMethod(
                        mBrightnessTranslationAnimator,
                        "addFloat",
                        qqsBrightness,
                        "translationY",
                        f2
                    )
                    setObjectField(
                        mBrightnessTranslationAnimator,
                        "mInterpolator",
                        callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                    )
                    //Slow down the brightness sliders animation if media player is visible and
                    // slider position is at the bottom
                    val getVisible = callMethod(mMediaHost, "getVisible") as Boolean
                    if (mQsBrightnessSliderPosition == 1 && mQQsBrightnessSlider && getVisible) {
                        setObjectField(
                            mBrightnessTranslationAnimator, "mInterpolator", getStaticObjectField(
                                Interpolators, "ALPHA_OUT"
                            )
                        )
                    }
                    setObjectField(
                        qSAnimator,
                        "mBrightnessTranslationAnimator",
                        callMethod(mBrightnessTranslationAnimator, "build")
                    )
                } else if (qsBrightness != null) {
                    // The brightness slider's visible bottom edge must maintain a constant margin from the
                    // QS tiles during transition. Thus the slider must (1) perform the same vertical
                    // translation as the tiles, and (2) compensate for the slider scaling.

                    // For (1), compute the distance via the vertical distance between QQS and QS tile
                    // layout top.
                    val quickSettingsRootView: View =
                        getObjectField(qSAnimator, "mQsRootView") as View
                    val mTmpLoc1 = getObjectField(qSAnimator, "mTmpLoc1") as IntArray
                    val mTmpLoc2 = getObjectField(qSAnimator, "mTmpLoc2") as IntArray

                    callMethod(
                        qSAnimator,
                        "getRelativePosition",
                        mTmpLoc1,
                        callMethod(mQsPanelController, "getTileLayout"),
                        quickSettingsRootView,
                    )

                    callMethod(
                        qSAnimator,
                        "getRelativePosition",
                        mTmpLoc2,
                        callMethod(mQuickQSPanelController, "getTileLayout"),
                        quickSettingsRootView
                    )


                    val tileMovement: Int = mTmpLoc2[1] - mTmpLoc1[1]

                    // For (2), the slider scales to the vertical center, so compensate with half the
                    // height at full collapse.
                    val scaleCompensation = qsBrightness.measuredHeight * 0.5f
                    val mBrightnessTranslationAnimator = newInstance(TouchAnimatorBuilder)
                    val f3 = floatArrayOf(scaleCompensation + tileMovement, 0f)
                    val f4 = floatArrayOf(0f, 1f)
                    val mQSExpansionPathInterpolator =
                        getObjectField(qSAnimator, "mQSExpansionPathInterpolator")

                    callMethod(
                        mBrightnessTranslationAnimator, "addFloat", qsBrightness, "translationY", f3
                    )

                    callMethod(
                        mBrightnessTranslationAnimator, "addFloat", qsBrightness, "sliderScaleY", f4
                    )

                    setObjectField(
                        mBrightnessTranslationAnimator,
                        "mInterpolator",
                        callMethod(mQSExpansionPathInterpolator, "getYInterpolator")
                    )

                    setObjectField(
                        qSAnimator,
                        "mBrightnessTranslationAnimator",
                        callMethod(mBrightnessTranslationAnimator, "build")
                    )

                    // While the slider's position and unfurl is animated throughout the motion, the
                    // fade in happens independently.
                    val mBrightnessOpacityAnimator = newInstance(TouchAnimatorBuilder)
                    callMethod(mBrightnessOpacityAnimator, "addFloat", qsBrightness, "alpha", f4)
                    // Remove the delay if brightness slider is at bottom so we can see the opacity change still
                    setObjectField(
                        mBrightnessOpacityAnimator,
                        "mStartDelay",
                        if (mQsBrightnessSliderPosition != 1) 0.2f else 0.5f
                    )
                    setObjectField(
                        mBrightnessOpacityAnimator,
                        "mEndDelay",
                        if (mQsBrightnessSliderPosition != 1) 0.5f else 0.0f
                    )
                    setObjectField(
                        qSAnimator,
                        "mBrightnessOpacityAnimator",
                        callMethod(mBrightnessOpacityAnimator, "build")
                    )
                    mAllViews.add(qsBrightness)
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun getRelativeTranslationY(qsAnimator: Any, view1: View, view2: View): Int {
            try {
                val qsPosition = IntArray(2)
                val qqsPosition = IntArray(2)
                val mQsRootView = getObjectField(qsAnimator, "mQsRootView")
                callMethod(qsAnimator, "getRelativePositionInt", qsPosition, view1, mQsRootView)
                callMethod(qsAnimator, "getRelativePositionInt", qqsPosition, view2, mQsRootView)

                return qsPosition[1] - qqsPosition[1]
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
                return 0
            }
        }

        fun setBrightnessView(parentView: ViewGroup, view: View) {
            try {
                val mBrightnessView = getObjectField(parentView, "mBrightnessView") as ViewGroup?
                var mMovableContentStartIndex = getIntField(parentView, "mMovableContentStartIndex")

                if (mBrightnessView != null) {
                    parentView.removeView(mBrightnessView)
                    mMovableContentStartIndex--
                    setIntField(parentView, "mMovableContentStartIndex", mMovableContentStartIndex)
                }

                setObjectField(parentView, "mBrightnessView", view)
                callMethod(parentView, "setBrightnessViewMargin")

                when (mQsBrightnessSliderPosition) {
                    0 -> {
                        parentView.addView(view, 0)
                        view.visibility = VISIBLE
                    }

                    1 -> {
                        parentView.addView(view, 1)
                        view.visibility = VISIBLE
                    }

                    2 -> {
                        view.visibility = GONE
                        return
                    }
                }

                mMovableContentStartIndex++
                setIntField(parentView, "mMovableContentStartIndex", mMovableContentStartIndex)

                setBrighnessSliderMargins(parentView)

                //Check whether it is QQS slider or QS
                if (parentView.javaClass.name == QUICK_QS_PANEL_CLASS) {
                    val mBrightnessView = getObjectField(parentView, "mBrightnessView") as View
                    if (!mQQsBrightnessSlider) {
                        mBrightnessView.visibility = GONE

                    } else {
                        mBrightnessView.visibility = VISIBLE
                    }
                    callMethod(mQQsBrightnessMirrorHandler, "updateBrightnessMirror")
                } else {
                    callMethod(mBrightnessMirrorHandler, "updateBrightnessMirror")
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        @SuppressLint("DiscouragedApi")
        fun setBrighnessSliderMargins(parentView: View) {
            try {
                val mContext = getObjectField(parentView, "mContext") as Context
                val mBrightnessView = getObjectField(parentView, "mBrightnessView") as View?
                if (mBrightnessView != null) {
                    val top: Int = mContext.resources.getDimensionPixelSize(
                        mContext.resources.getIdentifier(
                            "qs_brightness_margin_top", "dimen", "com.android.systemui"
                        )
                    )
                    val bottom: Int = mContext.resources.getDimensionPixelSize(
                        mContext.resources.getIdentifier(
                            "qs_brightness_margin_bottom", "dimen", "com.android.systemui"
                        )
                    )

                    val lp = mBrightnessView.layoutParams as MarginLayoutParams

                    when (mQsBrightnessSliderPosition) {
                        0 -> {
                            //Slightly alter the padding for when using modified QS style so it sits more central
                            when (mQsStyleConfig) {
                                0 -> {
                                    lp.topMargin = 0
                                    lp.bottomMargin = 0
                                }

                                else -> {
                                    lp.topMargin =
                                        if (parentView.javaClass.name == QUICK_QS_PANEL_CLASS) top * 3 else top
                                    lp.bottomMargin =
                                        if (parentView.javaClass.name == QUICK_QS_PANEL_CLASS) 0 else bottom
                                }
                            }
                        }

                        1 -> {
                            lp.topMargin = 0
                            lp.bottomMargin = 0
                        }
                    }
                    mBrightnessView.layoutParams = lp
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun reloadTiles() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                val firstTile = TileList.first()
                val lastTile = TileList.last()
                callMethod(CurrentTilesInteractorImpl, "removeTiles", listOf(lastTile, firstTile))
                callMethod(CurrentTilesInteractorImpl, "addTile", lastTile, Integer.MAX_VALUE)
                callMethod(CurrentTilesInteractorImpl, "addTile", firstTile, 0)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateAllQuicksettings() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                updateQsTileLayout()
                updateBrightnessSlider()
                updateBrightnessSliderColors()
                reloadTiles()
                updateBatteryIconColors(
                    getObjectField(ShadeHeaderController, "batteryIcon"), "QS"
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateBrightnessSliderColors() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                val mQsBrightnessController =
                    getObjectField(QSPanelController, "mBrightnessController")

                val mQQsBrightnessController =
                    getAdditionalInstanceField(QuickQSPanelController, "mQQsBrightnessController")

                val controllers = listOf(mQsBrightnessController, mQQsBrightnessController)

                controllers.forEach { controller ->

                    val mControl = getObjectField(controller, "mControl")

                    val mView = getObjectField(mControl, "mView") as View

                    val mContext = mView.context

                    val mSlider = getObjectField(mView, "mSlider")

                    val mSliderProgressDrawable =
                        callMethod(mSlider, "getProgressDrawable") as LayerDrawable

                    val mProgressDrawable =
                        getObjectField(mView, "mProgressDrawable") as GradientDrawable

                    val mBrightnessProgress =
                        mSliderProgressDrawable.findDrawableByLayerId(android.R.id.progress) as DrawableWrapper

                    val mBrightnessProgressDrawable = mBrightnessProgress.drawable as LayerDrawable

                    val mSliderIcon = mBrightnessProgressDrawable.findDrawableByLayerId(
                        mContext.resources.getIdentifier(
                            "slider_icon", "id", mContext.packageName
                        )
                    )

                    // If the tweak is enabled then set the container color
                    if (mDualColorQsPanel && !isDarkMode(mContext)) {
                        mSliderProgressDrawable.findDrawableByLayerId(android.R.id.background)
                            .setTint(
                                applyAlpha(
                                    0.3f, mContext.getColor(android.R.color.system_on_surface_light)
                                )
                            )

                        mProgressDrawable.setColor(mContext.getColor(android.R.color.system_primary_light))

                        mSliderIcon.setTint(
                            getColorAttrDefaultColor(
                                mContext,
                                android.R.attr.textColorPrimary
                            )
                        )
                    } else {
                        mSliderProgressDrawable.findDrawableByLayerId(android.R.id.background)
                            .setTint(
                                applyAlpha(
                                    0.3f, mContext.getColor(android.R.color.system_on_surface_dark)
                                )
                            )

                        mProgressDrawable.setColor(mContext.getColor(android.R.color.system_accent1_100))

                        mSliderIcon.setTint(mContext.getColor(android.R.color.system_on_primary_dark))
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateBatteryIconColors(batteryIcon: Any, location: String) {
            try {
                val mContext = callMethod(batteryIcon, "getContext") as Context

                val mDrawable = getObjectField(batteryIcon, "mDrawable")
                val shieldPaint = getObjectField(mDrawable, "shieldPaint")
                callMethod(shieldPaint, "setColor", Color.WHITE)

                val mainBatteryDrawable = callMethod(mDrawable, "getDrawable")

                val mainBatteryDrawableFillPaint = getObjectField(mainBatteryDrawable, "fillPaint")

                setIntField(
                    mainBatteryDrawable,
                    "fillColor",
                    getIconColorForSlotName("battery", mContext, location)
                )
                callMethod(
                    mainBatteryDrawableFillPaint,
                    "setColor",
                    getIconColorForSlotName("battery", mContext, location)
                )

                setIntField(
                    batteryIcon,
                    "mTextColor",
                    getIconColorForSlotName("battery_percent", mContext, location)
                )

                (getObjectField(
                    batteryIcon, "mBatteryPercentView"
                ) as TextView?)?.setTextColor(
                    getIconColorForSlotName(
                        "battery_percent", mContext, location
                    )
                )

                (getObjectField(
                    batteryIcon, "mUnknownStateDrawable"
                ) as Drawable?)?.setTint(getIconColorForSlotName("battery", mContext, location))
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun toggleFontScale() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                CoroutineScope(Dispatchers.IO).launch {
                    val fontScale =
                        Settings.System.getFloat(SystemUIContext.contentResolver, FONT_SCALE)
                    Settings.System.putFloat(
                        SystemUIContext.contentResolver, FONT_SCALE, fontScale + 0.01f
                    )
                    delay(1000)
                    Settings.System.putFloat(SystemUIContext.contentResolver, FONT_SCALE, fontScale)
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun setStatusbarIconColors() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                val mReceivers =
                    getObjectField(DarkIconDispatcherImpl, "mReceivers") as ArrayMap<*, *>
                val mDarkIntensity = getFloatField(DarkIconDispatcherImpl, "mDarkIntensity")
                val mTintAreas = getObjectField(DarkIconDispatcherImpl, "mTintAreas")
                val mDarkChangeFlow = getObjectField(DarkIconDispatcherImpl, "mDarkChangeFlow")
                val mContrastTint = getIntField(DarkIconDispatcherImpl, "mContrastTint")

                fun getTintColor(slot: String): Int {
                    return if (mDarkIntensity == 1f && mDualStatusbarColors) mStatusbarDarkIconColor
                    else getIconColorForSlotName(slot, SystemUIContext)
                }

                val darkChange = newInstance(
                    findClass(
                        SYSUI_DARK_ICON_DISPATCHER_DARK_CHANGE_CLASS, SystemUIContext.classLoader
                    ), mDarkIntensity, getTintColor("notifications"), mTintAreas
                )

                callMethod(mDarkChangeFlow, "updateState", null as Any?, darkChange)

                for (i in 0 until mReceivers.size) {
                    val receiver = mReceivers.valueAt(i)
                    val className = receiver.javaClass.name

                    when (className) {
                        "com.android.systemui.statusbar.policy.Clock" -> {
                            callMethod(
                                receiver,
                                "onDarkChanged",
                                mTintAreas,
                                mDarkIntensity,
                                getTintColor("clock")
                            )
                        }

                        "com.android.systemui.statusbar.phone.LegacyNotificationIconAreaControllerImpl", "com.android.systemui.statusbar.phone.HeadsUpAppearanceController" -> {
                            callMethod(
                                receiver,
                                "onDarkChanged",
                                mTintAreas,
                                mDarkIntensity,
                                getTintColor("notifications")
                            )
                        }

                        "com.android.systemui.statusbar.pipeline.mobile.ui.view.ModernStatusBarMobileView" -> {
                            callMethod(
                                receiver,
                                "onDarkChanged",
                                mTintAreas,
                                mDarkIntensity,
                                getTintColor("mobile")
                            )
                            callMethod(
                                receiver,
                                "onDarkChangedWithContrast",
                                mTintAreas,
                                getTintColor("mobile"),
                                mContrastTint
                            )
                        }

                        "com.android.systemui.statusbar.pipeline.wifi.ui.view.ModernStatusBarWifiView" -> {
                            callMethod(
                                receiver,
                                "onDarkChanged",
                                mTintAreas,
                                mDarkIntensity,
                                getTintColor("wifi")
                            )
                            callMethod(
                                receiver,
                                "onDarkChangedWithContrast",
                                mTintAreas,
                                getTintColor("wifi"),
                                mContrastTint
                            )
                        }

                        "com.android.systemui.statusbar.pipeline.shared.ui.view.SingleBindableStatusBarIconView" -> {
                            callMethod(
                                receiver,
                                "onDarkChangedWithContrast",
                                mTintAreas,
                                getTintColor("notifications"),
                                mContrastTint
                            )
                        }

                        STATUSBAR_ICON_VIEW_CLASS -> {
                            val slot = getObjectField(receiver, "mSlot") as String
                            callMethod(
                                receiver,
                                "onDarkChanged",
                                mTintAreas,
                                mDarkIntensity,
                                getTintColor(slot)
                            )
                        }

                        else -> {
                            val fallbackColor =
                                if (mDarkIntensity == 1f && mDualStatusbarColors) mStatusbarDarkIconColor
                                else if (mDarkIntensity == 0f) mStatusbarIconColor
                                else mStatusbarDarkIconColor

                            callMethod(
                                receiver, "onDarkChanged", mTintAreas, mDarkIntensity, fallbackColor
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun setBatteryIconColors() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                val resolvedIconColor =
                    if (mBatteryDarkIntensity != 0f) mStatusbarDarkIconColor else mStatusbarBatteryIconColorAdapted
                val resolvedTextColor =
                    if (mBatteryDarkIntensity != 0f) mStatusbarDarkIconColor else mStatusbarBatteryPercentColorAdapted

                val mDrawable = getObjectField(BatteryMeterView, "mDrawable")
                val shieldPaint = getObjectField(mDrawable, "shieldPaint")
                callMethod(shieldPaint, "setColor", resolvedIconColor)

                val mainBatteryDrawable = callMethod(mDrawable, "getDrawable")
                val mainBatteryDrawableFillPaint = getObjectField(mainBatteryDrawable, "fillPaint")

                setIntField(
                    mainBatteryDrawable, "fillColor", resolvedIconColor
                )
                callMethod(
                    mainBatteryDrawableFillPaint, "setColor", resolvedIconColor
                )

                setIntField(
                    BatteryMeterView, "mTextColor", resolvedTextColor
                )

                (getObjectField(
                    BatteryMeterView, "mBatteryPercentView"
                ) as TextView?)?.setTextColor(resolvedTextColor)

                (getObjectField(
                    BatteryMeterView, "mUnknownStateDrawable"
                ) as Drawable?)?.setTint(resolvedIconColor)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun setStatusbarClockPosition() {
            try {
                val mContext = SystemUIContext

                val mStatusBar =
                    getObjectField(CollapsedStatusBarFragment, "mStatusBar") as ViewGroup

                //Get the clock containing view
                val mSystemIconArea: LinearLayout = mStatusBar.findViewById(
                    mContext.resources.getIdentifier(
                        "statusIcons", "id", "com.android.systemui"
                    )
                )
                //Get the clock view
                val mClockView = getObjectField(CollapsedStatusBarFragment, "mClockView") as View

                val rightParent = mSystemIconArea.parent as ViewGroup

                //Remove clock
                (mClockView.parent as ViewGroup?)?.removeView(mClockView)

                //Parent view set by module
                val setParent: ViewGroup?

                //Set the paddings of the clock
                val paddingStart: Int = mContext.resources.getDimensionPixelSize(
                    mContext.resources.getIdentifier(
                        "status_bar_left_clock_starting_padding", "dimen", "com.android.systemui"
                    )
                )
                val paddingEnd: Int = mContext.resources.getDimensionPixelSize(
                    mContext.resources.getIdentifier(
                        "status_bar_left_clock_end_padding", "dimen", "com.android.systemui"
                    )
                )

                when (mStatusbarClockPosition) {
                    //left side
                    0 -> {
                        setParent = mDefaultClockContainer as ViewGroup
                        setParent.addView(mClockView, 0)
                        mClockView.setPadding(paddingStart, 0, paddingEnd, 0)
                    }
                    //right side
                    1 -> {
                        setParent = rightParent
                        setParent.addView(mClockView)
                        mClockView.setPadding(paddingEnd * 2, 0, 0, 0)
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateSystemUIAfterBootComplete() {
            try {
                updateStatusbarIconColors()
                updateAllQuicksettings()
                setStatusbarClockPosition()
                toggleFontScale()
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateStatusbarIconColors() {
            try {
                val sentAllBootPrefs = getAdditionalStaticField(
                    findClass(
                        SYSTEM_UI_APPLICATION_CLASS, SystemUIContext.classLoader
                    ), "mSentAllBootPrefs"
                ) as Boolean

                if (!sentAllBootPrefs) return

                // Home icons
                setStatusbarIconColors()

                // Lockscreen icons
                callMethod(KeyguardStatusBarView, "onThemeChanged", TintedIconManager)
                callMethod(KeyguardStatusBarView, "updateVisibilities")

                val mCarrierLabel =
                    getObjectField(KeyguardStatusBarView, "mCarrierLabel") as TextView
                mCarrierLabel.setTextColor(
                    getIconColorForSlotName(
                        "carrier",
                        SystemUIContext,
                        "KEYGUARD"
                    )
                )

                setBatteryIconColors()

                // QS icons
                callMethod(
                    getObjectField(ShadeHeaderController, "iconManager"), "setTint", -1, -1
                )

                updateBatteryIconColors(getObjectField(ShadeHeaderController, "batteryIcon"), "QS")

                val clock = getObjectField(ShadeHeaderController, "clock") as TextView
                clock.setTextColor(getIconColorForSlotName("clock", SystemUIContext, "QS"))

                val date = getObjectField(ShadeHeaderController, "date") as TextView
                date.setTextColor(getIconColorForSlotName("date", SystemUIContext, "QS"))

                ModernShadeCarrierGroupMobileView?.let {
                    updateCarrierLabelColor(it as View, SystemUIContext)
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun updateCarrierLabelColor(modernShadeCarrierGroupMobileView: View, mContext: Context) {
            try {
                val carrierText = callMethod(
                    modernShadeCarrierGroupMobileView,
                    "requireViewById",
                    mContext.resources.getIdentifier(
                        "mobile_carrier_text", "id", mContext.packageName
                    )
                ) as TextView

                carrierText.setTextColor(
                    getIconColorForSlotName("carrier", mContext, "QS")
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        /**
         * Framework functions
         */
        fun updateAllowAllRotations(value: Boolean) {
            try {
                if (!References.isDisplayRotationReady) return
                val intResult: Int = if (value) 1 else 0
                setIntField(DisplayRotation, "mAllowAllRotations", intResult)
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        fun shouldAutoTurnOffTorch(wakeReason: Int): Boolean {
            try {
                return when (wakeReason) {
                    WAKE_REASON_LIFT -> {
                        mTorchPowerScreenOffLift
                    }

                    WAKE_REASON_APPLICATION -> {
                        mTorchPowerScreenOffApplication
                    }

                    WAKE_REASON_TAP -> {
                        mTorchPowerScreenOffTap
                    }

                    WAKE_REASON_CAMERA_LAUNCH -> {
                        mTorchPowerScreenOffCameraLaunch
                    }

                    WAKE_REASON_GESTURE -> {
                        mTorchPowerScreenOffGesture
                    }

                    WAKE_REASON_POWER_BUTTON -> {
                        mTorchPowerScreenOffPowerButton
                    }

                    WAKE_REASON_PLUGGED_IN -> {
                        mTorchPowerScreenOffPlugIn
                    }

                    WAKE_REASON_BIOMETRIC -> {
                        mTorchPowerScreenOffBiometric
                    }

                    else -> {
                        mTorchPowerScreenOffOther
                    }
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
                return true
            }
        }
    }
}