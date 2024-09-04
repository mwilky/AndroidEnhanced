package com.mwilky.androidenhanced

import android.content.Context
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.PowerManager
import android.os.SystemClock
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod


class Utils(context: Context, handler: Handler) {

    companion object {
        //Torch enabled
        var isTorchEnabled: Boolean = false
        //Shared Prefs Keys
        const val ISDEVICESUPPORTEDKEY = "isDeviceSupported"
        const val ISONBOARDINGCOMPLETEDKEY = "isOnboardingComplete"
        const val LASTBACKUP = "lastBackupDate"
        const val LOGSKEY = "logs"

        //Tweak Values
        //Gestures
        const val gestureSleep = "gesture_Sleep"
        //Launcher
        const val doubleTapToSleepLauncher = "bool_DoubleTapToSleeplauncher"
        //Framework
        const val torchPowerScreenOff = "bool_LongPressPowerTorchScreenOff"
        const val torchAutoOffScreenOn = "bool_TorchAutoOffScreenOn"
        const val volKeyMediaControl = "bool_VolKeyMediaControl"
        const val allowAllRotations = "bool_AllowAllRotations"
        const val disableSecureScreenshots = "bool_DisableSecureScreenshots"
        //SystemUI
        const val doubleTapToSleep = "bool_DoubleTapToSleep"
        const val statusBarBrightnessControl = "bool_StatusbarBrightnessControl"
        const val statusBarClockPosition = "int_StatusbarClockPosition"
        const val statusBarClockSeconds = "bool_StatusbarClockSeconds"
        const val hideLockscreenStatusBar = "bool_HideLockscreenStatusbar"
        const val scrambleKeypad = "bool_ScrambleKeypad"
        const val disableLockscreenPowerMenu = "bool_DisableLockscreenPowerMenu"
        const val qsTileVibration = "bool_QsTileVibration"
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
        const val hideCollapsedWifiIcon = "bool_HideCollapsedWifiIcon"
        const val iconBlacklist = "str_IconBlacklist"
        const val statusbarIconAccentColor = "bool_StatusbarIconAccentColor"
        const val qsStatusbarIconAccentColor = "bool_QsStatusbarIconAccentColor"
        const val lsStatusbarIconAccentColor = "bool_LsStatusbarIconAccentColor"

        const val autoExpandFirstNotif = "bool_AutoExpandFirstNotif"
        const val notifSectionHeaders = "bool_NotifSectionHeaders"

        var mIsInitialBoot = true

        lateinit var mVibrator: Vibrator

        fun initVibrator(context: Context) {
            if (!::mVibrator.isInitialized) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as VibratorManager
                mVibrator = vibratorManager.defaultVibrator
            }
        }

        // Function to check if phone is unlocked
        fun isUnlocked(mKeyguardStateController: Any): Boolean {
            val isShowing =
                XposedHelpers.getBooleanField(mKeyguardStateController, "mShowing")
            val canDismissLockScreen =
                XposedHelpers.getBooleanField(mKeyguardStateController, "mCanDismissLockScreen")

            return !(isShowing && !canDismissLockScreen)
        }

        fun getColorAttrDefaultColor(context: Context, i: Int): Int {
            val obtainStyledAttributes = context.obtainStyledAttributes(intArrayOf(i))
            val color = obtainStyledAttributes.getColor(0, 0)
            obtainStyledAttributes.recycle()
            return color
        }

        fun applyAlpha(f: Float, i: Int): Int {
            return Color.argb(
                (f * Color.alpha(i)).toInt(),
                Color.red(i),
                Color.green(i),
                Color.blue(i)
            )
        }

        fun getThemeAttr(i: Int, context: Context): Int {
            val obtainStyledAttributes = context.obtainStyledAttributes(intArrayOf(i))
            val resourceId = obtainStyledAttributes.getResourceId(0, 0)
            obtainStyledAttributes.recycle()
            return resourceId
        }

        fun setIconBlacklist(mContext: Context, iconBlacklist: String) {

            val currentBlockedIcons: String = Settings.Secure.getString(mContext.contentResolver, "icon_blacklist") ?: ""

            val blockedIconsList = currentBlockedIcons.split(",")
                .filter { it.isNotBlank() } // Filter out any blank strings
                .map { it.trim() }
                .toMutableList()

            val newIconsList = iconBlacklist.split(",")
                .filter { it.isNotBlank() }
                .map { it.trim() }

            // We don't want to toggle values on the initial boot.
            for (icon in newIconsList) {
                if (blockedIconsList.contains(icon)) {
                    blockedIconsList.remove(icon)
                } else {
                    blockedIconsList.add(icon)
                }
            }

            val updatedBlockedIcons = blockedIconsList.joinToString(",")

            Settings.Secure.putString(mContext.contentResolver, "icon_blacklist", if (mIsInitialBoot) iconBlacklist else updatedBlockedIcons)
        }

        fun lockDevice(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

            callMethod(powerManager, "goToSleep", SystemClock.uptimeMillis())
        }
    }

    private val mContext: Context = context
    private val mHandler: Handler = handler
    private val mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE)
                as CameraManager
    private var mTorchCallback: TorchCallback = TorchCallback()



    fun registerTorchCallback() {
        mCameraManager.registerTorchCallback(mTorchCallback, mHandler)
    }

    fun toggleTorchMode() {
        setTorchMode(!isTorchEnabled)
    }

    private fun getCameraWithFlash(): String? {
        for (cameraId in mCameraManager.cameraIdList) {
            try {
                val characteristics = mCameraManager.getCameraCharacteristics(cameraId)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash == true && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId
                }
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Couldn't get camera characteristics.", e)
            }
        }
        return null
    }

    private fun setTorchMode(enabled: Boolean): Boolean {
        if (isTorchEnabled != enabled) {
            isTorchEnabled = enabled
            try {
                val cameraId = getCameraWithFlash() ?: return false
                mCameraManager.setTorchMode(cameraId, enabled)
                return true
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Couldn't set torch mode.", e)
            }
        }
        return false
    }

    inner class TorchCallback : CameraManager.TorchCallback() {

        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (isTorchEnabled != enabled) {
                isTorchEnabled = enabled
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            isTorchEnabled = false
        }
    }
}