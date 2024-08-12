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
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
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
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.expandAllNotifications
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedCallStrengthIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.iconBlacklist
import com.mwilky.androidenhanced.Utils.Companion.lsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.mIsInitialBoot
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
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
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.UtilsPremium.Companion.getIconColorForSlotName
import com.mwilky.androidenhanced.UtilsPremium.Companion.mLsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.UtilsPremium.Companion.mQsStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.UtilsPremium.Companion.mStatusbarIconUseAccentColor
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.xposed.Buttons.Companion.updateSupportLongPressPowerWhenNonInteractive
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.hideLockscreenStatusbarEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.keyguardStatusBarView
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mDisableLockscreenPowerMenuEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mDisableLockscreenQuicksettingsEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.scrambleKeypadEnabled
import com.mwilky.androidenhanced.xposed.Misc.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.xposed.Misc.Companion.mDisableSecureScreenshots
import com.mwilky.androidenhanced.xposed.Misc.Companion.updateAllowAllRotations
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mExpandedNotifications
import com.mwilky.androidenhanced.xposed.Notifications.Companion.mMuteScreenOnNotificationsEnabled
import com.mwilky.androidenhanced.xposed.Notifications.Companion.updateNotificationExpansion
import com.mwilky.androidenhanced.xposed.Quicksettings
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.PagedTileLayout
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.QSFooterView
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mClickVibrationEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mHideQSFooterBuildNumberEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsBrightnessSliderEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQQsRowsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQqsColumnsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQqsColumnsConfigLandscape
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsAnimator
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsBrightnessSliderPositionConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsColumnsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsColumnsConfigLandscape
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQsRowsConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mQuickPulldownConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mSmartPulldownConfig
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.setBrightnessView
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.CurrentTilesInteractorImpl
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.ModernShadeCarrierGroupMobileView
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.QSTileViewImpl
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.ShadeHeaderController
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.TintedIconManager
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.animateBrightnessSlider
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.tileList
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.updateBatteryIconColors
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
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.getObjectField

class BroadcastUtils: BroadcastReceiver() {
    companion object {

        const val PREFS = "prefs"

        fun registerBroadcastReceiver(mContext: Context, key: String, registeredClass: String, defaultValue: Any) {
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
                            callMethod(getObjectField(ShadeHeaderController, "clock"), "updateShowSeconds")
                        }
                        //Hide lockscreen statusbar
                        hideLockscreenStatusBar -> {
                            hideLockscreenStatusbarEnabled = value as Boolean
                            callMethod(keyguardStatusBarView, "updateVisibilities")
                        }
                        //Scramble Keypad
                        scrambleKeypad-> {
                            scrambleKeypadEnabled = value as Boolean
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
                            mExpandedNotifications = value as Boolean
                            updateNotificationExpansion()
                        }
                        //QS Style
                        qsStyle -> {
                            QuicksettingsPremium.mQsStyleConfig = value as Int
                            Quicksettings.mQsStyleConfig = value
                            updateQuicksettings(mContext)
                        }
                        qsColumns -> {
                            mQsColumnsConfig= value as Int
                            updateQuicksettings(mContext)
                        }
                        qsRows -> {
                            mQsRowsConfig= value as Int
                            updateQuicksettings(mContext)
                        }
                        qqsRows -> {
                            mQQsRowsConfig= value as Int
                            updateQuicksettings(mContext)
                        }
                        qsBrightnessSliderPosition -> {
                            mQsBrightnessSliderPositionConfig = value as Int
                            QuicksettingsPremium.mQsBrightnessSliderPositionConfig = value
                            updateQuicksettings(mContext)

                        }
                        qqsBrightnessSlider -> {
                            mQQsBrightnessSliderEnabled= value as Boolean
                            QuicksettingsPremium.mQQsBrightnessSliderEnabled= value
                            updateQuicksettings(mContext)
                        }
                        customStatusbarClockColor -> {
                            StatusbarPremium.mStatusbarClockColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarBatteryIconColor -> {
                            StatusbarPremium.mStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarWifiIconColor -> {
                            StatusbarPremium.mStatusbarWifiColor = value as Int
                            updateStatusbarIconColors(mContext)

                        }
                        customStatusbarMobileIconColor -> {
                            StatusbarPremium.mStatusbarMobileColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarNotificationIconColor -> {
                            StatusbarPremium.mStatusbarNotificationColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarOtherIconColor -> {
                            StatusbarPremium.mStatusbarIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarDndIconColor -> {
                            StatusbarPremium.mStatusbarDndColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarHotspotIconColor -> {
                            StatusbarPremium.mStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }


                        customStatusbarGlobalIconColor -> {
                            StatusbarPremium.mStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarGlobalIconColor -> {
                            StatusbarPremium.mQsStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarGlobalIconColor -> {
                            StatusbarPremium.mLsStatusbarGlobalColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }



                        customQsStatusbarClockColor -> {
                            StatusbarPremium.mQsStatusbarClockColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarBatteryIconColor -> {
                            StatusbarPremium.mQsStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mQsStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarWifiIconColor -> {
                            StatusbarPremium.mQsStatusbarWifiColor = value as Int
                            updateStatusbarIconColors(mContext)

                        }
                        customQsStatusbarCarrierColor -> {
                            StatusbarPremium.mQsStatusbarCarrierColor = value as Int
                            updateStatusbarIconColors(mContext)

                        }
                        customQsStatusbarDateColor -> {
                            StatusbarPremium.mQsStatusbarDateColor = value as Int
                            updateStatusbarIconColors(mContext)

                        }
                        customQsStatusbarMobileIconColor -> {
                            StatusbarPremium.mQsStatusbarMobileColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarOtherIconColor -> {
                            StatusbarPremium.mQsStatusbarIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarDndIconColor -> {
                            StatusbarPremium.mQsStatusbarDndColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mQsStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarHotspotIconColor -> {
                            StatusbarPremium.mQsStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customQsStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mQsStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }


                        customLsStatusbarBatteryIconColor -> {
                            StatusbarPremium.mLsStatusbarBatteryIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarBatteryPercentColor -> {
                            StatusbarPremium.mLsStatusbarBatteryPercentColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarWifiIconColor -> {
                            StatusbarPremium.mLsStatusbarWifiColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarMobileIconColor -> {
                            StatusbarPremium.mLsStatusbarMobileColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarOtherIconColor -> {
                            StatusbarPremium.mLsStatusbarIconColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarDndIconColor -> {
                            StatusbarPremium.mLsStatusbarDndColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarAirplaneIconColor -> {
                            StatusbarPremium.mLsStatusbarAirplaneColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarHotspotIconColor -> {
                            StatusbarPremium.mLsStatusbarHotspotColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarBluetoothIconColor -> {
                            StatusbarPremium.mLsStatusbarBluetoothColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }
                        customLsStatusbarCarrierColor -> {
                            StatusbarPremium.mLsStatusbarCarrierColor = value as Int
                            updateStatusbarIconColors(mContext)
                        }



                        hideCollapsedAlarmIcon -> {
                            mHideCollapsedAlarmEnabled = value as Boolean
                            callMethod(Statusbar.collapsedStatusBarFragment, "updateBlockedIcons")
                        }
                        hideCollapsedVolumeIcon -> {
                            mHideCollapsedVolumeEnabled = value as Boolean
                            callMethod(Statusbar.collapsedStatusBarFragment, "updateBlockedIcons")
                        }
                        hideCollapsedCallStrengthIcon -> {
                            mHideCollapsedCallStrengthEnabled = value as Boolean
                            callMethod(Statusbar.collapsedStatusBarFragment, "updateBlockedIcons")
                        }
                        qsColumnsLandscape -> {
                            mQsColumnsConfigLandscape = value as Int
                            updateQuicksettings(mContext)
                        }
                        qqsColumns -> {
                            mQqsColumnsConfig = value as Int
                            updateQuicksettings(mContext)
                        }
                        qqsColumnsLandscape -> {
                            mQqsColumnsConfigLandscape = value as Int
                            updateQuicksettings(mContext)
                        }
                        iconBlacklist -> {
                            Utils.setIconBlacklist(mContext, value as String)
                            mIsInitialBoot = false

                        }
                        statusbarIconAccentColor -> {
                            mStatusbarIconUseAccentColor = value as Boolean
                            updateStatusbarIconColors(mContext)

                        }
                        qsStatusbarIconAccentColor -> {
                            mQsStatusbarIconUseAccentColor = value as Boolean
                            updateStatusbarIconColors(mContext)

                        }
                        lsStatusbarIconAccentColor -> {
                            mLsStatusbarIconUseAccentColor = value as Boolean
                            updateStatusbarIconColors(mContext)

                        }
                    }
                    if (DEBUG) log("$TAG: broadcast received, $key = $value")

                }
            }

            val intentFilter = IntentFilter(key)
            mContext.registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
            if (DEBUG) log("$TAG: Registered '$key' receiver  in $registeredClass")
        }

        //This sends the broadcast containing the keys and values
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
            if (DEBUG) Log.d(TAG, "broadcast sent, $key = $value")
        }

        fun updateQuicksettings(mContext: Context) {

            callMethod(
                QuicksettingsPremium.QSPanel,
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

        }

        fun updateStatusbarIconColors(mContext: Context) {

            // Home icons
            StatusbarPremium.setStatusbarIconColorsOnBoot(mContext)

            // Lockscreen icons
            callMethod(keyguardStatusBarView, "onThemeChanged", TintedIconManager)
            callMethod(keyguardStatusBarView, "updateVisibilities")

            val mCarrierLabel = getObjectField(keyguardStatusBarView, "mCarrierLabel") as TextView
            mCarrierLabel.setTextColor(getIconColorForSlotName("carrier", mContext, "KEYGUARD"))

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

// Sends keys and values when the device has booted
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

        //Pause for 2 seconds before sending the boot broadcasts to allow things to init
        //Thread.sleep(2000)

        //Send the preferences and their values via broadcast
        val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
        val sharedPreferences: SharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(
                PREFS, MODE_PRIVATE
            )
        val allPrefs = sharedPreferences.all
        for ((key, value) in allPrefs) {

            sendBroadcast(deviceProtectedStorageContext, key, value)
        }
    }
}