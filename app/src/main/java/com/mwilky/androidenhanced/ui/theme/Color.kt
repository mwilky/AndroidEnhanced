package com.mwilky.androidenhanced.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mwilky.androidenhanced.dataclasses.LogEntryType

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Green50 = Color(0xFFE8F5E9)
val Green100 = Color(0xFFC8E6C9)
val Green200 = Color(0xFFA5D6A7)
val Green300 = Color(0xFF81C784)
val Green400 = Color(0xFF66BB6A)
val Green500 = Color(0xFF4CAF50)
val Green600 = Color(0xFF43A047)
val Green700 = Color(0xFF388E3C)
val Green800 = Color(0xFF2E7D32)
val Green900 = Color(0xFF1B5E20)
val GreenA100 = Color(0xFFB9F6CA)
val GreenA200 = Color(0xFF69F0AE)
val GreenA400 = Color(0xFF00E676)
val GreenA700 = Color(0xFF00C853)

val Amber50 = Color(0xFFFFF8E1)
val Amber100 = Color(0xFFFFECB3)
val Amber200 = Color(0xFFFFE082)
val Amber300 = Color(0xFFFFD54F)
val Amber400 = Color(0xFFFFCA28)
val Amber500 = Color(0xFFFFC107)
val Amber600 = Color(0xFFFFB300)
val Amber700 = Color(0xFFFFA000)
val Amber800 = Color(0xFFFF8F00)
val Amber900 = Color(0xFFFF6F00)
val AmberA100 = Color(0xFFFFE57F)
val AmberA200 = Color(0xFFFFD740)
val AmberA400 = Color(0xFFFFC400)
val AmberA700 = Color(0xFFFFAB00)

@Composable
fun getLogEntryTypeColor(type: LogEntryType): Color {
    return when (type) {
        LogEntryType.HOOKS -> if (isSystemInDarkTheme()) {
            Green300
        } else {
            Green700

        }

        LogEntryType.ERROR -> if (isSystemInDarkTheme()) {
            Amber300
        } else {
            Amber700
        }

        else -> MaterialTheme.colorScheme.onSurface

    }
}