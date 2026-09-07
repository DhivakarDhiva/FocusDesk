package com.focusdesk.app.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.focusdesk.app.presentation.designsystem.ElevatedTactileSlider
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.AppTheme
import com.focusdesk.domain.model.UserSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    onThemeChange: (AppTheme) -> Unit,
    onDailyGoalChange: (Int) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onResetAllData: () -> Unit,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current
    val listState = rememberLazyListState()

    var showResetDialog by remember { mutableStateOf(false) }

    // Screen Launch Entrance Animations
    val headerAnim = remember { Animatable(0f) }
    val levelAnim = remember { Animatable(0f) }
    val themeAnim = remember { Animatable(0f) }
    val goalAnim = remember { Animatable(0f) }
    val behaviourAnim = remember { Animatable(0f) }
    val resetAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(80)
            levelAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 240f))
        }
        launch {
            delay(150)
            themeAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 230f))
        }
        launch {
            delay(220)
            goalAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 230f))
        }
        launch {
            delay(290)
            behaviourAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 230f))
        }
        launch {
            delay(360)
            resetAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 230f))
        }
    }

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40
        }
    }

    LaunchedEffect(isScrolled) {
        onScrollStateChange(!isScrolled)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeColors.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = "Settings",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = themeColors.textPrimary,
                    modifier = Modifier.graphicsLayer {
                        val p = headerAnim.value
                        translationY = (1f - p) * -35.dp.toPx()
                        scaleX = 0.90f + 0.10f * p
                        scaleY = 0.90f + 0.10f * p
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Level 1 Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = levelAnim.value
                            translationY = (1f - p) * 35.dp.toPx()
                            scaleX = 0.90f + 0.10f * p
                            scaleY = 0.90f + 0.10f * p
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(themeColors.cardBackground)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE9F2E4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Spa,
                                    contentDescription = null,
                                    tint = themeColors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Level ${userSettings.level}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textPrimary
                                )
                                Text(
                                    text = "${userSettings.xp} XP · ${userSettings.dayStreak} day streak 🔥",
                                    fontSize = 13.sp,
                                    color = themeColors.textMuted
                                )
                            }
                        }

                        Text(
                            text = "Achievements",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = themeColors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Section: APPEARANCE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = themeAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.90f + 0.10f * p
                            scaleY = 0.90f + 0.10f * p
                            alpha = p.coerceIn(0f, 1f)
                        }
                ) {
                    SectionLabel(title = "APPEARANCE")
                    Spacer(modifier = Modifier.height(10.dp))

                    // 5 Theme Swatches Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(22.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                            .padding(vertical = 18.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val themes = listOf(
                                AppTheme.DefaultFocus to Color(0xFFC7EC68),
                                AppTheme.PaperStudio to Color(0xFFBA7742),
                                AppTheme.LowTide to Color(0xFF75BEB5),
                                AppTheme.LastLight to Color(0xFFEDA58F),
                                AppTheme.NightBloom to Color(0xFFBCA6E5)
                            )

                            themes.forEach { (theme, dotColor) ->
                                val isSelected = userSettings.appTheme == theme
                                val shortName = when (theme) {
                                    AppTheme.DefaultFocus -> "Default Fo..."
                                    AppTheme.PaperStudio -> "Paper Stud..."
                                    AppTheme.LowTide -> "Low Tide"
                                    AppTheme.LastLight -> "Last Light"
                                    AppTheme.NightBloom -> "Night Bloom"
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            hapticEngine.perform(HapticFeedbackType.Selection)
                                            onThemeChange(theme)
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF2F4EF))
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) themeColors.primary else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(dotColor)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = shortName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) themeColors.textPrimary else themeColors.textMuted
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Section: DAILY GOAL
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = goalAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.90f + 0.10f * p
                            scaleY = 0.90f + 0.10f * p
                            alpha = p.coerceIn(0f, 1f)
                        }
                ) {
                    SectionLabel(title = "DAILY GOAL")
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(22.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                            .padding(18.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeColors.textPrimary
                                )
                                Text(
                                    text = "${userSettings.dailyGoalHours}h",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            ElevatedTactileSlider(
                                value = userSettings.dailyGoalHours.toFloat(),
                                onValueChange = { onDailyGoalChange(it.toInt()) },
                                valueRange = 1f..12f,
                                steps = 10,
                                accentColor = themeColors.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Section: BEHAVIOUR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = behaviourAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.90f + 0.10f * p
                            scaleY = 0.90f + 0.10f * p
                            alpha = p.coerceIn(0f, 1f)
                        }
                ) {
                    SectionLabel(title = "BEHAVIOUR")
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(22.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Row 1: Notifications
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = null,
                                        tint = themeColors.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Notifications",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = themeColors.textPrimary
                                    )
                                }

                                Switch(
                                    checked = userSettings.notificationsEnabled,
                                    onCheckedChange = {
                                        hapticEngine.perform(HapticFeedbackType.Selection)
                                        onNotificationsChange(it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = themeColors.primary,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFD6DAD2)
                                    )
                                )
                            }

                            HorizontalDivider(color = themeColors.divider, thickness = 1.dp)

                            // Row 2: Haptics
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TouchApp,
                                        contentDescription = null,
                                        tint = themeColors.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Haptics",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = themeColors.textPrimary
                                    )
                                }

                                Switch(
                                    checked = userSettings.hapticsEnabled,
                                    onCheckedChange = {
                                        hapticEngine.perform(HapticFeedbackType.Selection)
                                        onHapticsChange(it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = themeColors.primary,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFD6DAD2)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Premium Promotion Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                            }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = themeColors.textMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Focus Desk Premium",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeColors.textPrimary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = themeColors.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Section: ABOUT & RESET
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = resetAnim.value
                            translationY = (1f - p) * 45.dp.toPx()
                            scaleX = 0.90f + 0.10f * p
                            scaleY = 0.90f + 0.10f * p
                            alpha = p.coerceIn(0f, 1f)
                        }
                ) {
                    SectionLabel(title = "ABOUT")
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(22.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Version",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = themeColors.textPrimary
                                )
                                Text(
                                    text = "1.0",
                                    fontSize = 16.sp,
                                    color = themeColors.textMuted
                                )
                            }

                            HorizontalDivider(color = themeColors.divider, thickness = 1.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Privacy Policy",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = themeColors.textPrimary
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    tint = themeColors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reset All Data Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(themeColors.cardBackground)
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                showResetDialog = true
                            }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Reset All Data",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD94D4D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Floating Settings Gear Icon when scrolled (frame 4801)
        AnimatedVisibility(
            visible = isScrolled,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(6.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.08f))
                    .clip(CircleShape)
                    .background(themeColors.cardBackground)
                    .border(1.dp, themeColors.cardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = themeColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // iOS Confirmation Dialog: "Reset All Data?" (screenshot 17)
    if (showResetDialog) {
        BasicAlertDialog(
            onDismissRequest = { showResetDialog = false }
        ) {
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp), ambientColor = Color.Black.copy(alpha = 0.15f))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF7F8F5).copy(alpha = 0.98f))
                    .padding(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reset All Data?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "This will delete all sessions, achievements, and your streak. This cannot be undone.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        color = themeColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(23.dp))
                                .background(Color(0xFFE2E5DF))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Light)
                                    showResetDialog = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = themeColors.textPrimary
                            )
                        }

                        // Reset Button (Red)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(23.dp))
                                .background(Color(0xFFE2E5DF))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Warning)
                                    showResetDialog = false
                                    onResetAllData()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reset",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD94D4D)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    val themeColors = LocalFocusDeskColors.current
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = themeColors.textMuted
    )
}
