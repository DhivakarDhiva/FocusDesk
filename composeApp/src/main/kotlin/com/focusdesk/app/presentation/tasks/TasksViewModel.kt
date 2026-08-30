package com.focusdesk.app.presentation.tasks

import androidx.lifecycle.viewModelScope
import com.focusdesk.core.mvi.MviViewModel
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority
import com.focusdesk.domain.usecase.ManageTasksUseCase
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.launch

class TasksViewModel(
    private val manageTasksUseCase: ManageTasksUseCase,
    private val timerEngineUseCase: TimerEngineUseCase
) : MviViewModel<TasksIntent, TasksState, TasksEffect>(TasksState()) {

    init {
        viewModelScope.launch {
            manageTasksUseCase.observeAllTasks().collect { taskList ->
                setState { copy(tasks = taskList) }
            }
        }
    }

    override fun handleIntent(intent: TasksIntent) {
        when (intent) {
            is TasksIntent.SetFilter -> setState { copy(filter = intent.filter) }
            is TasksIntent.SelectCategoryFilter -> setState { copy(selectedCategory = intent.category) }
            is TasksIntent.SearchQueryChanged -> setState { copy(searchQuery = intent.query) }
            is TasksIntent.ToggleTask -> handleToggleTask(intent.id)
            is TasksIntent.DeleteTask -> handleDeleteTask(intent.id)
            is TasksIntent.OpenAddTaskSheet -> setState {
                copy(
                    isAddTaskSheetOpen = true,
                    editingTaskTitle = "",
                    editingTaskDescription = "",
                    editingTaskPriority = TaskPriority.Medium,
                    editingTaskCategory = TaskCategory.DeepWork,
                    editingEstimatedPomodoros = 1
                )
            }
            is TasksIntent.DismissAddTaskSheet -> setState { copy(isAddTaskSheetOpen = false) }
            is TasksIntent.UpdateNewTaskTitle -> setState { copy(editingTaskTitle = intent.title) }
            is TasksIntent.UpdateNewTaskDescription -> setState { copy(editingTaskDescription = intent.description) }
            is TasksIntent.UpdateNewTaskPriority -> setState { copy(editingTaskPriority = intent.priority) }
            is TasksIntent.UpdateNewTaskCategory -> setState { copy(editingTaskCategory = intent.category) }
            is TasksIntent.UpdateNewTaskPomodoros -> setState { copy(editingEstimatedPomodoros = intent.count) }
            is TasksIntent.SubmitNewTask -> handleSubmitNewTask()
            is TasksIntent.StartFocusOnTask -> handleStartFocusOnTask(intent.task)
        }
    }

    private fun handleToggleTask(id: String) {
        viewModelScope.launch {
            manageTasksUseCase.toggleTask(id)
            setEffect { TasksEffect.PlayCheckmarkHaptic }
        }
    }

    private fun handleDeleteTask(id: String) {
        viewModelScope.launch {
            manageTasksUseCase.deleteTask(id)
            setEffect { TasksEffect.ShowSnackbar("Task removed") }
        }
    }

    private fun handleSubmitNewTask() {
        val title = currentState.editingTaskTitle.trim()
        if (title.isBlank()) {
            setEffect { TasksEffect.ShowSnackbar("Please enter a task title") }
            return
        }

        viewModelScope.launch {
            manageTasksUseCase.createTask(
                title = title,
                description = currentState.editingTaskDescription,
                priority = currentState.editingTaskPriority,
                category = currentState.editingTaskCategory,
                estimatedPomodoros = currentState.editingEstimatedPomodoros
            )
            setState { copy(isAddTaskSheetOpen = false) }
            setEffect { TasksEffect.ShowSnackbar("Task created successfully") }
        }
    }

    private fun handleStartFocusOnTask(task: TaskItem) {
        viewModelScope.launch {
            timerEngineUseCase.attachTask(task.id, task.title)
            setEffect { TasksEffect.NavigateToTimerWithTask(task.id, task.title) }
        }
    }
}
