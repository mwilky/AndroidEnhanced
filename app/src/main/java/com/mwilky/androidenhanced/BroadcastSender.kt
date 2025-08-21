package com.mwilky.androidenhanced

import android.content.Context
import android.content.Intent
import com.mwilky.androidenhanced.dataclasses.LogEntryType

object BroadcastSender {
    fun <T> send(context: Context, key: String, value: T) {
        val intent = Intent(key).apply {
            when (value) {
                is Boolean -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is String -> putExtra(key, value)
                is Float -> putExtra(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
        context.sendBroadcast(intent)
        LogManager.log("BroadcastSender", "Sent $key = $value", LogEntryType.DEBUG)
    }
}

