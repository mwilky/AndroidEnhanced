package com.mwilky.androidenhanced

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.UserManagerCompat
import com.mwilky.androidenhanced.BillingManager.Companion.resetPremiumTweaks
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSTEM_UI_APPLICATION_CLASS
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.BOOTCOMPLETED
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.ISPREMIUM
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.Utils.Companion.LOGSKEY
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEENABLED
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
import com.mwilky.androidenhanced.Utils.Companion.mIsInitialBoot
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.notifSectionHeaders
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerActiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerInactiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerUnavailableShape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.qsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.qsStyle
import com.mwilky.androidenhanced.Utils.Companion.qsTileVibration
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import com.mwilky.androidenhanced.Utils.Companion.statusBarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconDarkColor
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.useDualStatusbarColors
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.Utils.Companion.getIconColorForSlotName
import com.mwilky.androidenhanced.Utils.Companion.mLsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mQsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mBlockCameraGestureWhenLockedEnabled
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mDoubleTapSleepLauncherEnabled
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.xposed.Buttons.Companion.updateSupportLongPressPowerWhenNonInteractive
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mHideLockscreenStatusbarEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.KeyguardStatusBarView
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mDisableLockscreenPowerMenuEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mDisableLockscreenQuicksettingsEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mScrambleKeypadEnabled
import com.mwilky.androidenhanced.xposed.Misc.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.xposed.Misc.Companion.mDisableSecureScreenshots
import com.mwilky.androidenhanced.xposed.Misc.Companion.updateAllowAllRotations
import com.mwilky.androidenhanced.xposed.Notifications
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mAutoExpandFirstNotificationEnabled
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mExpandedNotificationsEnabled
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mKeyguardCoordinator
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mMuteScreenOnNotificationsEnabled
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mNotifCollection
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mNotificationSectionHeadersEnabled
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mRowAppearanceCoordinator
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mRowAppearanceCoordinatorAttach2
import com.mwilky.androidenhanced.xposed.Quicksettings
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.PagedTileLayout
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.QSFooterView
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mClickVibrationEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mHideQSFooterBuildNumberEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsBrightnessSliderEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsColumnsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsColumnsConfigLandscape
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsRowsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsAnimator
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsBrightnessSliderPositionConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsColumnsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsColumnsConfigLandscape
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsCustomizerController3
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsRowsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQuickPulldownConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mSmartPulldownConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.setBrightnessView
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.toggleFontScale
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.CurrentTilesInteractorImpl
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.ModernShadeCarrierGroupMobileView
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.QSTileViewImpl
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.ShadeHeaderController
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.TintedIconManager
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.animateBrightnessSlider
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.mDualColorQsPanelEnabled
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.tileList
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.updateBatteryIconColors
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.updateBrightnessSliderColors
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.updateCarrierLabelColor
import com.mwilky.androidenhanced.xposed.Statusbar
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.clock
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mDoubleTapToSleepEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mHideCollapsedAlarmEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mHideCollapsedCallStrengthEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mHideCollapsedVolumeEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarBrightnessControlEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarClockPosition
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarClockSecondsEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.setStatusbarClockPosition
import com.mwilky.androidenhanced.xposed.StatusbarPremium
import com.mwilky.androidenhanced.xposed.StatusbarPremium.Companion.setBatteryIconColorsOnChange
import com.mwilky.androidenhanced.xposed.SystemUIApplication.Companion.getApplicationContext
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getAdditionalStaticField
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setAdditionalStaticField
import de.robv.android.xposed.XposedHelpers.setBooleanField

class BroadcastUtils: BroadcastReceiver() {
    companion object {

        const val PREFS = "prefs"

        //This receives the broadcasts in the hooked processes
        fun registerBroadcastReceiver(
            mContext: Context, key: String, registeredClass: String, defaultValue: Any
        ) {
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val value = when (intent.extras?.get(key)) {
                        is Boolean -> intent.getBooleanExtra(key, defaultValue as Boolean)
                        is Int -> intent.getIntExtra(key, defaultValue as Int)
                        is String -> intent.getStringExtra(key)
                        else -> false // Default value if the type is not boolean or integer
                    }

                    //Set behaviour for each tweak change here
                    when (key) {
                        //Torch on power key whilst screen
                        torchPowerScreenOff -> {
                            mTorchPowerScreenOff = value as Boolean
                            updateSupportLongPressPowerWhenNonInteractive(value)
                        }
                        //Torch auto off when screen on
                        torchAutoOffScreenOn -> {
                            mTorchAutoOff = value as Boolean
                        }
                        //Vol key media control
                        volKeyMediaControl -> {
                            mVolKeyMedia = value as Boolean
                        }
                        //Allow all rotations
                        allowAllRotations -> {
                            mAllowAllRotations = value as Boolean
                            updateAllowAllRotations(value)
                        }
                        //Disable Secure Screenshots
                        disableSecureScreenshots -> {
                            mDisableSecureScreenshots = value as Boolean
                        }
                        //Double tap to sleep
                        doubleTapToSleep -> {
                            mDoubleTapToSleepEnabled = value as Boolean
                        }
                        //Statusbar Brightness Control
                        statusBarBrightnessControl -> {
                            mStatusbarBrightnessControlEnabled = value as Boolean
                        }
                        //Statusbar Clock Position
                        statusBarClockPosition -> {
                            mStatusbarClockPosition = value as Int
                            setStatusbarClockPosition()
                        }
                        //Statusbar Clock seconds
                        statusBarClockSeconds -> {
                            mStatusbarClockSecondsEnabled = value as Boolean
                            callMethod(clock, "updateShowSeconds")
                            callMethod(
                                getObjectField(ShadeHeaderController, "clock"),
                                "updateShowSeconds"
                            )
                        }
                        //Hide lockscreen statusbar
                        hideLockscreenStatusBar -> {
                            mHideLockscreenStatusbarEnabled = value as Boolean
                            callMethod(KeyguardStatusBarView, "updateVisibilities")
                        }
                        //Scramble Keypad
                        scrambleKeypad-> {
                            mScrambleKeypadEnabled = value as Boolean
                        }
                        //Disable power menu on lockscreen
                        disableLockscreenPowerMenu-> {
                            mDisableLockscreenPowerMenuEnabled = value as Boolean
                        }
                        //Qs tile click vibration
                        qsTileVibration-> {
                            mClickVibrationEnabled = value as Boolean
                        }
                        //Disable QS on lockscreen
                        disableQsLockscreen-> {
                            mDisableLockscreenQuicksettingsEnabled = value as Boolean
                        }
                        //Hide QS footer build number
                        hideQsFooterBuildNumber-> {
                            mHideQSFooterBuildNumberEnabled = value as Boolean
                            callMethod(QSFooterView, "setBuildText")
                        }
                        //QS smart pulldown
                        smartPulldown -> {
                            mSmartPulldownConfig = value as Int
                        }
                        //QS quick pulldown
                        quickPulldown -> {
                            mQuickPulldownConfig = value as Int
                        }
                        //Mute Screens on notifications
                        muteScreenOnNotifications -> {
                            mMuteScreenOnNotificationsEnabled = value as Boolean
                        }
                        //Expand all notifications
                        expandAllNotifications -> {
                            mExpandedNotificationsEnabled = value as Boolean
                            updateNotificationExpansion()
                        }
                        //QS Style
                        qsStyle -> {
                            QuicksettingsPremium.mQsStyleConfig = value as Int
                            Quicksettings.mQsStyleConfig = value
                            updateQuicksettings()
                        }
                        qsColumns -> {
                            mQsColumnsConfig= value as Int
                            updateQuicksettings()
                        }
                        qsRows -> {
                            mQsRowsConfig= value as Int
                            updateQuicksettings()
                        }
                        qqsRows -> {
                            mQQsRowsConfig= value as Int
                            updateQuicksettings()
                        }
                        qsBrightnessSliderPosition -> {
                            mQsBrightnessSliderPositionConfig = value as Int
                            QuicksettingsPremium.mQsBrightnessSliderPositionConfig = value
                            updateQuicksettings()

                        }
                        qqsBrightnessSlider -> {
                            mQQsBrightnessSliderEnabled= value as Boolean
                            QuicksettingsPremium.mQQsBrightnessSliderEnabled= value
                            updateQuicksettings()
                        }
                        customStatusbarClockColor -> {
                            StatusbarPremium.mStatusbarClockColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarBatteryIconColor -> {
                            StatusbarPremium.mStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarWifiIconColor -> {
                            StatusbarPremium.mStatusbarWifiColor = value as Int
                            updateStatusbarIconColors()

                        }
                        customStatusbarMobileIconColor -> {
                            StatusbarPremium.mStatusbarMobileColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarNotificationIconColor -> {
                            StatusbarPremium.mStatusbarNotificationColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarOtherIconColor -> {
                            StatusbarPremium.mStatusbarIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarDndIconColor -> {
                            StatusbarPremium.mStatusbarDndColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarHotspotIconColor -> {
                            StatusbarPremium.mStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customStatusbarGlobalIconColor -> {
                            StatusbarPremium.mStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarGlobalIconColor -> {
                            StatusbarPremium.mQsStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarGlobalIconColor -> {
                            StatusbarPremium.mLsStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarClockColor -> {
                            StatusbarPremium.mQsStatusbarClockColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarBatteryIconColor -> {
                            StatusbarPremium.mQsStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mQsStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarWifiIconColor -> {
                            StatusbarPremium.mQsStatusbarWifiColor = value as Int
                            updateStatusbarIconColors()

                        }
                        customQsStatusbarCarrierColor -> {
                            StatusbarPremium.mQsStatusbarCarrierColor = value as Int
                            updateStatusbarIconColors()

                        }
                        customQsStatusbarDateColor -> {
                            StatusbarPremium.mQsStatusbarDateColor = value as Int
                            updateStatusbarIconColors()

                        }
                        customQsStatusbarMobileIconColor -> {
                            StatusbarPremium.mQsStatusbarMobileColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarOtherIconColor -> {
                            StatusbarPremium.mQsStatusbarIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarDndIconColor -> {
                            StatusbarPremium.mQsStatusbarDndColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mQsStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarHotspotIconColor -> {
                            StatusbarPremium.mQsStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customQsStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mQsStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarBatteryIconColor -> {
                            StatusbarPremium.mLsStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mLsStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarWifiIconColor -> {
                            StatusbarPremium.mLsStatusbarWifiColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarMobileIconColor -> {
                            StatusbarPremium.mLsStatusbarMobileColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarOtherIconColor -> {
                            StatusbarPremium.mLsStatusbarIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarDndIconColor -> {
                            StatusbarPremium.mLsStatusbarDndColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mLsStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarHotspotIconColor -> {
                            StatusbarPremium.mLsStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mLsStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors()
                        }
                        customLsStatusbarCarrierColor -> {
                            StatusbarPremium.mLsStatusbarCarrierColor = value as Int
                            updateStatusbarIconColors()
                        }
                        hideCollapsedAlarmIcon -> {
                            mHideCollapsedAlarmEnabled = value as Boolean
                            callMethod(
                                Statusbar.collapsedStatusBarFragment, "updateBlockedIcons"
                            )
                        }
                        hideCollapsedVolumeIcon -> {
                            mHideCollapsedVolumeEnabled = value as Boolean
                            callMethod(
                                Statusbar.collapsedStatusBarFragment, "updateBlockedIcons"
                            )
                        }
                        hideCollapsedCallStrengthIcon -> {
                            mHideCollapsedCallStrengthEnabled = value as Boolean
                            callMethod(
                                Statusbar.collapsedStatusBarFragment, "updateBlockedIcons"
                            )
                        }
                        qsColumnsLandscape -> {
                            mQsColumnsConfigLandscape = value as Int
                            updateQuicksettings()
                        }
                        qqsColumns -> {
                            mQQsColumnsConfig = value as Int
                            updateQuicksettings()
                        }
                        qqsColumnsLandscape -> {
                            mQQsColumnsConfigLandscape = value as Int
                            updateQuicksettings()
                        }
                        iconBlacklist -> {
                            Utils.setIconBlacklist(mContext, value as String)
                            mIsInitialBoot = false

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
                        qsIconContainerActiveShape -> {
                            QuicksettingsPremium.mQsIconContainerActiveShapeConfig = value as Int
                            updateQuicksettings()
                        }
                        qsIconContainerInactiveShape -> {
                            QuicksettingsPremium.mQsIconContainerInactiveShapeConfig = value as Int
                            updateQuicksettings()
                        }
                        qsIconContainerUnavailableShape -> {
                            QuicksettingsPremium.mQsIconContainerUnavailableShapeConfig = value as Int
                            updateQuicksettings()
                        }
                        autoExpandFirstNotif -> {
                            Notifications.mAutoExpandFirstNotificationEnabled = value as Boolean
                            mRowAppearanceCoordinatorAttach2?.let { updateFirstNotificationExpansion(it) }

                        }
                        notifSectionHeaders -> {
                            mNotificationSectionHeadersEnabled = value as Boolean
                            mKeyguardCoordinator?.let { updateNotificationSectionHeaders(it) }
                        }
                        gestureSleep -> {
                            lockDevice(mContext)
                        }
                        doubleTapToSleepLauncher -> {
                            mDoubleTapSleepLauncherEnabled = value as Boolean
                        }
                        statusbarIconDarkColor -> {
                            StatusbarPremium.mStatusbarDarkIconColor = value as Int
                            updateStatusbarIconColors()
                        }
                        useDualStatusbarColors -> {
                            StatusbarPremium.mDualStatusbarColorsEnabled = value as Boolean
                            updateStatusbarIconColors()
                        }
                        dualToneQsPanel -> {
                            mDualColorQsPanelEnabled = value as Boolean
                            Quicksettings.mDualColorQsPanelEnabled = value as Boolean
                            updateQuicksettings()
                            toggleFontScale()
                        }
                        BOOTCOMPLETED -> {
                            setAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs", true)
                            updateSystemUiTweaks()
                        }
                        disableCameraScreenOff -> {
                            mBlockCameraGestureWhenLockedEnabled = value as Boolean
                        }

                        UNSUPPORTEDDEVICEENABLED -> {
                            val sharedPrefs = mContext.createDeviceProtectedStorageContext().getSharedPreferences(PREFS, MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean(UNSUPPORTEDDEVICEENABLED, value as Boolean).apply()

                        }
                    }
                    if (DEBUG) log("$TAG: broadcast received, $key = $value")
                }
            }

            val intentFilter = IntentFilter(key)
            mContext.registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
            if (DEBUG) log("$TAG: Registered '$key' receiver  in $registeredClass")
        }

        //This sends the broadcast containing the keys and values from the tweaks app to the hooked
        // processes.
        fun <T> sendBroadcast(
            context: Context,
            key: String,
            value: T
        ) {
            Intent().also { intent ->
                intent.action = key
                when (value) {
                    is Boolean -> intent.putExtra(key, value)
                    is Int -> intent.putExtra(key, value)
                    is String -> intent.putExtra(key, value)
                    else -> throw IllegalArgumentException("Unsupported type for value")
                }
                context.sendBroadcast(intent)
            }

            LogManager.log(
                "BroadcastUtils",
                "Broadcast sent: " +
                        "$key${value?.toString()?.takeIf { it.isNotEmpty() }?.let { " = $it" } ?: ""}"
            )
        }

        fun updateSystemUiTweaks() {

            updateQuicksettings()

            updateStatusbarIconColors()

            mKeyguardCoordinator?.let { updateNotificationSectionHeaders(it) }

            mRowAppearanceCoordinatorAttach2?.let { updateFirstNotificationExpansion(it) }

            updateNotificationExpansion()

            setStatusbarClockPosition()

        }

        fun updateNotificationExpansion() {

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs") as Boolean

            if (!sentAllBootPrefs)
                return

            setBooleanField(
                mRowAppearanceCoordinator,
                "mAlwaysExpandNonGroupedNotification",
                mExpandedNotificationsEnabled
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
                            mExpandedNotificationsEnabled,
                            mExpandedNotificationsEnabled
                        )
                    }
                }
            }
        }

        fun updateFirstNotificationExpansion(mRowAppearanceCoordinatorAttach2: Any) {

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs") as Boolean

            if (!sentAllBootPrefs)
                return

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

                                val mIsSummaryWithChildren =
                                    getBooleanField(
                                        expandableNotificationRow,
                                        "mIsSummaryWithChildren"
                                    )

                                if (mIsSummaryWithChildren) {

                                    val mChildrenContainer =
                                        getObjectField(
                                            expandableNotificationRow,
                                            "mChildrenContainer"
                                        )

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

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs") as Boolean

            if (!sentAllBootPrefs)
                return

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

        fun updateQuicksettings() {

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs") as Boolean

            if (!sentAllBootPrefs)
                return

            // Sets color of the qs build text in light and dark mode
            callMethod(QSFooterView, "setBuildText")

            callMethod(
                Quicksettings.QSPanel,
                "onConfigurationChanged",
                mContext.resources.configuration
            )

            callMethod(
                QuicksettingsPremium.QSPanelController,
                "onConfigurationChanged"
            )

            callMethod(
                QuicksettingsPremium.QuickQSPanelController,
                "onConfigurationChanged"
            )

            callMethod(
                QuicksettingsPremium.QuickQSPanelQQSSideLabelTileLayout, "" +
                        "onConfigurationChanged",
                mContext.resources.configuration
            )

            callMethod(
                QuicksettingsPremium.QSPanelControllerBase,
                "onConfigurationChanged"
            )

            callMethod(
                QSTileViewImpl,
                "onConfigurationChanged",
                mContext.resources.configuration
            )

            callMethod(
                mQsCustomizerController3,
                "onConfigChanged",
                mContext.resources.configuration
            )

            callMethod(
                QuicksettingsPremium.QSPanelController,
                "refreshAllTiles"
            )

            val firstTile = tileList.first()

            val lastTile = tileList.last()

            callMethod(CurrentTilesInteractorImpl, "removeTiles", listOf(lastTile, firstTile))

            callMethod(CurrentTilesInteractorImpl, "addTile",Integer.MAX_VALUE, lastTile)

            callMethod(CurrentTilesInteractorImpl, "addTile",0, firstTile)

            callMethod(Quicksettings.QuickQSPanelController, "switchTileLayout", true)

            callMethod(Quicksettings.QSPanelController, "switchTileLayout", true)

            callMethod(PagedTileLayout, "forceTilesRedistribution", "Android Enhanced change")

            callMethod(PagedTileLayout, "requestLayout")

            val mView =
                getObjectField(Quicksettings.QSPanelController, "mView")
                        as ViewGroup
            val mBrightnessView = getObjectField(mView, "mBrightnessView")
                    as View

            setBrightnessView(mView, mBrightnessView)

            val mQQsView =
                getObjectField(Quicksettings.QuickQSPanelController, "mView")
                        as ViewGroup
            val mQQsBrightnessView = getObjectField(mQQsView, "mBrightnessView")
                    as View

            setBrightnessView(mQQsView, mQQsBrightnessView)

            animateBrightnessSlider(mQsAnimator)

            updateBrightnessSliderColors()
        }

        fun updateStatusbarIconColors() {

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(findClass(SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader), "mSentAllBootPrefs") as Boolean

            if (!sentAllBootPrefs)
                return

            // Home icons
            StatusbarPremium.setStatusbarIconColorsOnBoot(mContext)

            // Lockscreen icons
            callMethod(KeyguardStatusBarView, "onThemeChanged", TintedIconManager)
            callMethod(KeyguardStatusBarView, "updateVisibilities")

            val mCarrierLabel = getObjectField(KeyguardStatusBarView, "mCarrierLabel") as TextView
            mCarrierLabel.setTextColor(getIconColorForSlotName("carrier", mContext, "KEYGUARD"))

            setBatteryIconColorsOnChange()

            // QS icons
            callMethod(
                getObjectField(ShadeHeaderController, "iconManager"),
                "setTint",
                -1,
                -1
            )

            updateBatteryIconColors(getObjectField(ShadeHeaderController, "batteryIcon"), "QS")

            val clock = getObjectField(ShadeHeaderController, "clock") as TextView
            clock.setTextColor(getIconColorForSlotName("clock", mContext, "QS"))

            val date = getObjectField(ShadeHeaderController, "date") as TextView
            date.setTextColor(getIconColorForSlotName("date", mContext, "QS"))

            updateCarrierLabelColor(ModernShadeCarrierGroupMobileView as View, mContext)
        }
    }

    // Listens for boot completed action, then sends keys and values to the hooked processes
    override fun onReceive(context: Context, intent: Intent) {
        val bootCompleted: Boolean
        val action = intent.action
        Log.i(
            TAG, "Received action: $action, user unlocked: " + UserManagerCompat
                .isUserUnlocked(context)
        )

        bootCompleted =
            Intent.ACTION_LOCKED_BOOT_COMPLETED == action

        if (!bootCompleted)
            return

        //Send the preferences and their values via broadcast
        val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
        val sharedPreferences: SharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(
                PREFS, MODE_PRIVATE
            )

        LogManager.init(deviceProtectedStorageContext)

        // Clear logs on boot to keep things tidy
        LogManager.clearLogs()

        // Exclude none tweak related keys
        val keysToExclude = setOf(LASTBACKUP, ISONBOARDINGCOMPLETEDKEY, LOGSKEY, ISPREMIUM, UNSUPPORTEDDEVICEDIALOGSHOWN, BOOTTIME)

        val bootPrefs = sharedPreferences.all.filterKeys { it !in keysToExclude }

        for ((key, value) in bootPrefs) {

            sendBroadcast(deviceProtectedStorageContext, key, value)
        }

        // Reset premium tweaks if not subscribed
        val isPremium = sharedPreferences.getBoolean(ISPREMIUM, false)
        if (!isPremium)
            resetPremiumTweaks(context)

        LogManager.log("BroadcastUtils", "Applied all settings at boot")
        // Send boot complete broadcast so we update SystemUI at once rather than per each key
        sendBroadcast(deviceProtectedStorageContext, BOOTCOMPLETED, true)
    }
}