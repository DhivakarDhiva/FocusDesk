package com.focusdesk.app.data.repository

import com.focusdesk.app.data.local.dao.TaskDao
import com.focusdesk.app.data.local.entity.toDomain
import com.focusdesk.app.data.local.entity.toEntity
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<TaskItem>> {
        return taskDao.observeAllTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getTask(id: String): TaskItem? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun upsertTask(task: TaskItem) {
        val entity = if (task.createdAtTimestamp == 0L) {
            task.copy(createdAtTimestamp = System.currentTimeMillis()).toEntity()
        } else {
            task.toEntity()
        }
        taskDao.upsertTask(entity)
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTaskById(id)
    }

    override suspend fun toggleTaskCompletion(id: String) {
        val current = taskDao.getTaskById(id) ?: return
        val newStatus = !current.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(id, newStatus, completedAt)
    }

    override suspend fun incrementPomodoro(taskId: String) {
        val current = taskDao.getTaskById(taskId) ?: return
        val newCount = current.completedPomodoros + 1
        val isCompleted = if (newCount >= current.estimatedPomodoros) true else current.isCompleted
        taskDao.updatePomodoroCount(taskId, newCount, isCompleted)
    }
}
