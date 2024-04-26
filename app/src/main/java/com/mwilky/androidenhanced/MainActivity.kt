package com.mwilky.androidenhanced

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "DEBUG: Android Enhanced"
        var DEBUG = BuildConfig.DEBUG
        val SECURITY_PATCH: LocalDate = LocalDate.parse(Build.VERSION.SECURITY_PATCH);
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
    ) {  uri ->
        if (uri == null) return@registerForActivityResult


        // Retrieve data from SharedPreferences
        val deviceProtectedStorageContext = applicationContext.createDeviceProtectedStorageContext()
        val sharedPreferences: SharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(
                BroadcastUtils.PREFS, MODE_PRIVATE
            )
        val dataToBackup = sharedPreferences.all // This gets all key-value pairs in the SharedPreferences

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
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()))
                    .apply()
                Toast.makeText(applicationContext, R.string.backupSuccess, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, R.string.backupFailed, Toast.LENGTH_LONG).show()
            Log.e(TAG, "backup failed: ${e.printStackTrace()}")
        }
    }

    fun createBackup() {
        val timestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AndroidEnhanced_$timestamp.json"
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
                            BroadcastUtils.PREFS, MODE_PRIVATE).edit()

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

                Toast.makeText(
                    applicationContext,
                    R.string.restoreSuccess,
                    Toast.LENGTH_LONG)
                        .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, R.string.restoreFailed, Toast.LENGTH_LONG).show()
            Log.e(TAG, "restore failed: ${e.printStackTrace()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        UtilsPremium.TAG = TAG
        setContent {
            AndroidEnhancedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Navigation(this@MainActivity)
                }
            }
        }
    }
}