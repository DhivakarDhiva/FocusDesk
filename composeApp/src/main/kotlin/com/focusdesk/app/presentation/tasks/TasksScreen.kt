package com.focusdesk.app.presentation.tasks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusdesk.app.R
import com.focusdesk.app.presentation.components.EmptyStateView
import com.focusdesk.app.presentation.designsystem.FocusPrimary
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import com.focusdesk.app.presentation.tasks.components.AddTaskBottomSheet
import com.focusdesk.app.presentation.tasks.components.TaskCard
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.TaskCategory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onNavigateToTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticEngine = LocalHapticEngine.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TasksEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is TasksEffect.PlayCheckmarkHaptic -> hapticEngine.perform(HapticFeedbackType.Success)
                is TasksEffect.NavigateToTimerWithTask -> onNavigateToTimer()
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(TasksIntent.OpenAddTaskSheet) },
                containerColor = FocusPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.tasks_add_btn))
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.tasks_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onIntent(TasksIntent.SearchQueryChanged(it)) },
                placeholder = { Text(stringResource(R.string.tasks_search_placeholder)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs (All / Active / Done)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.entries.forEach { f ->
                    val isSelected = f == state.filter
                    val label = when (f) {
                        TaskFilter.All -> stringResource(R.string.tasks_filter_all)
                        TaskFilter.Active -> stringResource(R.string.tasks_filter_active)
                        TaskFilter.Completed -> stringResource(R.string.tasks_filter_completed)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { viewModel.onIntent(TasksIntent.SetFilter(f)) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isAllCatSelected = state.selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isAllCatSelected) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                        .clickable { viewModel.onIntent(TasksIntent.SelectCategoryFilter(null)) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "All Tags",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isAllCatSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isAllCatSelected) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TaskCategory.entries.forEach { cat ->
                    val isSelected = state.selectedCategory == cat
                    val catColor = Color(cat.hexColor)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) catColor.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                            .clickable { viewModel.onIntent(TasksIntent.SelectCategoryFilter(cat)) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) catColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredTasks.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.TaskAlt,
                    title = stringResource(R.string.tasks_empty_title),
                    subtitle = stringResource(R.string.tasks_empty_subtitle)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize(animationSpec = SwiftUiMotion.smooth<androidx.compose.ui.unit.IntSize>()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.filteredTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggle = { viewModel.onIntent(TasksIntent.ToggleTask(task.id)) },
                            onDelete = { viewModel.onIntent(TasksIntent.DeleteTask(task.id)) },
                            onStartFocus = { viewModel.onIntent(TasksIntent.StartFocusOnTask(task)) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        if (state.isAddTaskSheetOpen) {
            AddTaskBottomSheet(
                sheetState = sheetState,
                title = state.editingTaskTitle,
                description = state.editingTaskDescription,
                priority = state.editingTaskPriority,
                category = state.editingTaskCategory,
                estimatedPomodoros = state.editingEstimatedPomodoros,
                onTitleChange = { viewModel.onIntent(TasksIntent.UpdateNewTaskTitle(it)) },
                onDescriptionChange = { viewModel.onIntent(TasksIntent.UpdateNewTaskDescription(it)) },
                onPriorityChange = { viewModel.onIntent(TasksIntent.UpdateNewTaskPriority(it)) },
                onCategoryChange = { viewModel.onIntent(TasksIntent.UpdateNewTaskCategory(it)) },
                onPomodorosChange = { viewModel.onIntent(TasksIntent.UpdateNewTaskPomodoros(it)) },
                onSubmit = { viewModel.onIntent(TasksIntent.SubmitNewTask) },
                onDismiss = { viewModel.onIntent(TasksIntent.DismissAddTaskSheet) }
            )
        }
    }
}
