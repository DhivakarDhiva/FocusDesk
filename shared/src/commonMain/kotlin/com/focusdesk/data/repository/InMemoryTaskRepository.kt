package com.focusdesk.data.repository

import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority
import com.focusdesk.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTaskRepository : TaskRepository {

    private val initialTasks = listOf(
        TaskItem(
            id = "t_1",
            title = "Architect KMP MVI Store",
            description = "Implement unidirectional data flow with pure reducer and one-shot effects channel.",
            isCompleted = false,
            priority = TaskPriority.Urgent,
            category = TaskCategory.Coding,
            estimatedPomodoros = 3,
            completedPomodoros = 2
        ),
        TaskItem(
            id = "t_2",
            title = "Tune Compose Spring Physics",
            description = "Ensure dampingRatio and stiffness 1:1 match SwiftUI .bouncy and .snappy curves.",
            isCompleted = false,
            priority = TaskPriority.High,
            category = TaskCategory.Design,
            estimatedPomodoros = 2,
            completedPomodoros = 1
        ),
        TaskItem(
            id = "t_3",
            title = "Implement Ambient Audio Engine",
            description = "Loop rainfall and forest soundscapes with dynamic volume control.",
            isCompleted = false,
            priority = TaskPriority.Medium,
            category = TaskCategory.DeepWork,
            estimatedPomodoros = 2,
            completedPomodoros = 0
        ),
        TaskItem(
            id = "t_4",
            title = "Write Architecture Spec",
            description = "Document component mapping and navigation graph.",
            isCompleted = true,
            priority = TaskPriority.Low,
            category = TaskCategory.Writing,
            estimatedPomodoros = 1,
            completedPomodoros = 1
        )
    )

    private val _tasks = MutableStateFlow(initialTasks)

    override fun observeTasks(): Flow<List<TaskItem>> = _tasks.asStateFlow()

    override suspend fun getTask(id: String): TaskItem? =
        _tasks.value.find { it.id == id }

    override suspend fun upsertTask(task: TaskItem) {
        _tasks.update { current ->
            val index = current.indexOfFirst { it.id == task.id }
            if (index >= 0) {
                current.toMutableList().apply { set(index, task) }
            } else {
                listOf(task) + current
            }
        }
    }

    override suspend fun deleteTask(id: String) {
        _tasks.update { current -> current.filter { it.id != id } }
    }

    override suspend fun toggleTaskCompletion(id: String) {
        _tasks.update { current ->
            current.map { task ->
                if (task.id == id) task.copy(isCompleted = !task.isCompleted) else task
            }
        }
    }

    override suspend fun incrementPomodoro(taskId: String) {
        _tasks.update { current ->
            current.map { task ->
                if (task.id == taskId) {
                    val newCount = task.completedPomodoros + 1
                    task.copy(
                        completedPomodoros = newCount,
                        isCompleted = if (newCount >= task.estimatedPomodoros) true else task.isCompleted
                    )
                } else task
            }
        }
    }
}
