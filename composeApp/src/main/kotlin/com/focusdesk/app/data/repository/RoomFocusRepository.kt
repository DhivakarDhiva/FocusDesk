package com.focusdesk.app.data.repository

import com.focusdesk.app.data.local.dao.FocusSessionDao
import com.focusdesk.app.data.local.dao.TaskDao
import com.focusdesk.app.data.local.dao.UserSettingsDao
import com.focusdesk.app.data.local.entity.toDomain
import com.focusdesk.app.data.local.entity.toEntity
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
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class RoomFocusRepository(
    private val focusSessionDao: FocusSessionDao,
    private val userSettingsDao: UserSettingsDao,
    private val taskDao: TaskDao
) : FocusRepository {

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

    override fun observeCurrentSession(): Flow<FocusSession> = _currentSession.asStateFlow()

    override suspend fun getCurrentSession(): FocusSession = _currentSession.value

    override suspend fun updateCurrentSession(session: FocusSession) {
        _currentSession.value = session
    }

    override suspend fun recordCompletedSession(session: FocusSession) {
        val sessionMinutes = ((session.targetDurationSeconds - session.remainingSeconds).coerceAtLeast(60L) / 60).toInt()
        val pointsEarned = maxOf(sessionMinutes, 6) // Base XP per completed session + duration bonus

        val entity = session.toEntity(pointsEarned = pointsEarned)
        focusSessionDao.upsertSession(entity)

        // Award XP and calculate daily streaks in Room
        val settings = userSettingsDao.getSettings()
        if (settings != null) {
            val nowMs = System.currentTimeMillis()
            val currentEpochDay = nowMs / (1000L * 60 * 60 * 24)

            val newStreak = when {
                settings.lastActiveEpochDay == currentEpochDay -> settings.dayStreak.coerceAtLeast(1)
                settings.lastActiveEpochDay == currentEpochDay - 1 -> settings.dayStreak + 1
                else -> 1
            }
            val bestStreak = maxOf(settings.bestStreak, newStreak)
            val newXp = settings.xp + pointsEarned
            val newLevel = (newXp / 100) + 1

            userSettingsDao.updateXpAndStreak(
                newXp = newXp,
                newLevel = newLevel,
                dayStreak = newStreak,
                bestStreak = bestStreak,
                lastActiveEpochDay = currentEpochDay
            )
        }
    }

    override fun observeProductivityStats(): Flow<ProductivityStats> {
        return combine(
            focusSessionDao.observeCompletedSessions(),
            taskDao.observeAllTasks(),
            userSettingsDao.observeSettings()
        ) { sessions, tasks, settings ->
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val todayStartMs = calendar.timeInMillis

            // Week calculation starting Monday
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val weekStartMs = calendar.timeInMillis

            var todayMinutes = 0
            var weekMinutes = 0

            // 7 days distribution: M, T, W, T, F, S, S
            val dayOfWeekBuckets = Array(7) { DayBucket() }
            val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

            sessions.forEach { s ->
                val compTime = s.completedAtTimestamp ?: s.startedAtTimestamp
                val durationMin = ((s.targetDurationSeconds - s.remainingSeconds).coerceAtLeast(60L) / 60).toInt()

                if (compTime >= todayStartMs) {
                    todayMinutes += durationMin
                }
                if (compTime >= weekStartMs) {
                    weekMinutes += durationMin

                    val sessionCal = Calendar.getInstance().apply { timeInMillis = compTime }
                    val calDay = sessionCal.get(Calendar.DAY_OF_WEEK)
                    // Convert Calendar.DAY_OF_WEEK (SUNDAY=1, MONDAY=2...) to Monday=0 ... Sunday=6
                    val bucketIndex = when (calDay) {
                        Calendar.MONDAY -> 0
                        Calendar.TUESDAY -> 1
                        Calendar.WEDNESDAY -> 2
                        Calendar.THURSDAY -> 3
                        Calendar.FRIDAY -> 4
                        Calendar.SATURDAY -> 5
                        Calendar.SUNDAY -> 6
                        else -> 0
                    }
                    dayOfWeekBuckets[bucketIndex].minutes += durationMin
                    dayOfWeekBuckets[bucketIndex].sessionsCount += 1
                }
            }

            val weeklyDistribution = dayOfWeekBuckets.mapIndexed { index, bucket ->
                DailyFocusMetric(
                    dayLabel = dayLabels[index],
                    dayOfWeekIndex = index + 1,
                    focusMinutes = bucket.minutes,
                    completedSessionsCount = bucket.sessionsCount
                )
            }

            val completedTasksToday = tasks.count {
                it.isCompleted && (it.completedAtTimestamp ?: 0L) >= todayStartMs
            }

            ProductivityStats(
                totalFocusMinutesToday = todayMinutes,
                totalFocusMinutesWeek = weekMinutes,
                currentDayStreak = settings?.dayStreak ?: 0,
                completedTasksToday = completedTasksToday,
                weeklyDistribution = weeklyDistribution,
                recentSessions = sessions.take(15).map { it.toDomain() }
            )
        }
    }

    override suspend fun resetAllStats() {
        focusSessionDao.deleteAllSessions()
        _currentSession.value = initialSession

        val settings = userSettingsDao.getSettings()
        if (settings != null) {
            userSettingsDao.updateXpAndStreak(
                newXp = 0,
                newLevel = 1,
                dayStreak = 0,
                bestStreak = settings.bestStreak,
                lastActiveEpochDay = 0L
            )
        }
    }

    private class DayBucket(var minutes: Int = 0, var sessionsCount: Int = 0)
}
