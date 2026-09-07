package com.focusdesk.domain.model

enum class SessionMode(val displayName: String, val defaultMinutes: Int) {
    Work("Focus", 25),
    ShortBreak("Short Break", 5),
    LongBreak("Long Break", 15)
}

enum class SessionStatus {
    Idle,
    Running,
    Paused,
    Completed
}

data class FocusSession(
    val id: String,
    val mode: SessionMode = SessionMode.Work,
    val taskTitle: String = "Test",
    val category: String = "Work",
    val mood: String = "Calm",
    val soundscape: Soundscape = Soundscape.None,
    val targetDurationSeconds: Long = 1500L,
    val remainingSeconds: Long = 1500L,
    val status: SessionStatus = SessionStatus.Idle,
    val currentRound: Int = 1,
    val totalRounds: Int = 4,
    val associatedTaskId: String? = null,
    val associatedTaskTitle: String? = null,
    val startedAtTimestamp: Long = 0L,
    val completedAtTimestamp: Long? = null
) {
    val progress: Float
        get() = if (targetDurationSeconds > 0) {
            1f - (remainingSeconds.toFloat() / targetDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedRemainingTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
}
