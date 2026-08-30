package com.focusdesk.domain.usecase

import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority
import com.focusdesk.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ManageTasksUseCase(
    private val taskRepository: TaskRepository
) {
    fun observeAllTasks(): Flow<List<TaskItem>> = taskRepository.observeTasks()

    fun observeActiveTasks(): Flow<List<TaskItem>> =
        taskRepository.observeTasks().map { tasks ->
            tasks.filter { !it.isCompleted }
        }

    fun observeCompletedTasks(): Flow<List<TaskItem>> =
        taskRepository.observeTasks().map { tasks ->
            tasks.filter { it.isCompleted }
        }

    suspend fun createTask(
        title: String,
        description: String,
        priority: TaskPriority,
        category: TaskCategory,
        estimatedPomodoros: Int
    ): TaskItem {
        val newTask = TaskItem(
            id = "task_${kotlin.random.Random.nextLong(100000, 999999)}",
            title = title.trim(),
            description = description.trim(),
            isCompleted = false,
            priority = priority,
            category = category,
            estimatedPomodoros = estimatedPomodoros.coerceAtLeast(1),
            completedPomodoros = 0,
            createdAtTimestamp = 0L
        )
        taskRepository.upsertTask(newTask)
        return newTask
    }

    suspend fun toggleTask(id: String) {
        taskRepository.toggleTaskCompletion(id)
    }

    suspend fun deleteTask(id: String) {
        taskRepository.deleteTask(id)
    }
}
