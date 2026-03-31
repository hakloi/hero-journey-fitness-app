package com.example.fitnessapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun FitnessAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80,
            background = Color(0xFFBBA1CE),
            surface = Color(0xFFBBA1CE),
            surfaceBright = Color(0xFFBBA1CE),
            surfaceDim = Color(0xFFBBA1CE),
            surfaceContainer = Color(0xFFBBA1CE),
            surfaceContainerHigh = Color(0xFFBBA1CE),
            surfaceContainerHighest = Color(0xFFBBA1CE),
            surfaceContainerLow = Color(0xFFBBA1CE),
            surfaceContainerLowest = Color(0xFFBBA1CE)
        )
    } else {
        lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40,
            background = Color(0xFFBBA1CE),
            surface = Color(0xFFBBA1CE),
            surfaceBright = Color(0xFFBBA1CE),
            surfaceDim = Color(0xFFBBA1CE),
            surfaceContainer = Color(0xFFBBA1CE),
            surfaceContainerHigh = Color(0xFFBBA1CE),
            surfaceContainerHighest = Color(0xFFBBA1CE),
            surfaceContainerLow = Color(0xFFBBA1CE),
            surfaceContainerLowest = Color(0xFFBBA1CE)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}