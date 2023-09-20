package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.BroadcastUtils.Companion.sendBroadcast
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.statusBarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.ui.Tweaks.Companion.readSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.writeSwitchState
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenShortcuts
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.qsTileVibration
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds


class Tweaks {
    companion object {
        fun readSwitchState(context: Context, key: String): Boolean {
            return try {
                val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.getBoolean(key, false)
            } catch (e: Exception) {
                Log.e(TAG, "readSwitchState error: $e")
                false
            }
        }

        fun writeSwitchState(context: Context, key: String, state: Boolean) {
            try {
                val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()

                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
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
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        },
        content = {
            TweaksScrollableContent(topPadding = it, screen = screen, navController = navController)
        }
    )
}

@Composable
fun TweaksScrollableContent(topPadding: PaddingValues, screen : String, navController: NavController) {
    val context = LocalContext.current


    //The below code is for updating the description of TweaksSelectionRow based off the user's section.
    // It forces a recomposition each time the value is changed
    val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)

    val statusBarClockPositionEntries =
        context.resources.getStringArray(R.array.statusbar_clock_position_entries)


    // Create a Composable state variable that depends on the SharedPreferences value
    var rememberStatusBarClockPosition by remember {
        mutableStateOf(sharedPreferences.getInt(statusBarClockPosition, 0))
    }

    // Set the listener and update the remembered value on change to force a recomposition
    val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == statusBarClockPosition) {
                rememberStatusBarClockPosition =
                    sharedPreferences.getInt(statusBarClockPosition, 0)
            }
        }

    // Add the listener when this Composable is first composed
    DisposableEffect(Unit) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        // Remove the listener when the Composable is disposed
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        val statusbar = "Statusbar"
        val buttons = "Buttons"
        val misc = "Miscellaneous"
        val lockscreen = "Lockscreen"
        val quicksettings = "Quicksettings"

        when (screen) {
            //Pages
            statusbar -> {
                //Tweaks Items
                //Gestures section
                item {
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.gestures
                        )
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.doubleTapToSleepTitle
                        ),
                        stringResource(
                            id = R.string.doubleTapToSleepSummary
                        ),
                        doubleTapToSleep
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.statusbarBrightnessControlTitle
                        ),
                        stringResource(
                            id = R.string.statusbarBrightnessControlSummary
                        ),
                        statusBarBrightnessControl
                    )
                }
                //Clock section
                item {
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.clock
                        )
                    )
                }
                item {
                    TweaksSelectionRow(
                        label = stringResource(
                            id = R.string.statusbarClockPositionTitle
                        ),
                        description = statusBarClockPositionEntries[rememberStatusBarClockPosition],
                        key = statusBarClockPosition
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.statusbarClockSecondsTitle
                        ),
                        stringResource(
                            id = R.string.statusbarClockSecondsSummary
                        ),
                        statusBarClockSeconds
                    )
                }
            }
            buttons -> {
                //Tweaks Items
                item {
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.powerButton
                        )
                    )
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
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.volumeButton
                        )
                    )
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
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.general
                        )
                    )
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
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.disableSecureScreenshotsTitle),
                        stringResource(
                            R.string.disableSecureScreenshotsSummary),
                        disableSecureScreenshots
                    )
                }
            }
            //Lockscreen Tweaks
            lockscreen -> {
                //Tweaks Items
                item {
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.general
                        )
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.hideLockscreenStatusbarTitle),
                        stringResource(
                            R.string.hideLockscreenStatusbarSummary),
                        hideLockscreenStatusBar
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.hideLockscreenShortcutsTitle),
                        stringResource(
                            R.string.hideLockscreenShortcutsSummary),
                        hideLockscreenShortcuts
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.randomKeypadTitle),
                        stringResource(
                            R.string.randomKeypadSummary),
                        scrambleKeypad
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.disableLockscreenPowerMenuTitle),
                        stringResource(
                            R.string.disableLockscreenPowerMenuSummary),
                        disableLockscreenPowerMenu
                    )
                }
            }
            //Quicksettings Tweaks
            quicksettings -> {
                //Tweaks Items
                item {
                    TweaksSectionHeader(
                        label = stringResource(
                            id = R.string.general
                        )
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.qsTileClickVibrationTitle),
                        stringResource(
                            R.string.qsTileClickVibrationSummary),
                        qsTileVibration
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
            .padding(
                top = 16.dp,
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
                        start = 16.dp,
                        top = 8.dp,
                        end = 4.dp
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
                            bottom = 8.dp,
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
                sendBroadcast(context, key, switchState)
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
                top = 16.dp
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweaksSelectionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    label: String,
    key: String,
) {

    val context = LocalContext.current
    val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
    val coroutineScope = rememberCoroutineScope()

    // Create a state variable to track the selected radio button
    var selectedOption by remember { mutableStateOf(0) }

    // Options for the radio buttons
    val options = remember {
        val resources = context.resources
        resources.getStringArray(R.array.statusbar_clock_position_entries).toList()
    }

    // Function to update the selected radio button
    val onOptionSelected: (Int) -> Unit = { index ->
        selectedOption = index
    }

    // Function to save the selected index to SharedPreferences
    val saveToSharedPreferences: () -> Unit = {
        coroutineScope.launch {
            val sharedPreferences =
                deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
            sharedPreferences.edit().putInt(key, selectedOption).apply()
        }
        onConfirmation()
    }

    // Function to read the saved index from SharedPreferences
    val readFromSharedPreferences: () -> Unit = {
        val sharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
        selectedOption = sharedPreferences.getInt(key, 0)
    }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            ) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(
                            top = 24.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 8.dp
                        )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier
                        .padding(
                            top = 8.dp
                        )
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(
                            start=24.dp
                        )
                ) {
                    items(options.size) { index ->
                        val option = options[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelected(index) }
                                .padding(
                                    top = 16.dp,
                                    bottom = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = index == selectedOption,
                                onClick = { onOptionSelected(index) },
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(
                                        start = 8.dp,
                                        end = 16.dp
                                    ),
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = option,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .padding(
                                end = 8.dp,
                                bottom = 16.dp
                            ),
                    ) {
                        Text(text = "Dismiss",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    TextButton(
                        onClick = {
                            saveToSharedPreferences()
                            sendBroadcast(context, key, selectedOption)
                                  },
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            ),
                    ) {
                        Text(text = "Confirm",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }

    // Call readFromSharedPreferences when the dialog is first displayed
    DisposableEffect(Unit) {
        readFromSharedPreferences()
        onDispose { }
    }
}
@Composable
fun TweaksSelectionRow(
    label: String,
    description: String,
    key: String,
) {
    // Create a state variable to track whether the dialog should be shown
    var isDialogVisible by remember { mutableStateOf(false) }

    // Conditionally render the dialog if isDialogVisible is true
    if (isDialogVisible) {
        // Call the TweaksSelectionDialog Composable
        TweaksSelectionDialog(
            onDismissRequest = { isDialogVisible = false },
            onConfirmation = { isDialogVisible = false },
            label = label,
            key = key
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp,
                bottom = 16.dp
            )
            .clickable(
                enabled = true,
                onClick = {
                    // Show the dialog when the row is clicked
                    isDialogVisible = true
                }
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
                        start = 16.dp,
                        top = 8.dp,
                        end = 4.dp
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
                            bottom = 8.dp,
                            end = 16.dp
                        )
                )
            }
        }
    }
}
