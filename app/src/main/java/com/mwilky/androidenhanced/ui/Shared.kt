package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.mwilky.androidenhanced.BillingManager.Companion.isPremium
import com.mwilky.androidenhanced.BroadcastSender
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.TAG
import com.mwilky.androidenhanced.Utils.Companion.qqsColumns
import com.mwilky.androidenhanced.Utils.Companion.qqsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qqsRows
import com.mwilky.androidenhanced.Utils.Companion.qsColumns
import com.mwilky.androidenhanced.Utils.Companion.qsColumnsLandscape
import com.mwilky.androidenhanced.Utils.Companion.qsRows
import com.mwilky.androidenhanced.Utils.Companion.statusBarClockPosition
import com.mwilky.androidenhanced.dataclasses.Chip
import com.mwilky.androidenhanced.ui.Shared.Companion.readIconSwitchState
import com.mwilky.androidenhanced.ui.Shared.Companion.readSwitchState
import com.mwilky.androidenhanced.ui.Shared.Companion.writeIconSwitchState
import com.mwilky.androidenhanced.ui.Shared.Companion.writeSwitchState
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import kotlinx.coroutines.launch
import java.util.Locale

class Shared {

    companion object {
        fun readSwitchState(
            deviceProtectedStorageContext: Context, key: String, defaultValue: Boolean = false
        ): Boolean {
            return try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
                sharedPreferences.getBoolean(key, defaultValue)
            } catch (e: Exception) {
                Log.e(TAG, "readSwitchState error: $e")
                false
            }
        }

        fun writeSwitchState(deviceProtectedStorageContext: Context, key: String, state: Boolean) {
            try {

                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
                sharedPreferences.edit { putBoolean(key, state) }
            } catch (e: Exception) {
                Log.e(TAG, "writeSwitchState error: $e")
            }
        }

        fun writeIconSwitchState(
            deviceProtectedStorageContext: Context, key: String, slot: String
        ) {
            try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

                val currentBlockedIcons: String? = sharedPreferences.getString(key, "")

                val blockedIconsList =
                    (currentBlockedIcons ?: "").split(",").filter { it.isNotBlank() }
                        .map { it.trim() }.toMutableList()

                if (blockedIconsList.contains(slot)) {

                    blockedIconsList.remove(slot)
                } else {

                    blockedIconsList.add(slot)
                }

                val updatedBlockedIcons = blockedIconsList.joinToString(",")

                sharedPreferences.edit { putString(key, updatedBlockedIcons) }
            } catch (e: Exception) {
                Log.e(TAG, "writeIconSwitchState error: $e")
            }
        }

        fun readIconSwitchState(
            deviceProtectedStorageContext: Context, key: String, slot: String
        ): Boolean {
            return try {
                val sharedPreferences =
                    deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)

                val currentBlockedIcons: String? = sharedPreferences.getString(key, "")

                val blockedIconsList =
                    (currentBlockedIcons ?: "").split(",").filter { it.isNotBlank() }
                        .map { it.trim() }.toMutableList()

                Log.e(TAG, "currentBlockedIcons = $currentBlockedIcons")

                blockedIconsList.contains(slot)
            } catch (e: Exception) {
                Log.e(TAG, "readIconSwitchState error: $e")
                false
            }
        }

    }
}

@Composable
fun TweakSwitch(
    context: Context,
    label: String,
    description: String,
    key: String,
    defaultValue: Boolean = false,
    premiumFeature: Boolean = false,
    extraContent: @Composable (() -> Unit)? = null,
) {
    var switchState by remember { mutableStateOf(readSwitchState(context, key, defaultValue)) }

    val isPremiumUnlocked = if (premiumFeature) isPremium else true

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .clickable(enabled = premiumFeature && !isPremium, onClick = {
                Toast.makeText(
                    context, context.getString(R.string.requires_subscription), Toast.LENGTH_SHORT
                ).show()
            }), colors = CardDefaults.elevatedCardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPremiumUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    ),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                        end = 4.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPremiumUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
                if (premiumFeature && !isPremium) {
                    Text(
                        text = stringResource(id = R.string.requires_subscription),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
            Switch(
                enabled = isPremiumUnlocked, checked = switchState, onCheckedChange = {
                    switchState = !switchState
                    writeSwitchState(context, key, switchState)
                    BroadcastSender.send(context, key, switchState)
                }, modifier = Modifier
                    .padding(
                        horizontal = 24.dp
                    )
                    .size(32.dp), colors = SwitchDefaults.colors()
            )
        }
        AnimatedVisibility(
            visible = switchState,
            enter = fadeIn(animationSpec = tween(durationMillis = 500))
                    + expandVertically(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
                    + shrinkVertically(animationSpec = tween(durationMillis = 500))
        ) {
            extraContent?.invoke()
        }
    }
}

@Composable
fun rememberIntPreference(
    sharedPreferences: SharedPreferences, key: String, defaultValue: Int = 0
): MutableState<Int> {
    val state = remember { mutableIntStateOf(sharedPreferences.getInt(key, defaultValue)) }

    DisposableEffect(key1 = sharedPreferences, key2 = key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
            if (changedKey == key) {
                state.intValue = prefs.getInt(key, defaultValue)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return state
}

@Composable
fun TweakRow(
    label: String, description: String, navController: NavController, disabled: Boolean
) {

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        colors = CardDefaults.elevatedCardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                )
                .clickable(enabled = !disabled, onClick = {
                    navController.navigate(label.toLowerCase(androidx.compose.ui.text.intl.Locale.current))
                }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp, top = 8.dp, end = 4.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        }
    }
}

@Composable
fun rememberBoolPreference(
    sharedPreferences: SharedPreferences, key: String, defaultValue: Boolean = false
): MutableState<Boolean> {
    val state = remember { mutableStateOf(sharedPreferences.getBoolean(key, defaultValue)) }

    DisposableEffect(key1 = sharedPreferences, key2 = key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
            if (changedKey == key) {
                state.value = prefs.getBoolean(key, defaultValue)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}


@Composable
fun TweakSelectionRow(
    label: String,
    description: String,
    key: String,
    entries: Array<String>,
    defaultIndex: Int,
    disabledIndexes: List<Int>? = null,
    disabled: Boolean = false,
    deviceProtectedStorageContext: Context,
    imageResourceIds: List<Int>? = null,
    premiumIndexes: List<Int>? = null
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    if (isDialogVisible) {
        TweakSelectionDialog(
            onDismissRequest = { isDialogVisible = false },
            onConfirmation = { isDialogVisible = false },
            label = label,
            key = key,
            entries = entries,
            defaultIndex = defaultIndex,
            disabledIndexes = disabledIndexes,
            deviceProtectedStorageContext = deviceProtectedStorageContext,
            imageResourceIds = imageResourceIds,
            premiumIndexes = premiumIndexes
        )
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        colors = CardDefaults.elevatedCardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                )
                .clickable(enabled = !disabled, onClick = {
                    // Show the dialog when the row is clicked
                    isDialogVisible = true
                }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp, top = 8.dp, end = 4.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        }
    }
}

@Composable
fun TweakSelectionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    label: String,
    key: String,
    entries: Array<String>,
    defaultIndex: Int,
    disabledIndexes: List<Int>? = null,
    deviceProtectedStorageContext: Context,
    imageResourceIds: List<Int>? = null,
    premiumIndexes: List<Int>? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedOption by remember { mutableIntStateOf(defaultIndex) }
    val options = remember {
        entries.toList()
    }

    // Function to update the selected radio button
    val onOptionSelected: (Int) -> Unit = { index ->
        selectedOption = index
    }

    // Function to save the selected index to SharedPreferences
    val saveToSharedPreferences: () -> Unit = {
        coroutineScope.launch {
            val sharedPreferences =
                deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
            sharedPreferences.edit { putInt(key, selectedOption) }
        }
        onConfirmation()
    }

    // Function to read the saved index from SharedPreferences
    val readFromSharedPreferences: () -> Unit = {
        val sharedPreferences =
            deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
        selectedOption = sharedPreferences.getInt(key, defaultIndex)
        //For certain keys we need to offset the index
        selectedOption = when (key) {
            qqsRows -> {
                if (sharedPreferences.contains(qqsRows)) {
                    selectedOption - 1
                } else {
                    selectedOption
                }
            }

            qsColumns -> {
                if (sharedPreferences.contains(qsColumns)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }

            qsColumnsLandscape -> {
                if (sharedPreferences.contains(qsColumnsLandscape)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }

            qqsColumns -> {
                if (sharedPreferences.contains(qqsColumns)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }

            qqsColumnsLandscape -> {
                if (sharedPreferences.contains(qqsColumnsLandscape)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }

            qsRows -> {
                if (sharedPreferences.contains(qsRows)) {
                    selectedOption - 2
                } else {
                    selectedOption
                }
            }

            else -> selectedOption
        }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(
                        start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (imageResourceIds != null) {

                    val safeSelectedOption = selectedOption.coerceIn(0, imageResourceIds.size - 1)

                    Image(
                        painter = painterResource(id = imageResourceIds[safeSelectedOption]),
                        contentDescription = "$key image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .height(100.dp),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primary, BlendMode.SrcIn
                        )
                    )

                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(options.size) { index ->
                        val option = options[index]
                        val isClickable = disabledIndexes?.contains(index) != true

                        Row(
                            modifier = Modifier.fillMaxWidth().let {
                                if (isClickable) it.clickable { onOptionSelected(index) } else it
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = index == selectedOption,
                                onClick = {
                                    if (isClickable) {
                                        onOptionSelected(index)
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isClickable) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontFamily = caviarDreamsFamily
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(
                            end = 8.dp, bottom = 8.dp, top = 16.dp
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = caviarDreamsFamily
                        )
                    }

                    val premiumCheck = if (isPremium) {
                        true
                    } else if (premiumIndexes?.contains(selectedOption) == true) {
                        Toast.makeText(
                            deviceProtectedStorageContext,
                            deviceProtectedStorageContext.getString(R.string.requires_subscription),
                            Toast.LENGTH_SHORT
                        ).show()
                        false
                    } else {
                        true
                    }

                    TextButton(
                        enabled = premiumCheck,
                        onClick = {
                            //For certain keys we need to offset the value saved to sharedprefs
                            selectedOption += when (key) {
                                qqsRows -> 1
                                qsColumns -> 2
                                qsColumnsLandscape -> 2
                                qqsColumns -> 2
                                qqsColumnsLandscape -> 2
                                qsRows -> 2
                                else -> 0
                            }
                            saveToSharedPreferences()
                            BroadcastSender.send(deviceProtectedStorageContext, key, selectedOption)
                        },
                        modifier = Modifier.padding(
                            start = 8.dp, end = 24.dp, bottom = 8.dp, top = 16.dp
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = caviarDreamsFamily
                        )
                    }
                }
            }
        }
    }

    // Call readFromSharedPreferences when the dialog is first displayed
    DisposableEffect(Unit) {
        readFromSharedPreferences()
        onDispose { }
    }
}

@Composable
fun TweakColor(
    deviceProtectedStorageContext: Context,
    label: String,
    key: String,
    previewColor: Int,
    sharedPreferences: SharedPreferences,
    disabled: Boolean = false,
    description: String = "",
    resetColor: Int = android.graphics.Color.WHITE,
    premiumFeature: Boolean = false
) {

    val isPremiumUnlocked = if (premiumFeature) isPremium else true
    var isColorPickerVisible by remember { mutableStateOf(false) }

    if (isColorPickerVisible) {
        TweakColorDialog(
            onDismissRequest = { isColorPickerVisible = false },
            onConfirmation = { isColorPickerVisible = false },
            key = key,
            defaultColor = previewColor,
            context = deviceProtectedStorageContext,
            sharedPreferences = sharedPreferences,
            label = label,
            resetColor
        )
    }
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .clickable(enabled = premiumFeature && !isPremium, onClick = {
                Toast.makeText(
                    deviceProtectedStorageContext,
                    deviceProtectedStorageContext.getString(R.string.requires_subscription),
                    Toast.LENGTH_SHORT
                ).show()
            }), colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (!isPremiumUnlocked || disabled) MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    )
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 4.dp,
                        bottom = if (description.isNotEmpty() || (premiumFeature && !isPremium)) 0.dp else 8.dp,
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (premiumFeature && !isPremium) {
                    Text(
                        text = stringResource(id = R.string.requires_subscription),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 4.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }

                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 4.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }

            }
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp
                    )
                    .size(32.dp)
                    .background(
                        color = intToColor(previewColor), shape = CircleShape
                    )
                    .clickable(
                        enabled = isPremiumUnlocked && !disabled,
                    ) {
                        isColorPickerVisible = true
                    })
        }
    }
}

@Composable
fun TweakColorDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    key: String,
    defaultColor: Int,
    context: Context,
    sharedPreferences: SharedPreferences,
    label: String,
    resetColor: Int = android.graphics.Color.WHITE
) {
    val colorPickerController = rememberColorPickerController()
    var selectedColorInt by remember { mutableIntStateOf(defaultColor) }
    var hexCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(24.dp),
                        fontFamily = caviarDreamsFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                colorPickerController.wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 24.dp),
                    controller = colorPickerController,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColorInt = colorEnvelope.color.toArgb()
                        hexCode = colorEnvelope.hexCode
                    },
                    initialColor = intToColor(defaultColor)
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                        .height(20.dp),
                    controller = colorPickerController,
                    initialColor = intToColor(defaultColor),
                    wheelRadius = 6.dp,
                    wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp)
                        .height(20.dp),
                    controller = colorPickerController,
                    initialColor = intToColor(defaultColor),
                    wheelRadius = 6.dp,
                    wheelColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = hexCode, onValueChange = { newHexCode ->

                            hexCode = newHexCode

                            if (isValidHexCode(newHexCode)) {
                                selectedColorInt = hexStringToColorInt(hexCode)
                                colorPickerController.selectByColor(
                                    Color(color = "#$hexCode".toColorInt()), false
                                )
                            }
                        }, colors = TextFieldDefaults.colors(
                            focusedTextColor = intToColor(intValue = selectedColorInt),
                            unfocusedTextColor = intToColor(intValue = selectedColorInt),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ), singleLine = true, textStyle = TextStyle(
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "reset",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 24.dp, bottom = 8.dp, top = 4.dp)
                                .clickable {
                                    selectedColorInt = resetColor
                                    colorPickerController.selectByColor(
                                        Color(resetColor), false
                                    )
                                    sharedPreferences.edit { putInt(key, selectedColorInt) }
                                    BroadcastSender.send(context, key, selectedColorInt)
                                })
                    }
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(
                            end = 4.dp, bottom = 8.dp, top = 4.dp
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss),
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(
                        onClick = {
                            sharedPreferences.edit { putInt(key, selectedColorInt) }
                            BroadcastSender.send(context, key, selectedColorInt)
                            onConfirmation()
                        },
                        modifier = Modifier.padding(
                            start = 4.dp, end = 16.dp, bottom = 8.dp, top = 4.dp
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = caviarDreamsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun isValidHexCode(hexCode: String): Boolean {
    return try {
        // Check if the hex code contains any spaces
        if (hexCode.contains(" ")) {
            false // Spaces are not allowed
        } else {
            // Attempt to parse the hex code, the below will fail if invalid color
            val color = Color("#$hexCode".toColorInt())

            // Check if parsing succeeds and if the hex code has exactly 8 digits
            hexCode.length == 8
        }
    } catch (e: IllegalArgumentException) {
        false // Parsing failed, invalid hex code
    }
}

fun hexStringToColorInt(hexString: String): Int {
    // Convert hex string to Color
    val color = Color("#$hexString".toColorInt())

    // Extract RGBA components and pack them into a single Int
    return android.graphics.Color.argb(
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
}

@Composable
fun intToColor(intValue: Int): Color {
    // Extract the alpha, red, green, and blue components from the intValue
    val alpha = (intValue shr 24) and 0xFF
    val red = (intValue shr 16) and 0xFF
    val green = (intValue shr 8) and 0xFF
    val blue = intValue and 0xFF

    // Create a Color object using the extracted components
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f, alpha = alpha / 255f)
}

@Composable
fun TweakSectionHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = 16.dp, end = 32.dp, top = 16.dp
        ),
        fontFamily = caviarDreamsFamily,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TweakIconSwitch(
    context: Context, label: String, description: String, key: String, slot: String
) {
    var switchState by remember { mutableStateOf(readIconSwitchState(context, key, slot)) }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        colors = CardDefaults.elevatedCardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                        end = 4.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
            Switch(
                checked = switchState, onCheckedChange = {
                    switchState = !switchState
                    writeIconSwitchState(context, key, slot)
                    BroadcastSender.send(context, key, slot)
                }, modifier = Modifier
                    .padding(
                        horizontal = 24.dp
                    )
                    .size(32.dp), colors = SwitchDefaults.colors()
            )
        }
    }
}

@Composable
fun SettingsSlider(
    context: Context,
    label: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    // Converts the raw value to a display string (default: one decimal place)
    displayValue: (Float) -> String = { v -> String.format(Locale.UK, "%.1f", v) },
    // Rounds the value when finished (default: one decimal place)
    roundingFn: (Float) -> Float = { v -> String.format(Locale.UK, "%.1f", v).toFloat() }
) {
    var localValue by remember { mutableFloatStateOf(value) }

    LaunchedEffect(value) {
        localValue = value
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = 16.dp, top = 24.dp, end = 4.dp
            ),
            fontFamily = caviarDreamsFamily
        )
        Text(
            text = valueLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp
            ),
            fontFamily = caviarDreamsFamily
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Slider(
                value = localValue,
                onValueChange = { newValue -> localValue = newValue },
                onValueChangeFinished = {
                    val roundedValue = roundingFn(localValue)
                    onValueChangeFinished(roundedValue)
                    onValueChange(roundedValue)
                },
                valueRange = valueRange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            )
            Text(
                text = displayValue(localValue),
                style = MaterialTheme.typography.titleSmall,
                fontFamily = caviarDreamsFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun ChipsFlowRow(
    chips: List<Chip>,
    description: String,
    modifier: Modifier = Modifier,
    context: Context
) {
    val sharedPrefs = remember {
        context.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
    }

    // State to track which chips are selected
    val chipStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            chips.forEach { chip ->
                chip.key?.let { key ->
                    this[key] = sharedPrefs.getBoolean(key, false)
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 0.dp, bottom = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
        ) {
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp, bottom = 8.dp, end = 16.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                FlowRow(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    chips.forEach { chip ->
                        val isSelected = chipStates[chip.key] ?: false

                        FilterChip(
                            onClick = {
                                val newState = !isSelected
                                chip.key?.let { key ->
                                    chipStates[key]
                                }

                                // Save to SharedPreferences
                                sharedPrefs.edit {
                                    putBoolean(chip.key, newState)
                                }

                                // Send broadcast
                                chip.key?.let { key ->
                                    BroadcastSender.send(context, key, newState)
                                }
                            },
                            label =
                                {
                                    Text(
                                        text = chip.label,
                                        fontFamily = caviarDreamsFamily,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                },
                            selected = isSelected,
                            modifier = Modifier.height(32.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleSelectionChipsFlowRow(
    chips: List<Chip>,
    label: String,
    description: String,
    key: String,
    modifier: Modifier = Modifier,
    defaultIndex: Int = 0,
    context: Context
) {
    val sharedPrefs = remember {
        context.getSharedPreferences(SHAREDPREFS, MODE_PRIVATE)
    }

    // State to track which chip is selected (by index)
    var selectedIndex by remember {
        mutableIntStateOf(
            run {
                val rawValue = sharedPrefs.getInt(key, defaultIndex)
                when (key) {
                    qqsRows -> {
                        if (sharedPrefs.contains(key)) {
                            rawValue - 1
                        } else {
                            rawValue
                        }
                    }

                    qsColumns, qsColumnsLandscape, qqsColumns, qqsColumnsLandscape, qsRows -> {
                        if (sharedPrefs.contains(key)) {
                            rawValue - 2
                        } else {
                            rawValue
                        }
                    }

                    else -> rawValue
                }
            }
        )
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp, bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = if (description.isNotEmpty()) 0.dp else 8.dp,
                        end = 4.dp
                    ),
                    fontFamily = caviarDreamsFamily
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            start = 16.dp, bottom = 8.dp, end = 16.dp
                        ),
                        fontFamily = caviarDreamsFamily
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        FlowRow(
                            modifier = modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.Start
                            ),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            chips.forEachIndexed { index, chip ->
                                val isSelected = selectedIndex == index

                                FilterChip(
                                    onClick = {
                                        selectedIndex = index

                                        // Apply offset when saving (same as TweakSelectionDialog)
                                        val valueToSave = index + when (key) {
                                            qqsRows -> 1
                                            qsColumns, qsColumnsLandscape, qqsColumns, qqsColumnsLandscape, qsRows -> 2
                                            else -> 0
                                        }

                                        // Save to SharedPreferences
                                        sharedPrefs.edit {
                                            putInt(key, valueToSave)
                                        }

                                        // Send broadcast with the offset value
                                        BroadcastSender.send(context, key, valueToSave)
                                    },
                                    label = {
                                        Text(
                                            text = chip.label,
                                            fontFamily = caviarDreamsFamily,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    selected = isSelected,
                                    modifier = Modifier.height(32.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            Color.Transparent
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// Preview composables
@Preview(showBackground = true)
@Composable
fun ChipsFlowRowPreview() {
    val clockPositionChips = listOf(
        Chip("Left"),
        Chip("Right"),
        Chip("Hidden")
    )
    SingleSelectionChipsFlowRow(
        chips = clockPositionChips,
        label = "Clock position",
        description = "Select the position of the statusbar clock",
        key = statusBarClockPosition,
        defaultIndex = 0,
        context = LocalContext.current
    )
}






