package com.mwilky.androidenhanced

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mwilky.androidenhanced.dataclasses.LogEntryType

class LogBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title") ?: return
        val summary = intent.getStringExtra("summary") ?: return
        val type =
            intent.getSerializableExtra("type", LogEntryType::class.java) ?: LogEntryType.DEFAULT

        val appContext = try {
            context.createPackageContext(
                "com.mwilky.androidenhanced",
                Context.CONTEXT_IGNORE_SECURITY
            )
                .createDeviceProtectedStorageContext() ?: context
        } catch (e: Exception) {
            context
        }

        if (!LogManager.isInitialized()) {
            LogManager.init(appContext)
        }
        LogManager.log(title, summary, type)
    }
}
