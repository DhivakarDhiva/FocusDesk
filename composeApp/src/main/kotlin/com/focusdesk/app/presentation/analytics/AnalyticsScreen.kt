package com.focusdesk.app.presentation.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.ProductivityStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(
    stats: ProductivityStats,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current
    val listState = rememberLazyListState()

    var selectedSegmentIndex by remember { mutableIntStateOf(1) } // 0 = Day, 1 = Week, 2 = Month
    val segments = listOf("Day", "Week", "Month")
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    // Magical Entrance Rearrangement Animations on screen launch
    val headerAnim = remember { Animatable(0f) }
    val bannerAnim = remember { Animatable(0f) }
    val segmentsAnim = remember { Animatable(0f) }
    val chartCardAnim = remember { Animatable(0f) }
    val statBoxesAnim = remember { Animatable(0f) }
    val activityAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(80L)
            bannerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(150L)
            segmentsAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.70f, stiffness = 240f))
        }
        launch {
            delay(220L)
            chartCardAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(290L)
            statBoxesAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(360L)
            activityAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.62f, stiffness = 240f))
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

    val productivityScore = if (stats.totalFocusMinutesToday > 0) {
        ((stats.totalFocusMinutesToday / 60f / 4f) * 100).toInt().coerceIn(0, 100)
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

                // Screen Title
                Text(
                    text = "Insights",
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

                // 1. PRODUCTIVITY Gradient Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(154.dp)
                        .graphicsLayer {
                            val p = bannerAnim.value
                            translationY = (1f - p) * 45.dp.toPx()
                            scaleX = 0.88f + (0.12f * p)
                            scaleY = 0.88f + (0.12f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    themeColors.darkCardBackground,
                                    Color(0xFF283B2F)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = "PRODUCTIVITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$productivityScore",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = " /100",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Text(
                                text = if (productivityScore > 0) "— Good Progress" else "— Getting Started",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = themeColors.primaryAccent
                            )
                        }

                        // Right Stats Column
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${stats.totalFocusMinutesToday}m total",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "$productivityScore% done",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFE59263),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${stats.currentDayStreak}d streak",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Day | Week | Month Segmented Control
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .graphicsLayer {
                            val p = segmentsAnim.value
                            translationY = (1f - p) * 30.dp.toPx()
                            scaleX = 0.92f + (0.08f * p)
                            scaleY = 0.92f + (0.08f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFE8ECE5))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        segments.forEachIndexed { idx, title ->
                            val isSelected = selectedSegmentIndex == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .shadow(
                                        elevation = if (isSelected) 2.dp else 0.dp,
                                        shape = RoundedCornerShape(19.dp),
                                        ambientColor = Color.Black.copy(alpha = 0.05f),
                                        spotColor = Color.Black.copy(alpha = 0.08f)
                                    )
                                    .clip(RoundedCornerShape(19.dp))
                                    .background(if (isSelected) themeColors.cardBackground else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        hapticEngine.perform(HapticFeedbackType.Selection)
                                        selectedSegmentIndex = idx
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) themeColors.textPrimary else themeColors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. FOCUSED TIME Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .graphicsLayer {
                            val p = chartCardAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.88f + (0.12f * p)
                            scaleY = 0.88f + (0.12f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(themeColors.cardBackground)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "FOCUSED TIME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = themeColors.textMuted
                        )

                        // Pill Banner: "• Sunday · Aug 30 · No sessions"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF3F6F1))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(themeColors.primary)
                                )
                                Text(
                                    text = "Sunday · Aug 30 · ${if (stats.totalFocusMinutesToday > 0) "${stats.totalFocusMinutesToday}m focused" else "No sessions"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeColors.textPrimary
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color(0xFFB5C0B2),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Bar Chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            daysOfWeek.forEachIndexed { i, day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    val isToday = i == 6
                                    val barFraction = if (isToday && stats.totalFocusMinutesToday > 0) {
                                        (stats.totalFocusMinutesToday / 120f).coerceIn(0.15f, 1f)
                                    } else 0f

                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .height(80.dp * (if (barFraction > 0f) barFraction else 0.05f))
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (barFraction > 0f) themeColors.primary else Color(0xFFEBECE7)
                                            )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = themeColors.textMuted
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. FOCUS TREND Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(themeColors.cardBackground)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "FOCUS TREND",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = themeColors.textMuted
                        )

                        // Line Chart Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            val lineColor = themeColors.primary
                            val baselineY = size.height * 0.75f
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, baselineY),
                                end = Offset(size.width, baselineY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Days Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = themeColors.textMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. 4 Stat Boxes (Longest, Average, Sessions, Best day)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = statBoxesAnim.value
                            translationY = (1f - p) * 35.dp.toPx()
                            scaleX = 0.90f + (0.10f * p)
                            scaleY = 0.90f + (0.10f * p)
                            alpha = p.coerceIn(0f, 1f)
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InsightsSmallStatBox(
                        icon = Icons.Outlined.Timer,
                        value = "${stats.totalFocusMinutesToday}m",
                        label = "Longest",
                        modifier = Modifier.weight(1f)
                    )
                    InsightsSmallStatBox(
                        icon = Icons.Outlined.BarChart,
                        value = "${stats.totalFocusMinutesToday}m",
                        label = "Average",
                        modifier = Modifier.weight(1f)
                    )
                    InsightsSmallStatBox(
                        icon = Icons.Outlined.CalendarToday,
                        value = "${stats.recentSessions.size}",
                        label = "Sessions",
                        modifier = Modifier.weight(1f)
                    )
                    InsightsSmallStatBox(
                        icon = Icons.Outlined.Star,
                        value = if (stats.recentSessions.isNotEmpty()) "Sun" else "—",
                        label = "Best day",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 6. ACTIVITY · 4 WEEKS (Heatmap Grid)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = activityAnim.value
                            translationY = (1f - p) * 40.dp.toPx()
                            scaleX = 0.88f + (0.12f * p)
                            scaleY = 0.88f + (0.12f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(themeColors.cardBackground)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ACTIVITY · 4 WEEKS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = themeColors.textMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Days Headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            daysOfWeek.forEach { day ->
                                Box(
                                    modifier = Modifier.size(34.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = themeColors.textMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 4 Rows x 7 Cols Matrix
                        repeat(4) { rowIndex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                repeat(7) { colIndex ->
                                    val isCurrentActiveDay = rowIndex == 3 && colIndex == 6 && stats.totalFocusMinutesToday > 0
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isCurrentActiveDay) themeColors.primary else Color(0xFFEBECE8)
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Legend: Less 🟩 🟩 🟩 🟩 More
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Less",
                                fontSize = 11.sp,
                                color = themeColors.textMuted
                            )
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFEBECE8)))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFD4E5C7)))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFA1D18A)))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(themeColors.primary))
                            Text(
                                text = "More",
                                fontSize = 11.sp,
                                color = themeColors.textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 7. PROGRESS Card (Level 1, XP, streak, and progress bar)
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
                        Text(
                            text = "PROGRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = themeColors.textMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE9F2E4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Spa,
                                        contentDescription = null,
                                        tint = themeColors.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Level 1",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.textPrimary
                                    )
                                    val currentXp = if (stats.recentSessions.isNotEmpty()) stats.recentSessions.size * 6 else 0
                                    Text(
                                        text = "$currentXp XP total",
                                        fontSize = 13.sp,
                                        color = themeColors.textMuted
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color(0xFFE59263),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${stats.currentDayStreak}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.textPrimary
                                    )
                                    Text(
                                        text = " current",
                                        fontSize = 11.sp,
                                        color = themeColors.textMuted
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFE5C058),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${stats.currentDayStreak}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.textPrimary
                                    )
                                    Text(
                                        text = " best",
                                        fontSize = 11.sp,
                                        color = themeColors.textMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress to Level 2",
                                fontSize = 12.sp,
                                color = themeColors.textMuted
                            )
                            val levelProgress = if (stats.recentSessions.isNotEmpty()) (stats.recentSessions.size * 6).coerceIn(0, 100) else 0
                            Text(
                                text = "$levelProgress%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = themeColors.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(Color(0xFFE8ECE5))
                        ) {
                            val levelProgress = if (stats.recentSessions.isNotEmpty()) (stats.recentSessions.size * 0.06f).coerceIn(0f, 1f) else 0f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (levelProgress > 0f) levelProgress else 0.01f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(themeColors.primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Floating Quick Icon at Bottom Left when scrolled (frame 2551)
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
                    imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                    contentDescription = "Insights",
                    tint = themeColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun InsightsSmallStatBox(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .height(96.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
            .clip(RoundedCornerShape(18.dp))
            .background(themeColors.cardBackground)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F6EC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeColors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = themeColors.textMuted
                )
            }
        }
    }
}
