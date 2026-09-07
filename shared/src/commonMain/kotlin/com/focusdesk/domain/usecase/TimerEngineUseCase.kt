package com.focusdesk.domain.usecase

import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
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

    suspend fun startCustomSession(
        taskTitle: String,
        category: String,
        mood: String,
        durationMinutes: Int,
        soundscape: Soundscape
    ): FocusSession {
        val durationSeconds = durationMinutes * 60L
        val newSession = FocusSession(
            id = "session_${System.currentTimeMillis()}",
            mode = SessionMode.Work,
            taskTitle = taskTitle,
            category = category,
            mood = mood,
            soundscape = soundscape,
            targetDurationSeconds = durationSeconds,
            remainingSeconds = durationSeconds,
            status = SessionStatus.Running,
            startedAtTimestamp = System.currentTimeMillis()
        )
        focusRepository.updateCurrentSession(newSession)
        return newSession
    }

    suspend fun addSeconds(seconds: Long) {
        val current = focusRepository.getCurrentSession()
        val updated = current.copy(
            remainingSeconds = current.remainingSeconds + seconds,
            targetDurationSeconds = current.targetDurationSeconds + seconds
        )
        focusRepository.updateCurrentSession(updated)
    }

    suspend fun endSessionEarly(): FocusSession {
        lastTickEpochMs = 0L
        val current = focusRepository.getCurrentSession()
        val completed = current.copy(
            status = SessionStatus.Completed,
            soundscape = Soundscape.None,
            completedAtTimestamp = System.currentTimeMillis()
        )
        focusRepository.recordCompletedSession(completed)
        focusRepository.updateCurrentSession(completed)
        return completed
    }

    private var lastTickEpochMs: Long = 0L

    suspend fun startTimer() {
        lastTickEpochMs = 0L
        val current = focusRepository.getCurrentSession()
        if (current.status == SessionStatus.Idle || current.status == SessionStatus.Paused) {
            val updated = current.copy(
                status = SessionStatus.Running,
                startedAtTimestamp = if (current.startedAtTimestamp == 0L) {
                    System.currentTimeMillis()
                } else current.startedAtTimestamp
            )
            focusRepository.updateCurrentSession(updated)
        }
    }

    suspend fun pauseTimer() {
        lastTickEpochMs = 0L
        val current = focusRepository.getCurrentSession()
        if (current.status == SessionStatus.Running) {
            focusRepository.updateCurrentSession(current.copy(status = SessionStatus.Paused))
        }
    }

    suspend fun tick(force: Boolean = false): FocusSession {
        val current = focusRepository.getCurrentSession()
        if (current.status != SessionStatus.Running) return current

        val now = System.currentTimeMillis()
        // Enforce minimum 850ms interval between ticks to strictly prevent double-ticking / fast ticking
        if (!force && lastTickEpochMs > 0 && (now - lastTickEpochMs) < 850L) {
            return current
        }
        lastTickEpochMs = now

        val newRemaining = current.remainingSeconds - 1
        if (newRemaining <= 0) {
            val completed = current.copy(
                remainingSeconds = 0,
                status = SessionStatus.Completed,
                soundscape = Soundscape.None,
                completedAtTimestamp = System.currentTimeMillis()
            )
            focusRepository.recordCompletedSession(completed)
            focusRepository.updateCurrentSession(completed)
            return completed
        } else {
            val updated = current.copy(remainingSeconds = newRemaining)
            focusRepository.updateCurrentSession(updated)
            return updated
        }
    }

    suspend fun resetTimer() {
        lastTickEpochMs = 0L
        val settings = settingsRepository.getSettings()
        val current = focusRepository.getCurrentSession()
        val duration = settings.intervals.workDurationSeconds
        val resetSession = current.copy(
            remainingSeconds = duration,
            targetDurationSeconds = duration,
            status = SessionStatus.Idle,
            soundscape = Soundscape.None
        )
        focusRepository.updateCurrentSession(resetSession)
    }

    suspend fun resetSession(): FocusSession {
        resetTimer()
        return focusRepository.getCurrentSession()
    }

    suspend fun switchMode(mode: SessionMode) {
        lastTickEpochMs = 0L
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

    suspend fun updateSoundscape(soundscape: Soundscape) {
        val current = focusRepository.getCurrentSession()
        focusRepository.updateCurrentSession(current.copy(soundscape = soundscape))
        if (soundscape != Soundscape.None) {
            val settings = settingsRepository.getSettings()
            settingsRepository.updateSettings(settings.copy(soundscape = soundscape))
        }
    }

    suspend fun toggleAudioMute(defaultSoundscape: Soundscape = Soundscape.Rain): Soundscape {
        val current = focusRepository.getCurrentSession()
        val settings = settingsRepository.getSettings()
        val newSoundscape = if (current.soundscape != Soundscape.None) {
            // Mute: save current soundscape in settings so it can be restored on unmute
            settingsRepository.updateSettings(settings.copy(soundscape = current.soundscape))
            Soundscape.None
        } else {
            // Unmute: restore previously saved soundscape or use default
            if (settings.soundscape != Soundscape.None) settings.soundscape else defaultSoundscape
        }
        val updated = current.copy(soundscape = newSoundscape)
        focusRepository.updateCurrentSession(updated)
        return newSoundscape
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
