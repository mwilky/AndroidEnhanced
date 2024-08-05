package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedCallStrengthIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarNotificationIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarWifiIconColor
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.expandAllNotifications
import com.mwilky.androidenhanced.Utils.Companion.hideAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedWifiIcon
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.qsStyle
import com.mwilky.androidenhanced.Utils.Companion.qsTileVibration
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds


class Tweaks {
    companion object {
        fun readSwitchState(context: Context, key: String, defaultValue: Boolean = false): Boolean {
            return try {
                val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.getBoolean(key, defaultValue)
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
        topBar = {
            ScaffoldTweaksAppBar(navController = navController, screen = screen, showBackIcon = true)
        },
        content = {
            TweaksScrollableContent(topPadding = it, screen = screen, navController = navController)
        }
    )
}

@Composable
fun TweaksScrollableContent(topPadding: PaddingValues, screen : String, navController: NavController) {
    val context = LocalContext.current

    //The below code is for updating the description of TweakSelectionRow based off the user's section.
    // It forces a recomposition each time the value is changed
    val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)

    val statusBarClockPositionEntries =
        context.resources.getStringArray(R.array.statusbar_clock_position_entries)
    val smartPulldownEntries =
        context.resources.getStringArray(R.array.smart_pulldown_entries)
    val quickPulldownEntries =
        context.resources.getStringArray(R.array.quick_pulldown_entries)
    val qqsRowsEntries =
        context.resources.getStringArray(R.array.qqs_rows_entries)
    val qsRowsEntries =
        context.resources.getStringArray(R.array.qs_rows_entries)
    val qsColumnsEntries =
        context.resources.getStringArray(R.array.qs_columns)
    val qsStyleEntries =
        context.resources.getStringArray(R.array.quicksettingsStyle)
    val qsBrightnessSliderPositionEntries =
        context.resources.getStringArray(R.array.quicksettingsBrightnessSliderPosition)


    // Create a Composable state variable that depends on the SharedPreferences value
    var rememberStatusBarClockPosition by remember {
        mutableIntStateOf(sharedPreferences.getInt(statusBarClockPosition, 0))
    }
    var rememberSmartPulldown by remember {
        mutableIntStateOf(sharedPreferences.getInt(smartPulldown, 0))
    }
    var rememberQuickPulldown by remember {
        mutableIntStateOf(sharedPreferences.getInt(quickPulldown, 0))
    }
    var rememberQsStyle by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsStyle, 0))
    }
    var rememberQqsRows by remember {
        mutableIntStateOf(sharedPreferences.getInt(qqsRows, 2) -1)
    }
    var rememberQsRows by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsRows, 4) -2)
    }
    var rememberQsColumns by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsColumns, 2) -2)
    }
    var rememberQsColumnsLandscape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsColumnsLandscape, 4) -2)
    }
    var rememberQQsColumns by remember {
        mutableIntStateOf(sharedPreferences.getInt(qqsColumns, 2) -2)
    }
    var rememberQQsColumnsLandscape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qqsColumnsLandscape, 4) -2)
    }
    var rememberQsBrightnessSliderPosition by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsBrightnessSliderPosition, 0))
    }
    var rememberStatusbarIconClockColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarClockColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconBatteryIconColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarBatteryIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconBatteryPercentColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarBatteryPercentColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconWifiColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarWifiIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconMobileColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarMobileIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconNotificationColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarNotificationIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconOtherColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarOtherIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconDndColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarDndIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconAirplaneColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarAirplaneIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconHotspotColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarHotspotIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconBluetoothColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarBluetoothIconColor, android.graphics.Color.WHITE))
    }
    var rememberStatusbarIconGlobalColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(customStatusbarGlobalIconColor, android.graphics.Color.WHITE))
    }

    // Set the listener and update the remembered value on change to force a recomposition
    val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                statusBarClockPosition -> rememberStatusBarClockPosition =
                    sharedPreferences.getInt(statusBarClockPosition, 0)
                smartPulldown -> rememberSmartPulldown =
                    sharedPreferences.getInt(smartPulldown, 0)
                quickPulldown -> rememberQuickPulldown =
                    sharedPreferences.getInt(quickPulldown, 0)
                qsStyle -> rememberQsStyle =
                    sharedPreferences.getInt(qsStyle, 0)
                qqsRows -> rememberQqsRows =
                    sharedPreferences.getInt(qqsRows, 2) - 1
                qsColumns -> rememberQsColumns =
                    sharedPreferences.getInt(qsColumns, 2) - 2
                qsColumnsLandscape -> rememberQsColumnsLandscape =
                    sharedPreferences.getInt(qsColumnsLandscape, 4) - 2
                qqsColumns -> rememberQQsColumns =
                    sharedPreferences.getInt(qqsColumns, 2) - 2
                qqsColumnsLandscape -> rememberQQsColumnsLandscape =
                    sharedPreferences.getInt(qqsColumnsLandscape, 4) - 2
                qsRows -> rememberQsRows =
                    sharedPreferences.getInt(qsRows, 4) - 2
                qsBrightnessSliderPosition -> rememberQsBrightnessSliderPosition =
                    sharedPreferences.getInt(qsBrightnessSliderPosition, 0)
                customStatusbarGlobalIconColor -> rememberStatusbarIconGlobalColor =
                    sharedPreferences.getInt(customStatusbarGlobalIconColor, -1)
                customStatusbarClockColor -> rememberStatusbarIconClockColor =
                    sharedPreferences.getInt(customStatusbarClockColor, -1)
                customStatusbarBatteryIconColor -> rememberStatusbarIconBatteryIconColor =
                    sharedPreferences.getInt(customStatusbarBatteryIconColor, -1)
                customStatusbarBatteryPercentColor -> rememberStatusbarIconBatteryPercentColor =
                    sharedPreferences.getInt(customStatusbarBatteryPercentColor, -1)
                customStatusbarWifiIconColor -> rememberStatusbarIconWifiColor =
                    sharedPreferences.getInt(customStatusbarWifiIconColor, -1)
                customStatusbarMobileIconColor -> rememberStatusbarIconMobileColor =
                    sharedPreferences.getInt(customStatusbarMobileIconColor, -1)
                customStatusbarNotificationIconColor -> rememberStatusbarIconNotificationColor =
                    sharedPreferences.getInt(customStatusbarNotificationIconColor, -1)
                customStatusbarDndIconColor -> rememberStatusbarIconDndColor =
                    sharedPreferences.getInt(customStatusbarDndIconColor, -1)
                customStatusbarAirplaneIconColor -> rememberStatusbarIconAirplaneColor =
                    sharedPreferences.getInt(customStatusbarAirplaneIconColor, -1)
                customStatusbarBluetoothIconColor -> rememberStatusbarIconBluetoothColor =
                    sharedPreferences.getInt(customStatusbarBluetoothIconColor, -1)
                customStatusbarHotspotIconColor -> rememberStatusbarIconHotspotColor =
                    sharedPreferences.getInt(customStatusbarHotspotIconColor, -1)
                customStatusbarOtherIconColor -> rememberStatusbarIconOtherColor =
                    sharedPreferences.getInt(customStatusbarOtherIconColor, -1)
            }
        }

    val individualColorsDisabled = rememberStatusbarIconGlobalColor != -1

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
        val notifications = "Notifications"
        val statusbarColors = "Individual icon colors"
        val collapsedIcons = "Hide collapsed statusbar icons"
        val allStatusbarIcons = "Hide statusbar icons"

        when (screen) {
            //Pages
            statusbar -> {
                //Tweaks Items
                //Gestures section
                item {
                    TweakSectionHeader(
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
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.clock
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.statusbarClockPositionTitle
                        ),
                        description = statusBarClockPositionEntries[rememberStatusBarClockPosition],
                        key = statusBarClockPosition,
                        entries = context.resources.getStringArray(R.array.statusbar_clock_position_entries),
                        0
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
                //Icon section
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.iconColors
                        )
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarGlobalIconColorTitle
                        ),
                        customStatusbarGlobalIconColor,
                        rememberStatusbarIconGlobalColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakRow(
                        context = context,
                        label = statusbarColors,
                        description = stringResource(
                            id = if (!individualColorsDisabled)
                                    R.string.customStatusbarIndividualIconColorSummary
                                else
                                    R.string.customStatusbarIndividualIconColorSummaryDisabled
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        individualColorsDisabled
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.iconManagement
                        )
                    )
                }
                item {
                    TweakRow(
                        context = context,
                        label = allStatusbarIcons,
                        description = stringResource(
                            id = R.string.allStatusbarIcons),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        false
                    )
                }
                item {
                    TweakRow(
                        context = context,
                        label = collapsedIcons,
                        description = stringResource(
                            id = R.string.collapsedStatusbarIcons),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        false
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .height(64.dp)
                    )
                }
            }
            allStatusbarIcons -> {
                //Tweaks Items
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.alarmTitle
                        ),
                        "",
                        hideAlarmIcon,
                        false
                    )
                }
            }
            collapsedIcons -> {
                //Tweaks Items
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.alarmTitle
                        ),
                        "",
                        hideCollapsedAlarmIcon,
                        true
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.volumeTitle
                        ),
                        "",
                        hideCollapsedVolumeIcon,
                        true
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            id = R.string.callStrengthTitle
                        ),
                        "",
                        hideCollapsedCallStrengthIcon,
                        true
                    )
                }
            }
            statusbarColors -> {
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarClockColorTitle
                        ),
                        customStatusbarClockColor,
                        rememberStatusbarIconClockColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarBatteryIconColorTitle
                        ),
                        customStatusbarBatteryIconColor,
                        rememberStatusbarIconBatteryIconColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarBatteryPercentColorTitle
                        ),
                        customStatusbarBatteryPercentColor,
                        rememberStatusbarIconBatteryPercentColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarWifiIconColorTitle
                        ),
                        customStatusbarWifiIconColor,
                        rememberStatusbarIconWifiColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarMobileIconColorTitle
                        ),
                        customStatusbarMobileIconColor,
                        rememberStatusbarIconMobileColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarNotificationIconColorTitle
                        ),
                        customStatusbarNotificationIconColor,
                        rememberStatusbarIconNotificationColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarDndIconColorTitle
                        ),
                        customStatusbarDndIconColor,
                        rememberStatusbarIconDndColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarAirplaneIconColorTitle
                        ),
                        customStatusbarAirplaneIconColor,
                        rememberStatusbarIconAirplaneColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarBluetoothIconColorTitle
                        ),
                        customStatusbarBluetoothIconColor,
                        rememberStatusbarIconBluetoothColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarHotspotIconColorTitle
                        ),
                        customStatusbarHotspotIconColor,
                        rememberStatusbarIconHotspotColor,
                        sharedPreferences
                    )
                }
                item {
                    TweakColor(
                        context,
                        stringResource(
                            id = R.string.customStatusbarOtherIconColorTitle
                        ),
                        customStatusbarOtherIconColor,
                        rememberStatusbarIconOtherColor,
                        sharedPreferences
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .height(64.dp)
                    )
                }
            }
            buttons -> {
                //Tweaks Items
                item {
                    TweakSectionHeader(
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
                    TweakSectionHeader(
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
                    TweakSectionHeader(
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
                    TweakSectionHeader(
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
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.disableQsLockscreenTitle),
                        stringResource(
                            R.string.disableQsLockscreenSummary),
                        disableQsLockscreen
                    )
                }
            }
            //Quicksettings Tweaks
            quicksettings -> {
                //Tweaks Items
                item {
                    TweakSectionHeader(
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
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.hideQsFooterBuildNumberTitle),
                        stringResource(
                            R.string.hideQsFooterBuildNumberSummary),
                        hideQsFooterBuildNumber
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.expansion
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.smartPulldownTitle
                        ),
                        description = smartPulldownEntries[rememberSmartPulldown],
                        key = smartPulldown,
                        entries = context.resources.getStringArray(R.array.smart_pulldown_entries),
                        0
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.quickPulldownTitle
                        ),
                        description = quickPulldownEntries[rememberQuickPulldown],
                        key = quickPulldown,
                        entries = context.resources.getStringArray(R.array.quick_pulldown_entries),
                        0
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.tileLayout
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsStyleTitle
                        ),
                        description = qsStyleEntries[rememberQsStyle],
                        key = qsStyle,
                        entries = context.resources.getStringArray(R.array.quicksettingsStyle),
                        0,
                        disabledIndex = 1
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsRowsTitle
                        ),
                        description = qqsRowsEntries[rememberQqsRows],
                        key = qqsRows,
                        entries = context.resources.getStringArray(R.array.qqs_rows_entries),
                        1
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsColumnsTitle
                        ),
                        description = qsColumnsEntries[rememberQQsColumns],
                        key = qqsColumns,
                        entries = context.resources.getStringArray(R.array.qs_columns),
                        0
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsColumnsLandscapeTitle
                        ),
                        description = qsColumnsEntries[rememberQQsColumnsLandscape],
                        key = qqsColumnsLandscape,
                        entries = context.resources.getStringArray(R.array.qs_columns),
                        2
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsRowsTitle
                        ),
                        description = qsRowsEntries[rememberQsRows],
                        key = qsRows,
                        entries = context.resources.getStringArray(R.array.qs_rows_entries),
                        2
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsColumnsTitle
                        ),
                        description = qsColumnsEntries[rememberQsColumns],
                        key = qsColumns,
                        entries = context.resources.getStringArray(R.array.qs_columns),
                        0
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsColumnsLandscapeTitle
                        ),
                        description = qsColumnsEntries[rememberQsColumnsLandscape],
                        key = qsColumnsLandscape,
                        entries = context.resources.getStringArray(R.array.qs_columns),
                        2
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.brightnessSlider
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsBrightnessSliderPositionTitle
                        ),
                        description = qsBrightnessSliderPositionEntries[rememberQsBrightnessSliderPosition],
                        key = qsBrightnessSliderPosition,
                        entries = context.resources.getStringArray(R.array.quicksettingsBrightnessSliderPosition),
                        0
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.qqsBrightnessSliderTitle),
                        stringResource(
                            R.string.qqsBrightnessSliderSummary),
                        qqsBrightnessSlider
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .height(64.dp)
                    )
                }
            }
            //Notification tweaks
            notifications -> {
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.general
                        )
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.expandedNotificationsTitle),
                        stringResource(
                            R.string.expandedNotificationsSummary),
                        expandAllNotifications
                    )
                }
                item {
                    TweakSwitch(
                        context,
                        stringResource(
                            R.string.muteScreenOnNotificationsTitle),
                        stringResource(
                            R.string.muteScreenOnNotificationsSummary),
                        muteScreenOnNotifications
                    )
                }
            }
        }

    }
}

@Composable
fun TweakSwitch(context: Context, label: String, description: String, key: String, defaultValue: Boolean = false) {
    var switchState by remember { mutableStateOf(readSwitchState(context, key, defaultValue)) }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {

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
                            bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                            end = 4.dp
                        )
                )
                if (description.isNotEmpty()) {
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
                        horizontal = 24.dp
                    )
                    .size(32.dp),
                colors = SwitchDefaults.colors()
            )
        }
    }
}

@Composable
fun TweakRow(
    context: Context,
    label: String,
    description: String,
    navController: NavController,
    sharedPreferences: SharedPreferences,
    disabled: Boolean
) {

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp
                )
                .clickable(
                    enabled = !disabled,
                    onClick = {
                        navController.navigate(Screens.Tweaks.withArgs(label))
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
                    color = if (disabled)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
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
                        color = if (disabled)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface,
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
}

@Composable
fun TweakColor(
    context: Context,
    label: String,
    key: String,
    previewColor: Int,
    sharedPreferences: SharedPreferences,
) {

    var isColorPickerVisible by remember { mutableStateOf(false) }


    // Conditionally render the dialog if isDialogVisible is true
    if (isColorPickerVisible) {
        // Call the TweakSelectionDialog Composable
        TweakColorDialog(
            onDismissRequest = { isColorPickerVisible = false },
            onConfirmation = { isColorPickerVisible = false },
            key = key,
            context = context,
            sharedPreferences = sharedPreferences,
            label = label
        )
    }
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                            end = 4.dp,
                            bottom = 8.dp,
                        )
                )
            }
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp
                    )
                    .size(32.dp)
                    .background(
                        color = intToColor(previewColor),
                        shape = CircleShape
                    )
                    .clickable {
                        isColorPickerVisible = true
                        //sharedPreferences.edit().putInt(key, android.graphics.Color.RED).apply()
                        //sendBroadcast(context, key, android.graphics.Color.RED)
                    }
            )
        }

    }

}

@Composable
fun intToColor(intValue: Int): Color {
    // Extract the red, green, and blue components from the intValue
    val red = (intValue shr 16) and 0xFF
    val green = (intValue shr 8) and 0xFF
    val blue = intValue and 0xFF

    // Create a Color object using the extracted components
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f, alpha = 1f)
}


@Composable
fun TweakSectionHeader(label: String) {
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

@Composable
fun TweakSelectionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    label: String,
    key: String,
    entries: Array<String>,
    defaultIndex: Int,
    disabledIndex: Int?
) {

    val context = LocalContext.current
    val deviceProtectedStorageContext = context.createDeviceProtectedStorageContext()
    val coroutineScope = rememberCoroutineScope()

    // Create a state variable to track the selected radio button
    var selectedOption by remember { mutableIntStateOf(defaultIndex) }

    // Options for the radio buttons
    val options = remember {
        entries.toList()
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
        selectedOption = sharedPreferences.getInt(key, defaultIndex)
        //For certain keys we need to offset the index
        selectedOption = when (key) {
            qqsRows -> {
                if (sharedPreferences.contains(qqsRows)) {
                    selectedOption - 1
                } else {
                    selectedOption
                }
            }
            qsColumns -> {
                if (sharedPreferences.contains(qsColumns)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }
            qsColumnsLandscape -> {
                if (sharedPreferences.contains(qsColumnsLandscape)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }
            qqsColumns -> {
                if (sharedPreferences.contains(qqsColumns)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }
            qqsColumnsLandscape -> {
                if (sharedPreferences.contains(qqsColumnsLandscape)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }
            qsRows -> {
                if (sharedPreferences.contains(qsRows)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }
            else -> selectedOption
        }
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
                LazyColumn(
                    modifier = Modifier
                        .padding(
                            start=24.dp
                        )
                ) {
                    items(options.size) { index ->
                        val option = options[index]
                        val isClickable = index != disabledIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .let {
                                    if (isClickable) it.clickable { onOptionSelected(index) } else it
                                }
                                .padding(
                                    top = 16.dp,
                                    bottom = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = index == selectedOption,
                                onClick = {
                                    if (isClickable) {
                                        onOptionSelected(index)
                                    }
                                },
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
                                        start = 16.dp,
                                        end = 24.dp
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isClickable) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
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
                            //For certain keys we need to offset the index
                            selectedOption += when (key) {
                                qqsRows -> 1
                                qsColumns -> 2
                                qsColumnsLandscape -> 2
                                qqsColumns -> 2
                                qqsColumnsLandscape -> 2
                                qsRows -> 2
                                else -> 0
                            }
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
fun TweakColorDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    key: String,
    context: Context,
    sharedPreferences: SharedPreferences,
    label: String
) {
    val colorPickerController = rememberColorPickerController()
    var selectedColorInt by remember { mutableIntStateOf(sharedPreferences.getInt(key, android.graphics.Color.WHITE)) }
    var hexCode by remember { mutableStateOf("") }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(
                                top = 18.dp,
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 8.dp
                            )
                    )
                }
                colorPickerController.wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 24.dp),
                    controller = colorPickerController,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColorInt = colorEnvelope.color.toArgb()
                        hexCode = colorEnvelope.hexCode
                    },
                    initialColor = intToColor(
                        sharedPreferences.getInt(key, android.graphics.Color.WHITE)
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = hexCode,
                        onValueChange = { newHexCode ->

                            hexCode = newHexCode

                            if (isValidHexCode(newHexCode)) {
                                selectedColorInt = hexStringToColorInt(hexCode)
                                colorPickerController.selectByColor(Color(android.graphics.Color.parseColor("#$hexCode")), false)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = intToColor(intValue = selectedColorInt),
                            unfocusedTextColor = intToColor(intValue = selectedColorInt),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f), // Take available horizontal space
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "reset",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 24.dp, bottom = 8.dp)
                                .clickable {
                                    selectedColorInt = android.graphics.Color.WHITE
                                    colorPickerController.selectByColor(
                                        Color(android.graphics.Color.WHITE),
                                        false
                                    )
                                    sharedPreferences
                                        .edit()
                                        .putInt(key, selectedColorInt)
                                        .apply()
                                    sendBroadcast(context, key, selectedColorInt)
                                }

                        )
                    }
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .padding(
                                end = 4.dp,
                                bottom = 4.dp
                            ),
                    ) {
                        Text(text = "Dismiss",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    TextButton(
                        onClick = {
                            sharedPreferences.edit().putInt(key, selectedColorInt).apply()
                            sendBroadcast(context, key, selectedColorInt)
                            onConfirmation()
                        },
                        modifier = Modifier
                            .padding(
                                start = 4.dp,
                                end = 16.dp,
                                bottom = 4.dp
                            ),
                    ) {
                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
fun isValidHexCode(hexCode: String): Boolean {
    return try {
        // Check if the hex code contains any spaces
        if (hexCode.contains(" ")) {
            false // Spaces are not allowed
        } else {
            // Attempt to parse the hex code
            val color = Color(android.graphics.Color.parseColor("#$hexCode"))

            // Check if parsing succeeds and if the hex code has exactly 8 digits
            hexCode.length == 8
        }
    } catch (e: IllegalArgumentException) {
        false // Parsing failed, invalid hex code
    }
}
fun hexStringToColorInt(hexString: String): Int {
    // Convert hex string to Color
    val color = Color(android.graphics.Color.parseColor("#$hexString"))

    // Extract RGBA components and pack them into a single Int
    return android.graphics.Color.argb(
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
}

@Composable
fun TweakSelectionRow(
    label: String,
    description: String,
    key: String,
    entries: Array<String>,
    defaultIndex: Int,
    disabledIndex: Int? = null
) {
    // Create a state variable to track whether the dialog should be shown
    var isDialogVisible by remember { mutableStateOf(false) }

    // Conditionally render the dialog if isDialogVisible is true
    if (isDialogVisible) {
        // Call the TweakSelectionDialog Composable
        TweakSelectionDialog(
            onDismissRequest = { isDialogVisible = false },
            onConfirmation = { isDialogVisible = false },
            label = label,
            key = key,
            entries = entries,
            defaultIndex = defaultIndex,
            disabledIndex = disabledIndex
        )
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {

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
}
