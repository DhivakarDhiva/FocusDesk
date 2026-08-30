package com.focusdesk.app.presentation.tasks

import com.focusdesk.core.mvi.MviEffect
import com.focusdesk.core.mvi.MviIntent
import com.focusdesk.core.mvi.MviState
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority

enum class TaskFilter {
    All,
    Active,
    Completed
}

data class TasksState(
    val tasks: List<TaskItem> = emptyList(),
    val filter: TaskFilter = TaskFilter.All,
    val selectedCategory: TaskCategory? = null,
    val isAddTaskSheetOpen: Boolean = false,
    val editingTaskTitle: String = "",
    val editingTaskDescription: String = "",
    val editingTaskPriority: TaskPriority = TaskPriority.Medium,
    val editingTaskCategory: TaskCategory = TaskCategory.DeepWork,
    val editingEstimatedPomodoros: Int = 1,
    val searchQuery: String = ""
) : MviState {
    val filteredTasks: List<TaskItem>
        get() = tasks.filter { task ->
            val matchesFilter = when (filter) {
                TaskFilter.All -> true
                TaskFilter.Active -> !task.isCompleted
                TaskFilter.Completed -> task.isCompleted
            }
            val matchesCategory = selectedCategory == null || task.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) || task.description.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesCategory && matchesSearch
        }
}

sealed interface TasksIntent : MviIntent {
    data class SetFilter(val filter: TaskFilter) : TasksIntent
    data class SelectCategoryFilter(val category: TaskCategory?) : TasksIntent
    data class SearchQueryChanged(val query: String) : TasksIntent
    data class ToggleTask(val id: String) : TasksIntent
    data class DeleteTask(val id: String) : TasksIntent
    data object OpenAddTaskSheet : TasksIntent
    data object DismissAddTaskSheet : TasksIntent
    data class UpdateNewTaskTitle(val title: String) : TasksIntent
    data class UpdateNewTaskDescription(val description: String) : TasksIntent
    data class UpdateNewTaskPriority(val priority: TaskPriority) : TasksIntent
    data class UpdateNewTaskCategory(val category: TaskCategory) : TasksIntent
    data class UpdateNewTaskPomodoros(val count: Int) : TasksIntent
    data object SubmitNewTask : TasksIntent
    data class StartFocusOnTask(val task: TaskItem) : TasksIntent
}

sealed interface TasksEffect : MviEffect {
    data class ShowSnackbar(val message: String) : TasksEffect
    data object PlayCheckmarkHaptic : TasksEffect
    data class NavigateToTimerWithTask(val taskId: String, val taskTitle: String) : TasksEffect
}
