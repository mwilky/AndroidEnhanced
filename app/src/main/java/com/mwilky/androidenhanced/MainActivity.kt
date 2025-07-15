package com.mwilky.androidenhanced

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create Device Protected Storage Context (can be accessed before device is unlocked)
        val deviceProtectedStorageContext = applicationContext.createDeviceProtectedStorageContext()

        LogManager.init(deviceProtectedStorageContext)

        // Initialize BillingManager
        billingManager = BillingManager(
            context = applicationContext,
            activity = this,
            lifecycleScope = lifecycleScope
        )


        setContent {
            SubscriptionChecker(billingManager = billingManager)

            AndroidEnhancedTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
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
                delay(60000L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.endBillingConnection()
    }

    // Backup launchers
    private val restoreBackupLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { BackupManager.restoreBackup(applicationContext, it) }
    }

    private val createBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { BackupManager.createBackup(applicationContext, it) }
    }

    // Log backup launcher
    private val backupLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            LogManager.exportLogsToUri(applicationContext, it)
        }
    }

    // Functions to start the backup/restore/log backup launchers
    fun createLogBackup() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AndroidEnhanced_logs_$timestamp.txt"
        backupLogsLauncher.launch(fileName)
    }

    fun createBackup() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AndroidEnhanced_settings_$timestamp.json"
        createBackupLauncher.launch(fileName)
    }

    fun restoreBackup() {
        restoreBackupLauncher.launch("application/json")
    }
}