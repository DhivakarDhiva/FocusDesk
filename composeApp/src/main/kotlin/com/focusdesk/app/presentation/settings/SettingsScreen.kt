package com.focusdesk.app.presentation.settings

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusdesk.app.R
import com.focusdesk.app.presentation.designsystem.FocusPrimary
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.app.presentation.settings.components.IntervalSlider
import com.focusdesk.app.presentation.settings.components.SettingSection
import com.focusdesk.app.presentation.settings.components.SoundscapeSelector
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.AppThemeMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticEngine = LocalHapticEngine.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is SettingsEffect.PlayFeedbackHaptic -> hapticEngine.perform(HapticFeedbackType.Selection)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Timer Intervals
            SettingSection(title = stringResource(R.string.settings_section_intervals)) {
                IntervalSlider(
                    title = stringResource(R.string.settings_work_duration),
                    value = state.settings.intervals.workDurationMinutes,
                    valueRange = 5f..90f,
                    steps = 16,
                    suffix = "min",
                    onValueChange = { viewModel.onIntent(SettingsIntent.UpdateWorkDuration(it)) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                IntervalSlider(
                    title = stringResource(R.string.settings_short_break_duration),
                    value = state.settings.intervals.shortBreakDurationMinutes,
                    valueRange = 1f..30f,
                    steps = 28,
                    suffix = "min",
                    onValueChange = { viewModel.onIntent(SettingsIntent.UpdateShortBreakDuration(it)) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                IntervalSlider(
                    title = stringResource(R.string.settings_long_break_duration),
                    value = state.settings.intervals.longBreakDurationMinutes,
                    valueRange = 5f..45f,
                    steps = 7,
                    suffix = "min",
                    onValueChange = { viewModel.onIntent(SettingsIntent.UpdateLongBreakDuration(it)) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                IntervalSlider(
                    title = stringResource(R.string.settings_rounds_count),
                    value = state.settings.intervals.sessionsBeforeLongBreak,
                    valueRange = 2f..8f,
                    steps = 5,
                    suffix = "rounds",
                    onValueChange = { viewModel.onIntent(SettingsIntent.UpdateSessionsBeforeLongBreak(it)) }
                )
            }

            // 2. Automation
            SettingSection(title = stringResource(R.string.settings_section_automation)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_auto_break),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.settings.autoStartBreaks,
                        onCheckedChange = { viewModel.onIntent(SettingsIntent.UpdateAutoStartBreaks(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FocusPrimary)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_auto_work),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.settings.autoStartWork,
                        onCheckedChange = { viewModel.onIntent(SettingsIntent.UpdateAutoStartWork(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FocusPrimary)
                    )
                }
            }

            // 3. Audio & Soundscapes
            SettingSection(title = stringResource(R.string.settings_section_audio)) {
                Text(
                    text = stringResource(R.string.settings_soundscape_label),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                SoundscapeSelector(
                    selectedSoundscape = state.settings.soundscape,
                    onSoundscapeSelect = { viewModel.onIntent(SettingsIntent.UpdateSoundscape(it)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_soundscape_volume),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(state.settings.soundscapeVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = FocusPrimary
                    )
                }
                Slider(
                    value = state.settings.soundscapeVolume,
                    onValueChange = { viewModel.onIntent(SettingsIntent.UpdateSoundscapeVolume(it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = FocusPrimary,
                        activeTrackColor = FocusPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_tick_sounds),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.settings.tickSoundsEnabled,
                        onCheckedChange = { viewModel.onIntent(SettingsIntent.UpdateTickSounds(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FocusPrimary)
                    )
                }
            }

            // 4. Appearance & Haptics
            SettingSection(title = stringResource(R.string.settings_section_appearance)) {
                Text(
                    text = stringResource(R.string.settings_theme),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = mode == state.settings.themeMode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { viewModel.onIntent(SettingsIntent.UpdateThemeMode(mode)) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_dynamic_color),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.settings.useDynamicColor,
                        onCheckedChange = { viewModel.onIntent(SettingsIntent.UpdateDynamicColor(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FocusPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_haptics),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.settings.hapticsEnabled,
                        onCheckedChange = { viewModel.onIntent(SettingsIntent.UpdateHaptics(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FocusPrimary)
                    )
                }
            }

            // 5. Reset Defaults Button
            Button(
                onClick = { viewModel.onIntent(SettingsIntent.ResetToDefaults) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.settings_reset_defaults),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
