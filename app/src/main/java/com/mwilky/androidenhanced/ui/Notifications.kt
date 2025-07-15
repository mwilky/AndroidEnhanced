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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastSender
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.autoExpandFirstNotif
import com.mwilky.androidenhanced.Utils.Companion.expandAllNotifications
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.notifScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.notifSectionHeaders
import java.util.Locale
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Notifications",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        NotificationsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun NotificationsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {

    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = "expansion_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.expansion
                )
            )
        }
        item(key = autoExpandFirstNotif) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.autoExpandedFirstTitle
                ), stringResource(
                    R.string.autoExpandedFirstSummary
                ), autoExpandFirstNotif, true
            )
        }
        item(key = expandAllNotifications) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.expandedNotificationsTitle
                ), stringResource(
                    R.string.expandedNotificationsSummary
                ), expandAllNotifications
            )
        }
        item(key = "general_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.general
                )
            )
        }
        item(key = notifSectionHeaders) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.notificationSectionHeadersTitle
                ), stringResource(
                    R.string.notificationSectionHeadersSummary
                ), notifSectionHeaders, true
            )
        }
        item(key = notifScrimAlpha) {
            var sliderValue by remember {
                mutableFloatStateOf(
                    sharedPreferences.getFloat(
                        notifScrimAlpha, 1.0f
                    )
                )
            }
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
                    sharedPreferences.edit { putFloat(notifScrimAlpha, roundedValue) }
                    // Update the local state to the new value
                    sliderValue = roundedValue
                    BroadcastSender.send(
                        deviceProtectedStorageContext, notifScrimAlpha, sliderValue
                    )
                },
                valueRange = 0.0f..1.0f,  // Adjust the range as needed
                displayValue = { v -> String.format(Locale.UK, "%.2f", v) },
                roundingFn = { v -> String.format(Locale.UK, "%.2f", v).toFloat() })
        }
        item(key = muteScreenOnNotifications) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.muteScreenOnNotificationsTitle
                ), stringResource(
                    R.string.muteScreenOnNotificationsSummary
                ), muteScreenOnNotifications
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}