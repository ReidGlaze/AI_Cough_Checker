package com.twintipsolutions.cough.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Always use dark theme to match iOS app
private val DarkColorScheme = darkColorScheme(
    primary = CoughColors.Blue,
    onPrimary = CoughColors.White,
    primaryContainer = CoughColors.BlueOpacity30,
    onPrimaryContainer = CoughColors.White,
    secondary = CoughColors.Purple,
    onSecondary = CoughColors.White,
    secondaryContainer = CoughColors.PurpleOpacity30,
    onSecondaryContainer = CoughColors.White,
    tertiary = CoughColors.Cyan,
    onTertiary = CoughColors.White,
    background = CoughColors.CustomDark4,
    onBackground = CoughColors.White,
    surface = CoughColors.CustomDark3,
    onSurface = CoughColors.White,
    surfaceVariant = CoughColors.CustomDark2,
    onSurfaceVariant = CoughColors.WhiteOpacity80,
    error = CoughColors.Red,
    onError = CoughColors.White,
    errorContainer = CoughColors.RedOpacity30,
    onErrorContainer = CoughColors.White,
    outline = CoughColors.WhiteOpacity20,
    outlineVariant = CoughColors.WhiteOpacity10,
    scrim = CoughColors.BlackOpacity30
)

@Composable
fun CoughTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always dark to match iOS
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Set light status bar icons to false (white icons on dark background)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}