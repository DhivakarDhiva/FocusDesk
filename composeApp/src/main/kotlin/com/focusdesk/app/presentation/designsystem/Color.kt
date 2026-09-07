package com.focusdesk.app.presentation.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.focusdesk.domain.model.AppTheme

@Immutable
data class FocusDeskThemeColors(
    val theme: AppTheme,
    val background: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val primary: Color,
    val primaryAccent: Color,
    val onPrimaryAccent: Color,
    val darkCardBackground: Color,
    val darkCardGlow: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val navBarBackground: Color,
    val navBarSelectedPill: Color,
    val divider: Color
)

// 1. Default Focus Theme (Forest Olive & Vibrant Lime)
val DefaultFocusColors = FocusDeskThemeColors(
    theme = AppTheme.DefaultFocus,
    background = Color(0xFFF7F6F1),
    cardBackground = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFEBEBE4),
    primary = Color(0xFF4A683F),
    primaryAccent = Color(0xFFD4F384),
    onPrimaryAccent = Color(0xFF1E2D1A),
    darkCardBackground = Color(0xFF16251C),
    darkCardGlow = Color(0x33A3D944),
    textPrimary = Color(0xFF1A2219),
    textSecondary = Color(0xFF6B7569),
    textMuted = Color(0xFF9BA499),
    navBarBackground = Color(0xFFFFFFFF),
    navBarSelectedPill = Color(0xFFE3ECD8),
    divider = Color(0xFFECECE6)
)

// 2. Paper Studio Theme (Warm Craft Paper & Terracotta)
val PaperStudioColors = FocusDeskThemeColors(
    theme = AppTheme.PaperStudio,
    background = Color(0xFFF5EBE1),
    cardBackground = Color(0xFFFFFDF9),
    cardBorder = Color(0xFFEDE3D7),
    primary = Color(0xFF8E532B),
    primaryAccent = Color(0xFFE5A169),
    onPrimaryAccent = Color(0xFF2C190F),
    darkCardBackground = Color(0xFF291D16),
    darkCardGlow = Color(0x33D98236),
    textPrimary = Color(0xFF261D17),
    textSecondary = Color(0xFF7A6D63),
    textMuted = Color(0xFFA5988F),
    navBarBackground = Color(0xFFFFFDF9),
    navBarSelectedPill = Color(0xFFECE0D2),
    divider = Color(0xFFEAE0D4)
)

// 3. Low Tide Theme (Sage Mint & Deep Sea Teal)
val LowTideColors = FocusDeskThemeColors(
    theme = AppTheme.LowTide,
    background = Color(0xFFE9F1ED),
    cardBackground = Color(0xFFF8FCFA),
    cardBorder = Color(0xFFDFE9E4),
    primary = Color(0xFF2B5C56),
    primaryAccent = Color(0xFF9EE0D4),
    onPrimaryAccent = Color(0xFF102826),
    darkCardBackground = Color(0xFF142927),
    darkCardGlow = Color(0x333EA296),
    textPrimary = Color(0xFF152523),
    textSecondary = Color(0xFF667875),
    textMuted = Color(0xFF92A3A0),
    navBarBackground = Color(0xFFF8FCFA),
    navBarSelectedPill = Color(0xFFD6E8E3),
    divider = Color(0xFFDEE9E5)
)

// 4. Last Light Theme (Peach Blush & Terracotta Coral)
val LastLightColors = FocusDeskThemeColors(
    theme = AppTheme.LastLight,
    background = Color(0xFFF8EFE9),
    cardBackground = Color(0xFFFFFBF9),
    cardBorder = Color(0xFFF0E4DE),
    primary = Color(0xFFB55739),
    primaryAccent = Color(0xFFF5AC95),
    onPrimaryAccent = Color(0xFF33160D),
    darkCardBackground = Color(0xFF2C1D1A),
    darkCardGlow = Color(0x33E07250),
    textPrimary = Color(0xFF281C18),
    textSecondary = Color(0xFF7C6C66),
    textMuted = Color(0xFFA69791),
    navBarBackground = Color(0xFFFFFBF9),
    navBarSelectedPill = Color(0xFFEEDCD4),
    divider = Color(0xFFEFE2DC)
)

// 5. Night Bloom Theme (Soft Lavender & Deep Violet)
val NightBloomColors = FocusDeskThemeColors(
    theme = AppTheme.NightBloom,
    background = Color(0xFFF1EEF6),
    cardBackground = Color(0xFFFBFAFD),
    cardBorder = Color(0xFFE7E2EF),
    primary = Color(0xFF5D4786),
    primaryAccent = Color(0xFFC0A6EB),
    onPrimaryAccent = Color(0xFF1D142E),
    darkCardBackground = Color(0xFF211A2C),
    darkCardGlow = Color(0x338965C9),
    textPrimary = Color(0xFF1F1929),
    textSecondary = Color(0xFF726A7E),
    textMuted = Color(0xFF9E95A8),
    navBarBackground = Color(0xFFFBFAFD),
    navBarSelectedPill = Color(0xFFE3DCED),
    divider = Color(0xFFE8E2F0)
)

fun getThemeColors(theme: AppTheme): FocusDeskThemeColors = when (theme) {
    AppTheme.DefaultFocus -> DefaultFocusColors
    AppTheme.PaperStudio -> PaperStudioColors
    AppTheme.LowTide -> LowTideColors
    AppTheme.LastLight -> LastLightColors
    AppTheme.NightBloom -> NightBloomColors
}

// Backward compatibility bindings for existing references
val InkCanvas = DefaultFocusColors.background
val LumenAction = DefaultFocusColors.primaryAccent
val EvergreenHero = DefaultFocusColors.darkCardBackground
val WarmStreak = Color(0xFFF2B68D)
val ResetBreak = Color(0xFF9DD9D0)
val LightCanvas = DefaultFocusColors.background
val LightSurface = DefaultFocusColors.cardBackground
val LightTextPrimary = DefaultFocusColors.textPrimary
val LightTextSecondary = DefaultFocusColors.textSecondary
val PriorityUrgent = Color(0xFFE55757)
