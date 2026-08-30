package com.focusdesk.data.repository

import com.focusdesk.domain.model.DailyFocusMetric
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.ProductivityStats
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryFocusRepository : FocusRepository {

    private val initialSession = FocusSession(
        id = "session_init",
        mode = SessionMode.Work,
        targetDurationSeconds = 25 * 60L,
        remainingSeconds = 25 * 60L,
        status = SessionStatus.Idle,
        currentRound = 1,
        totalRounds = 4
    )

    private val _currentSession = MutableStateFlow(initialSession)

    private val initialWeekly = listOf(
        DailyFocusMetric("Mon", 1, 125, 5),
        DailyFocusMetric("Tue", 2, 175, 7),
        DailyFocusMetric("Wed", 3, 200, 8),
        DailyFocusMetric("Thu", 4, 150, 6),
        DailyFocusMetric("Fri", 5, 225, 9),
        DailyFocusMetric("Sat", 6, 90, 3),
        DailyFocusMetric("Sun", 7, 120, 4)
    )

    private val _completedSessions = MutableStateFlow<List<FocusSession>>(
        listOf(
            FocusSession("hist_1", SessionMode.Work, 25 * 60L, 0L, SessionStatus.Completed, 1, 4, null, "Design System Tokens"),
            FocusSession("hist_2", SessionMode.ShortBreak, 5 * 60L, 0L, SessionStatus.Completed, 1, 4),
            FocusSession("hist_3", SessionMode.Work, 25 * 60L, 0L, SessionStatus.Completed, 2, 4, null, "MVI Store Architecture")
        )
    )

    private val _stats = MutableStateFlow(
        ProductivityStats(
            totalFocusMinutesToday = 175,
            totalFocusMinutesWeek = 1085,
            currentDayStreak = 5,
            completedTasksToday = 4,
            weeklyDistribution = initialWeekly,
            recentSessions = _completedSessions.value
        )
    )

    override fun observeCurrentSession(): Flow<FocusSession> = _currentSession.asStateFlow()

    override suspend fun getCurrentSession(): FocusSession = _currentSession.value

    override suspend fun updateCurrentSession(session: FocusSession) {
        _currentSession.value = session
    }

    override suspend fun recordCompletedSession(session: FocusSession) {
        _completedSessions.update { list -> listOf(session) + list }
        val sessionMinutes = (session.targetDurationSeconds / 60).toInt()
        _stats.update { current ->
            current.copy(
                totalFocusMinutesToday = current.totalFocusMinutesToday + sessionMinutes,
                totalFocusMinutesWeek = current.totalFocusMinutesWeek + sessionMinutes,
                recentSessions = _completedSessions.value
            )
        }
    }

    override fun observeProductivityStats(): Flow<ProductivityStats> = _stats.asStateFlow()

    override suspend fun resetAllStats() {
        _stats.value = ProductivityStats(
            totalFocusMinutesToday = 0,
            totalFocusMinutesWeek = 0,
            currentDayStreak = 0,
            completedTasksToday = 0,
            weeklyDistribution = listOf(
                DailyFocusMetric("Mon", 1, 0, 0),
                DailyFocusMetric("Tue", 2, 0, 0),
                DailyFocusMetric("Wed", 3, 0, 0),
                DailyFocusMetric("Thu", 4, 0, 0),
                DailyFocusMetric("Fri", 5, 0, 0),
                DailyFocusMetric("Sat", 6, 0, 0),
                DailyFocusMetric("Sun", 7, 0, 0)
            ),
            recentSessions = emptyList()
        )
    }
}
