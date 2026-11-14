package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenLight,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreenDark,
    onPrimaryContainer = EmeraldContainer,
    secondary = IslamicGoldAccent,
    onSecondary = OnIslamicGold,
    secondaryContainer = Color(0xFF382900),
    onSecondaryContainer = IslamicGoldContainer,
    background = BackgroundDark,
    onBackground = Color(0xFFE2ECE6),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2ECE6),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBCC8C1),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = IslamicGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = OnIslamicGold,
    background = BackgroundLight,
    onBackground = Color(0xFF131D18),
    surface = SurfaceLight,
    onSurface = Color(0xFF131D18),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3E4F47),
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional Islamic Emerald palette by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
