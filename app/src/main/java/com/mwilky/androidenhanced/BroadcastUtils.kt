package com.mwilky.androidenhanced

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.util.Log
import androidx.core.os.BuildCompat
import androidx.core.os.UserManagerCompat
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.xposed.Buttons

class BroadcastUtils: BroadcastReceiver() {
    companion object {

        const val PREFS = "prefs"
        const val torchPowerScreenOff = "bool_LongPressPowerTorchScreenOff"

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun registerBroadcastReceiver(mContext: Context, key: String, registeredClass: String) {
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val value = intent.getBooleanExtra(key, false)
                    //Set behaviour for each tweak change here
                    //
                    //Torch on power key whilst screen
                    if (key == torchPowerScreenOff) {
                        Buttons.mTorchPowerScreenOff = value
                        Buttons.updateSupportLongPressPowerWhenNonInteractive(value)
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
            value: Boolean) {
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
                PREFS, Application.MODE_PRIVATE
            )
        val allPrefs = sharedPreferences.all
        for ((key, value) in allPrefs) {
            sendBooleanBroadcast(deviceProtectedStorageContext, key, value as Boolean)
        }
    }
}