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
import com.mwilky.androidenhanced.Utils.Companion.disableCameraScreenOff
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnApplication
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnBiometric
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnCameraLaunch
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnGesture
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnLift
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnOther
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPlugIn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPowerButton
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnTap
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.dataclasses.Chip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Buttons(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Buttons",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        ButtonsScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun ButtonsScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val torchAutoOff =
        rememberBoolPreference(sharedPreferences, torchAutoOffScreenOn, false)

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.calculateTopPadding())
    ) {

        item(key = "launcher_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.launcher
                )
            )
        }
        item(key = doubleTapToSleepLauncher) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.doubleTapToSleepTitle
                ), stringResource(
                    id = R.string.doubleTapToSleepLauncherSummary
                ), doubleTapToSleepLauncher
            )
        }
        item(key = "power_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.powerButton
                )
            )
        }
        item(key = torchPowerScreenOff) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.longPressPowerTorchScreenOffTitle
                ), stringResource(
                    R.string.longPressPowerTorchScreenOffSummary
                ), torchPowerScreenOff
            )
        }
        item(key = torchAutoOffScreenOn) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.torchAutoOffScreenOnTitle
                ), stringResource(
                    R.string.torchAutoOffScreenOnSummary
                ), torchAutoOffScreenOn,
                extraContent = {
                    val sampleChips = listOf(
                        Chip("Lift", torchAutoOffScreenOnLift),
                        Chip("Biometric", torchAutoOffScreenOnBiometric),
                        Chip("Plugged in", torchAutoOffScreenOnPlugIn),
                        Chip("Power button", torchAutoOffScreenOnPowerButton),
                        Chip("Application", torchAutoOffScreenOnApplication),
                        Chip("Tap", torchAutoOffScreenOnTap),
                        Chip("Camera launch", torchAutoOffScreenOnCameraLaunch),
                        Chip("Gesture", torchAutoOffScreenOnGesture),
                        Chip("Other", torchAutoOffScreenOnOther),
                    )
                    ChipsFlowRow(
                        chips = sampleChips,
                        description = stringResource(R.string.torchAutoOffScreenOnReasonsSummary),
                        modifier = Modifier,
                        deviceProtectedStorageContext
                    )
                }
            )
        }
        item(key = disableCameraScreenOff) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.disableDoublePressCameraScreenOffTitle
                ), stringResource(
                    R.string.disableDoublePressCameraScreenOffSummary
                ), disableCameraScreenOff
            )
        }
        item(key = "volume_header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.volumeButton
                )
            )
        }
        item(key = volKeyMediaControl) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.volKeyMediaControlTitle
                ), stringResource(
                    R.string.volKeyMediaControlSummary
                ), volKeyMediaControl
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}