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
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarWifiIconColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockscreenIcons(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = stringResource(id = R.string.individualLockscreenStatusbarIconColors),
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        LockscreenIconsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun LockscreenIconsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val airplaneColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarAirplaneIconColor, Color.WHITE)
    val batteryColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarBatteryIconColor, Color.WHITE)
    val batteryPercentColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarBatteryPercentColor, Color.WHITE)
    val bluetoothColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarBluetoothIconColor, Color.WHITE)
    val carrierColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarCarrierColor, Color.WHITE)
    val dndColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarDndIconColor, Color.WHITE)
    val hotspotColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarHotspotIconColor, Color.WHITE)
    val mobileColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarMobileIconColor, Color.WHITE)
    val wifiColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarWifiIconColor, Color.WHITE)
    val otherColor =
        rememberIntPreference(sharedPreferences, customLsStatusbarOtherIconColor, Color.WHITE)

    //TODO: add alarm and sound


    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = customLsStatusbarAirplaneIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarAirplaneIconColorTitle
                ),
                customLsStatusbarAirplaneIconColor,
                airplaneColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarBatteryIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBatteryIconColorTitle
                ),
                customLsStatusbarBatteryIconColor,
                batteryColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarBatteryPercentColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBatteryPercentColorTitle
                ),
                customLsStatusbarBatteryPercentColor,
                batteryPercentColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarBluetoothIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBluetoothIconColorTitle
                ),
                customLsStatusbarBluetoothIconColor,
                bluetoothColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarCarrierColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarCarrierColorTitle
                ),
                customLsStatusbarCarrierColor,
                carrierColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarDndIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarDndIconColorTitle
                ),
                customLsStatusbarDndIconColor,
                dndColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarHotspotIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarHotspotIconColorTitle
                ),
                customLsStatusbarHotspotIconColor,
                hotspotColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarMobileIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarMobileIconColorTitle
                ),
                customLsStatusbarMobileIconColor,
                mobileColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarWifiIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarWifiIconColorTitle
                ),
                customLsStatusbarWifiIconColor,
                wifiColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customLsStatusbarOtherIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarOtherIconColorTitle
                ),
                customLsStatusbarOtherIconColor,
                otherColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}