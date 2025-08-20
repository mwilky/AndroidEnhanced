package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.core.content.edit
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastSender
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.dualToneQsPanel
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.qqsBrightnessSlider
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsBrightnessSliderPosition
import com.mwilky.androidenhanced.Utils.Companion.qsClickVibration
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerActiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerInactiveShape
import com.mwilky.androidenhanced.Utils.Companion.qsIconContainerUnavailableShape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.qsScrimAlpha
import com.mwilky.androidenhanced.Utils.Companion.qsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.qsStyle
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import com.mwilky.androidenhanced.dataclasses.Chip
import java.lang.String.format
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Quicksettings(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Quicksettings",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        QuicksettingsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun QuicksettingsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {

    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val resources = deviceProtectedStorageContext.resources

    val qsStyleValue =
        rememberIntPreference(sharedPreferences, qsStyle, 0)
    val qsStyleEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quicksettingsStyle)

    val globalIconColorValue =
        rememberIntPreference(
            sharedPreferences,
            customQsStatusbarGlobalIconColor,
            android.graphics.Color.WHITE
        )

    val useAccentColorValue =
        rememberBoolPreference(sharedPreferences, qsStatusbarIconAccentColor, false)

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        item(key = "general_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.general
                )
            )
        }
        item(key = dualToneQsPanel) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.qsPanelDualColorTitle
                ), stringResource(
                    R.string.qsPanelDualColorSummary
                ), dualToneQsPanel
            )
        }
        item(key = qsClickVibration) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.qsTileClickVibrationTitle
                ), stringResource(
                    R.string.qsTileClickVibrationSummary
                ), qsClickVibration
            )
        }
        item(key = hideQsFooterBuildNumber) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.hideQsFooterBuildNumberTitle
                ), stringResource(
                    R.string.hideQsFooterBuildNumberSummary
                ), hideQsFooterBuildNumber
            )
        }
        item(key = qsScrimAlpha) {
            var sliderValue by remember {
                mutableFloatStateOf(
                    sharedPreferences.getFloat(
                        qsScrimAlpha, 1.0f
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
                    sharedPreferences.edit { putFloat(qsScrimAlpha, roundedValue) }
                    // Update the local state to the new value
                    sliderValue = roundedValue
                    BroadcastSender.send(deviceProtectedStorageContext, qsScrimAlpha, sliderValue)
                },
                valueRange = 0.0f..1.0f,  // Adjust the range as needed
                displayValue = { v -> format(Locale.UK, "%.2f", v) },
                roundingFn = { v -> format(Locale.UK, "%.2f", v).toFloat() })
        }
        item(key = "expansion header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.expansion
                )
            )
        }
        val smartPulldownChips = listOf(
            Chip(resources.getString(R.string.never)),
            Chip(resources.getString(R.string.noImportantNotifications)),
            Chip(resources.getString(R.string.noNotifications)),
        )
        item(key = smartPulldown) {
            SingleSelectionChipsFlowRow(
                chips = smartPulldownChips,
                label = stringResource(R.string.smartPulldownTitle),
                description = stringResource(R.string.smartPulldownSummary),
                key = smartPulldown,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        val quickPulldownChips = listOf(
            Chip(resources.getString(R.string.never)),
            Chip(resources.getString(R.string.right)),
            Chip(resources.getString(R.string.left)),
            Chip(resources.getString(R.string.always)),
        )
        item(key = quickPulldown) {
            SingleSelectionChipsFlowRow(
                chips = quickPulldownChips,
                label = stringResource(R.string.quickPulldownTitle),
                description = stringResource(R.string.quickPulldownSummary),
                key = quickPulldown,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "slider header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.brightnessSlider
                )
            )
        }
        item(key = qqsBrightnessSlider) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.qqsBrightnessSliderTitle
                ), stringResource(
                    R.string.qqsBrightnessSliderSummary
                ), qqsBrightnessSlider
            )
        }
        val brightnessSliderPositionChips = listOf(
            Chip(resources.getString(R.string.above)),
            Chip(resources.getString(R.string.below)),
            Chip(resources.getString(R.string.hidden)),
        )
        item(key = qsBrightnessSliderPosition) {
            SingleSelectionChipsFlowRow(
                chips = brightnessSliderPositionChips,
                label = stringResource(R.string.qsBrightnessSliderPositionTitle),
                description = "",
                key = qsBrightnessSliderPosition,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "tile layout header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.tileLayout
                )
            )
        }
        item(key = qsStyle) {
            val qsStyleImageResourceIds: List<Int> = listOf(
                if (!isSystemInDarkTheme()) R.drawable.qs_style_0_light else R.drawable.qs_style_0_dark,
                if (!isSystemInDarkTheme()) R.drawable.qs_style_1_light else R.drawable.qs_style_1_dark,
                if (!isSystemInDarkTheme()) R.drawable.qs_style_2_light else R.drawable.qs_style_2_dark,
            )

            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsStyleTitle
                ),
                description = qsStyleEntries[qsStyleValue.value],
                key = qsStyle,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.quicksettingsStyle
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext,
                imageResourceIds = qsStyleImageResourceIds,
                premiumIndexes = listOf(1, 2)

            )
        }
        val tileShapeChips = listOf(
            Chip(resources.getString(R.string.circle)),
            Chip(resources.getString(R.string.roundedSquare)),
            Chip(resources.getString(R.string.square)),
        )
        item(key = qsIconContainerActiveShape) {
            SingleSelectionChipsFlowRow(
                chips = tileShapeChips,
                label = stringResource(if (qsStyleValue.value == 1) R.string.qsIconContainerActiveShapeTitle else R.string.qsIconContainerShapeDisabledTitle),
                description = "",
                key = qsIconContainerActiveShape,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = qsIconContainerInactiveShape) {
            SingleSelectionChipsFlowRow(
                chips = tileShapeChips,
                label = stringResource(if (qsStyleValue.value == 1) R.string.qsIconContainerInactiveShapeTitle else R.string.qsIconContainerShapeDisabledTitle),
                description = "",
                key = qsIconContainerInactiveShape,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = qsIconContainerUnavailableShape) {
            SingleSelectionChipsFlowRow(
                chips = tileShapeChips,
                label = stringResource(if (qsStyleValue.value == 1) R.string.qsIconContainerUnavailableShapeTitle else R.string.qsIconContainerShapeDisabledTitle),
                description = "",
                key = qsIconContainerUnavailableShape,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "row header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.rowCount
                )
            )
        }
        val qqsRowsChips = listOf(
            Chip(resources.getString(R.string.one)),
            Chip(resources.getString(R.string.two)),
        )
        item(key = qqsRows) {
            SingleSelectionChipsFlowRow(
                chips = qqsRowsChips,
                label = stringResource(R.string.qqsRowsTitle),
                description = "",
                key = qqsRows,
                defaultIndex = 1,
                context = deviceProtectedStorageContext
            )
        }
        val qsRowsChips = listOf(
            Chip(resources.getString(R.string.two)),
            Chip(resources.getString(R.string.three)),
            Chip(resources.getString(R.string.four)),
            Chip(resources.getString(R.string.five)),
            Chip(resources.getString(R.string.six)),
        )
        item(key = qsRows) {
            SingleSelectionChipsFlowRow(
                chips = qsRowsChips,
                label = stringResource(R.string.qsRowsTitle),
                description = "",
                key = qsRows,
                defaultIndex = 1,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "column header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.columnCount
                )
            )
        }
        val qqsColumnsChips = listOf(
            Chip(resources.getString(R.string.two)),
            Chip(resources.getString(R.string.three)),
            Chip(resources.getString(R.string.four)),
            Chip(resources.getString(R.string.five)),
            Chip(resources.getString(R.string.six)),
        )
        item(key = qqsColumns) {
            SingleSelectionChipsFlowRow(
                chips = qqsColumnsChips,
                label = stringResource(R.string.qqsColumnsTitle),
                description = "",
                key = qqsColumns,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        val qsColumnsChips = listOf(
            Chip(resources.getString(R.string.two)),
            Chip(resources.getString(R.string.three)),
            Chip(resources.getString(R.string.four)),
            Chip(resources.getString(R.string.five)),
            Chip(resources.getString(R.string.six)),
        )
        item(key = qsColumns) {
            SingleSelectionChipsFlowRow(
                chips = qsColumnsChips,
                label = stringResource(R.string.qsColumnsTitle),
                description = "",
                key = qsColumns,
                defaultIndex = 0,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "landscape column header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.columnCountLandscape
                )
            )
        }
        val qqsColumnsLandscapeChips = listOf(
            Chip(resources.getString(R.string.two)),
            Chip(resources.getString(R.string.three)),
            Chip(resources.getString(R.string.four)),
            Chip(resources.getString(R.string.five)),
            Chip(resources.getString(R.string.six)),
        )
        item(key = qqsColumnsLandscape) {
            SingleSelectionChipsFlowRow(
                chips = qqsColumnsLandscapeChips,
                label = stringResource(R.string.qqsColumnsLandscapeTitle),
                description = "",
                key = qqsColumnsLandscape,
                defaultIndex = 2,
                context = deviceProtectedStorageContext
            )
        }
        val qsColumnsLandscapeChips = listOf(
            Chip(resources.getString(R.string.two)),
            Chip(resources.getString(R.string.three)),
            Chip(resources.getString(R.string.four)),
            Chip(resources.getString(R.string.five)),
            Chip(resources.getString(R.string.six)),
        )
        item(key = qsColumnsLandscape) {
            SingleSelectionChipsFlowRow(
                chips = qsColumnsLandscapeChips,
                label = stringResource(R.string.qsColumnsLandscapeTitle),
                description = "",
                key = qsColumnsLandscape,
                defaultIndex = 2,
                context = deviceProtectedStorageContext
            )
        }
        item(key = "icon color header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.iconColors
                )
            )
        }
        item(key = qsStatusbarIconAccentColor) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.statusbarIconUseAccentColorTitle
                ), stringResource(
                    id = R.string.qsStatusbarIconUseAccentColorSummary
                ), qsStatusbarIconAccentColor
            )
        }
        item(key = customQsStatusbarGlobalIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customStatusbarGlobalIconColorTitle
                ),
                customQsStatusbarGlobalIconColor,
                globalIconColorValue.value,
                sharedPreferences,
                useAccentColorValue.value,
                stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else R.string.customQsStatusbarGlobalIconColorSummary
                ),
            )
        }
        item(key = "individual icon colors") {
            TweakRow(
                label = stringResource(id = R.string.individualQuicksettingsStatusbarIconColors),
                description = stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else if (globalIconColorValue.value == -1) R.string.customStatusbarIndividualIconColorSummary
                    else R.string.customStatusbarIndividualIconColorSummaryDisabled
                ),
                navController = navController,
                disabled = useAccentColorValue.value || globalIconColorValue.value != -1
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}