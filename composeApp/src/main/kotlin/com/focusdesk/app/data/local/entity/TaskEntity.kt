package com.focusdesk.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium",
    val category: String = "DeepWork",
    val estimatedPomodoros: Int = 1,
    val completedPomodoros: Int = 0,
    val createdAtTimestamp: Long = 0L,
    val completedAtTimestamp: Long? = null
)

fun TaskEntity.toDomain(): TaskItem {
    return TaskItem(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = runCatching { TaskPriority.valueOf(priority) }.getOrDefault(TaskPriority.Medium),
        category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.DeepWork),
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros,
        createdAtTimestamp = createdAtTimestamp,
        completedAtTimestamp = completedAtTimestamp
    )
}

fun TaskItem.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = priority.name,
        category = category.name,
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros,
        createdAtTimestamp = createdAtTimestamp,
        completedAtTimestamp = completedAtTimestamp
    )
}
