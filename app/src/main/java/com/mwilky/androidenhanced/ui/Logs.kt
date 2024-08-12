package com.mwilky.androidenhanced.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logs(navController: NavController, deviceProtectedStorageContext: Context) {

    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScaffoldTweaksAppBar(navController = navController, screen = "Logs", showBackIcon = false, scrollBehavior)
        },
        bottomBar = {
            ScaffoldNavigationBar(navController = navController)
        },
        content = {
            LogsScrollableContent(topPadding = it, bottomPadding = it, navController)
        }
    )
}
@Composable
fun LogsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            )
    ) {

    }
}