package com.focusdesk.app.presentation.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.focusdesk.core.platform.AmbientAudioEngine
import com.focusdesk.core.platform.HapticEngine
import com.focusdesk.domain.model.AppThemeMode

val LocalHapticEngine = compositionLocalOf<HapticEngine> {
    error("LocalHapticEngine not provided")
}

val LocalAudioPlayer = compositionLocalOf<AmbientAudioEngine> {
    error("LocalAudioPlayer not provided")
}

@Composable
fun FocusDeskTheme(
    themeMode: AppThemeMode = AppThemeMode.System,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    
    val isDark = when (themeMode) {
        AppThemeMode.System -> systemDark
        AppThemeMode.Dark, AppThemeMode.OledBlack -> true
        AppThemeMode.Light -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == AppThemeMode.OledBlack -> {
            DarkColorScheme.copy(
                background = Color.Black,
                surface = Color(0xFF070709),
                surfaceVariant = Color(0xFF121216)
            )
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
        }
    }

    val hapticEngine = HapticEngine(context)
    val audioEngine = AmbientAudioEngine(context)

    CompositionLocalProvider(
        LocalHapticEngine provides hapticEngine,
        LocalAudioPlayer provides audioEngine
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FocusTypography,
            shapes = FocusShapes,
            content = content
        )
    }
}
