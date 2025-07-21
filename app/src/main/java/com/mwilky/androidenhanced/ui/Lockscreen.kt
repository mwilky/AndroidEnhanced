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
import com.mwilky.androidenhanced.Utils.Companion.customLsStatusbarGlobalIconColor
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableQsLockscreen
import com.mwilky.androidenhanced.Utils.Companion.hideLockscreenStatusBar
import com.mwilky.androidenhanced.Utils.Companion.lsStatusbarIconAccentColor
import com.mwilky.androidenhanced.Utils.Companion.scrambleKeypad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lockscreen(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Lockscreen",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        LockscreenScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun LockscreenScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
    val sharedPrefs = deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

    val globalColor =
        rememberIntPreference(
            sharedPrefs,
            customLsStatusbarGlobalIconColor,
            android.graphics.Color.WHITE
        )
    val useAccentColorValue =
        rememberBoolPreference(sharedPrefs, lsStatusbarIconAccentColor, false)

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
        item(key = hideLockscreenStatusBar) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.hideLockscreenStatusbarTitle
                ), stringResource(
                    R.string.hideLockscreenStatusbarSummary
                ), hideLockscreenStatusBar
            )
        }
        item(key = scrambleKeypad) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.randomKeypadTitle
                ), stringResource(
                    R.string.randomKeypadSummary
                ), scrambleKeypad
            )
        }
        item(key = disableLockscreenPowerMenu) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.disableLockscreenPowerMenuTitle
                ), stringResource(
                    R.string.disableLockscreenPowerMenuSummary
                ), disableLockscreenPowerMenu
            )
        }
        item(key = disableQsLockscreen) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.disableQsLockscreenTitle
                ), stringResource(
                    R.string.disableQsLockscreenSummary
                ), disableQsLockscreen
            )
        }
        item(key = "colors header") {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.iconColors
                )
            )
        }
        item(key = lsStatusbarIconAccentColor) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    id = R.string.statusbarIconUseAccentColorTitle
                ), stringResource(
                    id = R.string.lsStatusbarIconUseAccentColorSummary
                ), lsStatusbarIconAccentColor
            )
        }
        item(key = customLsStatusbarGlobalIconColor) {
            TweakColor(
                deviceProtectedStorageContext,
                stringResource(
                    id = R.string.customLsStatusbarGlobalIconColorSummary
                ),
                customLsStatusbarGlobalIconColor,
                globalColor.value,
                sharedPrefs,
                useAccentColorValue.value,
                description = stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else R.string.customLsStatusbarGlobalIconColorSummary
                ),
                premiumFeature = true
            )
        }
        item(key = "individual icon colors") {
            TweakRow(
                label = stringResource(id = R.string.individualLockscreenStatusbarIconColors),
                description = stringResource(
                    id = if (useAccentColorValue.value) R.string.customStatusbarColorsSummaryDisabled
                    else if (globalColor.value == -1) R.string.customStatusbarIndividualIconColorSummary
                    else R.string.customStatusbarIndividualIconColorSummaryDisabled
                ),
                navController = navController,
                disabled = useAccentColorValue.value || globalColor.value != -1
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}