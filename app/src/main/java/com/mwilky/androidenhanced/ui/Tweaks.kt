package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.DataStoreManager
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.dataclasses.EnvironmentProp
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import com.mwilky.androidenhanced.ui.Tweaks.Companion.readSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.writeSwitchState
import com.mwilky.androidenhanced.xposed.XposedInit.Companion.SYSTEMUI_PREFS
import de.robv.android.xposed.XposedBridge.log

class Tweaks {
    companion object {
        fun readSwitchState(context: Context, key: String, prefs: String): Boolean {
            return try {
                val sharedPreferences =
                    context.getSharedPreferences(prefs, Context.MODE_WORLD_READABLE)
                sharedPreferences.getBoolean(key, false)
            } catch (e: SecurityException) {
                Log.e(TAG, "readSwitchState error: $e")
                false
            }
        }

        fun writeSwitchState(context: Context, key: String, prefs: String, state: Boolean) {
            try {
                val sharedPreferences =
                    context.getSharedPreferences(prefs, Context.MODE_WORLD_READABLE)
                sharedPreferences.edit().putBoolean(key, state).apply()
            } catch (e: SecurityException) {
                Log.e(TAG, "writeSwitchState error: $e")
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

        when (screen) {
            //Pages
            statusbar -> {
                //Tweaks Items
                item {
                    TweakSwitch(
                        context,
                        "First Switch",
                        "",
                        "key_one",
                        SYSTEMUI_PREFS
                    )
                }
            }
        }

    }
}

@Composable
fun TweakSwitch(context: Context, label: String, description: String, key: String, prefs: String) {
    var switchState by remember { mutableStateOf(readSwitchState(context, key, prefs)) }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp,
                        end = 16.dp
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
                writeSwitchState(context, key, prefs, switchState)
                },
            modifier = Modifier
                .padding(start = 16.dp,
                    end = 16.dp
                )
        )
    }
}