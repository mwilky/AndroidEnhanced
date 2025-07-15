package com.mwilky.androidenhanced.ui

import android.content.Context
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
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon

import com.mwilky.androidenhanced.Utils.Companion.iconBlacklist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusbarIconManagement(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = stringResource(id = R.string.hideStatusbarIcons),
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        StatusbarIconManagementScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun StatusbarIconManagementScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = "airplane") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.airplaneTitle
                ), "", iconBlacklist, "airplane"
            )
        }
        item(key = "alarm") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.alarmTitle
                ), "", iconBlacklist, "alarm_clock"
            )
        }
        item(key = "battery") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.batteryIconTitle
                ), "", iconBlacklist, "battery"
            )
        }
        item(key = "bluetooth") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.bluetoothTitle
                ), "", iconBlacklist, "bluetooth"
            )
        }
        item(key = "cast") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.castTitle
                ), "", iconBlacklist, "cast"
            )
        }
        item(key = "dnd") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.zenTitle
                ), "", iconBlacklist, "zen"
            )
        }
        item(key = "hotspot") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.hotspotTitle
                ), "", iconBlacklist, "hotspot"
            )
        }
        item(key = "location") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.locationTitle
                ), "", iconBlacklist, "location"
            )
        }
        item(key = "mobile") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.mobileTitle
                ), "", iconBlacklist, "mobile"
            )
        }
        item(key = "screen record") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.screenRecordTitle
                ), "", iconBlacklist, "screen_record"
            )
        }
        item(key = "speaker") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.speakerTitle
                ), "", iconBlacklist, "speakerphone"
            )
        }
        item(key = "volume") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.volumeTitle
                ), "", iconBlacklist, "volume"
            )
        }
        item(key = "vpn") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.vpnTitle
                ), "", iconBlacklist, "vpn"
            )
        }
        item(key = "wifi") {
            TweakIconSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.wifiTitle
                ), "", iconBlacklist, "wifi"
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}