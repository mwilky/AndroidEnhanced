package com.mwilky.androidenhanced.ui

import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.MainActivity
import com.mwilky.androidenhanced.dataclasses.LogEntry
import com.mwilky.androidenhanced.dataclasses.LogEntryType
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import com.mwilky.androidenhanced.ui.theme.getLogEntryTypeColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logs(navController: NavController, deviceProtectedStorageContext: Context) {
    val logs: List<LogEntry> by LogManager.logsFlow.collectAsState()
    var selectedFilter by remember { mutableStateOf<LogEntryType?>(LogEntryType.INFO) }

    // Filter logs based on selected filter
    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == null) {
            logs
        } else {
            logs.filter { it.type == selectedFilter }
        }
    }

    // Use a key to recreate the entire composable when logs are cleared
    key(logs.size) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LogsAppBar(
                    scrollBehavior = scrollBehavior,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            },
            bottomBar = {
                ScaffoldNavigationBar(navController = navController)
            },
            content = {
                LogsScrollableContent(
                    topPadding = it,
                    bottomPadding = it,
                    filteredLogs = filteredLogs
                )
            }
        )
    }
}

@Composable
fun LogsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    filteredLogs: List<LogEntry>
) {
    LaunchedEffect(Unit) {
        LogManager.reloadLogs()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            )
    ) {

        LazyColumn {
            items(filteredLogs.reversed()) { logEntry ->
                LogEntryItem(log = logEntry)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun LogEntryItem(log: LogEntry) {
    val formattedDate = remember(log.timestamp) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .background(getLogEntryTypeColor(log.type))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
        ) {
            Text(
                text = log.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = caviarDreamsFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = caviarDreamsFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = caviarDreamsFamily
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedFilter: LogEntryType?,
    onFilterSelected: (LogEntryType?) -> Unit
) {
    val mainActivity = (LocalActivity.current as MainActivity)
    var showFilterMenu by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
        ),
        scrollBehavior = scrollBehavior,
        title = {
            val titleText = when (selectedFilter) {
                LogEntryType.INFO -> "Logs"
                null -> "Logs (All)"
                else -> "Logs (${
                    selectedFilter.name.lowercase().replaceFirstChar { it.uppercase() }
                })"
            }
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = caviarDreamsFamily
            )
        },
        actions = {
            // Clear logs button
            IconButton(
                onClick = { LogManager.clearAllLogs() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Clear logs",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Backup button
            IconButton(
                onClick = { mainActivity.createLogBackup() }
            ) {
                Icon(
                    painter = painterResource(id = com.mwilky.androidenhanced.R.drawable.ic_save),
                    contentDescription = "Save logs",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Filter button with dropdown menu
            Box {
                IconButton(
                    onClick = { showFilterMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Filter logs",
                        tint = if (selectedFilter != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All", fontFamily = caviarDreamsFamily) },
                        onClick = {
                            onFilterSelected(null)
                            showFilterMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Default", fontFamily = caviarDreamsFamily) },
                        onClick = {
                            onFilterSelected(LogEntryType.INFO)
                            showFilterMenu = false
                        }
                    )
                    LogEntryType.entries.filter { it != LogEntryType.INFO }.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontFamily = caviarDreamsFamily
                                )
                            },
                            onClick = {
                                onFilterSelected(type)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

        }
    )
}