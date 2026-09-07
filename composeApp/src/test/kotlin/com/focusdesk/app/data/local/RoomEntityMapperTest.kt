package com.focusdesk.app.data.local

import com.focusdesk.app.data.local.entity.FocusSessionEntity
import com.focusdesk.app.data.local.entity.TaskEntity
import com.focusdesk.app.data.local.entity.UserSettingsEntity
import com.focusdesk.app.data.local.entity.toDomain
import com.focusdesk.app.data.local.entity.toEntity
import com.focusdesk.domain.model.AppTheme
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.model.TaskPriority
import com.focusdesk.domain.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomEntityMapperTest {

    @Test
    fun testFocusSessionEntityMapping() {
        val session = FocusSession(
            id = "test_sess_1",
            mode = SessionMode.Work,
            taskTitle = "Build Room DB",
            category = "Coding",
            mood = "Focused",
            soundscape = Soundscape.Rain,
            targetDurationSeconds = 1500L,
            remainingSeconds = 0L,
            status = SessionStatus.Completed,
            currentRound = 2,
            totalRounds = 4,
            associatedTaskId = "task_99",
            associatedTaskTitle = "Database Task",
            startedAtTimestamp = 1000L,
            completedAtTimestamp = 2500L
        )

        val entity = session.toEntity(pointsEarned = 25)
        assertEquals("test_sess_1", entity.id)
        assertEquals("Work", entity.mode)
        assertEquals(25, entity.pointsEarned)
        assertEquals("Rain", entity.soundscape)
        assertEquals("Completed", entity.status)

        val domain = entity.toDomain()
        assertEquals(session.id, domain.id)
        assertEquals(session.mode, domain.mode)
        assertEquals(session.taskTitle, domain.taskTitle)
        assertEquals(session.soundscape, domain.soundscape)
        assertEquals(session.status, domain.status)
        assertEquals(session.targetDurationSeconds, domain.targetDurationSeconds)
        assertEquals(session.remainingSeconds, domain.remainingSeconds)
        assertEquals(session.startedAtTimestamp, domain.startedAtTimestamp)
        assertEquals(session.completedAtTimestamp, domain.completedAtTimestamp)
    }

    @Test
    fun testUserSettingsEntityMapping() {
        val settings = UserSettings(
            autoStartBreaks = true,
            autoStartWork = true,
            soundscape = Soundscape.Forest,
            soundscapeVolume = 0.8f,
            appTheme = AppTheme.NightBloom,
            dailyGoalHours = 6,
            isOnboardingCompleted = true,
            xp = 350,
            level = 4,
            dayStreak = 7,
            bestStreak = 12
        )

        val entity = settings.toEntity(id = 1, lastActiveEpochDay = 19500L)
        assertEquals(1, entity.id)
        assertEquals(350, entity.xp)
        assertEquals(4, entity.level)
        assertEquals(7, entity.dayStreak)
        assertEquals(12, entity.bestStreak)
        assertEquals(19500L, entity.lastActiveEpochDay)
        assertTrue(entity.isOnboardingCompleted)

        val domain = entity.toDomain()
        assertEquals(settings.xp, domain.xp)
        assertEquals(settings.level, domain.level)
        assertEquals(settings.dayStreak, domain.dayStreak)
        assertEquals(settings.bestStreak, domain.bestStreak)
        assertEquals(settings.soundscape, domain.soundscape)
        assertEquals(settings.soundscapeVolume, domain.soundscapeVolume, 0.001f)
        assertEquals(settings.appTheme, domain.appTheme)
        assertEquals(settings.dailyGoalHours, domain.dailyGoalHours)
        assertTrue(domain.isOnboardingCompleted)
    }

    @Test
    fun testTaskEntityMapping() {
        val task = TaskItem(
            id = "task_123",
            title = "Integrate SQLite",
            description = "Use Android Room with KSP",
            isCompleted = true,
            priority = TaskPriority.Urgent,
            category = TaskCategory.Coding,
            estimatedPomodoros = 4,
            completedPomodoros = 4,
            createdAtTimestamp = 5000L,
            completedAtTimestamp = 9000L
        )

        val entity = task.toEntity()
        assertEquals("task_123", entity.id)
        assertEquals("Urgent", entity.priority)
        assertEquals("Coding", entity.category)
        assertTrue(entity.isCompleted)

        val domain = entity.toDomain()
        assertEquals(task.id, domain.id)
        assertEquals(task.title, domain.title)
        assertEquals(task.description, domain.description)
        assertEquals(task.priority, domain.priority)
        assertEquals(task.category, domain.category)
        assertEquals(task.estimatedPomodoros, domain.estimatedPomodoros)
        assertEquals(task.completedPomodoros, domain.completedPomodoros)
        assertTrue(domain.isCompleted)
    }
}
