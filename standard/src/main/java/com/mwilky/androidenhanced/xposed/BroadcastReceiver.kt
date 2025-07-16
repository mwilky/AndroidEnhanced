package com.mwilky.androidenhanced.xposed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.edit
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSTEM_UI_APPLICATION_CLASS
import com.mwilky.androidenhanced.References.Companion.Clock
import com.mwilky.androidenhanced.References.Companion.CollapsedStatusBarFragment
import com.mwilky.androidenhanced.References.Companion.KeyguardStatusBarView
import com.mwilky.androidenhanced.References.Companion.QSFooterView
import com.mwilky.androidenhanced.References.Companion.ShadeHeaderController
import com.mwilky.androidenhanced.Utils.Companion.BOOTCOMPLETED
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.autoExpandFirstNotif
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarWifiIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDateColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarWifiIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarNotificationIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarWifiIconColor
import com.mwilky.androidenhanced.Utils.Companion.disableCameraScreenOff
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.dualToneQsPanel
import com.mwilky.androidenhanced.Utils.Companion.expandAllNotifications
import com.mwilky.androidenhanced.Utils.Companion.gestureSleep
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedCallStrengthIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.iconBlacklist
import com.mwilky.androidenhanced.Utils.Companion.lockDevice
import com.mwilky.androidenhanced.Utils.Companion.lsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.mDisableCameraGestureWhenLocked
import com.mwilky.androidenhanced.Utils.Companion.mDisableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.mDisableLockscreenQuicksettings
import com.mwilky.androidenhanced.Utils.Companion.mDisableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.mDoubleTapSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.mDoubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.mDualColorQsPanel
import com.mwilky.androidenhanced.Utils.Companion.mDualStatusbarColors
import com.mwilky.androidenhanced.Utils.Companion.mExpandAllNotifcations
import com.mwilky.androidenhanced.Utils.Companion.mExpandFirstNotification
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedAlarm
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedCallStrength
import com.mwilky.androidenhanced.Utils.Companion.mHideCollapsedVolume
import com.mwilky.androidenhanced.Utils.Companion.mHideLockscreenStatusbar
import com.mwilky.androidenhanced.Utils.Companion.mHideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.mIsInitialBoot
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarAirplaneColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarBluetoothColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarDndColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarGlobalColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarHotspotColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarIconColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarMobileColor
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarWifiColor
import com.mwilky.androidenhanced.Utils.Companion.mMuteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.mNotificationScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.mNotificationSectionHeaders
import com.mwilky.androidenhanced.Utils.Companion.mQQsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.mQQsColumnsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQQsColumnsConfigLandscape
import com.mwilky.androidenhanced.Utils.Companion.mQQsRowsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.mQsClickVibration
import com.mwilky.androidenhanced.Utils.Companion.mQsColumnsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsColumnsConfigLandscape
import com.mwilky.androidenhanced.Utils.Companion.mQsIconContainerActiveShapeConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsIconContainerInactiveShapeConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsIconContainerUnavailableShapeConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsRowsConfig
import com.mwilky.androidenhanced.Utils.Companion.mQsScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarAirplaneColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarBluetoothColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarDateColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarDndColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarGlobalColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarHotspotColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarIconColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarMobileColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarWifiColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStyleConfig
import com.mwilky.androidenhanced.Utils.Companion.mQuickPulldownConfig
import com.mwilky.androidenhanced.Utils.Companion.mScrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.mSmartPulldownConfig
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarAirplaneColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarBluetoothColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarDarkIconColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarDndColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarGlobalColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarHotspotColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarIconColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarMobileColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarNotificationColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarWifiColor
import com.mwilky.androidenhanced.Utils.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffApplication
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffBiometric
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffCameraLaunch
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffGesture
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffLift
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffOther
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffPlugIn
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffPowerButton
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOffTap
import com.mwilky.androidenhanced.Utils.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.notifScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.notifSectionHeaders
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsClickVibration
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerActiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerInactiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerUnavailableShape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.qsScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.qsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.qsStyle
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.sendLogBroadcast
import com.mwilky.androidenhanced.Utils.Companion.setIconBlacklist
import com.mwilky.androidenhanced.Utils.Companion.setStatusbarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.statusbarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconDarkColor
import com.mwilky.androidenhanced.Utils.Companion.toggleFontScale
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnApplication
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnBiometric
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnCameraLaunch
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnGesture
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnLift
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnOther
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPlugIn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPowerButton
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnTap
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.updateAllowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.updateBatteryIconColors
import com.mwilky.androidenhanced.Utils.Companion.updateBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.updateBrightnessSliderColors
import com.mwilky.androidenhanced.Utils.Companion.updateQsTileLayout
import com.mwilky.androidenhanced.Utils.Companion.updateStatusbarIconColors
import com.mwilky.androidenhanced.Utils.Companion.updateSystemUIAfterBootComplete
import com.mwilky.androidenhanced.Utils.Companion.useDualStatusbarColors
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.xposed.Framework.Companion.updateSupportLongPressPowerWhenNonInteractive
import com.mwilky.androidenhanced.xposed.SystemUI.Companion.updateFirstNotificationExpansion
import com.mwilky.androidenhanced.xposed.SystemUI.Companion.updateNotificationExpansion
import com.mwilky.androidenhanced.xposed.SystemUI.Companion.updateNotificationSectionHeaders
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setAdditionalStaticField

class BroadcastReceiver {

    companion object {

        //This receives the broadcasts in the hooked processes
        fun registerBroadcastReceiver(
            mContext: Context, key: String, registeredClass: String, defaultValue: Any
        ) {

            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val rawValue = intent.extras?.get(key)

                    sendLogBroadcast(
                        mContext,
                        "Broadcast Receiver",
                        "Received $key in $registeredClass, value = $rawValue"
                    )

                    when (rawValue) {
                        is Boolean -> updateValues(key, rawValue, mContext)
                        is Int -> updateValues(key, rawValue, mContext)
                        is String -> updateValues(key, rawValue, mContext)
                        is Float -> updateValues(key, rawValue, mContext)
                        null -> {
                            // If rawValue is null, we fall back to the defaultValue
                            when (defaultValue) {
                                is Boolean -> updateValues(key, defaultValue, mContext)
                                is Int -> updateValues(key, defaultValue, mContext)
                                is String -> updateValues(key, defaultValue, mContext)
                                is Float -> updateValues(key, defaultValue, mContext)
                            }
                        }
                    }
                }
            }

            val intentFilter = IntentFilter(key)
            mContext.registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)

            sendLogBroadcast(
                mContext, "Broadcast Receiver",
                "Registered $key in $registeredClass, default value = $defaultValue"
            )
        }

        fun <T : Any> updateValues(key: String, value: T, mContext: Context) {
            //Set behaviour for each tweak change here
            when (key) {
                // Statusbar Clock Position
                statusBarClockPosition -> {
                    mStatusbarClockPosition = value as Int
                    setStatusbarClockPosition()
                }
                // Statusbar clock seconds
                statusBarClockSeconds -> {
                    mStatusbarClockSeconds = value as Boolean
                    callMethod(Clock, "updateShowSeconds")
                    callMethod(
                        getObjectField(ShadeHeaderController, "clock"), "updateShowSeconds"
                    )
                }

                // Double tap to sleep statusbar
                doubleTapToSleep -> {
                    mDoubleTapToSleep = value as Boolean
                }

                // Statusbar brightness control
                statusbarBrightnessControl -> {
                    mStatusbarBrightnessControl = value as Boolean
                }

                // Disable secure screenshots
                disableSecureScreenshots -> {
                    mDisableSecureScreenshots = value as Boolean
                }

                // Allow all rotations
                allowAllRotations -> {
                    mAllowAllRotations = value as Boolean
                    updateAllowAllRotations(value)
                }

                // Double tap to sleep launcher
                doubleTapToSleepLauncher -> {
                    val sharedPreferences = mContext.createDeviceProtectedStorageContext()
                        .getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
                    sharedPreferences.edit { putBoolean(key, value as Boolean) }
                    mDoubleTapSleepLauncher = value as Boolean
                }

                // Double tap to sleep launcher
                gestureSleep -> {
                    lockDevice(mContext)
                }

                // Torch on power
                torchPowerScreenOff -> {
                    mTorchPowerScreenOff = value as Boolean
                    updateSupportLongPressPowerWhenNonInteractive(value)
                }

                // Torch auto off
                torchAutoOffScreenOn -> {
                    mTorchAutoOff = value as Boolean
                }

                // Volume key media control
                volKeyMediaControl -> {
                    mVolKeyMedia = value as Boolean
                }

                // mute screen on notifications
                muteScreenOnNotifications -> {
                    mMuteScreenOnNotifications = value as Boolean
                }

                // expand notifications
                expandAllNotifications -> {
                    mExpandAllNotifcations = value as Boolean
                    updateNotificationExpansion()
                }

                // auto expand top notification
                autoExpandFirstNotif -> {
                    mExpandFirstNotification = value as Boolean
                    updateFirstNotificationExpansion()
                }

                // hide notif section header
                notifSectionHeaders -> {
                    mNotificationSectionHeaders = value as Boolean
                    updateNotificationSectionHeaders()
                }

                // notif scrim alpha
                notifScrimAlpha -> {
                    mNotificationScrimAlpha = value as Float
                }

                // qs scrim alpha
                qsScrimAlpha -> {
                    mQsScrimAlpha = value as Float
                }

                // hide lockscreen statusbar
                hideLockscreenStatusBar -> {
                    mHideLockscreenStatusbar = value as Boolean
                    callMethod(KeyguardStatusBarView, "updateVisibilities")
                }

                // scramble keypad
                scrambleKeypad -> {
                    mScrambleKeypad = value as Boolean
                }

                // disable power menu lockscreen
                disableLockscreenPowerMenu -> {
                    mDisableLockscreenPowerMenu = value as Boolean
                }

                // disable quicksettings on lockscreen
                disableQsLockscreen -> {
                    mDisableLockscreenQuicksettings = value as Boolean
                }

                // qs click vibration
                qsClickVibration -> {
                    mQsClickVibration = value as Boolean
                }

                //hide qs footer build text
                hideQsFooterBuildNumber -> {
                    mHideQsFooterBuildNumber = value as Boolean
                    callMethod(QSFooterView, "setBuildText")
                }

                // smart pulldown
                smartPulldown -> {
                    mSmartPulldownConfig = value as Int
                }

                // quick pulldown
                quickPulldown -> {
                    mQuickPulldownConfig = value as Int
                }

                // qs brightness slider position
                qsBrightnessSliderPosition -> {
                    mQsBrightnessSliderPosition = value as Int
                    updateBrightnessSlider()
                }

                // qqs brightness slider
                qqsBrightnessSlider -> {
                    mQQsBrightnessSlider = value as Boolean
                    updateBrightnessSlider()
                }

                qqsRows -> {
                    mQQsRowsConfig = value as Int
                    updateQsTileLayout()
                }

                qqsColumns -> {
                    mQQsColumnsConfig = value as Int
                    updateQsTileLayout()
                }

                qqsColumnsLandscape -> {
                    mQQsColumnsConfigLandscape = value as Int
                    updateQsTileLayout()
                }

                qsColumns -> {
                    mQsColumnsConfig = value as Int
                    updateQsTileLayout()
                }

                qsColumnsLandscape -> {
                    mQsColumnsConfigLandscape = value as Int
                    updateQsTileLayout()
                }

                qsRows -> {
                    mQsRowsConfig = value as Int
                    updateQsTileLayout()
                }

                dualToneQsPanel -> {
                    mDualColorQsPanel = value as Boolean
                    updateBrightnessSliderColors()
                    updateBatteryIconColors(
                        getObjectField(ShadeHeaderController, "batteryIcon"), "QS"
                    )
                    toggleFontScale()
                }

                qsStyle -> {
                    mQsStyleConfig = value as Int
                    toggleFontScale()
                }

                qsIconContainerActiveShape -> {
                    mQsIconContainerActiveShapeConfig = value as Int
                    toggleFontScale()
                }

                qsIconContainerInactiveShape -> {
                    mQsIconContainerInactiveShapeConfig = value as Int
                    toggleFontScale()
                }

                qsIconContainerUnavailableShape -> {
                    mQsIconContainerUnavailableShapeConfig = value as Int
                    toggleFontScale()
                }

                statusbarIconDarkColor -> {
                    mStatusbarDarkIconColor = value as Int
                    updateStatusbarIconColors()
                }

                useDualStatusbarColors -> {
                    mDualStatusbarColors = value as Boolean
                    updateStatusbarIconColors()
                }

                statusbarIconAccentColor -> {
                    mStatusbarIconUseAccentColor = value as Boolean
                    updateStatusbarIconColors()

                }

                qsStatusbarIconAccentColor -> {
                    mQsStatusbarIconUseAccentColor = value as Boolean
                    updateStatusbarIconColors()

                }

                lsStatusbarIconAccentColor -> {
                    mLsStatusbarIconUseAccentColor = value as Boolean
                    updateStatusbarIconColors()

                }

                customStatusbarClockColor -> {
                    mStatusbarClockColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarBatteryIconColor -> {
                    mStatusbarBatteryIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarBatteryPercentColor -> {
                    mStatusbarBatteryPercentColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarWifiIconColor -> {
                    mStatusbarWifiColor = value as Int
                    updateStatusbarIconColors()

                }

                customStatusbarMobileIconColor -> {
                    mStatusbarMobileColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarNotificationIconColor -> {
                    mStatusbarNotificationColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarOtherIconColor -> {
                    mStatusbarIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarDndIconColor -> {
                    mStatusbarDndColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarAirplaneIconColor -> {
                    mStatusbarAirplaneColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarHotspotIconColor -> {
                    mStatusbarHotspotColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarBluetoothIconColor -> {
                    mStatusbarBluetoothColor = value as Int
                    updateStatusbarIconColors()
                }

                customStatusbarGlobalIconColor -> {
                    mStatusbarGlobalColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarGlobalIconColor -> {
                    mQsStatusbarGlobalColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarGlobalIconColor -> {
                    mLsStatusbarGlobalColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarClockColor -> {
                    mQsStatusbarClockColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarBatteryIconColor -> {
                    mQsStatusbarBatteryIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarBatteryPercentColor -> {
                    mQsStatusbarBatteryPercentColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarWifiIconColor -> {
                    mQsStatusbarWifiColor = value as Int
                    updateStatusbarIconColors()

                }

                customQsStatusbarCarrierColor -> {
                    mQsStatusbarCarrierColor = value as Int
                    updateStatusbarIconColors()

                }

                customQsStatusbarDateColor -> {
                    mQsStatusbarDateColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarMobileIconColor -> {
                    mQsStatusbarMobileColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarOtherIconColor -> {
                    mQsStatusbarIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarDndIconColor -> {
                    mQsStatusbarDndColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarAirplaneIconColor -> {
                    mQsStatusbarAirplaneColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarHotspotIconColor -> {
                    mQsStatusbarHotspotColor = value as Int
                    updateStatusbarIconColors()
                }

                customQsStatusbarBluetoothIconColor -> {
                    mQsStatusbarBluetoothColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarBatteryIconColor -> {
                    mLsStatusbarBatteryIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarBatteryPercentColor -> {
                    mLsStatusbarBatteryPercentColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarWifiIconColor -> {
                    mLsStatusbarWifiColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarMobileIconColor -> {
                    mLsStatusbarMobileColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarOtherIconColor -> {
                    mLsStatusbarIconColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarDndIconColor -> {
                    mLsStatusbarDndColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarAirplaneIconColor -> {
                    mLsStatusbarAirplaneColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarHotspotIconColor -> {
                    mLsStatusbarHotspotColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarBluetoothIconColor -> {
                    mLsStatusbarBluetoothColor = value as Int
                    updateStatusbarIconColors()
                }

                customLsStatusbarCarrierColor -> {
                    mLsStatusbarCarrierColor = value as Int
                    updateStatusbarIconColors()
                }

                iconBlacklist -> {
                    setIconBlacklist(mContext, value as String)
                    mIsInitialBoot = false
                }

                hideCollapsedAlarmIcon -> {
                    mHideCollapsedAlarm = value as Boolean
                    callMethod(
                        CollapsedStatusBarFragment, "updateBlockedIcons"
                    )
                }

                hideCollapsedVolumeIcon -> {
                    mHideCollapsedVolume = value as Boolean
                    callMethod(
                        CollapsedStatusBarFragment, "updateBlockedIcons"
                    )
                }

                hideCollapsedCallStrengthIcon -> {
                    mHideCollapsedCallStrength = value as Boolean
                    callMethod(
                        CollapsedStatusBarFragment, "updateBlockedIcons"
                    )
                }

                BOOTCOMPLETED -> {
                    setAdditionalStaticField(
                        findClass(
                            SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader
                        ), "mSentAllBootPrefs", true
                    )
                    updateSystemUIAfterBootComplete()
                }

                disableCameraScreenOff -> {
                    mDisableCameraGestureWhenLocked = value as Boolean
                }

                torchAutoOffScreenOnLift -> {
                    mTorchPowerScreenOffLift = value as Boolean
                }

                torchAutoOffScreenOnBiometric -> {
                    mTorchPowerScreenOffBiometric = value as Boolean
                }

                torchAutoOffScreenOnPlugIn -> {
                    mTorchPowerScreenOffPlugIn = value as Boolean
                }

                torchAutoOffScreenOnPowerButton -> {
                    mTorchPowerScreenOffPowerButton = value as Boolean
                }

                torchAutoOffScreenOnApplication -> {
                    mTorchPowerScreenOffApplication = value as Boolean
                }

                torchAutoOffScreenOnTap -> {
                    mTorchPowerScreenOffTap = value as Boolean
                }

                torchAutoOffScreenOnCameraLaunch -> {
                    mTorchPowerScreenOffCameraLaunch = value as Boolean
                }

                torchAutoOffScreenOnGesture -> {
                    mTorchPowerScreenOffGesture = value as Boolean
                }

                torchAutoOffScreenOnOther -> {
                    mTorchPowerScreenOffOther = value as Boolean
                }
            }
        }
    }
}