package com.focusdesk.app.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.focusdesk.core.platform.AmbientAudioEngine
import com.focusdesk.core.platform.HapticEngine
import com.focusdesk.domain.model.AppTheme

val LocalFocusDeskColors = compositionLocalOf<FocusDeskThemeColors> {
    DefaultFocusColors
}

val LocalHapticEngine = compositionLocalOf<HapticEngine> {
    error("LocalHapticEngine not provided")
}

val LocalAudioPlayer = compositionLocalOf<AmbientAudioEngine> {
    error("LocalAudioPlayer not provided")
}

@Composable
fun FocusDeskTheme(
    appTheme: AppTheme = AppTheme.DefaultFocus,
    isDarkOverride: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentThemeColors = getThemeColors(appTheme)

    val materialColors = lightColorScheme(
        primary = currentThemeColors.primary,
        onPrimary = Color.White,
        primaryContainer = currentThemeColors.primaryAccent,
        onPrimaryContainer = currentThemeColors.onPrimaryAccent,
        secondary = currentThemeColors.primary,
        background = if (isDarkOverride) currentThemeColors.darkCardBackground else currentThemeColors.background,
        onBackground = if (isDarkOverride) Color.White else currentThemeColors.textPrimary,
        surface = if (isDarkOverride) currentThemeColors.darkCardBackground else currentThemeColors.cardBackground,
        onSurface = if (isDarkOverride) Color.White else currentThemeColors.textPrimary,
        surfaceVariant = currentThemeColors.cardBorder,
        outline = currentThemeColors.cardBorder
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDarkOverride
                    isAppearanceLightNavigationBars = !isDarkOverride
                }
            }
        }
    }

    val hapticEngine = remember(context) { HapticEngine(context) }
    val audioEngine = remember(context) {
        org.koin.core.context.GlobalContext.getOrNull()?.get<AmbientAudioEngine>() ?: AmbientAudioEngine(context)
    }

    CompositionLocalProvider(
        LocalFocusDeskColors provides currentThemeColors,
        LocalHapticEngine provides hapticEngine,
        LocalAudioPlayer provides audioEngine
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = FocusTypography,
            shapes = FocusShapes,
            content = content
        )
    }
}
