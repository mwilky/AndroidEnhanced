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
import com.mwilky.androidenhanced.Utils.Companion.customStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleep
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockSeconds
import com.mwilky.androidenhanced.Utils.Companion.statusbarBrightnessControl
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.statusbarIconDarkColor
import com.mwilky.androidenhanced.Utils.Companion.useDualStatusbarColors

class Statusbar {}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statusbar(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Statusbar",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        StatusbarScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun StatusbarScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {

    val sharedPrefs = deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
    val clockPositionValues = rememberIntPreference(sharedPrefs, statusBarClockPosition, 0)
    val clockPositionEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.statusbar_clock_position_entries)

    val globalColor =
        rememberIntPreference(
            sharedPrefs,
            customStatusbarGlobalIconColor,
            android.graphics.Color.WHITE
        )
    val darkColor =
        rememberIntPreference(sharedPrefs, statusbarIconDarkColor, -1728053248)
    val useAccentColorValue =
        rememberBoolPreference(sharedPrefs, statusbarIconAccentColor, false)
    val useDualColorValue =
        rememberBoolPreference(sharedPrefs, useDualStatusbarColors, true)

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {

        item(key = "gestures_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.gestures
                )
            )
        }

        item(key = doubleTapToSleep) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.doubleTapToSleepTitle
                ), stringResource(
                    id = R.string.doubleTapToSleepSummary
                ), doubleTapToSleep
            )
        }

        item(key = statusbarBrightnessControl) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.statusbarBrightnessControlTitle
                ), stringResource(
                    id = R.string.statusbarBrightnessControlSummary
                ), statusbarBrightnessControl
            )
        }

        item(key = "clock_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.clock
                )
            )
        }
//        item {
//            SingleSelectConnectedButtonGroup(
//                entries = clockPositionEntries,
//                defaultIndex = 0,
//                key = statusBarClockPosition,
//                deviceProtectedStorageContext = deviceProtectedStorageContext,
//                onSelectionChanged = { index ->
//                    // Handle selection change
//                }
//            )
//        }

        item(key = statusBarClockPosition) {
            TweakSelectionRow(
                label = stringResource(R.string.statusbarClockPositionTitle),
                description = clockPositionEntries[clockPositionValues.value],
                key = statusBarClockPosition,
                entries = clockPositionEntries,
                defaultIndex = 0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = statusBarClockSeconds) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.statusbarClockSecondsTitle
                ), stringResource(
                    id = R.string.statusbarClockSecondsSummary
                ), statusBarClockSeconds
            )
        }
        item(key = "icon colors header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.iconColors
                )
            )
        }
        item(key = statusbarIconAccentColor) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.statusbarIconUseAccentColorTitle
                ), stringResource(
                    id = R.string.statusbarIconUseAccentColorSummary
                ), statusbarIconAccentColor
            )
        }
        item(key = customStatusbarGlobalIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarGlobalIconColorTitle
                ),
                customStatusbarGlobalIconColor,
                globalColor.value,
                sharedPrefs,
                disabled = useAccentColorValue.value,
                description = stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else R.string.customStatusbarGlobalIconColorSummary
                )
            )
        }
        item(key = "individual icon colors") {
            TweakRow(
                label = stringResource(id = R.string.individualStatusbarIconColors),
                description = stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else if (globalColor.value == -1) R.string.customStatusbarIndividualIconColorSummary
                    else R.string.customStatusbarIndividualIconColorSummaryDisabled
                ),
                navController = navController,
                disabled = useAccentColorValue.value || globalColor.value != -1
            )
        }
        item(key = useDualStatusbarColors) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.useDualStatusbarColorsTitle
                ), stringResource(
                    id = R.string.useDualStatusbarColorsSummary
                ), useDualStatusbarColors, defaultValue = true, premiumFeature = true
            )
        }
        item(key = statusbarIconDarkColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarDarkIconColorTitle
                ),
                statusbarIconDarkColor,
                darkColor.value,
                sharedPrefs,
                !useDualColorValue.value,
                stringResource(
                    if (useDualColorValue.value) R.string.customStatusbarDarkIconColorSummary
                    else R.string.customStatusbarDarkIconColorSummaryDisabled
                ),
                -1728053248
            )
        }
        item(key = "icon management header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.iconManagement
                )
            )
        }
        item(key = "hide icons") {
            TweakRow(
                label = stringResource(id = R.string.hideStatusbarIcons),
                description = stringResource(
                    id = R.string.allStatusbarIcons
                ),
                navController = navController,
                false
            )
        }
        item(key = "hide collapsed icons") {
            TweakRow(
                label = stringResource(id = R.string.hideCollapsedStatusbarIcons),
                description = stringResource(
                    id = R.string.collapsedStatusbarIcons
                ),
                navController = navController,
                false
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }

    }
}