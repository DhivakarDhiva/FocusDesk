package com.focusdesk.domain.usecase

import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TimerEngineUseCase(
    private val focusRepository: FocusRepository,
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) {
    fun observeSession(): Flow<FocusSession> = focusRepository.observeCurrentSession()

    suspend fun startTimer() {
        val current = focusRepository.getCurrentSession()
        if (current.status == SessionStatus.Idle || current.status == SessionStatus.Paused) {
            val updated = current.copy(
                status = SessionStatus.Running,
                startedAtTimestamp = if (current.startedAtTimestamp == 0L) {
                    // System time in millis
                    kotlin.math.max(1L, current.startedAtTimestamp)
                } else current.startedAtTimestamp
            )
            focusRepository.updateCurrentSession(updated)
        }
    }

    suspend fun pauseTimer() {
        val current = focusRepository.getCurrentSession()
        if (current.status == SessionStatus.Running) {
            focusRepository.updateCurrentSession(current.copy(status = SessionStatus.Paused))
        }
    }

    suspend fun tick(): FocusSession {
        val current = focusRepository.getCurrentSession()
        if (current.status != SessionStatus.Running) return current

        val newRemaining = current.remainingSeconds - 1
        if (newRemaining <= 0) {
            val completed = current.copy(
                remainingSeconds = 0,
                status = SessionStatus.Completed
            )
            focusRepository.recordCompletedSession(completed)
            current.associatedTaskId?.let { taskId ->
                if (current.mode == SessionMode.Work) {
                    taskRepository.incrementPomodoro(taskId)
                }
            }
            // Transition to next session
            val nextMode = determineNextMode(current.mode, current.currentRound, current.totalRounds)
            val settings = settingsRepository.getSettings()
            val nextDuration = when (nextMode) {
                SessionMode.Work -> settings.intervals.workDurationSeconds
                SessionMode.ShortBreak -> settings.intervals.shortBreakDurationSeconds
                SessionMode.LongBreak -> settings.intervals.longBreakDurationSeconds
            }
            val nextRound = if (current.mode != SessionMode.Work) {
                if (current.currentRound >= current.totalRounds) 1 else current.currentRound + 1
            } else {
                current.currentRound
            }

            val nextSession = FocusSession(
                id = "session_${nextRound}_${nextMode.name}",
                mode = nextMode,
                targetDurationSeconds = nextDuration,
                remainingSeconds = nextDuration,
                status = if (settings.autoStartBreaks && nextMode != SessionMode.Work) SessionStatus.Running else SessionStatus.Idle,
                currentRound = nextRound,
                totalRounds = settings.intervals.sessionsBeforeLongBreak,
                associatedTaskId = if (nextMode == SessionMode.Work) current.associatedTaskId else null,
                associatedTaskTitle = if (nextMode == SessionMode.Work) current.associatedTaskTitle else null
            )
            focusRepository.updateCurrentSession(nextSession)
            return completed
        } else {
            val updated = current.copy(remainingSeconds = newRemaining)
            focusRepository.updateCurrentSession(updated)
            return updated
        }
    }

    suspend fun resetTimer() {
        val settings = settingsRepository.getSettings()
        val current = focusRepository.getCurrentSession()
        val duration = when (current.mode) {
            SessionMode.Work -> settings.intervals.workDurationSeconds
            SessionMode.ShortBreak -> settings.intervals.shortBreakDurationSeconds
            SessionMode.LongBreak -> settings.intervals.longBreakDurationSeconds
        }
        val resetSession = current.copy(
            remainingSeconds = duration,
            targetDurationSeconds = duration,
            status = SessionStatus.Idle
        )
        focusRepository.updateCurrentSession(resetSession)
    }

    suspend fun switchMode(mode: SessionMode) {
        val settings = settingsRepository.getSettings()
        val duration = when (mode) {
            SessionMode.Work -> settings.intervals.workDurationSeconds
            SessionMode.ShortBreak -> settings.intervals.shortBreakDurationSeconds
            SessionMode.LongBreak -> settings.intervals.longBreakDurationSeconds
        }
        val current = focusRepository.getCurrentSession()
        val newSession = current.copy(
            mode = mode,
            targetDurationSeconds = duration,
            remainingSeconds = duration,
            status = SessionStatus.Idle
        )
        focusRepository.updateCurrentSession(newSession)
    }

    suspend fun skipSession() {
        val current = focusRepository.getCurrentSession()
        val nextMode = determineNextMode(current.mode, current.currentRound, current.totalRounds)
        switchMode(nextMode)
    }

    suspend fun attachTask(taskId: String?, taskTitle: String?) {
        val current = focusRepository.getCurrentSession()
        focusRepository.updateCurrentSession(
            current.copy(associatedTaskId = taskId, associatedTaskTitle = taskTitle)
        )
    }

    private fun determineNextMode(currentMode: SessionMode, currentRound: Int, totalRounds: Int): SessionMode {
        return when (currentMode) {
            SessionMode.Work -> {
                if (currentRound >= totalRounds) SessionMode.LongBreak else SessionMode.ShortBreak
            }
            SessionMode.ShortBreak, SessionMode.LongBreak -> SessionMode.Work
        }
    }
}
