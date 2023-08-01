package com.mwilky.androidenhanced.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector

data class EnvironmentProp(
    val icon: ImageVector,
    val label: String,
    val value: String
)
