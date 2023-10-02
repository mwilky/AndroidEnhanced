package com.mwilky.androidenhanced

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.util.Log
import androidx.core.os.BuildCompat
import androidx.core.os.UserManagerCompat
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenShortcuts
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.qsTileVibration
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.statusBarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.xposed.Buttons.Companion.updateSupportLongPressPowerWhenNonInteractive
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.hideLockscreenShortcutsEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.hideLockscreenStatusbarEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.keyguardStatusBarView
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.mDisableLockscreenQuicksettingsEnabled
import com.mwilky.androidenhanced.xposed.Lockscreen.Companion.scrambleKeypadEnabled
import com.mwilky.androidenhanced.xposed.Misc.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.xposed.Misc.Companion.mDisableSecureScreenshots
import com.mwilky.androidenhanced.xposed.Misc.Companion.updateAllowAllRotations
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.QSFooterView
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mClickVibrationEnabled
import com.mwilky.androidenhanced.xposed.Quicksettings.Companion.mHideQSFooterBuildNumberEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.clock
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mDoubleTapToSleepEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.setStatusbarClockPosition
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarBrightnessControlEnabled
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarClockPosition
import com.mwilky.androidenhanced.xposed.Statusbar.Companion.mStatusbarClockSecondsEnabled
import de.robv.android.xposed.XposedHelpers.callMethod

class BroadcastUtils: BroadcastReceiver() {
    companion object {

        const val PREFS = "prefs"

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun registerBroadcastReceiver(mContext: Context, key: String, registeredClass: String) {
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val value = when (intent.extras?.get(key)) {
                        is Boolean -> intent.getBooleanExtra(key, false)
                        is Int -> intent.getIntExtra(key, 0)
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
                        }
                        //Hide lockscreen statusbar
                        hideLockscreenStatusBar -> {
                            hideLockscreenStatusbarEnabled = value as Boolean
                            callMethod(keyguardStatusBarView, "updateVisibilities")
                        }
                        //Hide lockscreen shortcuts
                        hideLockscreenShortcuts-> {
                            hideLockscreenShortcutsEnabled = value as Boolean
                        }
                        //Scramble Keypad
                        scrambleKeypad-> {
                            scrambleKeypadEnabled = value as Boolean
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
                    }
                    if (DEBUG) Log.d(TAG, "broadcast received, $key = $value ")
                }
            }

            val intentFilter = IntentFilter(key)
            mContext.registerReceiver(myReceiver, intentFilter)
            if (DEBUG) Log.d(TAG, "Registered new receiver in $registeredClass")
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
                    else -> throw IllegalArgumentException("Unsupported type for value")
                }
                context.sendBroadcast(intent)
            }
            if (DEBUG) Log.d(TAG, "broadcast sent, $key = $value")
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
        bootCompleted = if (BuildCompat.isAtLeastN()) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED == action
        } else {
            Intent.ACTION_BOOT_COMPLETED == action
        }
        if (!bootCompleted) {
            return
        }

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