package com.mwilky.androidenhanced

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.os.UserManagerCompat
import com.mwilky.androidenhanced.BillingManager.Companion.resetPremiumTweaks
import com.mwilky.androidenhanced.Utils.Companion.BOOTCOMPLETED
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.ISONETIMEPURCHASE
import com.mwilky.androidenhanced.Utils.Companion.ISPREMIUM
import com.mwilky.androidenhanced.Utils.Companion.ISSUBSCRIPTION
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.Utils.Companion.LOGSKEY
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN

class BootReceiver : BroadcastReceiver() {

    // Listens for boot completed action, then sends keys and values to the hooked processes
    override fun onReceive(context: Context, intent: Intent) {
        val bootCompleted: Boolean
        val action = intent.action
        Log.i(
            "BroadcastReceiver",
            "Received action: $action, user unlocked: " + UserManagerCompat.isUserUnlocked(context)
        )

        bootCompleted = Intent.ACTION_LOCKED_BOOT_COMPLETED == action

        if (!bootCompleted) return

        //Send the preferences and their values via broadcast
        val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
        val sharedPreferences: SharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(
                SHAREDPREFS, MODE_PRIVATE
            )

        LogManager.init(deviceProtectedStorageContext)

        // Clear logs on boot to keep things tidy
        LogManager.clearLogsBeforeBoot()

        // Exclude none tweak related keys
        val keysToExclude = setOf(
            LASTBACKUP,
            ISONBOARDINGCOMPLETEDKEY,
            LOGSKEY,
            ISPREMIUM,
            UNSUPPORTEDDEVICEDIALOGSHOWN,
            BOOTTIME,
            ISONETIMEPURCHASE,
            ISSUBSCRIPTION
        )

        val bootPrefs = sharedPreferences.all.filterKeys { it !in keysToExclude }

        for ((key, value) in bootPrefs) {
            BroadcastSender.send(deviceProtectedStorageContext, key, value)
        }

        // Reset premium tweaks if not subscribed
        val isPremium = sharedPreferences.getBoolean(ISPREMIUM, false)
        if (!isPremium) resetPremiumTweaks(context)

        LogManager.log("BroadcastUtils", "Applied all settings at boot")
        BroadcastSender.send(deviceProtectedStorageContext, BOOTCOMPLETED, true)
    }
}