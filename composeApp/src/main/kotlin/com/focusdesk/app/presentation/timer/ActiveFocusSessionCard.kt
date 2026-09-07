package com.focusdesk.app.presentation.timer

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.app.presentation.sounds.EqualizerSoundwave
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionStatus

@Composable
fun ActiveFocusSessionCard(
    session: FocusSession,
    isPlayingAudio: Boolean,
    onTogglePauseResume: () -> Unit,
    onToggleAudioMute: () -> Unit,
    onMaximize: () -> Unit,
    onEndSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    val isRunning = session.status == SessionStatus.Running
    val progress = session.progress

    // Ambient breathing pulse for running status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_card")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1116).copy(alpha = 0.98f))
            .border(
                width = 1.dp,
                color = themeColors.primaryAccent.copy(alpha = if (isRunning) 0.35f else 0.15f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onMaximize
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Status Badge + Task Name + Maximize Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRunning)
                                    themeColors.primaryAccent.copy(alpha = pulseAlpha)
                                else
                                    Color(0xFFE5A43B)
                            )
                    )
                    Text(
                        text = if (isRunning) "FOCUSING" else "PAUSED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isRunning) themeColors.primaryAccent else Color(0xFFE5A43B)
                    )
                    Text(
                        text = "• ${session.taskTitle.ifBlank { session.mood }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1
                    )
                }

                // Maximize full-screen button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1C1F28))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Light)
                            onMaximize()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = "Maximize Timer",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Middle Section: Large Timer Display & Circular Ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Timer Ring & Large Digits
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(42.dp),
                            color = if (isRunning) themeColors.primaryAccent else Color(0xFFE5A43B),
                            trackColor = Color.White.copy(alpha = 0.12f),
                            strokeWidth = 3.5.dp
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) themeColors.primaryAccent else Color(0xFFE5A43B))
                        )
                    }

                    Column {
                        Text(
                            text = session.formattedRemainingTime,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.55f)
                        )
                    }
                }

                // Right: Soundscape indicator with live soundwave
                if (isPlayingAudio) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColors.primaryAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        EqualizerSoundwave(
                            color = themeColors.primaryAccent,
                            barCount = 3,
                            modifier = Modifier.height(12.dp)
                        )
                        Text(
                            text = session.soundscape.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.primaryAccent
                        )
                    }
                }
            }

            // Progress Track Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.10f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0.01f, 1f))
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    themeColors.primaryAccent,
                                    themeColors.primaryAccent.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
            }

            // Bottom Action Buttons: Pause/Resume, Music Mute/Unmute, Stop
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Pause / Resume Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(
                            if (isRunning) Color(0xFF1E212A) else themeColors.primaryAccent.copy(alpha = 0.20f)
                        )
                        .border(
                            width = 0.8.dp,
                            color = if (isRunning) Color(0xFF2E3240) else themeColors.primaryAccent.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Medium)
                            onTogglePauseResume()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            tint = if (isRunning) Color(0xFFE5A43B) else themeColors.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isRunning) "Pause" else "Resume",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // 2. Music Mute / Unmute Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(Color(0xFF1E212A))
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF2E3240),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Light)
                            onToggleAudioMute()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                            contentDescription = if (isPlayingAudio) "Mute Sound" else "Unmute Sound",
                            tint = if (isPlayingAudio) themeColors.primaryAccent else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isPlayingAudio) "Sound On" else "Muted",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // 3. Stop Session Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1519))
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF522126),
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Warning)
                            onEndSession()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Stop",
                        tint = Color(0xFFE55762),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
