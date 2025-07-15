package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS

import com.mwilky.androidenhanced.Utils.Companion.customStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarNotificationIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarWifiIconColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusbarIcons(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = stringResource(id = R.string.individualStatusbarIconColors),
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        StatusbarIconsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun StatusbarIconsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val airplaneColor =
        rememberIntPreference(sharedPreferences, customStatusbarAirplaneIconColor, Color.WHITE)
    val batteryColor =
        rememberIntPreference(sharedPreferences, customStatusbarBatteryIconColor, Color.WHITE)
    val batteryPercentColor =
        rememberIntPreference(sharedPreferences, customStatusbarBatteryPercentColor, Color.WHITE)
    val bluetoothColor =
        rememberIntPreference(sharedPreferences, customStatusbarBluetoothIconColor, Color.WHITE)
    val clockColor =
        rememberIntPreference(sharedPreferences, customStatusbarClockColor, Color.WHITE)
    val dndColor =
        rememberIntPreference(sharedPreferences, customStatusbarDndIconColor, Color.WHITE)
    val hotspotColor =
        rememberIntPreference(sharedPreferences, customStatusbarHotspotIconColor, Color.WHITE)
    val mobileColor =
        rememberIntPreference(sharedPreferences, customStatusbarMobileIconColor, Color.WHITE)
    val notificationsColor =
        rememberIntPreference(sharedPreferences, customStatusbarNotificationIconColor, Color.WHITE)
    val wifiColor =
        rememberIntPreference(sharedPreferences, customStatusbarWifiIconColor, Color.WHITE)
    val otherColor =
        rememberIntPreference(sharedPreferences, customStatusbarOtherIconColor, Color.WHITE)

    //TODO: add alarm and sound


    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarAirplaneIconColorTitle
                ),
                customStatusbarAirplaneIconColor,
                airplaneColor.value,
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
                batteryColor.value,
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
                batteryPercentColor.value,
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
                bluetoothColor.value,
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
                clockColor.value,
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
                dndColor.value,
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
                hotspotColor.value,
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
                mobileColor.value,
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
                notificationsColor.value,
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
                wifiColor.value,
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
                otherColor.value,
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
}