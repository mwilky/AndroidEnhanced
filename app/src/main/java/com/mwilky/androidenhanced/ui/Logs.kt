package com.mwilky.androidenhanced.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.MainActivity
import com.mwilky.androidenhanced.dataclasses.LogEntry
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logs(navController: NavController, deviceProtectedStorageContext: Context) {

    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LogsAppBar(scrollBehavior)
        },
        bottomBar = {
            ScaffoldNavigationBar(navController = navController)
        },
        content = {
            LogsScrollableContent(topPadding = it, bottomPadding = it)
        }
    )
}
@Composable
fun LogsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
) {
    val logs: List<LogEntry> by LogManager.logsFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            )
    ) {

        LazyColumn {
            items(logs.reversed()) { logEntry ->
                LogItem(logEntry = logEntry)
            }
        }

    }
}

@Composable
fun LogItem(logEntry: LogEntry) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        .background(MaterialTheme.colorScheme.surface)
    ) {

        Text(
            text = logEntry.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = caviarDreamsFamily
        )
        Text(
            text = logEntry.summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = caviarDreamsFamily
        )
        Text(
            text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(logEntry.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier,
            fontFamily = caviarDreamsFamily
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    val mainActivity = (LocalContext.current as MainActivity)
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
        ),
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = "Logs",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = caviarDreamsFamily
            )
        },
        actions = {
            IconButton(
                onClick = { mainActivity.createLogsBackup() }
            ) {
                Icon(
                    painter = painterResource(id = com.mwilky.androidenhanced.R.drawable.ic_save),
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = { LogManager.clearLogs() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    )
}