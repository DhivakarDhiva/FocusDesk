package com.focusdesk.app.presentation.focus

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FocusScreen(
    onQuickStart: (Int, String) -> Unit,
    onOpenCustomSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    // Magical Entrance Rearrangement Animations on screen launch
    val headerAnim = remember { Animatable(0f) }
    val bannerAnim = remember { Animatable(0f) }
    val quickStartAnim = remember { Animatable(0f) }
    val option1Anim = remember { Animatable(0f) }
    val option2Anim = remember { Animatable(0f) }
    val option3Anim = remember { Animatable(0f) }
    val ctaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(80L)
            bannerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(160L)
            quickStartAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.70f, stiffness = 240f))
        }
        launch {
            delay(220L)
            option1Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(280L)
            option2Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(340L)
            option3Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(400L)
            ctaAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.62f, stiffness = 260f))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeColors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = "Focus",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = themeColors.textPrimary,
                    modifier = Modifier.graphicsLayer {
                        val p = headerAnim.value
                        translationY = (1f - p) * -35.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Dark Session Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .graphicsLayer {
                            val p = bannerAnim.value
                            translationY = (1f - p) * 45.dp.toPx()
                            scaleX = 0.88f + (0.12f * p)
                            scaleY = 0.88f + (0.12f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    themeColors.darkCardBackground,
                                    Color(0xFF1E2E25)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    // Circular timer dial watermark background
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .border(8.dp, Color.White.copy(alpha = 0.04f), CircleShape)
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Badge: "☾ No active session"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bedtime,
                                contentDescription = null,
                                tint = themeColors.primaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "No active session",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Text(
                            text = "What will you\nfocus on today?",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // QUICK START Header
                Text(
                    text = "QUICK START",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = themeColors.textMuted,
                    modifier = Modifier.graphicsLayer {
                        val p = quickStartAnim.value
                        translationY = (1f - p) * 20.dp.toPx()
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Row Card 1: Pomodoro
                FocusOptionRow(
                    icon = Icons.Outlined.Timer,
                    iconTint = themeColors.primary,
                    iconBg = themeColors.navBarSelectedPill,
                    title = "Pomodoro",
                    subtitle = "Classic sprint",
                    durationBadge = "25 min",
                    badgeBg = Color(0xFFF1F6EC),
                    badgeTextColor = themeColors.primary,
                    onClick = {
                        hapticEngine.perform(HapticFeedbackType.Medium)
                        onQuickStart(25, "Pomodoro")
                    },
                    modifier = Modifier.graphicsLayer {
                        val p = option1Anim.value
                        translationY = (1f - p) * 35.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row Card 2: Deep Work
                FocusOptionRow(
                    icon = Icons.Outlined.Psychology,
                    iconTint = themeColors.primary,
                    iconBg = themeColors.navBarSelectedPill,
                    title = "Deep Work",
                    subtitle = "Focused effort",
                    durationBadge = "45 min",
                    badgeBg = Color(0xFFF1F6EC),
                    badgeTextColor = themeColors.primary,
                    onClick = {
                        hapticEngine.perform(HapticFeedbackType.Medium)
                        onQuickStart(45, "Deep Work")
                    },
                    modifier = Modifier.graphicsLayer {
                        val p = option2Anim.value
                        translationY = (1f - p) * 35.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row Card 3: Flow State
                FocusOptionRow(
                    icon = Icons.Outlined.LocalFireDepartment,
                    iconTint = Color(0xFFD6743A),
                    iconBg = Color(0xFFFDECE3),
                    title = "Flow State",
                    subtitle = "Peak performance",
                    durationBadge = "90 min",
                    badgeBg = Color(0xFFFDF0E9),
                    badgeTextColor = Color(0xFFD6743A),
                    onClick = {
                        hapticEngine.perform(HapticFeedbackType.Medium)
                        onQuickStart(90, "Flow State")
                    },
                    modifier = Modifier.graphicsLayer {
                        val p = option3Anim.value
                        translationY = (1f - p) * 35.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Session Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .graphicsLayer {
                            val p = ctaAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.88f + (0.12f * p)
                            scaleY = 0.88f + (0.12f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(themeColors.primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Medium)
                            onOpenCustomSession()
                        }
                        .padding(horizontal = 20.dp),
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
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Custom Session",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
private fun FocusOptionRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    durationBadge: String,
    badgeBg: Color,
    badgeTextColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(themeColors.cardBackground)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = themeColors.textMuted
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = durationBadge,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }

                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFCCD4C9),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
