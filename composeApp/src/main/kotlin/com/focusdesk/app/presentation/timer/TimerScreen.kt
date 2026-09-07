package com.focusdesk.app.presentation.timer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionStatus

@Composable
fun TimerScreen(
    session: FocusSession,
    isPlayingAudio: Boolean,
    soundVolume: Float,
    onTogglePauseResume: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onEndSession: () -> Unit,
    onToggleAudioMute: () -> Unit,
    onStopAudio: () -> Unit,
    modifier: Modifier = Modifier,
    onMinimize: () -> Unit = {}
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    // Screen Launch Entrance Animations
    val topAnim = remember { Animatable(0f) }
    val dialAnim = remember { Animatable(0f) }
    val middleAnim = remember { Animatable(0f) }
    val controlsAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            topAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(90)
            dialAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.66f, stiffness = 230f))
        }
        launch {
            delay(170)
            middleAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 230f))
        }
        launch {
            delay(250)
            controlsAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 240f))
        }
    }

    // Pulsing animation for "Focusing" dot & timer outline
    val infiniteTransition = rememberInfiniteTransition(label = "timer_ambient")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Animated rotating beam angle along the outline
    val outlineRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outline_rotation"
    )

    // Breathing glow aura for the timer outline
    val outlineGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outline_glow_alpha"
    )

    // Animated breathing ripple expansion on the leading bead
    val tipRippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "tip_ripple_scale"
    )
    val tipRippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "tip_ripple_alpha"
    )

    val progressPercent = (session.progress * 100).toInt().coerceIn(0, 100)
    val isRunning = session.status == SessionStatus.Running

    val activeAccentColor = themeColors.primaryAccent
    val darkBgColor = themeColors.darkCardBackground

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBgColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Minimize button, Mood Pill & Task Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = topAnim.value
                        translationY = (1f - p) * -30.dp.toPx()
                        scaleX = 0.90f + 0.10f * p
                        scaleY = 0.90f + 0.10f * p
                        alpha = p.coerceIn(0f, 1f)
                    }
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Minimize button (minimizes to punch-hole dynamic island)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                                onMinimize()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Minimize to Dynamic Island",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Mood Pill: "🍃 CALM"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Eco,
                            contentDescription = null,
                            tint = activeAccentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = session.mood.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Task Name
                Text(
                    text = session.taskTitle.ifEmpty { "Test" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Center Circular Timer Dial
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .graphicsLayer {
                        val p = dialAnim.value
                        scaleX = 0.85f + 0.15f * p
                        scaleY = 0.85f + 0.15f * p
                        alpha = p.coerceIn(0f, 1f)
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val glowWidth = 14.dp.toPx()
                    val radius = (size.minDimension - glowWidth) / 2
                    val center = this.center

                    // Background Track Ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Active Glowing Progress Arc
                    val sweepAngle = 360f * session.progress.coerceIn(0f, 1f)

                    if (isRunning) {
                        // Ambient Breathing Aura around the outline
                        drawArc(
                            color = activeAccentColor.copy(alpha = outlineGlowAlpha * 0.35f),
                            startAngle = -90f,
                            sweepAngle = if (sweepAngle > 0f) sweepAngle else 360f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = glowWidth, cap = StrokeCap.Round)
                        )

                        // Traveling Shimmer Beam along the circular outline
                        val beamSweep = 55f
                        val beamStartAngle = (outlineRotation - 90f) % 360f
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to Color.Transparent,
                                0.65f to activeAccentColor.copy(alpha = 0.15f),
                                1.0f to Color.White.copy(alpha = 0.85f),
                                center = center
                            ),
                            startAngle = beamStartAngle,
                            sweepAngle = beamSweep,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth * 1.15f, cap = StrokeCap.Round)
                        )
                    }

                    if (sweepAngle > 0f) {
                        // Outer Glow Arc for Active Progress
                        drawArc(
                            color = activeAccentColor.copy(alpha = if (isRunning) outlineGlowAlpha else 0.4f),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round)
                        )

                        // Main Crisp Progress Arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to activeAccentColor.copy(alpha = 0.85f),
                                0.5f to activeAccentColor,
                                1.0f to Color.White,
                                center = center
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Glowing Animated Head Bead at the tip of progress arc
                        val tipAngleRad = ((-90f + sweepAngle) * (PI / 180.0)).toFloat()
                        val tipX = center.x + radius * cos(tipAngleRad)
                        val tipY = center.y + radius * sin(tipAngleRad)
                        val tipOffset = androidx.compose.ui.geometry.Offset(tipX, tipY)

                        if (isRunning) {
                            // Pulsing Ripple Wave around the bead tip
                            drawCircle(
                                color = activeAccentColor.copy(alpha = tipRippleAlpha),
                                radius = 6.dp.toPx() * tipRippleScale,
                                center = tipOffset
                            )
                        }

                        // Outer Bead Halo
                        drawCircle(
                            color = activeAccentColor,
                            radius = 6.dp.toPx(),
                            center = tipOffset
                        )

                        // Inner Bright White Bead Core
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = tipOffset
                        )
                    }
                }

                // Inner Time & Percent Readout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = session.formattedRemainingTime,
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isRunning) "⏸ $progressPercent%" else "▶ $progressPercent%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Status Indicator & Audio Pill & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = middleAnim.value
                        translationY = (1f - p) * 35.dp.toPx()
                        scaleX = 0.92f + 0.08f * p
                        scaleY = 0.92f + 0.08f * p
                        alpha = p.coerceIn(0f, 1f)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "• Focusing" Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isRunning) pulseAlpha else 0.4f))
                    )
                    Text(
                        text = if (isRunning) "Focusing" else "Paused",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ambient Sound Pill: "||| Birdsong · 65%"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = activeAccentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${session.soundscape.title} · ${(soundVolume * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.size(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speaker / Mute Button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Light)
                                    onToggleAudioMute()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Stop Square Button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE25B81).copy(alpha = 0.25f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Light)
                                    onStopAudio()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = Color(0xFFF28DAA),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Session Progress Card with 15 Segmented Dashes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Session Progress",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            val elapsedMinutes = ((session.targetDurationSeconds - session.remainingSeconds) / 60).toInt()
                            Text(
                                text = "${elapsedMinutes}m elapsed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 15 Segmented Dashes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val totalSegments = 15
                            val filledSegments = (session.progress * totalSegments).toInt().coerceIn(0, totalSegments)
                            repeat(totalSegments) { index ->
                                val isFilled = index < filledSegments || (index == 0 && session.progress > 0.01f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(7.dp)
                                        .clip(RoundedCornerShape(3.5.dp))
                                        .background(
                                            if (isFilled) activeAccentColor else Color.White.copy(alpha = 0.1f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Controls (End, Pause/Resume, +5 min)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = controlsAnim.value
                        translationY = (1f - p) * 40.dp.toPx()
                        scaleX = 0.88f + 0.12f * p
                        scaleY = 0.88f + 0.12f * p
                        alpha = p.coerceIn(0f, 1f)
                    }
                    .padding(bottom = 90.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: End Button (✕)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                onEndSession()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "End",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "End",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Center: Big Illuminated Pause/Resume Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = activeAccentColor.copy(alpha = 0.5f),
                                spotColor = activeAccentColor.copy(alpha = 0.6f)
                            )
                            .clip(CircleShape)
                            .background(activeAccentColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                onTogglePauseResume()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            tint = Color(0xFF142927),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isRunning) "Pause" else "Resume",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Right: +5 min Button (+)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                                onAddFiveMinutes()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "+5 min",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "+5 min",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
