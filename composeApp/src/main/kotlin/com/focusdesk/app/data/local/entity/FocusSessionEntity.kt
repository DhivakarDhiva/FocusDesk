package com.focusdesk.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    val mode: String,
    val taskTitle: String,
    val category: String,
    val mood: String,
    val soundscape: String,
    val targetDurationSeconds: Long,
    val remainingSeconds: Long,
    val status: String,
    val currentRound: Int,
    val totalRounds: Int,
    val associatedTaskId: String?,
    val associatedTaskTitle: String?,
    val startedAtTimestamp: Long,
    val completedAtTimestamp: Long?,
    val pointsEarned: Int = 0
)

fun FocusSessionEntity.toDomain(): FocusSession {
    return FocusSession(
        id = id,
        mode = runCatching { SessionMode.valueOf(mode) }.getOrDefault(SessionMode.Work),
        taskTitle = taskTitle,
        category = category,
        mood = mood,
        soundscape = runCatching { Soundscape.valueOf(soundscape) }.getOrDefault(Soundscape.None),
        targetDurationSeconds = targetDurationSeconds,
        remainingSeconds = remainingSeconds,
        status = runCatching { SessionStatus.valueOf(status) }.getOrDefault(SessionStatus.Idle),
        currentRound = currentRound,
        totalRounds = totalRounds,
        associatedTaskId = associatedTaskId,
        associatedTaskTitle = associatedTaskTitle,
        startedAtTimestamp = startedAtTimestamp,
        completedAtTimestamp = completedAtTimestamp
    )
}

fun FocusSession.toEntity(pointsEarned: Int = 0): FocusSessionEntity {
    return FocusSessionEntity(
        id = id,
        mode = mode.name,
        taskTitle = taskTitle,
        category = category,
        mood = mood,
        soundscape = soundscape.name,
        targetDurationSeconds = targetDurationSeconds,
        remainingSeconds = remainingSeconds,
        status = status.name,
        currentRound = currentRound,
        totalRounds = totalRounds,
        associatedTaskId = associatedTaskId,
        associatedTaskTitle = associatedTaskTitle,
        startedAtTimestamp = startedAtTimestamp,
        completedAtTimestamp = completedAtTimestamp,
        pointsEarned = pointsEarned
    )
}
