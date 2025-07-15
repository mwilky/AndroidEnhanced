package com.mwilky.androidenhanced.dataclasses

data class LogEntry(
    val title: String,
    val summary: String,
    val timestamp: Long,
    val type: LogEntryType = LogEntryType.DEFAULT
)

enum class LogEntryType {
    DEFAULT,
    ERROR,
    SUCCESS,
}
