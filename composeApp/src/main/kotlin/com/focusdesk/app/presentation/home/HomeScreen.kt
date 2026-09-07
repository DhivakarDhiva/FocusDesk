package com.focusdesk.app.presentation.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.FocusSession
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    focusedMinutesToday: Int,
    completedSessionsToday: Int,
    dayStreak: Int,
    dailyGoalHours: Int,
    recentSessions: List<FocusSession>,
    onQuickStart: (Int, String) -> Unit,
    onStartFocusSessionClick: () -> Unit,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current
    val listState = rememberLazyListState()

    var isButtonElaborated by remember { mutableStateOf(true) }
    var buttonInteractionTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Auto-compress "Start focusing" button after 5 seconds of idle
    LaunchedEffect(isButtonElaborated, buttonInteractionTimestamp) {
        if (isButtonElaborated) {
            delay(5000L)
            isButtonElaborated = false
        }
    }

    // Magical Entrance Rearrangement Animations (collapsing & rearranging like magic)
    val headerAnim = remember { Animatable(0f) }
    val card1Anim = remember { Animatable(0f) }
    val card2Anim = remember { Animatable(0f) }
    val card3Anim = remember { Animatable(0f) }
    val dailyGoalAnim = remember { Animatable(0f) }
    val quickStartAnim = remember { Animatable(0f) }
    val sessionsAnim = remember { Animatable(0f) }
    val ctaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(80L)
            card1Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(160L)
            card2Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.60f, stiffness = 240f))
        }
        launch {
            delay(240L)
            card3Anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(320L)
            dailyGoalAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 200f))
        }
        launch {
            delay(400L)
            quickStartAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 240f))
        }
        launch {
            delay(480L)
            sessionsAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.70f, stiffness = 220f))
        }
        launch {
            delay(560L)
            ctaAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.62f, stiffness = 260f))
        }
    }

    val ambientTransition = rememberInfiniteTransition(label = "home_ambient")
    val flameWiggle by ambientTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_wiggle"
    )
    val flameScale by ambientTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )
    val bokehShift by ambientTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bokeh_shift"
    )

    // Detect scroll to hide/show bottom bar
    val isScrollingDown by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40
        }
    }

    LaunchedEffect(isScrollingDown) {
        onScrollStateChange(!isScrollingDown)
    }

    val goalPercentage = if (dailyGoalHours > 0) {
        ((focusedMinutesToday.toFloat() / (dailyGoalHours * 60f)) * 100).toInt().coerceIn(0, 100)
    } else 0

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

                // Greeting and Header Column with Animated Entrance
                Column(
                    modifier = Modifier.graphicsLayer {
                        val p = headerAnim.value
                        translationY = (1f - p) * -40.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                ) {
                    // Greeting Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = null,
                            tint = themeColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Good morning",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = themeColors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Ready to focus?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = themeColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "You've focused ${focusedMinutesToday}m today.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = themeColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Stat Cards in a row (Rearranging like magic from collapsed cluster)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Focused Time (Unfolds from center with rotation)
                    StatCardItem(
                        icon = Icons.Outlined.Timer,
                        iconTint = themeColors.primary,
                        iconBg = themeColors.navBarSelectedPill,
                        value = "${focusedMinutesToday}m",
                        label = "Focused",
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                val p = card1Anim.value
                                translationX = (1f - p) * 90.dp.toPx()
                                translationY = (1f - p) * 35.dp.toPx()
                                scaleX = 0.35f + (0.65f * p)
                                scaleY = 0.35f + (0.65f * p)
                                rotationZ = (1f - p) * -16f
                                alpha = p.coerceIn(0f, 1f)
                            }
                    )

                    // Card 2: Sessions Count (Pops with elastic scale bounce from center)
                    StatCardItem(
                        icon = Icons.Outlined.Check,
                        iconTint = themeColors.primary,
                        iconBg = themeColors.navBarSelectedPill,
                        value = "$completedSessionsToday",
                        label = if (completedSessionsToday == 1) "Session" else "Sessions",
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                val p = card2Anim.value
                                translationY = (1f - p) * 45.dp.toPx()
                                scaleX = 0.35f + (0.65f * p)
                                scaleY = 0.35f + (0.65f * p)
                                alpha = p.coerceIn(0f, 1f)
                            }
                    )

                    // Card 3: Day Streak (Unfolds from center with warm flame flicker)
                    StatCardItem(
                        icon = Icons.Outlined.LocalFireDepartment,
                        iconTint = Color(0xFFD6743A),
                        iconBg = Color(0xFFFDECE3),
                        value = "$dayStreak",
                        label = "Day streak",
                        iconModifier = Modifier.graphicsLayer {
                            rotationZ = flameWiggle
                            scaleX = flameScale
                            scaleY = flameScale
                        },
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                val p = card3Anim.value
                                translationX = (1f - p) * -90.dp.toPx()
                                translationY = (1f - p) * 35.dp.toPx()
                                scaleX = 0.35f + (0.65f * p)
                                scaleY = 0.35f + (0.65f * p)
                                rotationZ = (1f - p) * 16f
                                alpha = p.coerceIn(0f, 1f)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // DAILY GOAL Banner Card (Unfolds vertically with drifting bokeh orbs)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .graphicsLayer {
                            val p = dailyGoalAnim.value
                            scaleY = 0.35f + (0.65f * p)
                            translationY = (1f - p) * 40.dp.toPx()
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .shadow(4.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.04f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    themeColors.darkCardBackground,
                                    Color(0xFF283B2E)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Subtle background drifting bokeh overlay circles
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.CenterEnd)
                            .graphicsLayer {
                                translationX = bokehShift
                                translationY = -bokehShift * 0.5f
                            }
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.045f))
                    )
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.BottomEnd)
                            .graphicsLayer {
                                translationX = -bokehShift
                                translationY = bokehShift * 0.5f
                            }
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.035f))
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DAILY GOAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                            Text(
                                text = "$goalPercentage%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${focusedMinutesToday}m",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = " of ${dailyGoalHours}h 0m",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.65f),
                                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                            )
                        }

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            val fillFraction = (focusedMinutesToday.toFloat() / (dailyGoalHours * 60f)).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (fillFraction > 0f) fillFraction else 0.01f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(themeColors.primaryAccent)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // QUICK START Section (Animated Entrance)
                Column(
                    modifier = Modifier.graphicsLayer {
                        val p = quickStartAnim.value
                        translationY = (1f - p) * 35.dp.toPx()
                        scaleX = 0.85f + (0.15f * p)
                        scaleY = 0.85f + (0.15f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                ) {
                    Text(
                        text = "QUICK START",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = themeColors.textMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Quick Start Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickStartCardItem(
                            minutes = 25,
                            title = "25 min",
                            subtitle = "Pomodoro",
                            onClick = {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                onQuickStart(25, "Pomodoro")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        QuickStartCardItem(
                            minutes = 45,
                            title = "45 min",
                            subtitle = "Deep Work",
                            onClick = {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                onQuickStart(45, "Deep Work")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        QuickStartCardItem(
                            minutes = 90,
                            title = "90 min",
                            subtitle = "Flow",
                            onClick = {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                onQuickStart(90, "Flow")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Completed Sessions Header or Empty State
                if (recentSessions.isNotEmpty()) {
                    Text(
                        text = "TODAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = themeColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (recentSessions.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE9EFE6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = themeColors.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No completed sessions yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start your first session above",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = themeColors.textMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(120.dp))
                }
            } else {
                items(recentSessions) { session ->
                    CompletedSessionCard(session = session)
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

        // Dynamic "Start focusing" CTA Button (compressing to circular FAB after 5s)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
                .graphicsLayer {
                    val p = ctaAnim.value
                    scaleX = p
                    scaleY = p
                    rotationZ = (1f - p) * -25f
                    translationY = (1f - p) * 50.dp.toPx()
                    alpha = p.coerceIn(0f, 1f)
                }
        ) {
            Box(
                modifier = Modifier
                    .height(54.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(27.dp),
                        ambientColor = themeColors.primaryAccent.copy(alpha = 0.35f),
                        spotColor = themeColors.primaryAccent.copy(alpha = 0.45f)
                    )
                    .clip(RoundedCornerShape(27.dp))
                    .background(themeColors.primaryAccent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        hapticEngine.perform(HapticFeedbackType.Medium)
                        if (!isButtonElaborated) {
                            // If currently compressed: elaborate and show text, start 5s countdown
                            isButtonElaborated = true
                            buttonInteractionTimestamp = System.currentTimeMillis()
                        } else {
                            // If already elaborated: open session launcher
                            onStartFocusSessionClick()
                        }
                    }
                    .padding(horizontal = if (isButtonElaborated) 20.dp else 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start focusing",
                        tint = themeColors.onPrimaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    AnimatedVisibility(
                        visible = isButtonElaborated,
                        enter = expandHorizontally(
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 350f)
                        ) + fadeIn(),
                        exit = shrinkHorizontally(
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 350f)
                        ) + fadeOut()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start focusing",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.onPrimaryAccent,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCardItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    value: String,
    label: String,
    iconModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .height(115.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(themeColors.cardBackground)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = iconModifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = themeColors.textMuted
                )
            }
        }
    }
}

@Composable
private fun QuickStartCardItem(
    minutes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .height(115.dp)
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
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(themeColors.navBarSelectedPill),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = themeColors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = themeColors.textMuted
                )
            }
        }
    }
}

@Composable
private fun CompletedSessionCard(
    session: FocusSession,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(themeColors.cardBackground)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
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
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeColors.navBarSelectedPill),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = themeColors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = session.taskTitle.ifEmpty { "Test" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeColors.textPrimary
                    )
                    Text(
                        text = "11:39 AM",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = themeColors.textMuted
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                val durationMin = ((session.targetDurationSeconds - session.remainingSeconds).coerceAtLeast(60L) / 60).toInt()
                Text(
                    text = "${durationMin}m",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary)
                    )
                    Text(
                        text = "Done",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColors.primary
                    )
                }
            }
        }
    }
}
