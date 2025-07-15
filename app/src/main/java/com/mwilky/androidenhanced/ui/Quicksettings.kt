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
import androidx.navigation.NavController
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.hideQsFooterBuildNumber
import com.mwilky.androidenhanced.Utils.Companion.qsClickVibration

import java.util.Locale
import androidx.core.content.edit
import com.mwilky.androidenhanced.BroadcastSender
import com.mwilky.androidenhanced.Utils.Companion.customQsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.dualToneQsPanel
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
import com.mwilky.androidenhanced.Utils.Companion.quickPulldown
import com.mwilky.androidenhanced.Utils.Companion.smartPulldown
import java.lang.String.format

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

    val smartPullDown =
        rememberIntPreference(sharedPreferences, smartPulldown, 0)
    val smartPulldownEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.smart_pulldown_entries)
    val quickPullDown =
        rememberIntPreference(sharedPreferences, quickPulldown, 0)
    val quickPulldownEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quick_pulldown_entries)
    val qsBrightnessSlider =
        rememberIntPreference(sharedPreferences, qsBrightnessSliderPosition, 0)
    val qsBrightnessSliderPositionEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quicksettingsBrightnessSliderPosition)
    val qqsRowsValue =
        rememberIntPreference(sharedPreferences, qqsRows, 2)
    val qqsRowsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qqs_rows_entries)
    val qqsColumnsValue =
        rememberIntPreference(sharedPreferences, qqsColumns, 2)
    val qqsColumnsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns)
    val qqsColumnsLandscapeValue =
        rememberIntPreference(sharedPreferences, qqsColumnsLandscape, 4)
    val qsColumnsValue =
        rememberIntPreference(sharedPreferences, qsColumns, 2)
    val qsColumnsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns)
    val qsColumnsLandscapeValue =
        rememberIntPreference(sharedPreferences, qsColumnsLandscape, 4)
    val qsRowsValue =
        rememberIntPreference(sharedPreferences, qsRows, 4)
    val qsRowsEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qs_rows_entries)
    val qsStyleValue =
        rememberIntPreference(sharedPreferences, qsStyle, 0)
    val qsStyleEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.quicksettingsStyle)
    val qsActiveShapeValue =
        rememberIntPreference(sharedPreferences, qsIconContainerActiveShape, 0)
    val qsActiveShapeEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qsIconContainerShape)
    val qsInactiveShapeValue =
        rememberIntPreference(sharedPreferences, qsIconContainerInactiveShape, 0)
    val qsInactiveShapeEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qsIconContainerShape)
    val qsUnavailableShapeValue =
        rememberIntPreference(sharedPreferences, qsIconContainerUnavailableShape, 0)
    val qsUnavailableShapeEntries =
        deviceProtectedStorageContext.resources.getStringArray(R.array.qsIconContainerShape)

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
        item(key = smartPulldown) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.smartPulldownTitle
                ),
                description = smartPulldownEntries[smartPullDown.value],
                key = smartPulldown,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.smart_pulldown_entries
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = quickPulldown) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.quickPulldownTitle
                ),
                description = quickPulldownEntries[quickPullDown.value],
                key = quickPulldown,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.quick_pulldown_entries
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
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
        item(key = qsBrightnessSliderPosition) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsBrightnessSliderPositionTitle
                ),
                description = qsBrightnessSliderPositionEntries[qsBrightnessSlider.value],
                key = qsBrightnessSliderPosition,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.quicksettingsBrightnessSliderPosition
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
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
        item(key = qsIconContainerActiveShape) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsIconContainerActiveShapeTitle
                ),
                description = if (qsStyleValue.value == 1) qsActiveShapeEntries[qsActiveShapeValue.value]
                else stringResource(
                    id = R.string.qsIconContainerShapeDisabledTitle
                ),
                key = qsIconContainerActiveShape,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qsIconContainerShape
                ),
                0,
                disabled = qsStyleValue.value != 1,
                deviceProtectedStorageContext = deviceProtectedStorageContext,
            )
        }
        item(key = qsIconContainerInactiveShape) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsIconContainerInactiveShapeTitle
                ),
                description = if (qsStyleValue.value == 1) qsInactiveShapeEntries[qsInactiveShapeValue.value]
                else stringResource(
                    id = R.string.qsIconContainerShapeDisabledTitle
                ),
                key = qsIconContainerInactiveShape,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qsIconContainerShape
                ),
                0,
                disabled = qsStyleValue.value != 1,
                deviceProtectedStorageContext = deviceProtectedStorageContext,
            )
        }
        item(key = qsIconContainerUnavailableShape) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsIconContainerUnavailableShapeTitle
                ),
                description = if (qsStyleValue.value == 1) qsUnavailableShapeEntries[qsUnavailableShapeValue.value]
                else stringResource(
                    id = R.string.qsIconContainerShapeDisabledTitle
                ),
                key = qsIconContainerUnavailableShape,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qsIconContainerShape
                ),
                0,
                disabled = qsStyleValue.value != 1,
                deviceProtectedStorageContext = deviceProtectedStorageContext,
            )
        }
        item(key = "row header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.rowCount
                )
            )
        }
        item(key = qqsRows) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qqsRowsTitle
                ),
                description = qqsRowsEntries[qqsRowsValue.value - 1],
                key = qqsRows,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qqs_rows_entries
                ),
                1,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = qsRows) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsRowsTitle
                ),
                description = qsRowsEntries[qsRowsValue.value - 2],
                key = qsRows,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qs_rows_entries
                ),
                2,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = "column header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.columnCount
                )
            )
        }
        item(key = qqsColumns) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qqsColumnsTitle
                ),
                description = qqsColumnsEntries[qqsColumnsValue.value - 2],
                key = qqsColumns,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qs_columns
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = qsColumns) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsColumnsTitle
                ),
                description = qsColumnsEntries[qsColumnsValue.value - 2],
                key = qsColumns,
                entries = deviceProtectedStorageContext.resources.getStringArray(
                    R.array.qs_columns
                ),
                0,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = "landscape column header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.columnCountLandscape
                )
            )
        }
        item(key = qqsColumnsLandscape) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qqsColumnsLandscapeTitle
                ),
                description = qsColumnsEntries[qqsColumnsLandscapeValue.value - 2],
                key = qqsColumnsLandscape,
                entries = deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns),
                2,
                deviceProtectedStorageContext = deviceProtectedStorageContext
            )
        }
        item(key = qsColumnsLandscape) {
            TweakSelectionRow(
                label = stringResource(
                    id = R.string.qsColumnsLandscapeTitle
                ),
                description = qsColumnsEntries[qsColumnsLandscapeValue.value - 2],
                key = qsColumnsLandscape,
                entries = deviceProtectedStorageContext.resources.getStringArray(R.array.qs_columns),
                2,
                deviceProtectedStorageContext = deviceProtectedStorageContext
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