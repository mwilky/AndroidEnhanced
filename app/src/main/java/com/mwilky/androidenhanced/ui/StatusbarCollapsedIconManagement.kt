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

import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedAlarmIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedCallStrengthIcon
import com.mwilky.androidenhanced.Utils.Companion.hideCollapsedVolumeIcon
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconDarkColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusbarCollapsedIconManagement(
    navController: NavController,
    deviceProtectedStorageContext: Context
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = stringResource(id = R.string.hideCollapsedStatusbarIcons),
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        StatusbarCollapsedIconManagementScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun StatusbarCollapsedIconManagementScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = hideCollapsedAlarmIcon) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.alarmTitle
                ), "", hideCollapsedAlarmIcon, true
            )
        }
        item(key = hideCollapsedVolumeIcon) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.volumeTitle
                ), "", hideCollapsedVolumeIcon, true
            )
        }
        item(key = hideCollapsedCallStrengthIcon) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.callStrengthTitle
                ), "", hideCollapsedCallStrengthIcon, true
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}