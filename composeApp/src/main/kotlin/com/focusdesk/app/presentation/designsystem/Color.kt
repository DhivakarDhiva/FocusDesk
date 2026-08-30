package com.focusdesk.app.presentation.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Brand Palette
val FocusPrimary = Color(0xFF6366F1)
val FocusPrimaryDark = Color(0xFF4F46E5)
val FocusSecondary = Color(0xFF06B6D4)
val FocusTertiary = Color(0xFF10B981)

val FocusBreak = Color(0xFF10B981)
val FocusLongBreak = Color(0xFF3B82F6)
val FocusWork = Color(0xFF6366F1)

// Dark Theme Surfaces
val DarkBackground = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceVariant = Color(0xFF1F2937)
val DarkBorder = Color(0xFF374151)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)

// Light Theme Surfaces
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)

// Status / Priority Colors
val PriorityUrgent = Color(0xFFEF4444)
val PriorityHigh = Color(0xFFF97316)
val PriorityMedium = Color(0xFFEAB308)
val PriorityLow = Color(0xFF3B82F6)

// Gradient Brushes
val WorkGradient = Brush.linearGradient(
    listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
)

val ShortBreakGradient = Brush.linearGradient(
    listOf(Color(0xFF10B981), Color(0xFF06B6D4))
)

val LongBreakGradient = Brush.linearGradient(
    listOf(Color(0xFF3B82F6), Color(0xFF6366F1))
)

val GlassBorderGradient = Brush.linearGradient(
    listOf(Color(0x33FFFFFF), Color(0x0DFFFFFF))
)

val DarkColorScheme = darkColorScheme(
    primary = FocusPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = FocusSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = FocusTertiary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = PriorityUrgent,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = FocusPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = FocusSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),
    tertiary = FocusTertiary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = PriorityUrgent,
    onError = Color.White
)
