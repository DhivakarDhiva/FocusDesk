package com.focusdesk.data.repository

import com.focusdesk.domain.model.DailyFocusMetric
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.ProductivityStats
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryFocusRepository : FocusRepository {

    private val initialSession = FocusSession(
        id = "session_default",
        mode = SessionMode.Work,
        taskTitle = "Test",
        category = "Work",
        mood = "Calm",
        soundscape = Soundscape.None,
        targetDurationSeconds = 25 * 60L,
        remainingSeconds = 25 * 60L,
        status = SessionStatus.Idle,
        currentRound = 1,
        totalRounds = 4
    )

    private val _currentSession = MutableStateFlow(initialSession)

    private val emptyWeekly = listOf(
        DailyFocusMetric("M", 1, 0, 0),
        DailyFocusMetric("T", 2, 0, 0),
        DailyFocusMetric("W", 3, 0, 0),
        DailyFocusMetric("T", 4, 0, 0),
        DailyFocusMetric("F", 5, 0, 0),
        DailyFocusMetric("S", 6, 0, 0),
        DailyFocusMetric("S", 7, 0, 0)
    )

    private val _completedSessions = MutableStateFlow<List<FocusSession>>(emptyList())

    private val _stats = MutableStateFlow(
        ProductivityStats(
            totalFocusMinutesToday = 0,
            totalFocusMinutesWeek = 0,
            currentDayStreak = 0,
            completedTasksToday = 0,
            weeklyDistribution = emptyWeekly,
            recentSessions = emptyList()
        )
    )

    override fun observeCurrentSession(): Flow<FocusSession> = _currentSession.asStateFlow()

    override suspend fun getCurrentSession(): FocusSession = _currentSession.value

    override suspend fun updateCurrentSession(session: FocusSession) {
        _currentSession.value = session
    }

    override suspend fun recordCompletedSession(session: FocusSession) {
        _completedSessions.update { list -> listOf(session) + list }
        val sessionMinutes = ((session.targetDurationSeconds - session.remainingSeconds).coerceAtLeast(60L) / 60).toInt()
        _stats.update { current ->
            current.copy(
                totalFocusMinutesToday = current.totalFocusMinutesToday + sessionMinutes,
                totalFocusMinutesWeek = current.totalFocusMinutesWeek + sessionMinutes,
                currentDayStreak = 1,
                completedTasksToday = current.completedTasksToday + 1,
                recentSessions = _completedSessions.value
            )
        }
    }

    override fun observeProductivityStats(): Flow<ProductivityStats> = _stats.asStateFlow()

    override suspend fun resetAllStats() {
        _completedSessions.value = emptyList()
        _currentSession.value = initialSession
        _stats.value = ProductivityStats(
            totalFocusMinutesToday = 0,
            totalFocusMinutesWeek = 0,
            currentDayStreak = 0,
            completedTasksToday = 0,
            weeklyDistribution = emptyWeekly,
            recentSessions = emptyList()
        )
    }
}
