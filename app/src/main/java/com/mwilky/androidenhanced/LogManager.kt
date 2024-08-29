package com.mwilky.androidenhanced

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.Utils.Companion.LOGSKEY
import com.mwilky.androidenhanced.dataclasses.LogEntry

object LogManager {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    // MutableState to notify Compose about changes
    val logsState: MutableState<List<LogEntry>> = mutableStateOf(emptyList())

    // Initialize the LogManager with context
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        logsState.value = loadLogs().toMutableList()
    }

    // Function to call when logging
    fun log(title: String, summary: String) {
        val timestamp = System.currentTimeMillis()
        val logEntry = LogEntry(title, summary, timestamp)
        val logs = logsState.value.toMutableList()
        logs.add(logEntry)
        saveLogs(logs)
        logsState.value = logs // Notify Compose about the change
    }

    // Saves logs to Shared Preferences
    private fun saveLogs(logs: List<LogEntry>) {
        val logsJson = gson.toJson(logs)
        sharedPreferences.edit().putString(LOGSKEY, logsJson).apply()
    }

    // Loads all logs, these get displayed in logs screen
    private fun loadLogs(): List<LogEntry> {
        val logsJson = sharedPreferences.getString(LOGSKEY, null)
        return if (logsJson != null) {
            val type = object : TypeToken<List<LogEntry>>() {}.type
            gson.fromJson(logsJson, type)
        } else {
            emptyList()
        }
    }

    // Clears logs out of shared prefs
    fun clearLogs() {
        logsState.value = emptyList()
        saveLogs(emptyList())
    }
}