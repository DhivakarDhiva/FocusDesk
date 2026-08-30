package com.focusdesk.app.presentation.timer.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.R
import com.focusdesk.app.presentation.designsystem.FocusBreak
import com.focusdesk.app.presentation.designsystem.FocusLongBreak
import com.focusdesk.app.presentation.designsystem.FocusWork
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import com.focusdesk.domain.model.SessionMode

@Composable
fun ModeSelector(
    selectedMode: SessionMode,
    onModeSelected: (SessionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(SessionMode.Work, SessionMode.ShortBreak, SessionMode.LongBreak)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            modes.forEach { mode ->
                val isSelected = mode == selectedMode
                val targetBgColor = when {
                    isSelected && mode == SessionMode.Work -> FocusWork
                    isSelected && mode == SessionMode.ShortBreak -> FocusBreak
                    isSelected && mode == SessionMode.LongBreak -> FocusLongBreak
                    else -> Color.Transparent
                }

                val animatedBgColor by animateColorAsState(
                    targetValue = targetBgColor,
                    animationSpec = SwiftUiMotion.snappy(),
                    label = "mode_bg_anim"
                )

                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = SwiftUiMotion.snappy(),
                    label = "mode_text_anim"
                )

                val modeLabel = when (mode) {
                    SessionMode.Work -> stringResource(R.string.timer_mode_work)
                    SessionMode.ShortBreak -> stringResource(R.string.timer_mode_short_break)
                    SessionMode.LongBreak -> stringResource(R.string.timer_mode_long_break)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(animatedBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onModeSelected(mode)
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = modeLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = animatedTextColor
                    )
                }
            }
        }
    }
}
