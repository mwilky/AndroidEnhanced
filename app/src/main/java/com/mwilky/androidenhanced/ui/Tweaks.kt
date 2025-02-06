package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.mwilky.androidenhanced.BillingManager.Companion.isPremium
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.BroadcastUtils.Companion.sendBroadcast
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.autoExpandFirstNotif
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarWifiIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDateColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarWifiIconColor
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
import com.mwilky.androidenhanced.Utils.Companion.disableCameraScreenOff
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.dualToneQsPanel
import com.mwilky.androidenhanced.Utils.Companion.expandAllNotifications
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedCallStrengthIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.iconBlacklist
import com.mwilky.androidenhanced.Utils.Companion.lsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.notifScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.notifSectionHeaders
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerActiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerInactiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerUnavailableShape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.qsScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.qsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.qsStyle
import com.mwilky.androidenhanced.Utils.Companion.qsTileVibration
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import com.mwilky.androidenhanced.Utils.Companion.statusBarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconDarkColor
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.useDualStatusbarColors
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.ui.Tweaks.Companion.readIconSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.readSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.writeIconSwitchState
import com.mwilky.androidenhanced.ui.Tweaks.Companion.writeSwitchState
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import kotlinx.coroutines.launch
import java.util.Locale


class Tweaks {

    companion object {
        fun readSwitchState(
            deviceProtectedStorageContext: Context, key: String, defaultValue: Boolean = false
        ): Boolean {
            return try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.getBoolean(key, defaultValue)
            } catch (e: Exception) {
                Log.e(TAG, "readSwitchState error: $e")
                false
            }
        }

        fun writeSwitchState(deviceProtectedStorageContext: Context, key: String, state: Boolean) {
            try {

                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)
                sharedPreferences.edit().putBoolean(key, state).apply()
            } catch (e: Exception) {
                Log.e(TAG, "writeSwitchState error: $e")
            }
        }

        fun writeIconSwitchState(
            deviceProtectedStorageContext: Context, key: String, slot: String
        ) {
            try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)

                val currentBlockedIcons: String? = sharedPreferences.getString(key, "")

                val blockedIconsList = (currentBlockedIcons ?: "").split(",")
                    .filter { it.isNotBlank() } // Filter out any blank strings
                    .map { it.trim() }.toMutableList()


                if (blockedIconsList.contains(slot)) {

                    blockedIconsList.remove(slot)
                } else {

                    blockedIconsList.add(slot)
                }

                val updatedBlockedIcons = blockedIconsList.joinToString(",")

                sharedPreferences.edit().putString(key, updatedBlockedIcons).apply()
            } catch (e: Exception) {
                Log.e(TAG, "writeIconSwitchState error: $e")
            }
        }

        fun readIconSwitchState(
            deviceProtectedStorageContext: Context, key: String, slot: String
        ): Boolean {
            return try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)

                val currentBlockedIcons: String? = sharedPreferences.getString(key, "")

                val blockedIconsList = (currentBlockedIcons ?: "").split(",")
                    .filter { it.isNotBlank() } // Filter out any blank strings
                    .map { it.trim() }.toMutableList()

                Log.e(TAG, "currentBlockedIcons = $currentBlockedIcons")

                blockedIconsList.contains(slot)
            } catch (e: Exception) {
                Log.e(TAG, "readIconSwitchState error: $e")
                false
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tweaks(navController: NavController, deviceProtectedStorageContext: Context, screen: String) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = screen,
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        TweaksScrollableContent(
            topPadding = it,
            screen = screen,
            navController = navController,
            deviceProtectedStorageContext
        )
    })
}

@Composable
fun TweaksScrollableContent(
    topPadding: PaddingValues,
    screen: String,
    navController: NavController,
    deviceProtectedStorageContext: Context
) {

    val sharedPreferences = deviceProtectedStorageContext.getSharedPreferences(PREFS, MODE_PRIVATE)

    val statusBarClockPositionEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.statusbar_clock_position_entries)
    val smartPulldownEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.smart_pulldown_entries)
    val quickPulldownEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quick_pulldown_entries)
    val qqsRowsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qqs_rows_entries)
    val qsRowsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qs_rows_entries)
    val qsColumnsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns)
    val qsStyleEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quicksettingsStyle)
    val qsBrightnessSliderPositionEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quicksettingsBrightnessSliderPosition)
    val qsIconContainerShapeEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qsIconContainerShape)


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
        mutableIntStateOf(sharedPreferences.getInt(qqsRows, 2) - 1)
    }
    var rememberQsRows by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsRows, 4) - 2)
    }
    var rememberQsColumns by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsColumns, 2) - 2)
    }
    var rememberQsColumnsLandscape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsColumnsLandscape, 4) - 2)
    }
    var rememberQQsColumns by remember {
        mutableIntStateOf(sharedPreferences.getInt(qqsColumns, 2) - 2)
    }
    var rememberQQsColumnsLandscape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qqsColumnsLandscape, 4) - 2)
    }
    var rememberQsBrightnessSliderPosition by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsBrightnessSliderPosition, 0))
    }
    var rememberStatusbarIconClockColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarClockColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconBatteryIconColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarBatteryIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconBatteryPercentColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarBatteryPercentColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconWifiColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarWifiIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconMobileColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarMobileIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconNotificationColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarNotificationIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconOtherColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarOtherIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconDndColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarDndIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconAirplaneColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarAirplaneIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconHotspotColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarHotspotIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconBluetoothColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarBluetoothIconColor, android.graphics.Color.WHITE
            )
        )
    }


    var rememberLsStatusbarCarrierColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarCarrierColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconBatteryIconColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarBatteryIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconBatteryPercentColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarBatteryPercentColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconWifiColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarWifiIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconMobileColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarMobileIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconOtherColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarOtherIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconDndColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarDndIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconAirplaneColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarAirplaneIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconHotspotColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarHotspotIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconBluetoothColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarBluetoothIconColor, android.graphics.Color.WHITE
            )
        )
    }


    var rememberQsStatusbarIconClockColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarClockColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconBatteryIconColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarBatteryIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconBatteryPercentColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarBatteryPercentColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconWifiColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarWifiIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconMobileColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarMobileIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarCarrierColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarCarrierColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarDateColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarDateColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconOtherColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarOtherIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconDndColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarDndIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconAirplaneColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarAirplaneIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconHotspotColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarHotspotIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconBluetoothColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarBluetoothIconColor, android.graphics.Color.WHITE
            )
        )
    }


    var rememberStatusbarIconGlobalColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customStatusbarGlobalIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberQsStatusbarIconGlobalColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customQsStatusbarGlobalIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberLsStatusbarIconGlobalColor by remember {
        mutableIntStateOf(
            sharedPreferences.getInt(
                customLsStatusbarGlobalIconColor, android.graphics.Color.WHITE
            )
        )
    }
    var rememberStatusbarIconAccentColor by remember {
        mutableStateOf(sharedPreferences.getBoolean(statusbarIconAccentColor, false))
    }
    var rememberQsStatusbarIconAccentColor by remember {
        mutableStateOf(sharedPreferences.getBoolean(qsStatusbarIconAccentColor, false))
    }
    var rememberLsStatusbarIconAccentColor by remember {
        mutableStateOf(sharedPreferences.getBoolean(lsStatusbarIconAccentColor, false))
    }
    var rememberQsIconContainerActiveShape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsIconContainerActiveShape, 0))
    }
    var rememberQsIconContainerInactiveShape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsIconContainerInactiveShape, 0))
    }
    var rememberQsIconContainerUnavailableShape by remember {
        mutableIntStateOf(sharedPreferences.getInt(qsIconContainerUnavailableShape, 0))
    }

    var rememberStatusbarIconDarkColor by remember {
        mutableIntStateOf(sharedPreferences.getInt(statusbarIconDarkColor, -1728053248))
    }

    var rememberUseDualStatusbarColors by remember {
        mutableStateOf(sharedPreferences.getBoolean(useDualStatusbarColors, true))
    }

    // Set the listener and update the remembered value on change to force a recomposition
    val sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            statusBarClockPosition -> rememberStatusBarClockPosition =
                sharedPreferences.getInt(statusBarClockPosition, 0)

            smartPulldown -> rememberSmartPulldown = sharedPreferences.getInt(smartPulldown, 0)

            quickPulldown -> rememberQuickPulldown = sharedPreferences.getInt(quickPulldown, 0)

            qsStyle -> rememberQsStyle = sharedPreferences.getInt(qsStyle, 0)

            qqsRows -> rememberQqsRows = sharedPreferences.getInt(qqsRows, 2) - 1

            qsColumns -> rememberQsColumns = sharedPreferences.getInt(qsColumns, 2) - 2

            qsColumnsLandscape -> rememberQsColumnsLandscape =
                sharedPreferences.getInt(qsColumnsLandscape, 4) - 2

            qqsColumns -> rememberQQsColumns = sharedPreferences.getInt(qqsColumns, 2) - 2

            qqsColumnsLandscape -> rememberQQsColumnsLandscape =
                sharedPreferences.getInt(qqsColumnsLandscape, 4) - 2

            qsRows -> rememberQsRows = sharedPreferences.getInt(qsRows, 4) - 2

            qsBrightnessSliderPosition -> rememberQsBrightnessSliderPosition =
                sharedPreferences.getInt(qsBrightnessSliderPosition, 0)

            customStatusbarGlobalIconColor -> rememberStatusbarIconGlobalColor =
                sharedPreferences.getInt(customStatusbarGlobalIconColor, -1)

            customQsStatusbarGlobalIconColor -> rememberQsStatusbarIconGlobalColor =
                sharedPreferences.getInt(customQsStatusbarGlobalIconColor, -1)

            customLsStatusbarGlobalIconColor -> rememberLsStatusbarIconGlobalColor =
                sharedPreferences.getInt(customLsStatusbarGlobalIconColor, -1)

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


            customQsStatusbarClockColor -> rememberQsStatusbarIconClockColor =
                sharedPreferences.getInt(customQsStatusbarClockColor, -1)

            customQsStatusbarBatteryIconColor -> rememberQsStatusbarIconBatteryIconColor =
                sharedPreferences.getInt(customQsStatusbarBatteryIconColor, -1)

            customQsStatusbarBatteryPercentColor -> rememberQsStatusbarIconBatteryPercentColor =
                sharedPreferences.getInt(customQsStatusbarBatteryPercentColor, -1)

            customQsStatusbarWifiIconColor -> rememberQsStatusbarIconWifiColor =
                sharedPreferences.getInt(customQsStatusbarWifiIconColor, -1)

            customQsStatusbarMobileIconColor -> rememberQsStatusbarIconMobileColor =
                sharedPreferences.getInt(customQsStatusbarMobileIconColor, -1)

            customQsStatusbarCarrierColor -> rememberQsStatusbarCarrierColor =
                sharedPreferences.getInt(customQsStatusbarCarrierColor, -1)

            customQsStatusbarDateColor -> rememberQsStatusbarDateColor =
                sharedPreferences.getInt(customQsStatusbarDateColor, -1)

            customQsStatusbarDndIconColor -> rememberQsStatusbarIconDndColor =
                sharedPreferences.getInt(customQsStatusbarDndIconColor, -1)

            customQsStatusbarAirplaneIconColor -> rememberQsStatusbarIconAirplaneColor =
                sharedPreferences.getInt(customQsStatusbarAirplaneIconColor, -1)

            customQsStatusbarBluetoothIconColor -> rememberQsStatusbarIconBluetoothColor =
                sharedPreferences.getInt(customQsStatusbarBluetoothIconColor, -1)

            customQsStatusbarHotspotIconColor -> rememberQsStatusbarIconHotspotColor =
                sharedPreferences.getInt(customQsStatusbarHotspotIconColor, -1)

            customQsStatusbarOtherIconColor -> rememberQsStatusbarIconOtherColor =
                sharedPreferences.getInt(customQsStatusbarOtherIconColor, -1)

            customLsStatusbarBatteryIconColor -> rememberLsStatusbarIconBatteryIconColor =
                sharedPreferences.getInt(customLsStatusbarBatteryIconColor, -1)

            customLsStatusbarBatteryPercentColor -> rememberLsStatusbarIconBatteryPercentColor =
                sharedPreferences.getInt(customLsStatusbarBatteryPercentColor, -1)

            customLsStatusbarWifiIconColor -> rememberLsStatusbarIconWifiColor =
                sharedPreferences.getInt(customLsStatusbarWifiIconColor, -1)

            customLsStatusbarMobileIconColor -> rememberLsStatusbarIconMobileColor =
                sharedPreferences.getInt(customLsStatusbarMobileIconColor, -1)

            customLsStatusbarCarrierColor -> rememberLsStatusbarCarrierColor =
                sharedPreferences.getInt(customLsStatusbarCarrierColor, -1)

            customLsStatusbarDndIconColor -> rememberLsStatusbarIconDndColor =
                sharedPreferences.getInt(customLsStatusbarDndIconColor, -1)

            customLsStatusbarAirplaneIconColor -> rememberLsStatusbarIconAirplaneColor =
                sharedPreferences.getInt(customLsStatusbarAirplaneIconColor, -1)

            customLsStatusbarBluetoothIconColor -> rememberLsStatusbarIconBluetoothColor =
                sharedPreferences.getInt(customLsStatusbarBluetoothIconColor, -1)

            customLsStatusbarHotspotIconColor -> rememberLsStatusbarIconHotspotColor =
                sharedPreferences.getInt(customLsStatusbarHotspotIconColor, -1)

            customLsStatusbarOtherIconColor -> rememberLsStatusbarIconOtherColor =
                sharedPreferences.getInt(customLsStatusbarOtherIconColor, -1)

            statusbarIconDarkColor -> rememberStatusbarIconDarkColor =
                sharedPreferences.getInt(statusbarIconDarkColor, -1728053248)


            statusbarIconAccentColor -> rememberStatusbarIconAccentColor =
                sharedPreferences.getBoolean(statusbarIconAccentColor, false)

            qsStatusbarIconAccentColor -> rememberQsStatusbarIconAccentColor =
                sharedPreferences.getBoolean(qsStatusbarIconAccentColor, false)

            lsStatusbarIconAccentColor -> rememberLsStatusbarIconAccentColor =
                sharedPreferences.getBoolean(lsStatusbarIconAccentColor, false)

            qsIconContainerActiveShape -> rememberQsIconContainerActiveShape =
                sharedPreferences.getInt(qsIconContainerActiveShape, 0)

            qsIconContainerInactiveShape -> rememberQsIconContainerInactiveShape =
                sharedPreferences.getInt(qsIconContainerInactiveShape, 0)

            qsIconContainerUnavailableShape -> rememberQsIconContainerUnavailableShape =
                sharedPreferences.getInt(qsIconContainerUnavailableShape, 0)

            useDualStatusbarColors -> rememberUseDualStatusbarColors =
                sharedPreferences.getBoolean(useDualStatusbarColors, true)
        }
    }

    val individualStatusbarColorsDisabled = rememberStatusbarIconGlobalColor != -1
    val individualQsStatusbarColorsDisabled = rememberQsStatusbarIconGlobalColor != -1
    val individualLsStatusbarColorsDisabled = rememberLsStatusbarIconGlobalColor != -1

    val statusbarIconAccentEnabled = rememberStatusbarIconAccentColor
    val qsStatusbarIconAccentEnabled = rememberQsStatusbarIconAccentColor
    val lsStatusbarIconAccentEnabled = rememberLsStatusbarIconAccentColor

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
        val statusbarColors = "Individual statusbar icon colors"
        val qsStatusbarColors = "Individual quicksettings statusbar icon colors"
        val lsStatusbarColors = "Individual lockscreen statusbar icon colors"
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
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.doubleTapToSleepTitle
                        ), stringResource(
                            id = R.string.doubleTapToSleepSummary
                        ), doubleTapToSleep
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.statusbarBrightnessControlTitle
                        ), stringResource(
                            id = R.string.statusbarBrightnessControlSummary
                        ), statusBarBrightnessControl
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
                        entries = deviceProtectedStorageContext.resources.getStringArray(R.array.statusbar_clock_position_entries),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.statusbarClockSecondsTitle
                        ), stringResource(
                            id = R.string.statusbarClockSecondsSummary
                        ), statusBarClockSeconds
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
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.statusbarIconUseAccentColorTitle
                        ), stringResource(
                            id = R.string.statusbarIconUseAccentColorSummary
                        ), statusbarIconAccentColor
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarGlobalIconColorTitle
                        ),
                        customStatusbarGlobalIconColor,
                        rememberStatusbarIconGlobalColor,
                        sharedPreferences,
                        statusbarIconAccentEnabled,
                        stringResource(
                            id = if (statusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else R.string.customStatusbarGlobalIconColorSummary
                        ),
                    )
                }

                item {
                    TweakRow(
                        context = deviceProtectedStorageContext,
                        label = statusbarColors,
                        description = stringResource(
                            id = if (statusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else if (!individualStatusbarColorsDisabled) R.string.customStatusbarIndividualIconColorSummary
                            else R.string.customStatusbarIndividualIconColorSummaryDisabled
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        statusbarIconAccentEnabled || individualStatusbarColorsDisabled
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.useDualStatusbarColorsTitle
                        ), stringResource(
                            id = R.string.useDualStatusbarColorsSummary
                        ), useDualStatusbarColors, defaultValue = true, premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarDarkIconColorTitle
                        ),
                        statusbarIconDarkColor,
                        rememberStatusbarIconDarkColor,
                        sharedPreferences,
                        !rememberUseDualStatusbarColors,
                        stringResource(
                            if (rememberUseDualStatusbarColors) R.string.customStatusbarDarkIconColorSummary
                            else R.string.customStatusbarDarkIconColorSummaryDisabled
                        ),
                        -1728053248
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
                        context = deviceProtectedStorageContext,
                        label = allStatusbarIcons,
                        description = stringResource(
                            id = R.string.allStatusbarIcons
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        false
                    )
                }
                item {
                    TweakRow(
                        context = deviceProtectedStorageContext,
                        label = collapsedIcons,
                        description = stringResource(
                            id = R.string.collapsedStatusbarIcons
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        false
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }

            allStatusbarIcons -> {
                //Tweaks Items
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.airplaneTitle
                        ), "", iconBlacklist, "airplane"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.alarmTitle
                        ), "", iconBlacklist, "alarm_clock"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.batteryIconTitle
                        ), "", iconBlacklist, "battery"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.bluetoothTitle
                        ), "", iconBlacklist, "bluetooth"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.castTitle
                        ), "", iconBlacklist, "cast"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.zenTitle
                        ), "", iconBlacklist, "zen"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.hotspotTitle
                        ), "", iconBlacklist, "hotspot"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.locationTitle
                        ), "", iconBlacklist, "location"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.mobileTitle
                        ), "", iconBlacklist, "mobile"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.screenRecordTitle
                        ), "", iconBlacklist, "screen_record"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.speakerTitle
                        ), "", iconBlacklist, "speakerphone"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.volumeTitle
                        ), "", iconBlacklist, "volume"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = com.mwilky.androidenhanced.R.string.vpnTitle
                        ), "", iconBlacklist, "vpn"
                    )
                }
                item {
                    TweakIconSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.wifiTitle
                        ), "", iconBlacklist, "wifi"
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }

            collapsedIcons -> {
                //Tweaks Items
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.alarmTitle
                        ), "", hideCollapsedAlarmIcon, true
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.volumeTitle
                        ), "", hideCollapsedVolumeIcon, true
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.callStrengthTitle
                        ), "", hideCollapsedCallStrengthIcon, true
                    )
                }
            }

            statusbarColors -> {
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarAirplaneIconColorTitle
                        ),
                        customStatusbarAirplaneIconColor,
                        rememberStatusbarIconAirplaneColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryIconColorTitle
                        ),
                        customStatusbarBatteryIconColor,
                        rememberStatusbarIconBatteryIconColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryPercentColorTitle
                        ),
                        customStatusbarBatteryPercentColor,
                        rememberStatusbarIconBatteryPercentColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBluetoothIconColorTitle
                        ),
                        customStatusbarBluetoothIconColor,
                        rememberStatusbarIconBluetoothColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarClockColorTitle
                        ),
                        customStatusbarClockColor,
                        rememberStatusbarIconClockColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarDndIconColorTitle
                        ),
                        customStatusbarDndIconColor,
                        rememberStatusbarIconDndColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarHotspotIconColorTitle
                        ),
                        customStatusbarHotspotIconColor,
                        rememberStatusbarIconHotspotColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarMobileIconColorTitle
                        ),
                        customStatusbarMobileIconColor,
                        rememberStatusbarIconMobileColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarNotificationIconColorTitle
                        ),
                        customStatusbarNotificationIconColor,
                        rememberStatusbarIconNotificationColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarWifiIconColorTitle
                        ),
                        customStatusbarWifiIconColor,
                        rememberStatusbarIconWifiColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarOtherIconColorTitle
                        ),
                        customStatusbarOtherIconColor,
                        rememberStatusbarIconOtherColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }

            lsStatusbarColors -> {
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarAirplaneIconColorTitle
                        ),
                        customLsStatusbarAirplaneIconColor,
                        rememberLsStatusbarIconAirplaneColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryIconColorTitle
                        ),
                        customLsStatusbarBatteryIconColor,
                        rememberLsStatusbarIconBatteryIconColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryPercentColorTitle
                        ),
                        customLsStatusbarBatteryPercentColor,
                        rememberLsStatusbarIconBatteryPercentColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBluetoothIconColorTitle
                        ),
                        customLsStatusbarBluetoothIconColor,
                        rememberLsStatusbarIconBluetoothColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarCarrierColorTitle
                        ),
                        customLsStatusbarCarrierColor,
                        rememberLsStatusbarCarrierColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarDndIconColorTitle
                        ),
                        customLsStatusbarDndIconColor,
                        rememberLsStatusbarIconDndColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarHotspotIconColorTitle
                        ),
                        customLsStatusbarHotspotIconColor,
                        rememberLsStatusbarIconHotspotColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarMobileIconColorTitle
                        ),
                        customLsStatusbarMobileIconColor,
                        rememberLsStatusbarIconMobileColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarWifiIconColorTitle
                        ),
                        customLsStatusbarWifiIconColor,
                        rememberLsStatusbarIconWifiColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarOtherIconColorTitle
                        ),
                        customLsStatusbarOtherIconColor,
                        rememberLsStatusbarIconOtherColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }

            qsStatusbarColors -> {
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarAirplaneIconColorTitle
                        ),
                        customQsStatusbarAirplaneIconColor,
                        rememberQsStatusbarIconAirplaneColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryIconColorTitle
                        ),
                        customQsStatusbarBatteryIconColor,
                        rememberQsStatusbarIconBatteryIconColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBatteryPercentColorTitle
                        ),
                        customQsStatusbarBatteryPercentColor,
                        rememberQsStatusbarIconBatteryPercentColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarBluetoothIconColorTitle
                        ),
                        customQsStatusbarBluetoothIconColor,
                        rememberQsStatusbarIconBluetoothColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarCarrierColorTitle
                        ),
                        customQsStatusbarCarrierColor,
                        rememberQsStatusbarCarrierColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarClockColorTitle
                        ),
                        customQsStatusbarClockColor,
                        rememberQsStatusbarIconClockColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarDateColorTitle
                        ),
                        customQsStatusbarDateColor,
                        rememberQsStatusbarDateColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarDndIconColorTitle
                        ),
                        customQsStatusbarDndIconColor,
                        rememberQsStatusbarIconDndColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarHotspotIconColorTitle
                        ),
                        customQsStatusbarHotspotIconColor,
                        rememberQsStatusbarIconHotspotColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarMobileIconColorTitle
                        ),
                        customQsStatusbarMobileIconColor,
                        rememberQsStatusbarIconMobileColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarWifiIconColorTitle
                        ),
                        customQsStatusbarWifiIconColor,
                        rememberQsStatusbarIconWifiColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarOtherIconColorTitle
                        ),
                        customQsStatusbarOtherIconColor,
                        rememberQsStatusbarIconOtherColor,
                        sharedPreferences,
                        premiumFeature = true
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }

            buttons -> {
                //Tweaks Items
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.launcher
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.doubleTapToSleepTitle
                        ), stringResource(
                            id = R.string.doubleTapToSleepLauncherSummary
                        ), doubleTapToSleepLauncher
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.powerButton
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.longPressPowerTorchScreenOffTitle
                        ), stringResource(
                            R.string.longPressPowerTorchScreenOffSummary
                        ), torchPowerScreenOff
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.torchAutoOffScreenOnTitle
                        ), stringResource(
                            R.string.torchAutoOffScreenOnSummary
                        ), torchAutoOffScreenOn
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.disableDoublePressCameraScreenOffTitle
                        ), stringResource(
                            R.string.disableDoublePressCameraScreenOffSummary
                        ), disableCameraScreenOff
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
                        deviceProtectedStorageContext, stringResource(
                            R.string.volKeyMediaControlTitle
                        ), stringResource(
                            R.string.volKeyMediaControlSummary
                        ), volKeyMediaControl
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
                        deviceProtectedStorageContext, stringResource(
                            R.string.allowAllRotationsTitle
                        ), stringResource(
                            R.string.allowAllRotationsSummary
                        ), allowAllRotations
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.disableSecureScreenshotsTitle
                        ), stringResource(
                            R.string.disableSecureScreenshotsSummary
                        ), disableSecureScreenshots
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
                        deviceProtectedStorageContext, stringResource(
                            R.string.hideLockscreenStatusbarTitle
                        ), stringResource(
                            R.string.hideLockscreenStatusbarSummary
                        ), hideLockscreenStatusBar
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.randomKeypadTitle
                        ), stringResource(
                            R.string.randomKeypadSummary
                        ), scrambleKeypad
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.disableLockscreenPowerMenuTitle
                        ), stringResource(
                            R.string.disableLockscreenPowerMenuSummary
                        ), disableLockscreenPowerMenu
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.disableQsLockscreenTitle
                        ), stringResource(
                            R.string.disableQsLockscreenSummary
                        ), disableQsLockscreen
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.iconColors
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.statusbarIconUseAccentColorTitle
                        ), stringResource(
                            id = R.string.qsStatusbarIconUseAccentColorSummary
                        ), lsStatusbarIconAccentColor
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarGlobalIconColorTitle
                        ),
                        customLsStatusbarGlobalIconColor,
                        rememberLsStatusbarIconGlobalColor,
                        sharedPreferences,
                        lsStatusbarIconAccentEnabled,
                        stringResource(
                            id = if (lsStatusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else R.string.customLsStatusbarGlobalIconColorSummary
                        ),
                    )
                }
                item {
                    TweakRow(
                        context = deviceProtectedStorageContext,
                        label = lsStatusbarColors,
                        description = stringResource(
                            id = if (lsStatusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else if (!individualLsStatusbarColorsDisabled) R.string.customStatusbarIndividualIconColorSummary
                            else R.string.customStatusbarIndividualIconColorSummaryDisabled
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        lsStatusbarIconAccentEnabled || individualLsStatusbarColorsDisabled
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
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
                        deviceProtectedStorageContext, stringResource(
                            R.string.qsPanelDualColorTitle
                        ), stringResource(
                            R.string.qsPanelDualColorSummary
                        ), dualToneQsPanel
                    )
                }
                item {
                    var sliderValue by remember { mutableFloatStateOf(sharedPreferences.getFloat(qsScrimAlpha, 1.0f)) }
                    SettingsSlider(
                        context = deviceProtectedStorageContext,
                        label = deviceProtectedStorageContext.getString(R.string.scrimAlphaTitle),
                        valueLabel = deviceProtectedStorageContext.getString(R.string.scrimAlphaSummary),
                        value = sliderValue,
                        onValueChange = { newValue ->
                            sliderValue = newValue
                        },
                        onValueChangeFinished = { roundedValue ->
                            // Update SharedPreferences with the new rounded value.
                            sharedPreferences.edit().putFloat(qsScrimAlpha, roundedValue).apply()
                            // Update the local state to the new value
                            sliderValue = roundedValue
                            sendBroadcast(deviceProtectedStorageContext, qsScrimAlpha, sliderValue)
                        },
                        valueRange = 0.0f..1.0f,  // Adjust the range as needed
                        displayValue = { v -> String.format(Locale.UK, "%.2f", v) },
                        roundingFn = { v -> String.format(Locale.UK, "%.2f", v).toFloat() }
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.qsTileClickVibrationTitle
                        ), stringResource(
                            R.string.qsTileClickVibrationSummary
                        ), qsTileVibration
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.hideQsFooterBuildNumberTitle
                        ), stringResource(
                            R.string.hideQsFooterBuildNumberSummary
                        ), hideQsFooterBuildNumber
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
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.smart_pulldown_entries
                        ),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.quickPulldownTitle
                        ),
                        description = quickPulldownEntries[rememberQuickPulldown],
                        key = quickPulldown,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.quick_pulldown_entries
                        ),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
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
                    val qsStyleImageResourceIds: List<Int> = listOf(
                        if (!isSystemInDarkTheme()) R.drawable.qs_style_0_light else R.drawable.qs_style_0_dark,
                        if (!isSystemInDarkTheme()) R.drawable.qs_style_1_light else R.drawable.qs_style_1_dark,
                        if (!isSystemInDarkTheme()) R.drawable.qs_style_2_light else R.drawable.qs_style_2_dark,
                    )

                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsStyleTitle
                        ),
                        description = qsStyleEntries[rememberQsStyle],
                        key = qsStyle,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.quicksettingsStyle
                        ),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext,
                        imageResourceIds = qsStyleImageResourceIds,
                        premiumIndexes = listOf<Int>(1,2)

                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsIconContainerActiveShapeTitle
                        ),
                        description = if (rememberQsStyle == 1) qsIconContainerShapeEntries[rememberQsIconContainerActiveShape]
                        else stringResource(
                            id = R.string.qsIconContainerShapeDisabledTitle
                        ),
                        key = qsIconContainerActiveShape,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qsIconContainerShape
                        ),
                        0,
                        disabled = rememberQsStyle != 1,
                        deviceProtectedStorageContext = deviceProtectedStorageContext,
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsIconContainerInactiveShapeTitle
                        ),
                        description = if (rememberQsStyle == 1) qsIconContainerShapeEntries[rememberQsIconContainerInactiveShape]
                        else stringResource(
                            id = R.string.qsIconContainerShapeDisabledTitle
                        ),
                        key = qsIconContainerInactiveShape,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qsIconContainerShape
                        ),
                        0,
                        disabled = rememberQsStyle != 1,
                        deviceProtectedStorageContext = deviceProtectedStorageContext,
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsIconContainerUnavailableShapeTitle
                        ),
                        description = if (rememberQsStyle == 1) qsIconContainerShapeEntries[rememberQsIconContainerUnavailableShape]
                        else stringResource(
                            id = R.string.qsIconContainerShapeDisabledTitle
                        ),
                        key = qsIconContainerUnavailableShape,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qsIconContainerShape
                        ),
                        0,
                        disabled = rememberQsStyle != 1,
                        deviceProtectedStorageContext = deviceProtectedStorageContext,
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.rowCount
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsRowsTitle
                        ),
                        description = qqsRowsEntries[rememberQqsRows],
                        key = qqsRows,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qqs_rows_entries
                        ),
                        1,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsRowsTitle
                        ),
                        description = qsRowsEntries[rememberQsRows],
                        key = qsRows,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qs_rows_entries
                        ),
                        2,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.columnCount
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsColumnsTitle
                        ),
                        description = qsColumnsEntries[rememberQQsColumns],
                        key = qqsColumns,
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.qs_columns
                        ),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsColumnsTitle
                        ),
                        description = qsColumnsEntries[rememberQsColumns],
                        key = qsColumns,
                        entries = deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.columnCountLandscape
                        )
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qqsColumnsLandscapeTitle
                        ),
                        description = qsColumnsEntries[rememberQQsColumnsLandscape],
                        key = qqsColumnsLandscape,
                        entries = deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns),
                        2,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSelectionRow(
                        label = stringResource(
                            id = R.string.qsColumnsLandscapeTitle
                        ),
                        description = qsColumnsEntries[rememberQsColumnsLandscape],
                        key = qsColumnsLandscape,
                        entries = deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns),
                        2,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
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
                        entries = deviceProtectedStorageContext.resources.getStringArray(
                            R.array.quicksettingsBrightnessSliderPosition
                        ),
                        0,
                        deviceProtectedStorageContext = deviceProtectedStorageContext
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.qqsBrightnessSliderTitle
                        ), stringResource(
                            R.string.qqsBrightnessSliderSummary
                        ), qqsBrightnessSlider
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.iconColors
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            id = R.string.statusbarIconUseAccentColorTitle
                        ), stringResource(
                            id = R.string.qsStatusbarIconUseAccentColorSummary
                        ), qsStatusbarIconAccentColor
                    )
                }
                item {
                    TweakColor(
                        deviceProtectedStorageContext,
                        stringResource(
                            id = R.string.customStatusbarGlobalIconColorTitle
                        ),
                        customQsStatusbarGlobalIconColor,
                        rememberQsStatusbarIconGlobalColor,
                        sharedPreferences,
                        qsStatusbarIconAccentEnabled,
                        stringResource(
                            id = if (qsStatusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else R.string.customQsStatusbarGlobalIconColorSummary
                        ),
                    )
                }
                item {
                    TweakRow(
                        context = deviceProtectedStorageContext,
                        label = qsStatusbarColors,
                        description = stringResource(
                            id = if (qsStatusbarIconAccentEnabled) R.string.customStatusbarColorsSummaryDisabled
                            else if (!individualQsStatusbarColorsDisabled) R.string.customStatusbarIndividualIconColorSummary
                            else R.string.customStatusbarIndividualIconColorSummaryDisabled
                        ),
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        qsStatusbarIconAccentEnabled || individualQsStatusbarColorsDisabled
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
            //Notification tweaks
            notifications -> {
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.expansion
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.autoExpandedFirstTitle
                        ), stringResource(
                            R.string.autoExpandedFirstSummary
                        ), autoExpandFirstNotif, true
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.expandedNotificationsTitle
                        ), stringResource(
                            R.string.expandedNotificationsSummary
                        ), expandAllNotifications
                    )
                }
                item {
                    TweakSectionHeader(
                        label = stringResource(
                            id = R.string.general
                        )
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.notificationSectionHeadersTitle
                        ), stringResource(
                            R.string.notificationSectionHeadersSummary
                        ), notifSectionHeaders, true
                    )
                }
                item {
                    var sliderValue by remember { mutableFloatStateOf(sharedPreferences.getFloat(notifScrimAlpha, 1.0f)) }
                    SettingsSlider(
                        context = deviceProtectedStorageContext,
                        label = deviceProtectedStorageContext.getString(R.string.scrimAlphaTitle),
                        valueLabel = deviceProtectedStorageContext.getString(R.string.scrimAlphaSummary),
                        value = sliderValue,
                        onValueChange = { newValue ->
                            sliderValue = newValue
                        },
                        onValueChangeFinished = { roundedValue ->
                            // Update SharedPreferences with the new rounded value.
                            sharedPreferences.edit().putFloat(notifScrimAlpha, roundedValue).apply()
                            // Update the local state to the new value
                            sliderValue = roundedValue
                            sendBroadcast(deviceProtectedStorageContext, notifScrimAlpha, sliderValue)
                        },
                        valueRange = 0.0f..1.0f,  // Adjust the range as needed
                        displayValue = { v -> String.format(Locale.UK, "%.2f", v) },
                        roundingFn = { v -> String.format(Locale.UK, "%.2f", v).toFloat() }
                    )
                }
                item {
                    TweakSwitch(
                        deviceProtectedStorageContext, stringResource(
                            R.string.muteScreenOnNotificationsTitle
                        ), stringResource(
                            R.string.muteScreenOnNotificationsSummary
                        ), muteScreenOnNotifications
                    )
                }
            }
        }

    }
}

@Composable
fun TweakSwitch(
    context: Context,
    label: String,
    description: String,
    key: String,
    defaultValue: Boolean = false,
    premiumFeature: Boolean = false
) {
    var switchState by remember { mutableStateOf(readSwitchState(context, key, defaultValue)) }

    val isPremiumUnlocked = !premiumFeature || (premiumFeature && isPremium)

    ElevatedCard(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()
        .clickable(enabled = premiumFeature && !isPremium, onClick = {
            Toast
                .makeText(
                    context, context.getString(R.string.requires_subscription), Toast.LENGTH_SHORT
                )
                .show()
        }), shape = RoundedCornerShape(10.dp), colors = CardDefaults.elevatedCardColors()) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), // Take available horizontal space
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isPremiumUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    ),
                    modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                            end = 4.dp
                        ),
                    fontFamily = caviarDreamsFamily
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPremiumUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.padding(
                                start = 16.dp, bottom = 8.dp, end = 16.dp
                            ),
                        fontFamily = caviarDreamsFamily
                    )
                }
                if (premiumFeature && !isPremium) {
                    Text(
                        text = stringResource(id = R.string.requires_subscription),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
            Switch(
                enabled = isPremiumUnlocked, checked = switchState, onCheckedChange = {
                    switchState = !switchState
                    writeSwitchState(context, key, switchState)
                    sendBroadcast(context, key, switchState)
                }, modifier = Modifier
                    .padding(
                        horizontal = 24.dp
                    )
                    .size(32.dp), colors = SwitchDefaults.colors()
            )
        }
    }
}

@Composable
fun TweakIconSwitch(
    context: Context, label: String, description: String, key: String, slot: String
) {
    var switchState by remember { mutableStateOf(readIconSwitchState(context, key, slot)) }

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
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), // Take available horizontal space
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                            end = 4.dp
                        ),
                    fontFamily = caviarDreamsFamily
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                                start = 16.dp, bottom = 8.dp, end = 16.dp
                            ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
            Switch(
                checked = switchState, onCheckedChange = {
                    switchState = !switchState
                    writeIconSwitchState(context, key, slot)
                    sendBroadcast(context, key, slot)
                }, modifier = Modifier
                    .padding(
                        horizontal = 24.dp
                    )
                    .size(32.dp), colors = SwitchDefaults.colors()
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

        Row(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp, bottom = 16.dp
            )
            .clickable(enabled = !disabled, onClick = {
                navController.navigate(Screens.Tweaks.withArgs(label))
            }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.weight(1f), // Take available horizontal space
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                            start = 16.dp, top = 8.dp, end = 4.dp
                        ),
                    fontFamily = caviarDreamsFamily
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                                start = 16.dp, bottom = 8.dp, end = 16.dp
                            ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        }
    }
}

@Composable
fun TweakColor(
    deviceProtectedStorageContext: Context,
    label: String,
    key: String,
    previewColor: Int,
    sharedPreferences: SharedPreferences,
    disabled: Boolean = false,
    description: String = "",
    resetColor: Int = android.graphics.Color.WHITE,
    premiumFeature: Boolean = false
) {

    val isPremiumUnlocked = !premiumFeature || (premiumFeature && isPremium)

    var isColorPickerVisible by remember { mutableStateOf(false) }


    // Conditionally render the dialog if isDialogVisible is true
    if (isColorPickerVisible) {
        // Call the TweakSelectionDialog Composable
        TweakColorDialog(onDismissRequest = { isColorPickerVisible = false },
            onConfirmation = { isColorPickerVisible = false },
            key = key,
            defaultColor = previewColor,
            context = deviceProtectedStorageContext,
            sharedPreferences = sharedPreferences,
            label = label,
            resetColor
        )
    }
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .clickable(enabled = premiumFeature && !isPremium, onClick = {
                Toast
                    .makeText(
                        deviceProtectedStorageContext,
                        deviceProtectedStorageContext.getString(R.string.requires_subscription),
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f), // Take available horizontal space
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (!isPremiumUnlocked || disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            end = 4.dp,
                            bottom = if (description.isNotEmpty() || (premiumFeature && !isPremium)) 0.dp else 8.dp,
                        ),
                    fontFamily = caviarDreamsFamily
                )
                if (premiumFeature && !isPremium) {
                    Text(
                        text = stringResource(id = R.string.requires_subscription),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 4.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }

                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                                start = 16.dp, bottom = 8.dp, end = 4.dp
                            ),
                        fontFamily = caviarDreamsFamily
                    )
                }

            }
            Box(modifier = Modifier
                .padding(
                    horizontal = 16.dp
                )
                .size(32.dp)
                .background(
                    color = intToColor(previewColor), shape = CircleShape
                )
                .clickable(
                    enabled = isPremiumUnlocked && !disabled,
                ) {
                    isColorPickerVisible = true
                })
        }
    }
}

@Composable
fun intToColor(intValue: Int): Color {
    // Extract the alpha, red, green, and blue components from the intValue
    val alpha = (intValue shr 24) and 0xFF
    val red = (intValue shr 16) and 0xFF
    val green = (intValue shr 8) and 0xFF
    val blue = intValue and 0xFF

    // Create a Color object using the extracted components
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f, alpha = alpha / 255f)
}

@Composable
fun TweakSectionHeader(label: String) {
    val label = label
    Text(
        text = label,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
                start = 16.dp, end = 32.dp, top = 16.dp
            ),
        fontFamily = caviarDreamsFamily,
        fontWeight = FontWeight.Bold
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
    disabledIndexes: List<Int>? = null,
    deviceProtectedStorageContext: Context,
    imageResourceIds: List<Int>? = null,
    premiumIndexes: List<Int>? = null
) {
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

    Dialog(onDismissRequest = { onDismissRequest() }) {
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
                    style = MaterialTheme.typography.labelLarge,
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(24.dp),
                    fontFamily = caviarDreamsFamily
                )
                if (imageResourceIds != null) {

                    // Ensure selectedOption is within valid indices
                    val safeSelectedOption = selectedOption.coerceIn(0, imageResourceIds.size - 1)

                    Image(
                        painter = painterResource(id = imageResourceIds[safeSelectedOption]), // Replace with your image resource
                        contentDescription = "$key image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .height(100.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcIn)
                    )

                }
                LazyColumn(
                    modifier = Modifier.padding(
                            start = 24.dp
                        )
                ) {
                    items(options.size) { index ->
                        val option = options[index]
                        val isClickable = disabledIndexes?.contains(index) != true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .let {
                                    if (isClickable) it.clickable { onOptionSelected(index) } else it
                                }
                                .padding(
                                    top = 16.dp, bottom = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = index == selectedOption, onClick = {
                                    if (isClickable) {
                                        onOptionSelected(index)
                                    }
                                }, modifier = Modifier
                                    .size(20.dp)
                                    .padding(
                                        start = 8.dp, end = 16.dp
                                    ), colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(
                                        start = 16.dp, end = 24.dp
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isClickable) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontFamily = caviarDreamsFamily
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(
                                end = 8.dp, bottom = 8.dp
                            ),
                    ) {
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily
                        )
                    }

                    val premiumCheck = if (isPremium) {
                        true
                    } else if (premiumIndexes?.contains(selectedOption) == true) {
                        Toast
                            .makeText(
                                deviceProtectedStorageContext, deviceProtectedStorageContext.getString(R.string.requires_subscription), Toast.LENGTH_SHORT
                            )
                            .show()
                        false
                    }  else {
                        true
                    }

                    TextButton(
                        enabled = premiumCheck,
                        onClick = {
                            //For certain keys we need to offset the value saved to sharedprefs
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
                            sendBroadcast(deviceProtectedStorageContext, key, selectedOption)
                        },
                        modifier = Modifier.padding(
                                start = 8.dp, end = 24.dp, bottom = 8.dp
                            ),
                    ) {
                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily
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
fun SettingsSlider(
    context: Context,
    label: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    // Converts the raw value to a display string (default: one decimal place)
    displayValue: (Float) -> String = { v -> String.format(Locale.UK, "%.1f", v) },
    // Rounds the value when finished (default: one decimal place)
    roundingFn: (Float) -> Float = { v -> String.format(Locale.UK, "%.1f", v).toFloat() },
    modifier: Modifier = Modifier,
) {
    var localValue by remember { mutableFloatStateOf(value) }

    LaunchedEffect(value) {
        localValue = value
    }

    ElevatedCard(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize(),
        shape = RoundedCornerShape(10.dp), colors = CardDefaults.elevatedCardColors()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = 16.dp,
                top = 24.dp,
                end = 4.dp
            ),
            fontFamily = caviarDreamsFamily
        )
        Text(
            text = valueLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp
            ),
            fontFamily = caviarDreamsFamily
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Slider(
                value = localValue,
                onValueChange = { newValue -> localValue = newValue },
                onValueChangeFinished = {
                    val roundedValue = roundingFn(localValue)
                    onValueChangeFinished(roundedValue)
                    onValueChange(roundedValue)
                },
                valueRange = valueRange,
                modifier = Modifier.weight(1f)
                    .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            )
            Text(
                text = displayValue(localValue),
                style = MaterialTheme.typography.titleSmall,
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp)
            )
        }


    }
}

@Composable
fun TweakColorDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    key: String,
    defaultColor: Int,
    context: Context,
    sharedPreferences: SharedPreferences,
    label: String,
    resetColor: Int = android.graphics.Color.WHITE
) {
    val colorPickerController = rememberColorPickerController()
    var selectedColorInt by remember { mutableIntStateOf(defaultColor) }
    var hexCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(24.dp),
                        fontFamily = caviarDreamsFamily,
                        fontWeight = FontWeight.Bold
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
                    initialColor = intToColor(defaultColor)
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                        .height(20.dp),
                    controller = colorPickerController,
                    initialColor = intToColor(defaultColor),
                    wheelRadius = 6.dp,
                    wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp)
                        .height(20.dp),
                    controller = colorPickerController,
                    initialColor = intToColor(defaultColor),
                    wheelRadius = 6.dp,
                    wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = hexCode, onValueChange = { newHexCode ->

                            hexCode = newHexCode

                            if (isValidHexCode(newHexCode)) {
                                selectedColorInt = hexStringToColorInt(hexCode)
                                colorPickerController.selectByColor(
                                    Color(android.graphics.Color.parseColor("#$hexCode")), false
                                )
                            }
                        }, colors = TextFieldDefaults.colors(
                            focusedTextColor = intToColor(intValue = selectedColorInt),
                            unfocusedTextColor = intToColor(intValue = selectedColorInt),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ), singleLine = true, textStyle = TextStyle(
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f), // Take available horizontal space
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh,
                            contentDescription = "reset",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 24.dp, bottom = 8.dp, top = 4.dp)
                                .clickable {
                                    selectedColorInt = resetColor
                                    colorPickerController.selectByColor(
                                        Color(resetColor), false
                                    )
                                    sharedPreferences
                                        .edit()
                                        .putInt(key, selectedColorInt)
                                        .apply()
                                    sendBroadcast(context, key, selectedColorInt)
                                })
                    }
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(
                                end = 4.dp, bottom = 8.dp, top = 4.dp
                            ),
                    ) {
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(
                        onClick = {
                            sharedPreferences.edit().putInt(key, selectedColorInt).apply()
                            sendBroadcast(context, key, selectedColorInt)
                            onConfirmation()
                        },
                        modifier = Modifier.padding(
                                start = 4.dp, end = 16.dp, bottom = 8.dp, top = 4.dp
                            ),
                    ) {
                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
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
    disabledIndexes: List<Int>? = null,
    disabled: Boolean = false,
    deviceProtectedStorageContext: Context,
    imageResourceIds: List<Int>? = null,
    premiumIndexes: List<Int>? = null
) {
    // Create a state variable to track whether the dialog should be shown
    var isDialogVisible by remember { mutableStateOf(false) }

    // Conditionally render the dialog if isDialogVisible is true
    if (isDialogVisible) {
        // Call the TweakSelectionDialog Composable
        TweakSelectionDialog(onDismissRequest = { isDialogVisible = false },
            onConfirmation = { isDialogVisible = false },
            label = label,
            key = key,
            entries = entries,
            defaultIndex = defaultIndex,
            disabledIndexes = disabledIndexes,
            deviceProtectedStorageContext = deviceProtectedStorageContext,
            imageResourceIds = imageResourceIds,
            premiumIndexes = premiumIndexes
        )
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {

        Row(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp, bottom = 16.dp
            )
            .clickable(enabled = !disabled, onClick = {
                // Show the dialog when the row is clicked
                isDialogVisible = true
            }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.weight(1f), // Take available horizontal space
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                            start = 16.dp, top = 8.dp, end = 4.dp
                        ),
                    fontFamily = caviarDreamsFamily
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                                start = 16.dp, bottom = 8.dp, end = 16.dp
                            ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        }
    }
}
