package com.mwilky.androidenhanced.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.android.billingclient.api.ProductDetails
import com.mwilky.androidenhanced.BillingManager
import com.mwilky.androidenhanced.BillingManager.Companion.isPremium
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.MainActivity
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, deviceProtectedStorageContext: Context, billingManager: BillingManager) {

    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScaffoldTweaksAppBar(
                navController = navController,
                screen = deviceProtectedStorageContext.resources.getString(R.string.settings),
                showBackIcon = false,
                scrollBehavior
            )
        },
        bottomBar = {
            ScaffoldNavigationBar(navController = navController)
        },
        content = {
            SettingsScrollableContent(topPadding = it, bottomPadding = it, navController, deviceProtectedStorageContext, billingManager)
        }
    )
}

@Composable
fun SettingsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context,
    billingManager: BillingManager
) {
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            BroadcastUtils.PREFS, MODE_PRIVATE
        )

    LogManager.init(deviceProtectedStorageContext)

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

    // Collect the product details from the StateFlow
    val productDetailsList by billingManager.productDetailsFlow.collectAsState()

    LaunchedEffect(Unit) {
        // Check subscription status
        billingManager.checkSubscriptionStatus()
    }

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
                    id = R.string.subscription
                )
            )
        }
        item {
            Text(
                text = "Unlock advanced features and support the developer in continuing this project.",
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
            )
        }
        items(productDetailsList) { productDetails ->
            ProductDetailsItem(
                productDetails = productDetails,
                billingManager = billingManager,
                isPremium = isPremium
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }



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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = caviarDreamsFamily
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
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = caviarDreamsFamily
                        )
                    }
                }
            }
        }
        item{
            BackupButtonsRow(deviceProtectedStorageContext = deviceProtectedStorageContext)
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

@Composable
fun ProductDetailsItem(
    productDetails: ProductDetails,
    billingManager: BillingManager,
    isPremium: Boolean,
) {
    val subscriptionOffer = productDetails.subscriptionOfferDetails?.firstOrNull()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            OutlinedButton(
                onClick = { showDialog = true },
                enabled = isPremium
            ) {
                Text(
                    "Cancel",
                    fontFamily = caviarDreamsFamily
                )
            }

            Button(
                onClick = {
                    billingManager.launchPurchaseFlow(productDetails)
                },
                enabled = !isPremium,
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    "Subscribe",
                    fontFamily = caviarDreamsFamily
                )
            }
        }
        if (isPremium)
            Text(
                "Subscribed! Thank you for your support.",
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
    }

    // Display the AlertDialog when showDialog is true
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user taps outside or presses the back button
                showDialog = false
            },
            title = {
                Text(
                    text = "Change Subscription",
                    fontFamily = caviarDreamsFamily
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to change your subscription?",
                    fontFamily = caviarDreamsFamily
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // User confirmed, proceed to launch the Play Store intent
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://play.google.com/store/account/subscriptions?package=com.mwilky.androidenhanced&sku=${productDetails.productId}")
                            setPackage("com.android.vending")
                        }
                        try {
                            startActivity(context, intent, null)
                        } catch (e: ActivityNotFoundException) {
                            // Handle exception if the Play Store is not installed
                            Toast.makeText(
                                context,
                                "Google Play Store is not installed on this device.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        // Dismiss the dialog after action
                        showDialog = false
                    }
                ) {
                    Text(
                        text = "Yes",
                        fontFamily = caviarDreamsFamily
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // User canceled the action, just dismiss the dialog
                        showDialog = false
                    }
                ) {
                    Text(text = "No",
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackupButtonsRow(deviceProtectedStorageContext: Context) {
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
                text = deviceProtectedStorageContext.resources.getString(R.string.backup),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
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
                text = deviceProtectedStorageContext.resources.getString(R.string.restore),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
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