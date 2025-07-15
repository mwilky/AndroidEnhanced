package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarAirplaneIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBatteryPercentColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarBluetoothIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarCarrierColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarClockColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDateColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarDndIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarHotspotIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarMobileIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarOtherIconColor
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarWifiIconColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuicksettingsIcons(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = stringResource(id = R.string.individualQuicksettingsStatusbarIconColors),
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        QuicksettingsIconsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun QuicksettingsIconsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val airplaneColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarAirplaneIconColor,
            android.graphics.Color.WHITE
        )
    val batteryColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarBatteryIconColor,
            android.graphics.Color.WHITE
        )
    val batteryPercentColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarBatteryPercentColor,
            android.graphics.Color.WHITE
        )
    val bluetoothColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarBluetoothIconColor,
            android.graphics.Color.WHITE
        )
    val carrierColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarCarrierColor,
            android.graphics.Color.WHITE
        )
    val clockColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarClockColor,
            android.graphics.Color.WHITE
        )
    val dateColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarDateColor,
            android.graphics.Color.WHITE
        )
    val dndColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarDndIconColor,
            android.graphics.Color.WHITE
        )
    val hotspotColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarHotspotIconColor,
            android.graphics.Color.WHITE
        )
    val mobileColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarMobileIconColor,
            android.graphics.Color.WHITE
        )
    val wifiColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarWifiIconColor,
            android.graphics.Color.WHITE
        )
    val otherColor =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarOtherIconColor,
            android.graphics.Color.WHITE
        )

    //TODO: add alarm and sound


    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = customQsStatusbarAirplaneIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarAirplaneIconColorTitle
                ),
                customQsStatusbarAirplaneIconColor,
                airplaneColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarBatteryIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBatteryIconColorTitle
                ),
                customQsStatusbarBatteryIconColor,
                batteryColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarBatteryPercentColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBatteryPercentColorTitle
                ),
                customQsStatusbarBatteryPercentColor,
                batteryPercentColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarBluetoothIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarBluetoothIconColorTitle
                ),
                customQsStatusbarBluetoothIconColor,
                bluetoothColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarCarrierColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarCarrierColorTitle
                ),
                customQsStatusbarCarrierColor,
                carrierColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarClockColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarClockColorTitle
                ),
                customQsStatusbarClockColor,
                clockColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarDateColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarDateColorTitle
                ),
                customQsStatusbarDateColor,
                dateColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarDndIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarDndIconColorTitle
                ),
                customQsStatusbarDndIconColor,
                dndColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarHotspotIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarHotspotIconColorTitle
                ),
                customQsStatusbarHotspotIconColor,
                hotspotColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarMobileIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarMobileIconColorTitle
                ),
                customQsStatusbarMobileIconColor,
                mobileColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarWifiIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarWifiIconColorTitle
                ),
                customQsStatusbarWifiIconColor,
                wifiColor.value,
                sharedPreferences,
                premiumFeature = true
            )
        }
        item(key = customQsStatusbarOtherIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarOtherIconColorTitle
                ),
                customQsStatusbarOtherIconColor,
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