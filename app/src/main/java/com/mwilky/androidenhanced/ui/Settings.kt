package com.mwilky.androidenhanced.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.mwilky.androidenhanced.BillingManager
import com.mwilky.androidenhanced.BillingManager.Companion.isOneTimePurchase
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
        }
    ) { paddingValues ->
        SettingsScrollableContent(
            paddingValues = paddingValues,
            navController = navController,
            deviceProtectedStorageContext = deviceProtectedStorageContext,
            billingManager = billingManager
        )
    }
}

@Composable
fun SettingsScrollableContent(
    paddingValues: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context,
    billingManager: BillingManager
) {
    val sharedPreferences: SharedPreferences = deviceProtectedStorageContext.getSharedPreferences(
        SHAREDPREFS, MODE_PRIVATE
    )

    LogManager.init(deviceProtectedStorageContext)

    var dateFromSharedPrefs by remember {
        mutableStateOf(sharedPreferences.getString(LASTBACKUP, ""))
    }

    val sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            LASTBACKUP -> dateFromSharedPrefs = sharedPreferences.getString(LASTBACKUP, "")
        }
    }

    val formattedDate = dateFromSharedPrefs?.let {
        convertLastBackupDate(it, deviceProtectedStorageContext)
    }

    val subscriptionProductDetailsList by billingManager.subscriptionDetailsFlow.collectAsState()
    val oneTimeProductDetailsList by billingManager.oneTimeDetailsFlow.collectAsState()

    LaunchedEffect(Unit) {
        billingManager.checkSubscriptionStatus()
    }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            PremiumSection(
                oneTimeProductDetailsList = oneTimeProductDetailsList,
                subscriptionProductDetailsList = subscriptionProductDetailsList,
                billingManager = billingManager,
                isSubscription = isSubscription,
                isOneTimePurchase = isOneTimePurchase
            )
        }

        item {
            BackupSection(
                deviceProtectedStorageContext = deviceProtectedStorageContext,
                formattedDate = formattedDate
            )
        }
    }

    DisposableEffect(Unit) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        }
    }
}

@Composable
private fun PremiumSection(
    oneTimeProductDetailsList: List<ProductDetails>,
    subscriptionProductDetailsList: List<ProductDetails>,
    billingManager: BillingManager,
    isSubscription: Boolean,
    isOneTimePurchase: Boolean
) {
    SettingsSectionCard(
        title = stringResource(id = R.string.premiumFeatures)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.licenceTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = caviarDreamsFamily,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // One-Time Purchase Products
            oneTimeProductDetailsList.forEach { product ->
                ProductDetailsItem(
                    productDetails = product,
                    billingManager = billingManager,
                    isSubscription = isSubscription,
                    isOneTimePurchase = isOneTimePurchase
                )
            }

            // Subscription Products
            subscriptionProductDetailsList.forEach { product ->
                ProductDetailsItem(
                    productDetails = product,
                    billingManager = billingManager,
                    isSubscription = isSubscription,
                    isOneTimePurchase = isOneTimePurchase
                )
            }
        }
    }
}

@Composable
private fun BackupSection(
    deviceProtectedStorageContext: Context,
    formattedDate: String?
) {
    SettingsSectionCard(
        title = stringResource(id = R.string.backup_restore)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Last Backup Info
            BackupInfoCard(formattedDate = formattedDate)

            // Backup Actions
            BackupButtonsRow(deviceProtectedStorageContext = deviceProtectedStorageContext)
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = caviarDreamsFamily,
                    fontWeight = FontWeight.Medium
                )
            }
            content()
        }
    }
}

@Composable
private fun BackupInfoCard(formattedDate: String?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.lastBackupTitle),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = caviarDreamsFamily
                )
            }

            if (formattedDate != null) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = caviarDreamsFamily,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = stringResource(R.string.notBackedUpYet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = caviarDreamsFamily
                )
            }
        }
    }
}

@Composable
fun ProductDetailsItem(
    productDetails: ProductDetails,
    billingManager: BillingManager,
    isSubscription: Boolean,
    isOneTimePurchase: Boolean
) {
    val productType = productDetails.productType
    val context = LocalContext.current

    val subscriptionOffer = productDetails.subscriptionOfferDetails?.firstOrNull {
        it.offerToken.isNotBlank() && it.pricingPhases.pricingPhaseList.isNotEmpty()
    }

    val freeTrialDuration = subscriptionOffer?.let {
        billingManager.getFreeTrialDuration(it, context)
    }
    val regularPrice = subscriptionOffer?.let { billingManager.getRegularPrice(it) }
    val oneTimePrice = productDetails.oneTimePurchaseOfferDetails?.formattedPrice

    var showDialog by remember { mutableStateOf(false) }

    // Determine card color and text/icon colors based on product type and purchase status
    val (cardContainerColor, textColor, iconColor) = when (productType) {
        BillingClient.ProductType.SUBS -> {
            if (isSubscription) {
                Triple(
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerHighest,
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.onSurface
                )
            } else {
                Triple(
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainerLow,
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.primary
                )
            }
        }

        BillingClient.ProductType.INAPP -> {
            if (isOneTimePurchase) {
                Triple(
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerHighest,
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.onSurface
                )
            } else {
                Triple(
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainerLow,
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.primary
                )
            }
        }

        else -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.primary
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardContainerColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Type Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (typeTitle, isActive) = when (productType) {
                    BillingClient.ProductType.SUBS -> Pair(
                        stringResource(R.string.subscriptionTitle),
                        isSubscription
                    )

                    BillingClient.ProductType.INAPP -> Pair(
                        stringResource(R.string.otpTitle),
                        isOneTimePurchase
                    )

                    else -> Pair("", false)
                }

                if (isActive) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = iconColor
                    )
                }

                Text(
                    text = typeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = caviarDreamsFamily
                )
            }

            // Product Details Content
            ProductDetailsContent(
                productType = productType,
                isSubscription = isSubscription,
                isOneTimePurchase = isOneTimePurchase,
                freeTrialDuration = freeTrialDuration,
                regularPrice = regularPrice,
                oneTimePrice = oneTimePrice
            )

            // Action Buttons
            ProductActionButtons(
                productType = productType,
                productDetails = productDetails,
                billingManager = billingManager,
                isSubscription = isSubscription,
                isOneTimePurchase = isOneTimePurchase,
                onShowDialog = { showDialog = true }
            )
        }
    }

    // Subscription Cancel Dialog
    if (showDialog) {
        SubscriptionCancelDialog(
            productDetails = productDetails,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ProductDetailsContent(
    productType: String,
    isSubscription: Boolean,
    isOneTimePurchase: Boolean,
    freeTrialDuration: String?,
    regularPrice: String?,
    oneTimePrice: String?
) {
    when (productType) {
        BillingClient.ProductType.SUBS -> {
            if (isSubscription) {
                Text(
                    text = stringResource(R.string.subscriptionSuccess),
                    fontFamily = caviarDreamsFamily,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (freeTrialDuration != null) {
                        Text(
                            text = "$freeTrialDuration ${stringResource(R.string.freeTrialAvailable)}",
                            fontFamily = caviarDreamsFamily,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        if (regularPrice != null) {
                            Text(
                                text = "${stringResource(R.string.afterTrial)} $regularPrice ${
                                    stringResource(
                                        R.string.perMonth
                                    )
                                }",
                                fontFamily = caviarDreamsFamily,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        if (regularPrice != null) {
                            Text(
                                text = "$regularPrice ${stringResource(R.string.perMonth)}",
                                fontFamily = caviarDreamsFamily,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
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
        }

        BillingClient.ProductType.INAPP -> {
            if (isOneTimePurchase) {
                Text(
                    text = stringResource(R.string.otpSuccess),
                    fontFamily = caviarDreamsFamily,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                if (oneTimePrice != null) {
                    Text(
                        text = oneTimePrice,
                        fontFamily = caviarDreamsFamily,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
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
}

@Composable
private fun ProductActionButtons(
    productType: String,
    productDetails: ProductDetails,
    billingManager: BillingManager,
    isSubscription: Boolean,
    isOneTimePurchase: Boolean,
    onShowDialog: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (productType) {
            BillingClient.ProductType.SUBS -> {
                OutlinedButton(
                    onClick = onShowDialog,
                    enabled = isSubscription,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontFamily = caviarDreamsFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { billingManager.launchSubscriptionPurchaseFlow(productDetails) },
                    enabled = !isSubscription,
                    modifier = Modifier.weight(1f)
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
                    onClick = { billingManager.launchOneTimePurchaseFlow(productDetails) },
                    enabled = !isOneTimePurchase,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.purchase),
                        fontFamily = caviarDreamsFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionCancelDialog(
    productDetails: ProductDetails,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(R.string.changeSubscription),
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.changeSubscriptionConfirm),
                fontFamily = caviarDreamsFamily
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data =
                            "https://play.google.com/store/account/subscriptions?package=com.mwilky.androidenhanced&sku=${productDetails.productId}".toUri()
                        setPackage("com.android.vending")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.googlePlayError),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(R.string.yes),
                    fontFamily = caviarDreamsFamily,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.no),
                    fontFamily = caviarDreamsFamily,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackupButtonsRow(deviceProtectedStorageContext: Context) {
    val mainActivity = (LocalActivity.current as MainActivity)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledTonalButton(
            onClick = { mainActivity.createBackup() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Text(
                text = deviceProtectedStorageContext.resources.getString(R.string.backup),
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        FilledTonalButton(
            onClick = { mainActivity.restoreBackup() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Text(
                text = deviceProtectedStorageContext.resources.getString(R.string.restore),
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}