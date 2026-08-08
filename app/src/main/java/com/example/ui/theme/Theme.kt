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

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple700,
    onPrimary = Color.White,
    primaryContainer = LightPurple100,
    onPrimaryContainer = DeepPurple900,
    secondary = CyanDark,
    onSecondary = Color.White,
    secondaryContainer = LightCyan100,
    onSecondaryContainer = DarkNavy,
    tertiary = CyanAccent,
    background = SoftGrayBackground,
    onBackground = DarkNavy,
    surface = SurfaceCard,
    onSurface = DarkNavy,
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = TextGray,
    outline = BorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFCE93D8),
    onPrimary = DeepPurple900,
    primaryContainer = DeepPurple700,
    onPrimaryContainer = Color.White,
    secondary = CyanAccent,
    onSecondary = DarkNavy,
    background = DarkNavy,
    onBackground = Color.White,
    surface = Color(0xFF252542),
    onSurface = Color.White
)

@Composable
fun CustomWearTheme(
    darkTheme: Boolean = false, // Clean light minimalist design by default
    dynamicColor: Boolean = false,
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
        typography = Typography,
        content = content
    )
}
