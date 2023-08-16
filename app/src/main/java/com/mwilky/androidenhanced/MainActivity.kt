package com.mwilky.androidenhanced

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "DEBUG: Android Enhanced"
        const val DEBUG = true
    }

    private lateinit var dataStoreManager : DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)
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