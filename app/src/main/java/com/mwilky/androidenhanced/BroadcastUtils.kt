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
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.xposed.Buttons
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.xposed.Buttons.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.xposed.Buttons.Companion.updateSupportLongPressPowerWhenNonInteractive
import com.mwilky.androidenhanced.xposed.Misc.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.xposed.Misc.Companion.updateAllowAllRotations
import de.robv.android.xposed.XposedBridge.log

class BroadcastUtils: BroadcastReceiver() {
    companion object {

        const val PREFS = "prefs"

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun registerBroadcastReceiver(mContext: Context, key: String, registeredClass: String) {
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val value = intent.getBooleanExtra(key, false)

                    //Set behaviour for each tweak change here
                    when (key) {
                        //Torch on power key whilst screen
                        torchPowerScreenOff -> {
                            mTorchPowerScreenOff = value
                            updateSupportLongPressPowerWhenNonInteractive(value)
                        }
                        //Torch auto off when screen on
                        torchAutoOffScreenOn -> {
                            mTorchAutoOff = value
                        }
                        //Vol key media control
                        volKeyMediaControl -> {
                            mVolKeyMedia = value
                        }
                        //Allow all rotations
                        allowAllRotations -> {
                            mAllowAllRotations = value
                            updateAllowAllRotations(value)
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
        fun sendBooleanBroadcast(
            context: Context,
            key: String,
            value: Boolean
        ) {
            Intent().also { intent ->
                intent.action = key
                intent.putExtra(key, value)
                context.sendBroadcast(intent)
            }
            if (DEBUG) Log.d(TAG, "broadcast sent, $key = $value ")
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
            sendBooleanBroadcast(deviceProtectedStorageContext, key, value as Boolean)
        }
    }
}