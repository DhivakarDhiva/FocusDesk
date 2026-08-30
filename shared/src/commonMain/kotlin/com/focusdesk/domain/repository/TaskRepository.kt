package com.focusdesk.domain.repository

import com.focusdesk.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<TaskItem>>
    suspend fun getTask(id: String): TaskItem?
    suspend fun upsertTask(task: TaskItem)
    suspend fun deleteTask(id: String)
    suspend fun toggleTaskCompletion(id: String)
    suspend fun incrementPomodoro(taskId: String)
}
