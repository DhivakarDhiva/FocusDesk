package com.focusdesk.app.presentation.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusdesk.app.R
import com.focusdesk.app.presentation.analytics.components.FocusBarChart
import com.focusdesk.app.presentation.analytics.components.SessionHistoryList
import com.focusdesk.app.presentation.analytics.components.StatCard
import com.focusdesk.app.presentation.designsystem.FocusBreak
import com.focusdesk.app.presentation.designsystem.FocusLongBreak
import com.focusdesk.app.presentation.designsystem.FocusPrimary
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.app.presentation.designsystem.PriorityHigh
import com.focusdesk.core.platform.HapticFeedbackType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticEngine = LocalHapticEngine.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AnalyticsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is AnalyticsEffect.PlayImpactHaptic -> hapticEngine.perform(HapticFeedbackType.Heavy)
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
                        text = stringResource(R.string.analytics_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(AnalyticsIntent.RequestResetStats) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = stringResource(R.string.analytics_reset_btn),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.analytics_stat_today),
                    value = state.stats.formattedTotalTimeToday,
                    icon = Icons.Default.AccessTime,
                    accentColor = FocusPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.analytics_stat_week),
                    value = state.stats.formattedTotalTimeWeek,
                    icon = Icons.Default.DateRange,
                    accentColor = FocusLongBreak,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.analytics_stat_streak),
                    value = "${state.stats.currentDayStreak} days",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = PriorityHigh,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.analytics_stat_completed),
                    value = "${state.stats.completedTasksToday} done",
                    icon = Icons.Default.CheckCircleOutline,
                    accentColor = FocusBreak,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Focus Distribution Chart
            FocusBarChart(
                metrics = state.stats.weeklyDistribution,
                selectedDayIndex = state.selectedDayIndex,
                onSelectDay = { index -> viewModel.onIntent(AnalyticsIntent.SelectDay(index)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Sessions
            Text(
                text = stringResource(R.string.analytics_history_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.stats.recentSessions.isEmpty()) {
                Text(
                    text = stringResource(R.string.analytics_empty_history),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                SessionHistoryList(sessions = state.stats.recentSessions)
            }

            Spacer(modifier = Modifier.height(90.dp))
        }

        if (state.isResetDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.onIntent(AnalyticsIntent.DismissResetDialog) },
                title = { Text(text = stringResource(R.string.analytics_reset_dialog_title)) },
                text = { Text(text = stringResource(R.string.analytics_reset_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onIntent(AnalyticsIntent.ConfirmResetStats) }) {
                        Text(
                            text = stringResource(R.string.timer_dialog_confirm),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onIntent(AnalyticsIntent.DismissResetDialog) }) {
                        Text(text = stringResource(R.string.timer_dialog_cancel))
                    }
                }
            )
        }
    }
}
