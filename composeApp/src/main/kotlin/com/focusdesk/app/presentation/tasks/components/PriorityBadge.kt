package com.focusdesk.app.presentation.tasks.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.focusdesk.app.presentation.components.TagChip
import com.focusdesk.app.presentation.designsystem.PriorityHigh
import com.focusdesk.app.presentation.designsystem.PriorityLow
import com.focusdesk.app.presentation.designsystem.PriorityMedium
import com.focusdesk.app.presentation.designsystem.PriorityUrgent
import com.focusdesk.domain.model.TaskPriority

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val color: Color = when (priority) {
        TaskPriority.Low -> PriorityLow
        TaskPriority.Medium -> PriorityMedium
        TaskPriority.High -> PriorityHigh
        TaskPriority.Urgent -> PriorityUrgent
    }

    TagChip(
        text = priority.title,
        color = color,
        modifier = modifier
    )
}
