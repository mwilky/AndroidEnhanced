package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.MainActivity
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, deviceProtectedStorageContext: Context) {

    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScaffoldTweaksAppBar(
                navController = navController,
                screen = deviceProtectedStorageContext.resources.getString(R.string.settings),
                showBackIcon = false
            )
        },
        bottomBar = {
            ScaffoldNavigationBar(navController = navController)
        },
        content = {
            SettingsScrollableContent(topPadding = it, bottomPadding = it, navController, deviceProtectedStorageContext)
        }
    )
}

@Composable
fun SettingsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context
) {
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            BroadcastUtils.PREFS, MODE_PRIVATE
        )

    // Create a Composable state variable that depends on the SharedPreferences value
    var dateFromSharedPrefs by remember {
        mutableStateOf(sharedPreferences.getString(LASTBACKUP, ""))
    }

    // Set the listener and update the remembered value on change to force a recomposition
    val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            //For certain keys we need to offset the index
            when (key) {
                LASTBACKUP -> dateFromSharedPrefs =
                    sharedPreferences.getString(LASTBACKUP, "")
            }
        }

    val formattedDate = dateFromSharedPrefs?.let { convertDate(it, deviceProtectedStorageContext) }



    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            )
    ) {
        item {
            TweakSectionHeader(
                label = stringResource(
                    id = R.string.backup_restore
                )
            )
        }
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Last backup:",
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                            .fillMaxWidth(0.5f),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (formattedDate != null) {
                        Text(
                            text = formattedDate,
                            modifier = Modifier
                                .padding(
                                    bottom = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                )
                                .fillMaxWidth(0.5f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        item{
            BackupButtonsRow(context = deviceProtectedStorageContext)
        }

    }

    // Add the listener when this Composable is first composed
    DisposableEffect(Unit) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        // Remove the listener when the Composable is disposed
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackupButtonsRow(context: Context) {
    val mainActivity = (LocalContext.current as MainActivity)
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(end = 24.dp, start = 8.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        mainActivity.createBackup()
                    }
                ),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Text(
                text = context.resources.getString(R.string.backup),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        ElevatedCard(
            modifier = Modifier
                .padding(start = 24.dp, end = 8.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        mainActivity.restoreBackup()
                    }
                ),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Text(
                text = context.resources.getString(R.string.restore),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun convertDate(dateFromSharedPrefs: String, context: Context) : String {
    // Check if the savedDateStr is not empty
    if (dateFromSharedPrefs.isNotEmpty()) {
        try {
            // Create a SimpleDateFormat for the "yyyyMMdd_HHmmss" format
            val originalFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

            // Parse the saved string into a Date object
            val date = originalFormat.parse(dateFromSharedPrefs)

            // Create a new SimpleDateFormat for the desired format
            val desiredFormat = SimpleDateFormat("HH:mm EEE dd MMM yyyy", Locale.getDefault())

            // Format the Date object into the desired format
            return desiredFormat.format(date)

            // You can use formattedDate as needed in your app
        } catch (e: ParseException) {
            e.printStackTrace()
            // Handle parsing errors, if any
            return context.resources.getString(R.string.never)
        }
    } else {
       return context.resources.getString(R.string.never)
    }
}