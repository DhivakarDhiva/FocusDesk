package com.focusdesk.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusdesk.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAtTimestamp DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAtTimestamp = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: String, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET completedPomodoros = :completedCount, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updatePomodoroCount(id: String, completedCount: Int, isCompleted: Boolean)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun countTasks(): Int
}
