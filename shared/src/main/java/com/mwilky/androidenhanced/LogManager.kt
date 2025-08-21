package com.mwilky.androidenhanced

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mwilky.androidenhanced.dataclasses.LogEntry
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import com.mwilky.androidenhanced.shared.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogManager {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    private val _logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
    val logsFlow: StateFlow<List<LogEntry>> get() = _logsFlow.asStateFlow()

    fun init(context: Context) {
        val safeContext = try {
            context.createPackageContext(
                "com.mwilky.androidenhanced",
                Context.CONTEXT_IGNORE_SECURITY
            )
                .createDeviceProtectedStorageContext() ?: context
        } catch (e: Exception) {
            context
        }

        sharedPreferences =
            safeContext.getSharedPreferences(Utils.SHAREDPREFS, Context.MODE_PRIVATE)
        _logsFlow.value = loadLogs()
    }

    fun log(title: String, summary: String, type: LogEntryType = LogEntryType.INFO) {
        if (!this::sharedPreferences.isInitialized) {
            throw IllegalStateException("LogManager not initialized. Call LogManager.init(context) first.")
        }

        val timestamp = System.currentTimeMillis()
        val newEntry = LogEntry(title, summary, timestamp, type)

        val updatedLogs = _logsFlow.value + newEntry
        saveLogs(updatedLogs)

        // Refresh logs from storage to update _logsFlow
        _logsFlow.value = loadLogs()
    }

    fun clearAllLogs() {
        if (!this::sharedPreferences.isInitialized) {
            throw IllegalStateException("LogManager not initialized. Call LogManager.init(context) first.")
        }

        val emptyList = emptyList<LogEntry>()
        _logsFlow.value = emptyList
        saveLogs(emptyList)
    }

    fun clearLogsBeforeBoot() {
        if (!this::sharedPreferences.isInitialized) {
            throw IllegalStateException("LogManager not initialized. Call LogManager.init(context) first.")
        }

        val currentTimeMillis = System.currentTimeMillis()
        val elapsedRealtime = android.os.SystemClock.elapsedRealtime()
        val bootTime = currentTimeMillis - elapsedRealtime

        val filteredLogs = _logsFlow.value.filter { it.timestamp >= bootTime }
        _logsFlow.value = filteredLogs
        saveLogs(filteredLogs)
    }


    fun reloadLogs() {
        if (!this::sharedPreferences.isInitialized) return
        _logsFlow.value = loadLogs()
    }

    private fun saveLogs(logs: List<LogEntry>) {
        val logsJson = gson.toJson(logs)
        sharedPreferences.edit { putString(Utils.LOGSKEY, logsJson) }
    }

    private fun loadLogs(): List<LogEntry> {
        val logsJson = sharedPreferences.getString(Utils.LOGSKEY, null)
        return if (logsJson != null) {
            val type = object : TypeToken<List<LogEntry>>() {}.type
            gson.fromJson(logsJson, type)
        } else {
            emptyList()
        }
    }

    fun exportLogsToUri(context: Context, uri: Uri) {
        try {
            val logs = logsFlow.value
            val logContent = buildString {
                logs.forEach { logEntry ->
                    appendLine(
                        "${
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date(logEntry.timestamp))
                        }  ${logEntry.title}: ${logEntry.summary}"
                    )
                }
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(logContent)
                }
            }

            Toast.makeText(context, context.getString(R.string.backupSuccess), Toast.LENGTH_LONG)
                .show()
            log("Logs", context.getString(R.string.backupSuccess))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Logs backup failed", Toast.LENGTH_LONG).show()
            Log.e(Utils.TAG, "Logs backup failed: ${e.message}", e)
            log("Logs", "Logs backup failed: ${e.message}", LogEntryType.ERROR)
        }
    }

    fun isInitialized(): Boolean {
        return this::sharedPreferences.isInitialized
    }

}
