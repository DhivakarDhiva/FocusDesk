package com.focusdesk.app.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusdesk.app.R
import com.focusdesk.app.presentation.components.CircularProgressRing
import com.focusdesk.app.presentation.components.PulsingGlow
import com.focusdesk.app.presentation.designsystem.FocusBreak
import com.focusdesk.app.presentation.designsystem.FocusLongBreak
import com.focusdesk.app.presentation.designsystem.FocusWork
import com.focusdesk.app.presentation.designsystem.LocalAudioPlayer
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.app.presentation.designsystem.LongBreakGradient
import com.focusdesk.app.presentation.designsystem.ShortBreakGradient
import com.focusdesk.app.presentation.designsystem.WorkGradient
import com.focusdesk.app.presentation.timer.components.AmbientSoundBadge
import com.focusdesk.app.presentation.timer.components.ModeSelector
import com.focusdesk.app.presentation.timer.components.TimerControls
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.core.platform.SoundEffect
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticEngine = LocalHapticEngine.current
    val audioEngine = LocalAudioPlayer.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TimerEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is TimerEffect.PlayCelebrationHaptic -> {
                    hapticEngine.perform(HapticFeedbackType.Success)
                    audioEngine.playOneShot(SoundEffect.Complete)
                }
                is TimerEffect.PlayTickHaptic -> {
                    hapticEngine.perform(HapticFeedbackType.Light)
                    audioEngine.playOneShot(SoundEffect.Tick)
                }
                is TimerEffect.PlaySoundEffect -> audioEngine.playOneShot(SoundEffect.Bell)
            }
        }
    }

    val currentGradient = when (state.session.mode) {
        SessionMode.Work -> WorkGradient
        SessionMode.ShortBreak -> ShortBreakGradient
        SessionMode.LongBreak -> LongBreakGradient
    }

    val currentThemeColor = when (state.session.mode) {
        SessionMode.Work -> FocusWork
        SessionMode.ShortBreak -> FocusBreak
        SessionMode.LongBreak -> FocusLongBreak
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Section: Round Counter & Mode Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.timer_round_format,
                            state.session.currentRound,
                            state.session.totalRounds
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ModeSelector(
                    selectedMode = state.session.mode,
                    onModeSelected = { mode -> viewModel.onIntent(TimerIntent.SelectMode(mode)) }
                )
            }

            // 2. Center Section: Circular Progress Ring + Digital Timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                PulsingGlow(
                    isPulsing = state.session.status == SessionStatus.Running,
                    glowColor = currentThemeColor,
                    size = 310.dp
                ) {
                    CircularProgressRing(
                        progress = state.session.progress,
                        gradientBrush = currentGradient,
                        size = 270.dp,
                        strokeWidth = 16.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.session.formattedRemainingTime,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 50.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (state.session.status) {
                                    SessionStatus.Running -> "Focusing..."
                                    SessionStatus.Paused -> "Paused"
                                    SessionStatus.Completed -> "Completed"
                                    SessionStatus.Idle -> "Ready"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                ),
                                color = currentThemeColor
                            )
                        }
                    }
                }
            }

            // 3. Bottom Section: Associated Task, Ambient Sound & Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .clickable { viewModel.onIntent(TimerIntent.OpenTaskSelector) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            tint = currentThemeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.session.associatedTaskTitle
                                ?: stringResource(R.string.timer_task_select_prompt),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AmbientSoundBadge(
                    soundscape = state.soundscape,
                    isPlaying = state.isPlayingAudio,
                    onToggle = { viewModel.onIntent(TimerIntent.ToggleSoundscapePlayback) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                TimerControls(
                    status = state.session.status,
                    gradientBrush = currentGradient,
                    onToggle = { viewModel.onIntent(TimerIntent.ToggleTimer) },
                    onReset = { viewModel.onIntent(TimerIntent.RequestReset) },
                    onSkip = { viewModel.onIntent(TimerIntent.SkipSession) }
                )
            }
        }

        if (state.isResetDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.onIntent(TimerIntent.DismissResetDialog) },
                title = { Text(text = stringResource(R.string.timer_reset_dialog_title)) },
                text = { Text(text = stringResource(R.string.timer_reset_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onIntent(TimerIntent.ConfirmReset) }) {
                        Text(
                            text = stringResource(R.string.timer_dialog_confirm),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onIntent(TimerIntent.DismissResetDialog) }) {
                        Text(text = stringResource(R.string.timer_dialog_cancel))
                    }
                }
            )
        }

        if (state.isTaskSelectorOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onIntent(TimerIntent.DismissTaskSelector) },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Link Task to Focus Session",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { viewModel.onIntent(TimerIntent.SelectAssociatedTask(null)) }
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "None (Independent Session)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.availableTasks, key = { it.id }) { task ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (state.session.associatedTaskId == task.id)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    )
                                    .clickable {
                                        viewModel.onIntent(TimerIntent.SelectAssociatedTask(task))
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${task.completedPomodoros}/${task.estimatedPomodoros} 🍅",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
