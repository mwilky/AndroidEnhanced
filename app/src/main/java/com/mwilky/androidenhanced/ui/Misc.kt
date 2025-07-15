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
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Misc(navController: NavController, deviceProtectedStorageContext: Context) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = "Miscellaneous",
            showBackIcon = true,
            scrollBehavior = scrollBehavior
        )
    }, content = {
        MiscScrollableContent(
            topPadding = it, navController = navController, deviceProtectedStorageContext
        )
    })
}

@Composable
fun MiscScrollableContent(
    topPadding: PaddingValues, navController: NavController, deviceProtectedStorageContext: Context
) {
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

        item(key = allowAllRotations) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.allowAllRotationsTitle
                ), stringResource(
                    R.string.allowAllRotationsSummary
                ), allowAllRotations
            )
        }

        item(key = disableSecureScreenshots) {
            TweakSwitch(
                deviceProtectedStorageContext, stringResource(
                    R.string.disableSecureScreenshotsTitle
                ), stringResource(
                    R.string.disableSecureScreenshotsSummary
                ), disableSecureScreenshots
            )
        }
        item(key = "spacer") {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
        }
    }
}