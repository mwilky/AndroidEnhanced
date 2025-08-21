package com.mwilky.androidenhanced.dataclasses

data class LogEntry(
    val title: String,
    val summary: String,
    val timestamp: Long,
    val type: LogEntryType = LogEntryType.INFO
)

enum class LogEntryType {
    INFO,
    ERROR,
    HOOKS,
    DEBUG
}
