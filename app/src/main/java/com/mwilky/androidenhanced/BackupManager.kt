package com.mwilky.androidenhanced

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.ISONETIMEPURCHASE
import com.mwilky.androidenhanced.Utils.Companion.ISPREMIUM
import com.mwilky.androidenhanced.Utils.Companion.ISSUBSCRIPTION
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.Utils.Companion.LOGSKEY
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    private val exclusionKeys = setOf(
        LASTBACKUP,
        ISONBOARDINGCOMPLETEDKEY,
        LOGSKEY,
        ISPREMIUM,
        UNSUPPORTEDDEVICEDIALOGSHOWN,
        BOOTTIME,
        ISSUBSCRIPTION,
        ISONETIMEPURCHASE
    )

    fun createBackup(context: Context, uri: Uri) {
        try {
            val sharedPrefs = context.createDeviceProtectedStorageContext()
                .getSharedPreferences(SHAREDPREFS, Context.MODE_PRIVATE)

            val dataToBackup = sharedPrefs.all.filterKeys { it !in exclusionKeys }

            val jsonArray = JSONArray().apply {
                dataToBackup.forEach { (key, value) ->
                    put(JSONObject().apply {
                        put("key", key)
                        put("value", value)
                    })
                }
            }

            val additionalData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
            }

            val backupData = JSONObject().apply {
                put("sharedPreferencesData", jsonArray)
                put("additionalData", additionalData)
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(backupData.toString())
                }
            }

            sharedPrefs.edit {
                putString(
                    LASTBACKUP,
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                )
            }

            Toast.makeText(
                context,
                com.mwilky.androidenhanced.shared.R.string.backupSuccess,
                Toast.LENGTH_LONG
            ).show()
            LogManager.log(
                "Settings",
                context.getString(com.mwilky.androidenhanced.shared.R.string.backupSuccess)
            )

        } catch (e: Exception) {
            Log.e(TAG, "Backup failed: ${e.message}", e)
            Toast.makeText(context, R.string.backupFailed, Toast.LENGTH_LONG).show()
            LogManager.log("Settings", "${context.getString(R.string.backupFailed)}: ${e.message}")
        }
    }

    fun restoreBackup(context: Context, backupUri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(backupUri)
            val jsonData = inputStream?.bufferedReader()?.readText() ?: return
            val backupData = JSONObject(jsonData)

            if (!backupData.has("sharedPreferencesData")) {
                Toast.makeText(context, "Invalid or corrupted backup file", Toast.LENGTH_LONG)
                    .show()
                Log.e(TAG, "Invalid backup file: missing 'sharedPreferencesData'")
                return
            }

            val sharedPreferencesData = backupData.getJSONArray("sharedPreferencesData")
            val sharedPrefs = context.createDeviceProtectedStorageContext()
                .getSharedPreferences(SHAREDPREFS, Context.MODE_PRIVATE)

            sharedPrefs.edit {
                for (i in 0 until sharedPreferencesData.length()) {
                    val entry = sharedPreferencesData.getJSONObject(i)
                    val key = entry.optString("key")
                    val raw = entry.opt("value")

                    if (!key.isNullOrBlank() && raw != null) {
                        // Normalize types that org.json returns
                        val valueForPrefs: Any? = when (raw) {
                            is String -> {
                                putString(key, raw); raw
                            }

                            is Int -> {
                                putInt(key, raw); raw
                            }

                            is Long -> {
                                putLong(key, raw); raw
                            }

                            is Float -> {
                                putFloat(key, raw); raw
                            }

                            is Double -> { // This is needed as org.jsn parses any number with a decimal point as a double rather than float
                                val f = raw.toFloat()
                                putFloat(key, f)
                                f
                            }

                            is Boolean -> {
                                putBoolean(key, raw); raw
                            }

                            else -> {
                                LogManager.log(
                                    "Settings",
                                    "Unsupported type for key: $key, value: $raw",
                                    LogEntryType.ERROR
                                )
                                null
                            }
                        }

                        if (valueForPrefs != null) {
                            // Broadcast the normalized value (Float instead of Double, etc.)
                            BroadcastSender.send(context, key, valueForPrefs)
                        }
                    }
                }
            }

            Toast.makeText(context, R.string.restoreSuccess, Toast.LENGTH_LONG).show()
            LogManager.log("Settings", context.getString(R.string.restoreSuccess))

        } catch (e: Exception) {
            Log.e(TAG, "Restore failed: ${e.message}", e)
            Toast.makeText(context, R.string.restoreFailed, Toast.LENGTH_LONG).show()
            LogManager.log("Settings", "${context.getString(R.string.restoreFailed)}: ${e.message}")
        }
    }

}
