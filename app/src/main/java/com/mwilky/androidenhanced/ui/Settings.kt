package com.mwilky.androidenhanced.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.mwilky.androidenhanced.BillingManager
import com.mwilky.androidenhanced.BillingManager.Companion.isOneTimePurchase
import com.mwilky.androidenhanced.BillingManager.Companion.isPremium
import com.mwilky.androidenhanced.BillingManager.Companion.isSubscription
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.MainActivity
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.LASTBACKUP
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.convertLastBackupDate
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavController,
    deviceProtectedStorageContext: Context,
    billingManager: BillingManager
) {

    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldTweaksAppBar(
            navController = navController,
            screen = deviceProtectedStorageContext.resources.getString(R.string.settings),
            showBackIcon = false,
            scrollBehavior
        )
    }, bottomBar = {
        ScaffoldNavigationBar(navController = navController)
    }, content = {
        SettingsScrollableContent(
            topPadding = it,
            bottomPadding = it,
            navController,
            deviceProtectedStorageContext,
            billingManager
        )
    })
}

@Composable
fun SettingsScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context,
    billingManager: BillingManager
) {
    val sharedPreferences: SharedPreferences = deviceProtectedStorageContext.getSharedPreferences(
        SHAREDPREFS, MODE_PRIVATE
    )

    LogManager.init(deviceProtectedStorageContext)

    // Create a Composable state variable that depends on the SharedPreferences value
    var dateFromSharedPrefs by remember {
        mutableStateOf(sharedPreferences.getString(LASTBACKUP, ""))
    }

    // Set the listener and update the remembered value on change to force a recomposition
    val sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            LASTBACKUP -> dateFromSharedPrefs = sharedPreferences.getString(LASTBACKUP, "")
        }
    }

    val formattedDate =
        dateFromSharedPrefs?.let { convertLastBackupDate(it, deviceProtectedStorageContext) }

    // Collect the product details from the StateFlow
    val subscriptionProductDetailsList by billingManager.subscriptionDetailsFlow.collectAsState()

    val oneTimeProductDetailsList by billingManager.oneTimeDetailsFlow.collectAsState()

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
                    id = R.string.premiumFeatures
                )
            )
        }
        item {
            Text(
                text = stringResource(R.string.licenceTitle),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
            )
        }

        // Display One-Time Purchase Products
        items(oneTimeProductDetailsList) { product ->
            ProductDetailsItem(
                productDetails = product,
                billingManager = billingManager,
                isPremium = isPremium,
                isSubscription = isSubscription,
                isOneTimePurchase = isOneTimePurchase
            )
        }
        // Display Subscription Products
        items(subscriptionProductDetailsList) { product ->
            ProductDetailsItem(
                productDetails = product,
                billingManager = billingManager,
                isPremium = isPremium,
                isSubscription = isSubscription,
                isOneTimePurchase = isOneTimePurchase
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
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.lastBackupTitle),
                        modifier = Modifier
                            .padding(
                                top = 16.dp, start = 16.dp, end = 16.dp
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
                                    bottom = 16.dp, start = 16.dp, end = 16.dp
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
        item {
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
    isSubscription: Boolean,
    isOneTimePurchase: Boolean
) {
    val productType = productDetails.productType
    val context = LocalContext.current

    // For subscription
    val subscriptionOffer =
        productDetails.subscriptionOfferDetails?.firstOrNull { it.offerToken.isNotBlank() && it.pricingPhases.pricingPhaseList.isNotEmpty() }

    val freeTrialDuration =
        subscriptionOffer?.let { billingManager.getFreeTrialDuration(it, context) }
    val regularPrice = subscriptionOffer?.let { billingManager.getRegularPrice(it) }

    // For one-time purchase
    val oneTimePrice = productDetails.oneTimePurchaseOfferDetails?.formattedPrice

    var showDialog by remember { mutableStateOf(false) }

    Column {
        when (productType) {
            BillingClient.ProductType.SUBS -> {
                Text(
                    text = stringResource(R.string.subscriptionTitle),
                    modifier = Modifier
                        .padding(
                            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontFamily = caviarDreamsFamily
                )
            }

            BillingClient.ProductType.INAPP -> {
                Text(
                    text = stringResource(R.string.otpTitle),
                    modifier = Modifier
                        .padding(
                            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontFamily = caviarDreamsFamily
                )
            }
        }
        // Display product details based on type and premium status
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
        ) {
            when (productType) {
                BillingClient.ProductType.SUBS -> {
                    if (isSubscription) {
                        Text(
                            text = stringResource(R.string.subscriptionSuccess),
                            fontFamily = caviarDreamsFamily,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        if (freeTrialDuration != null) {
                            Text(
                                text = "$freeTrialDuration ${stringResource(R.string.freeTrialAvailable)}",
                                fontFamily = caviarDreamsFamily,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (regularPrice != null) {
                                Text(
                                    text = stringResource(R.string.afterTrial) + " " + regularPrice + " " + stringResource(
                                        R.string.perMonth
                                    ),
                                    fontFamily = caviarDreamsFamily,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            if (regularPrice != null) {
                                Text(
                                    text = regularPrice + " " + stringResource(R.string.perMonth),
                                    fontFamily = caviarDreamsFamily,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.priceError),
                                    fontFamily = caviarDreamsFamily,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                BillingClient.ProductType.INAPP -> {
                    if (isOneTimePurchase) {
                        Text(
                            text = stringResource(R.string.otpSuccess),
                            fontFamily = caviarDreamsFamily,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        if (oneTimePrice != null) {
                            Text(
                                text = oneTimePrice,
                                fontFamily = caviarDreamsFamily,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.priceError),
                                fontFamily = caviarDreamsFamily,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                else -> {
                    // Handle other product types if any
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (productType) {
                BillingClient.ProductType.SUBS -> {
                    OutlinedButton(
                        onClick = { showDialog = true }, enabled = isSubscription
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            billingManager.launchSubscriptionPurchaseFlow(productDetails)
                        }, enabled = !isSubscription, modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            stringResource(R.string.subscribe),
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                BillingClient.ProductType.INAPP -> {
                    Button(
                        onClick = {
                            billingManager.launchOneTimePurchaseFlow(productDetails)
                        }, enabled = !isOneTimePurchase, modifier = Modifier
                    ) {
                        Text(
                            stringResource(R.string.purchase),
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    // Handle other product types if any
                }
            }
        }
    }

    // Display the AlertDialog when showDialog is true (only for subscriptions)
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            // Dismiss the dialog when the user taps outside or presses the back button
            showDialog = false
        }, title = {
            Text(
                text = stringResource(R.string.changeSubscription),
                fontFamily = caviarDreamsFamily
            )
        }, text = {
            Text(
                text = stringResource(R.string.changeSubscriptionConfirm),
                fontFamily = caviarDreamsFamily
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    // User confirmed, proceed to launch the Play Store intent
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data =
                            "https://play.google.com/store/account/subscriptions?package=com.mwilky.androidenhanced&sku=${productDetails.productId}".toUri()
                        setPackage("com.android.vending")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Handle exception if the Play Store is not installed
                        Toast.makeText(
                            context,
                            context.getString(R.string.googlePlayError),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    // Dismiss the dialog after action
                    showDialog = false
                }) {
                Text(
                    text = stringResource(R.string.yes), fontFamily = caviarDreamsFamily
                )
            }
        }, dismissButton = {
            Button(
                onClick = {
                    // User canceled the action, just dismiss the dialog
                    showDialog = false
                }) {
                Text(
                    text = stringResource(R.string.no), fontFamily = caviarDreamsFamily
                )
            }
        })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackupButtonsRow(deviceProtectedStorageContext: Context) {
    val mainActivity = (LocalActivity.current as MainActivity)
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(end = 24.dp, start = 8.dp)
                .clickable(
                    enabled = true, onClick = {
                        mainActivity.createBackup()
                    }),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Text(
                text = deviceProtectedStorageContext.resources.getString(R.string.backup),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
            )
        }
        ElevatedCard(
            modifier = Modifier
                .padding(start = 24.dp, end = 8.dp)
                .clickable(
                    enabled = true, onClick = {
                        mainActivity.restoreBackup()
                    }),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Text(
                text = deviceProtectedStorageContext.resources.getString(R.string.restore),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily
            )
        }
    }
}