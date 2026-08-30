package com.focusdesk.domain.model

enum class TaskPriority(val title: String, val level: Int) {
    Low("Low", 1),
    Medium("Medium", 2),
    High("High", 3),
    Urgent("Urgent", 4)
}

enum class TaskCategory(val displayName: String, val hexColor: Long) {
    DeepWork("Deep Work", 0xFF6366F1),
    Coding("Development", 0xFF06B6D4),
    Writing("Writing", 0xFF10B981),
    Planning("Planning", 0xFFF59E0B),
    Design("Design", 0xFFEC4899)
}

data class TaskItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.Medium,
    val category: TaskCategory = TaskCategory.DeepWork,
    val estimatedPomodoros: Int = 1,
    val completedPomodoros: Int = 0,
    val createdAtTimestamp: Long = 0L,
    val completedAtTimestamp: Long? = null
)
