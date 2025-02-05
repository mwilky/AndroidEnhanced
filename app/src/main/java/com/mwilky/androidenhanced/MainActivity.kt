package com.mwilky.androidenhanced

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY
import com.mwilky.androidenhanced.Utils.Companion.ISONETIMEPURCHASE
import com.mwilky.androidenhanced.Utils.Companion.ISPREMIUM
import com.mwilky.androidenhanced.Utils.Companion.ISSUBSCRIPTION
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.Utils.Companion.LOGSKEY
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN
import com.mwilky.androidenhanced.dataclasses.LogEntry
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    private lateinit var billingManager: BillingManager

    companion object {
        const val TAG = "DEBUG: Android Enhanced"
        var DEBUG = BuildConfig.DEBUG
    }

    private val restoreBackupLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            restoreBackup(applicationContext, uri)
        }
    }

    private val createBackupLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@registerForActivityResult


        // Retrieve data from SharedPreferences
        val deviceProtectedStorageContext = applicationContext.createDeviceProtectedStorageContext()
        val sharedPreferences: SharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(
                BroadcastUtils.PREFS, MODE_PRIVATE
            )

        // Exclude none tweak related keys
        val keysToExclude =
            setOf(LASTBACKUP, ISONBOARDINGCOMPLETEDKEY, LOGSKEY, ISPREMIUM, UNSUPPORTEDDEVICEDIALOGSHOWN, BOOTTIME, ISSUBSCRIPTION, ISONETIMEPURCHASE)

        val dataToBackup = sharedPreferences.all.filterKeys { it !in keysToExclude }

        // Convert SharedPreferences data to JSON
        val jsonArray = JSONArray()
        dataToBackup.forEach { (key, value) ->
            val jsonObject = JSONObject()
            jsonObject.put("key", key)
            jsonObject.put("value", value)
            jsonArray.put(jsonObject)
        }

        // Additional data to include in the JSON file, if needed
        val additionalData = JSONObject()
        additionalData.put("timestamp", System.currentTimeMillis())

        // Create a JSON object to hold everything
        val backupData = JSONObject()
        backupData.put("sharedPreferencesData", jsonArray)
        backupData.put("additionalData", additionalData)

        // Save the JSON data to the document
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val writer = OutputStreamWriter(outputStream)
                writer.write(backupData.toString())
                writer.close()
                outputStream.close()
                sharedPreferences.edit().putString(
                    LASTBACKUP,
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                )
                    .apply()
                Toast.makeText(applicationContext, R.string.backupSuccess, Toast.LENGTH_LONG).show()
                LogManager.log(
                    "Settings",
                    applicationContext.resources.getString(R.string.backupSuccess)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, R.string.backupFailed, Toast.LENGTH_LONG).show()
            Log.e(TAG, ", ${R.string.backupFailed}: ${e.printStackTrace()}")
            LogManager.log(
                "Settings",
                "${applicationContext.resources.getString(R.string.backupFailed)}: ${e.printStackTrace()}"
            )
        }
    }

    private val backupLogsLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri == null) return@registerForActivityResult

        // Retrieve the logs
        val logs = LogManager.logsFlow.value

        try {

            // Prepare the content to write to the file
            val logContent = StringBuilder()
            logs.forEach { logEntry ->
                val formattedLog = "${
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date(logEntry.timestamp)
                    )
                }  ${logEntry.title}: ${logEntry.summary}\n"
                logContent.append(formattedLog)
            }

            // Write the logs to the file
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val writer = OutputStreamWriter(outputStream)
                writer.write(logContent.toString())
                writer.close()
                outputStream.close()

                // Optionally, save the backup timestamp in SharedPreferences
                val sharedPreferences = getSharedPreferences(BroadcastUtils.PREFS, MODE_PRIVATE)
                sharedPreferences.edit().putString(
                    LASTBACKUP,
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                ).apply()

                Toast.makeText(
                    applicationContext,
                    applicationContext.resources.getString(R.string.backupSuccess),
                    Toast.LENGTH_LONG
                ).show()
                LogManager.log(
                    "Logs",
                    applicationContext.resources.getString(R.string.backupSuccess)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Logs backup failed", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Logs backup failed: ${e.printStackTrace()}")
            LogManager.log("Logs", "Logs backup failed: ${e.printStackTrace()}")
        }
    }

    fun createLogsBackup() {
        val timestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AndroidEnhanced_logs_$timestamp.txt"
        backupLogsLauncher.launch(fileName)
    }


    fun createBackup() {
        val timestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AndroidEnhanced_settings_$timestamp.json"
        createBackupLauncher.launch(fileName)
    }

    fun restoreBackup() {
        restoreBackupLauncher.launch("application/json")
    }

    private fun restoreBackup(context: Context, backupUri: Uri) {
        // Read the JSON data from the backup file
        try {
            val inputStream = context.contentResolver.openInputStream(backupUri)
            if (inputStream != null) {
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonData = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    jsonData.append(line)
                }
                reader.close()
                inputStream.close()

                // Parse the JSON data
                val backupData = JSONObject(jsonData.toString())

                // Retrieve the SharedPreferences data from the JSON
                val sharedPreferencesData = backupData.optJSONArray("sharedPreferencesData")
                if (sharedPreferencesData != null) {

                    val deviceProtectedStorageContext =
                        applicationContext.createDeviceProtectedStorageContext()
                    val sharedPreferences =
                        deviceProtectedStorageContext.getSharedPreferences(
                            BroadcastUtils.PREFS, MODE_PRIVATE
                        ).edit()

                    for (i in 0 until sharedPreferencesData.length()) {
                        val entry = sharedPreferencesData.getJSONObject(i)
                        val key = entry.optString("key")
                        val value = entry.opt("value")
                        if (!key.isNullOrBlank() && value != null) {
                            when (value) {
                                is String -> sharedPreferences.putString(key, value)
                                is Int -> sharedPreferences.putInt(key, value)
                                is Long -> sharedPreferences.putLong(key, value)
                                is Float -> sharedPreferences.putFloat(key, value)
                                is Boolean -> sharedPreferences.putBoolean(key, value)
                            }
                            //Send the broadcast to update everything
                            BroadcastUtils.sendBroadcast(context, key, value)
                        }
                    }

                    sharedPreferences.apply()
                }

                // Handle any additional data from the JSON if needed
                val additionalData = backupData.optJSONObject("additionalData")
                if (additionalData != null) {
                    // Handle additional data here, if required
                }

                Toast.makeText(applicationContext, R.string.restoreSuccess, Toast.LENGTH_LONG)
                    .show()
                LogManager.log(
                    "Settings",
                    applicationContext.resources.getString(R.string.restoreSuccess)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, R.string.restoreFailed, Toast.LENGTH_LONG).show()
            Log.e(TAG, "${R.string.restoreFailed}: ${e.printStackTrace()}")
            LogManager.log(
                "Settings",
                "${applicationContext.resources.getString(R.string.restoreFailed)}: ${e.printStackTrace()}"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //installSplashScreen()
        super.onCreate(savedInstanceState)
        Utils.TAG = TAG

        val deviceProtectedStorageContext = applicationContext.createDeviceProtectedStorageContext()

        LogManager.init(deviceProtectedStorageContext)

        // Initialize BillingManager
        billingManager = BillingManager(
            context = applicationContext,
            activity = this,
            lifecycleScope = lifecycleScope
        )

        setContent {
            //SubscriptionChecker(billingManager = billingManager)

            AndroidEnhancedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(deviceProtectedStorageContext, billingManager)
                }
            }
        }
    }

    @Composable
    fun SubscriptionChecker(billingManager: BillingManager) {
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    // Start checking subscription when the app is in the foreground
                    billingManager.startCheckingSubscriptionStatus()
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    // Stop checking subscription when the app goes to the background
                    billingManager.stopCheckingSubscriptionStatus()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        LaunchedEffect(Unit) {
            // Continuous check every minute while the composable is active
            while (true) {
                billingManager.checkSubscriptionStatus()
                delay(60000L) // Check every 1 minute
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        billingManager.endBillingConnection()
    }

}