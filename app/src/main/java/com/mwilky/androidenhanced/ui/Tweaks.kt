package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.BroadcastUtils.Companion.sendBooleanBroadcast
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.ui.Tweaks.Companion.readSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.writeSwitchState


class Tweaks {
    companion object {
        fun readSwitchState(context: Context, key: String): Boolean {
            return try {
                val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
                val sharedPreferences = deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.getBoolean(key, false)
            } catch (e: Exception) {
                Log.e(TAG, "readSwitchState error: $e")
                false
            }
        }

        fun writeSwitchState(context: Context, key: String, state: Boolean) {
            try {
                val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
                val sharedPreferences = deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.edit().putBoolean(key, state).apply()
            } catch (e: Exception) {
                Log.e(TAG, "writeSwitchState error: $e")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tweaks(navController: NavController, context: Context, screen : String) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        //Background color of everything below
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = screen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        },
        content = {
            TweaksScrollableContent(topPadding = it, screen = screen)
        }
    )
}

@Composable
fun TweaksScrollableContent(topPadding: PaddingValues, screen : String) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        val statusbar = "Statusbar"
        val buttons = "Buttons"
        val misc = "Miscellaneous"

        when (screen) {
            //Pages
            statusbar -> {
                //Tweaks Items
                item {
                    TweakSwitch(
                        context,
                        "Test Switch",
                        "",
                        "key_one"
                    )
                }
            }

            buttons -> {
                //Tweaks Items
                item {
                    TweaksSectionHeader(label = "Power Button")
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.longPressPowerTorchScreenOffTitle),
                        stringResource(
                            R.string.longPressPowerTorchScreenOffSummary),
                        torchPowerScreenOff
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.torchAutoOffScreenOnTitle),
                        stringResource(
                            R.string.torchAutoOffScreenOnSummary),
                        torchAutoOffScreenOn
                    )
                }
                item {
                    TweaksSectionHeader(label = "Volume Button")
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.volKeyMediaControlTitle),
                        stringResource(
                            R.string.volKeyMediaControlSummary),
                        volKeyMediaControl
                    )
                }
            }
            misc -> {
                //Tweaks Items
                item {
                    TweaksSectionHeader(label = "General")
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.allowAllRotationsTitle),
                        stringResource(
                            R.string.allowAllRotationsSummary),
                        allowAllRotations
                    )
                }
            }
        }

    }
}

@Composable
fun TweakSwitch(context: Context, label: String, description: String, key: String) {
    var switchState by remember { mutableStateOf(readSwitchState(context, key)) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp,
                bottom = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f), // Take available horizontal space
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(
                        start = 16.dp
                    )
            )
            if (description != "") {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp
                        )
                )
            }
        }
        Switch(
            checked = switchState,
            onCheckedChange = {
                switchState = !switchState
                writeSwitchState(context, key, switchState)
                sendBooleanBroadcast(context, key, switchState)
                },
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
        )
    }
}

@Composable
fun TweaksSectionHeader(label: String) {
    val label = label
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 32.dp,
                top = 16.dp,
                bottom = 8.dp
            ),
    )
}
